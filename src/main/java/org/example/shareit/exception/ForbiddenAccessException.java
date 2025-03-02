package org.example.shareit.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException() {
        super();
    }

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
