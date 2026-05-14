package com.galaxium.booking.resource;

import com.galaxium.booking.client.HoldServiceClient;
import com.galaxium.booking.client.dto.HoldDto;
import com.galaxium.booking.client.dto.QuoteDto;
import com.galaxium.booking.dto.AddOnDto;
import com.galaxium.booking.dto.BookingDto;
import com.galaxium.booking.dto.Result;
import com.galaxium.booking.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Proxy resource for Java Hold Service integration.
 * Provides endpoints for quotes, holds, and internal booking creation.
 */
@Path("/")
public class HoldServiceProxyResource {

    @Inject
    @RestClient
    HoldServiceClient holdServiceClient;

    @Inject
    BookingService bookingService;

    // ==================== INTERNAL ENDPOINT ====================

    /**
     * Internal endpoint for Java hold service to create bookings.
     * Called by the Java service when confirming a hold.
     */
    @POST
    @Path("/internal/bookings/from-hold")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookingFromHold(Map<String, Object> holdData) {
        // Extract fields with fallback for different naming conventions
        Long userId = getLong(holdData, "travelerId", "user_id");
        String name = getString(holdData, "travelerName", "traveler_name");
        Long flightId = getLong(holdData, "flightId", "flight_id");
        String seatClass = getString(holdData, "seatClass", "seat_class");
        
        if (seatClass == null) {
            seatClass = "economy";
        }

        List<Map<String,?>> addOnsRaw = (List<Map<String,?>>) holdData.get("addons");

        if (addOnsRaw == null) {
            addOnsRaw = new ArrayList<>();
        }

        List<AddOnDto> addons = addOnsRaw
            .stream()
            .map(AddOnDto::from)
            .toList();

        Result<BookingDto> result = bookingService.bookFlight(
            userId,
            name,
            flightId,
            seatClass,
            addons
        );

        return switch (result) {
            case Result.Success<BookingDto> success -> Response.ok(success.value()).build();
            case Result.Failure<BookingDto> failure -> 
                Response.status(Response.Status.BAD_REQUEST).entity(failure.error()).build();
        };
    }

    // ==================== QUOTE PROXY ENDPOINTS ====================

    @Inject
    Logger logger;

    /**
     * Proxy endpoint to create a quote in the Java hold service.
     */
    @POST
    @Path("/quotes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createQuote(Map<String, Object> quoteData) {

        try {
            String quote = holdServiceClient.createQuote(quoteData);
            return Response.ok(quote).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to create quote: " + e.getMessage())).build();
        }
    }

    /**
     * Proxy endpoint to get a quote from the Java hold service.
     */
    @GET
    @Path("/quotes/{quoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuote(@PathParam("quoteId") String quoteId) {

        try {
            String quote = holdServiceClient.getQuote(quoteId);
            return Response.ok(quote).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to get quote: " + e.getMessage())).build();
        }
    }

    // ==================== HOLD PROXY ENDPOINTS ====================

    /**
     * Proxy endpoint to create a hold from a quote in the Java hold service.
     */
    @POST
    @Path("/quotes/{quoteId}/holds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHold(@PathParam("quoteId") String quoteId) {
        try {
            String hold = holdServiceClient.createHold(quoteId);
            return Response.ok(hold).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to create hold: " + e.getMessage())).build();
        }
    }

    /**
     * Proxy endpoint to get a hold from the Java hold service.
     */
    @GET
    @Path("/holds/{holdId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHold(@PathParam("holdId") String holdId) {
        try {
            String hold = holdServiceClient.getHold(holdId);
            return Response.ok(hold).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to get hold: " + e.getMessage())).build();
        }
    }

    /**
     * Proxy endpoint to confirm a hold in the Java hold service.
     */
    @POST
    @Path("/holds/{holdId}/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmHold(
            @PathParam("holdId") String holdId,
            Map<String, Object> requestBody) {
        try {
            if (requestBody == null) {
                requestBody = Map.of();
            }
            String result = holdServiceClient.confirmHold(holdId, requestBody);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to confirm hold: " + e.getMessage())).build();
        }
    }

    /**
     * Proxy endpoint to release a hold in the Java hold service.
     */
    @POST
    @Path("/holds/{holdId}/release")
    @Produces(MediaType.APPLICATION_JSON)
    public Response releaseHold(@PathParam("holdId") String holdId) {
        try {
            String hold = holdServiceClient.releaseHold(holdId);
            return Response.ok(hold).build();
        } catch (Exception e) {
            return Response.ok(Map.of("error", "Failed to release hold: " + e.getMessage())).build();
        }
    }

    // ==================== HELPER METHODS ====================

    private Long getLong(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        // Continue to next key
                    }
                }
            }
        }
        return null;
    }

    private String getString(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}

// Made with Bob
