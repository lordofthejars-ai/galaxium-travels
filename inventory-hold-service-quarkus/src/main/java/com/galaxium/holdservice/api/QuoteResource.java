package com.galaxium.holdservice.api;

import com.galaxium.holdservice.api.dto.CreateQuoteRequest;
import com.galaxium.holdservice.domain.Quote;
import com.galaxium.holdservice.service.QuoteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/quotes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuoteResource {

    @Inject
    Logger log;

    @Inject
    QuoteService quoteService;

    @POST
    public Response createQuote(@Valid CreateQuoteRequest request) {
        log.infof("POST /api/v1/quotes - Creating quote for flight %d", request.flightId);
        Quote quote = quoteService.createQuote(request);
        return Response.status(Response.Status.CREATED).entity(quote).build();
    }

    @GET
    @Path("/{quoteId}")
    public Response getQuote(@PathParam("quoteId") String quoteId) {
        log.infof("GET /api/v1/quotes/%s - Retrieving quote", quoteId);
        return quoteService.getQuote(quoteId)
                .map(quote -> Response.ok(quote).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}

// Made with Bob
