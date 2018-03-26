package com.sattlerio.microservices.api_gateway;

import com.sattlerio.microservices.api_gateway.filters.route.RouteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;

import com.sattlerio.microservices.api_gateway.filters.pre.PreFilter;

@SpringBootApplication
@EnableZuulProxy
@RestController
public class Application {

    @Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }

    @Bean
    public RouteFilter routeFilter() {
        return new RouteFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}