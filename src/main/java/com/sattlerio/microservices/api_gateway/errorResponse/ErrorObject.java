package com.sattlerio.microservices.api_gateway.errorResponse;

public class ErrorObject {

    private String requestId;
    private String message;
    private String status;

    public ErrorObject(String requestId, String message, String status) {
        this.requestId = requestId;
        this.message = message;
        this.status = status;
    }

    public ErrorObject() {}

    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }
}
