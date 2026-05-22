package com.galaxium.holdservice.api;

import com.galaxium.holdservice.domain.Hold;
import com.galaxium.holdservice.service.HoldService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Map;

@Path("/api/v1/holds")
public class HoldResource {

    @Inject
    Logger log;

    @Inject
    HoldService holdService;

    @POST
    @Path("/from-quote/{quoteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHold(@PathParam("quoteId") String quoteId) {
        log.infof("POST /api/v1/holds/from-quote/%s - Creating hold", quoteId);
        try {
            Hold hold = holdService.createHold(quoteId);
            return Response.status(Response.Status.CREATED).entity(hold).build();
        } catch (IllegalArgumentException e) {
            log.errorf(e, "Invalid quote ID: %s", quoteId);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.errorf("Cannot create hold: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/{holdId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHold(@PathParam("holdId") String holdId) {
        log.infof("GET /api/v1/holds/%s - Retrieving hold", holdId);
        return holdService.getHold(holdId)
                .map(hold -> Response.ok(hold).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Path("/{holdId}/confirm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmHold(
            @PathParam("holdId") String holdId,
            Map<String, Object> requestBody
    ) {
        log.infof("POST /api/v1/holds/%s/confirm - Confirming hold", holdId);
        try {
            List<Map<String, Object>> addons = null;
            if (requestBody != null && requestBody.containsKey("addons")) {
                Object rawAddons = requestBody.get("addons");
                
                // Validate that addons is a list
                if (!(rawAddons instanceof List<?>)) {
                    log.errorf("Invalid addons type: expected List but got %s",
                            rawAddons != null ? rawAddons.getClass().getSimpleName() : "null");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(Map.of("error", "Invalid request: 'addons' must be a list"))
                            .build();
                }
                
                List<?> addonList = (List<?>) rawAddons;
                
                // Validate that all elements are maps
                for (int i = 0; i < addonList.size(); i++) {
                    Object item = addonList.get(i);
                    if (!(item instanceof Map)) {
                        log.errorf("Invalid addon element at index %d: expected Map but got %s",
                                i, item != null ? item.getClass().getSimpleName() : "null");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(Map.of("error", "Invalid request: all elements in 'addons' must be objects"))
                                .build();
                    }
                }
                
                addons = addonList.stream()
                        .map(addon -> (Map<String, Object>) addon)
                        .toList();
            }

            Hold hold = holdService.confirmHold(holdId, addons);
            return Response.ok(hold).build();
        } catch (IllegalArgumentException e) {
            log.errorf(e, "Hold not found: %s", holdId);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.errorf("Cannot confirm hold: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{holdId}/release")
    @Produces(MediaType.APPLICATION_JSON)
    public Response releaseHold(@PathParam("holdId") String holdId) {
        log.infof("POST /api/v1/holds/%s/release - Releasing hold", holdId);
        try {
            Hold hold = holdService.releaseHold(holdId);
            return Response.ok(hold).build();
        } catch (IllegalArgumentException e) {
            log.errorf(e, "Hold not found: %s", holdId);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.errorf("Cannot release hold: %s", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}

// Made with Bob
