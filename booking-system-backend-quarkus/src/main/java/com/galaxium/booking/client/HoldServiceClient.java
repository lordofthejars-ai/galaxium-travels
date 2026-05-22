package com.galaxium.booking.client;

import com.galaxium.booking.client.dto.HoldDto;
import com.galaxium.booking.client.dto.QuoteDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

/**
 * REST Client for the Java Inventory Hold Service.
 * Provides type-safe access to quote and hold operations.
 */
@Path("/api/v1")
@RegisterRestClient(configKey = "hold-service")
public interface HoldServiceClient {

    /**
     * Create a new quote for a flight reservation.
     */
    @POST
    @Path("/quotes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String createQuote(Map<String, Object> quoteData);

    /**
     * Get an existing quote by ID.
     */
    @GET
    @Path("/quotes/{quoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    String getQuote(@PathParam("quoteId") String quoteId);

    /**
     * Create a hold from an existing quote.
     */
    @POST
    @Path("/holds/from-quote/{quoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    String createHold(@PathParam("quoteId") String quoteId);

    /**
     * Get an existing hold by ID.
     */
    @GET
    @Path("/holds/{holdId}")
    @Produces(MediaType.APPLICATION_JSON)
    String getHold(@PathParam("holdId") String holdId);

    /**
     * Confirm a hold and convert it to a booking.
     */
    @POST
    @Path("/holds/{holdId}/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String confirmHold(@PathParam("holdId") String holdId, Map<String, Object> requestBody);

    /**
     * Release a hold without confirming.
     */
    @POST
    @Path("/holds/{holdId}/release")
    @Produces(MediaType.APPLICATION_JSON)
    String releaseHold(@PathParam("holdId") String holdId);
}

// Made with Bob
