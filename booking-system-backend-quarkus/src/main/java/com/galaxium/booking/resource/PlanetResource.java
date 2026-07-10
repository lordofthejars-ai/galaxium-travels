package com.galaxium.booking.resource;

import com.galaxium.booking.ai.PlanetInformation;
import com.galaxium.booking.ai.PlanetInformationService;
import com.galaxium.booking.ai.SchemaDefinitions;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

@Path("/planet")
public class PlanetResource {

    @Inject
    PlanetInformationService planetInformationService;

    @Inject
    Logger logger;

    @Path("/info/{planet}")
    @GET
    public PlanetInformation info(@RestPath String planet) {

        logger.infof("Getting information for planet %s", planet);

        return planetInformationService.findInformationAbout(planet,
                SchemaDefinitions.schemaValidation());
    }

}
