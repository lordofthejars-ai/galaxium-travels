package com.galaxium.booking.boardingpass;

import java.time.LocalDateTime;

public record BoardingPassData(
    String passengerName,
    String origin,
    String destination,
    String flightId,
    String bookingId,
    String tripClass,
    LocalDateTime departureTime
) {
}
