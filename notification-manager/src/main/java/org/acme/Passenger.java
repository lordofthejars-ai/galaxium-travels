package org.acme;

public record Passenger(
        long id,
        String name,
        String email,
        long telegramId) {
}
