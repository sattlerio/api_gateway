package com.sattlerio.microservices.api_gateway.Exceptions;


public class JWTParsingException extends ExceptionObject {

    public JWTParsingException() {}

    public JWTParsingException(String s) { super(s); }

    public JWTParsingException(String s, Throwable throwable) { super(s, throwable); }

    public JWTParsingException(Throwable throwable) { super(throwable); }
}
