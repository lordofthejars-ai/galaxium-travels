package com.galaxium.booking.resource;

import com.galaxium.booking.dto.FlightDto;
import com.galaxium.booking.service.FlightService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

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
    public List<FlightDto> listFlights(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();

        return flightService.listFlights(queryParameters);
    }
}

// Made with Bob
