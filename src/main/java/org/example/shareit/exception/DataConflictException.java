package org.example.shareit.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }
}
