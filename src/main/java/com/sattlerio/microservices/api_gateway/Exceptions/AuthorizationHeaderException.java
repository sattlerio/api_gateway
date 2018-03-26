package com.sattlerio.microservices.api_gateway.Exceptions;


public class AuthorizationHeaderException extends ExceptionObject {

    public AuthorizationHeaderException() {}

    public AuthorizationHeaderException(String s) { super(s); }

    public AuthorizationHeaderException(String s, Throwable throwable) { super(s, throwable); }

    public AuthorizationHeaderException(Throwable throwable) { super(throwable); }
}
