package com.galaxium.holdservice.client;

public class FlightLookupException extends RuntimeException {
    public FlightLookupException(String message) {
        super(message);
    }
    
    public FlightLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob
