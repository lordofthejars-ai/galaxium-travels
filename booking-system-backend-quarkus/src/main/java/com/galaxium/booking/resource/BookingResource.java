package com.galaxium.booking.resource;

import com.galaxium.booking.dto.BookingDto;
import com.galaxium.booking.dto.BookingRequest;
import com.galaxium.booking.dto.ErrorResponse;
import com.galaxium.booking.dto.Result;
import com.galaxium.booking.service.BookingService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST resource for booking operations.
 */
@Path("/")
public class BookingResource {

    @Inject
    BookingService bookingService;

    @POST
    @Path("/book")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookFlight(@Valid BookingRequest request) {
        Result<BookingDto> result = bookingService.bookFlight(
            request.userId(),
            request.name(),
            request.flightId(),
            request.seatClass().getValue(),
            request.addons()
        );
        
        return switch (result) {
            case Result.Success<BookingDto> success -> Response.ok(success.value()).build();
            case Result.Failure<BookingDto> failure -> toErrorResponse(failure.error());
        };
    }

    @GET
    @Path("/bookings/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookingDto> getBookings(@PathParam("userId") Long userId) {
        List<BookingDto> bookings = bookingService.getBookings(userId);
        bookings.forEach(System.out::println);
        return bookings;
    }

    @POST
    @Path("/cancel/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("bookingId") Long bookingId) {
        Result<BookingDto> result = bookingService.cancelBooking(bookingId);
        
        return switch (result) {
            case Result.Success<BookingDto> success -> Response.ok(success.value()).build();
            case Result.Failure<BookingDto> failure -> toErrorResponse(failure.error());
        };
    }

    private Response toErrorResponse(ErrorResponse error) {
        Response.Status status = switch (error.errorCode) {
            case "FLIGHT_NOT_FOUND", "BOOKING_NOT_FOUND", "USER_NOT_FOUND" -> Response.Status.NOT_FOUND;
            case "NO_SEATS_AVAILABLE" -> Response.Status.CONFLICT;
            case "ALREADY_CANCELLED" -> Response.Status.BAD_REQUEST;
            case "INVALID_SEAT_CLASS" -> Response.Status.BAD_REQUEST;
            case "NAME_MISMATCH" -> Response.Status.FORBIDDEN;
            case "ADDON_PRICE_MISMATCH" -> Response.Status.BAD_REQUEST;
            default -> Response.Status.BAD_REQUEST;
        };
        return Response.status(status).entity(error).build();
    }
}

// Made with Bob
