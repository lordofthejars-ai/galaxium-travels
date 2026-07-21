package org.acme;

public record FlightNotification(
        String flightId,
        String message) {
}