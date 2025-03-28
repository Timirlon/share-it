package org.example.shareit.exceptions;

public class DataConflictException extends RuntimeException {
    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }
}
