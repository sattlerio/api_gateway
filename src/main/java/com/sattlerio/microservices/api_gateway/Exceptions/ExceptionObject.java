package com.sattlerio.microservices.api_gateway.Exceptions;

public class ExceptionObject extends Exception {
    public ExceptionObject() {}

    public ExceptionObject(String s) { super(s); }

    public ExceptionObject(String s, Throwable throwable) { super(s, throwable); }

    public ExceptionObject(Throwable throwable) { super(throwable); }

}
