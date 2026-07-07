package com.galaxium.booking.client.dto;

public record SupportTicketDto(String user, String userEmail, Long bookingId, String message) {
}
