package br.com.sales.hexagonal_architecture_demo.infrastructure.input.response;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timeStamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}