package org.acme;

public record FlightNotification(
        long flightId,
        String message) {
}