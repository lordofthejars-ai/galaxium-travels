package com.galaxium.holdservice.client;

import com.galaxium.holdservice.client.dto.BookingResponse;
import com.galaxium.holdservice.client.dto.FlightResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;

@Path("/")
@RegisterRestClient(configKey = "booking-system-backend")
public interface BookingSystemBackend {

    @POST
    @Path("/internal/bookings/from-hold")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    BookingResponse createBookingFromHold(Map<String, Object> holdData);

    @GET
    @Path("/flights")
    @Produces(MediaType.APPLICATION_JSON)
    List<FlightResponse> getFlights();
}

// Made with Bob
