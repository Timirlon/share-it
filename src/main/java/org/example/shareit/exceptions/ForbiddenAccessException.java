package org.example.shareit.exceptions;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException() {
        super();
    }

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
