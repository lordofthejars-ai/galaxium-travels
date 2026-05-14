package com.galaxium.booking.resource;

import com.galaxium.booking.dto.FlightDto;
import com.galaxium.booking.service.FlightService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * REST resource for flight operations.
 */
@Path("/flights")
public class FlightResource {

    @Inject
    FlightService flightService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FlightDto> listFlights() {
        return flightService.listFlights();
    }
}

// Made with Bob
