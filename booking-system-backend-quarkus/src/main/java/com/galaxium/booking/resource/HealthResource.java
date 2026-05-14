package com.galaxium.booking.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

/**
 * Health check endpoint.
 */
@Path("/")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> healthCheck() {
        return Map.of("status", "OK");
    }
}

// Made with Bob
