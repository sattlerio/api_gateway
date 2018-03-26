package com.sattlerio.microservices.api_gateway.filters.pre;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;

import com.sattlerio.microservices.api_gateway.Exceptions.AuthorizationHeaderException;
import com.sattlerio.microservices.api_gateway.Exceptions.JWTParsingException;
import com.sattlerio.microservices.api_gateway.errorResponse.ErrorObject;
import com.sattlerio.microservices.api_gateway.objects.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import io.jsonwebtoken.Jwts;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.*;

public class PreFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(PreFilter.class);

    @Value("${zuul.requestUriKey}")
    private String requestUriKey;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    private RequestContext generateContext(String tid) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulResponseHeader("X-TransactionID", tid);
        ctx.addZuulRequestHeader("X-TransactionID", tid);

        return ctx;
    }

    @Override
    public Object run() {
        String transactionID = UUID.randomUUID().toString();

        try {

            RequestContext ctx = generateContext(transactionID);

            HttpServletRequest request = ctx.getRequest();

            logInfoWithTransactionID(transactionID, "new request to" + request.getRequestURL().toString());

            if (request.getRequestURI().contains(requestUriKey)) {
                String header = request.getHeader("Authorization");

                log.info("--------------");
                log.info(request.getMethod());
                MultiValueMap<String,String> headers = new HttpHeaders();
                for (Enumeration names = request.getHeaderNames(); names.hasMoreElements();) {
                    String name = (String)names.nextElement();
                    for (Enumeration values = request.getHeaders(name); values.hasMoreElements();) {
                        String value = (String)values.nextElement();
                        headers.add(name,value);
                    }
                };
                log.info(headers.toString());
                log.info("---------------");


                if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {

                    logInfoWithTransactionID(transactionID, "no aut header found abort transaction");

                    return null;

                } else {

                    String token = header.replace("Bearer ", "");

                    try {
                        User user = generateUser(token);

                        ctx.addZuulRequestHeader("X-User-Firstname", user.getFirstname());
                        ctx.addZuulRequestHeader("X-User-Lastname", user.getLastname());
                        ctx.addZuulRequestHeader("X-User-Email", user.getEmail());
                        ctx.addZuulRequestHeader("X-User-UUID", user.getUuid());
                        ctx.addZuulRequestHeader("X-User-ID", user.getUser_id());

                        log.info(String.format("%s: everything good, forward transaction", transactionID));

                    } catch (Exception e) {
                        log.error(String.format("%s: exception during JWT validation --> %s", transactionID, e.getMessage()));
                        log.error(e.toString());

                        throw new JWTParsingException("exception with jwt validation", e);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            try {
                RequestContext ctx = generateContext(transactionID);
                ObjectMapper mapper = new ObjectMapper();

                String responseJson;

                if (e instanceof JWTParsingException) {
                    ErrorObject response = new ErrorObject(transactionID, "not possible to parse JWT", "ERROR");
                    responseJson = mapper.writeValueAsString(response);
                    ctx.setResponseStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                } else if (e instanceof AuthorizationHeaderException) {
                    ErrorObject response = new ErrorObject(transactionID, "no authorization header found", "ERROR");
                    responseJson = mapper.writeValueAsString(response);
                    ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                } else {
                    ErrorObject response = new ErrorObject(transactionID, "unkown error", "ERROR");
                    responseJson = mapper.writeValueAsString(response);
                    ctx.setResponseStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                }

                ctx.setResponseBody(responseJson);

                ctx.setSendZuulResponse(false);
                return null;
            } catch (Exception finalError) {
                log.error(finalError.getMessage());
                RequestContext ctx = generateContext(transactionID);
                ctx.setResponseStatusCode(HttpStatus.EXPECTATION_FAILED.value());

                return null;
            }
        }
    }

    private void logInfoWithTransactionID(String tid, String message) {
        log.info(String.format("%s: %s", tid, message));
    }

    private User generateUser(String token) throws JWTParsingException {
        try {
            final String SECRET = Base64.getEncoder().encodeToString("secret".getBytes());

            Claims userJWT = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            Map userMap = userJWT.get("identity", Map.class);


            String firstname = "";
            String lastname = "";
            String email = "";
            String uuid = "";
            String user_id = "";

            for (Object key : userMap.keySet()) {
                String value = userMap.get(key).toString();
                if(key == "firstname") {
                    firstname = value;
                }
                if(key == "lastname") {
                    lastname = value;
                }
                if(key == "email") {
                    email = value;
                }
                if(key == "uuid") {
                    uuid = value;
                }
                if(key == "user_id") {
                    user_id = value;
                }
            }
            if((firstname == null || firstname.isEmpty()) || (lastname == null || lastname.isEmpty())
                    || (email == null || email.isEmpty()) || (uuid == null || uuid.isEmpty()) || (user_id == null || user_id.isEmpty())) {
                throw new JWTParsingException("missing data in user object");
            }

            User user = new User(firstname, lastname, email, uuid, user_id);
            return user;

        } catch (Exception e) {
            log.info("exception during JWT parsing");
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
            log.error(e.getMessage(), e);
            throw new JWTParsingException(e);
        }
    }

}