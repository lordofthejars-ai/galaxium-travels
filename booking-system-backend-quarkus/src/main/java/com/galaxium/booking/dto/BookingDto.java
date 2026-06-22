package com.galaxium.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galaxium.booking.entity.Booking;
import com.galaxium.booking.entity.BookingStatus;
import com.galaxium.booking.entity.SeatClass;

/**
 * Booking data transfer object.
 * Matches Python backend's BookingOut schema.
 */
public class BookingDto {

    static ObjectMapper objectMapper = new ObjectMapper();

    @JsonProperty("booking_id")
    public Long bookingId;
    
    @JsonProperty("user_id")
    public Long userId;
    
    @JsonProperty("flight_id")
    public Long flightId;

    //@JsonRawValue
    public String status;
    
    @JsonProperty("booking_time")
    public String bookingTime;
    
    @JsonProperty("seat_class")
    public String seatClass;
    
    @JsonProperty("price_paid")
    public Integer pricePaid;
    
    public JsonNode addons;  // JSON string

    public BookingDto() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BookingDto{");
        sb.append("bookingId=").append(bookingId);
        sb.append(", userId=").append(userId);
        sb.append(", flightId=").append(flightId);
        sb.append(", status='").append(status).append('\'');
        sb.append(", bookingTime='").append(bookingTime).append('\'');
        sb.append(", seatClass='").append(seatClass).append('\'');
        sb.append(", pricePaid=").append(pricePaid);
        sb.append(", addons='").append(addons).append('\'');
        sb.append('}');
        return sb.toString();
    }

    /**
     * Create DTO from entity.
     */
    public static BookingDto from(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.bookingId = booking.id;
        dto.userId = booking.user.id;
        dto.flightId = booking.flight.id;
        dto.status = booking.status.getValue();
        dto.bookingTime = booking.bookingTime;
        dto.seatClass = booking.seatClass.getValue();
        dto.pricePaid = booking.pricePaid;

        if (booking.addons != null) {
            try {

                dto.addons = objectMapper.readValue(booking.addons, JsonNode.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return dto;
    }
}

// Made with Bob
