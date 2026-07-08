package com.galaxium.booking.client.dto;

public record SupportTicketDto(String user, String email, Long bookingId, String message) {
}
