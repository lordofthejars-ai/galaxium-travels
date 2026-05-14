package com.galaxium.booking.resource;

import com.galaxium.booking.dto.AddOnDto;
import com.galaxium.booking.service.AddonsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * REST resource for add-ons catalog.
 */
@Path("/addons")
public class AddonsResource {

    @Inject
    AddonsService addonsService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AddOnDto> getAddonsCatalog() {
        return addonsService.getAddonsCatalog();
    }
}

// Made with Bob
