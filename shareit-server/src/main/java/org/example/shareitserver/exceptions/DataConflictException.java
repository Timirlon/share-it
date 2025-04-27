package org.example.shareitserver.exceptions;

public class DataConflictException extends RuntimeException {
    public DataConflictException() {
        super();
    }

    public DataConflictException(String message) {
        super(message);
    }
}
