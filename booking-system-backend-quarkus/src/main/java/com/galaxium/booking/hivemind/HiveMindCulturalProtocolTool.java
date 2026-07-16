package com.galaxium.booking.hivemind;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class HiveMindCulturalProtocolTool {

    @Inject
    HiveMindDirectory hiveMindDirectory;

    @Inject
    Logger logger;

    @Tool("Finds the cultural protocol for a planet")
    public String culturalProtocolByPlanet(String planet) {
        logger.infof("Finding Cultural Protocol for %s", planet);
        return hiveMindDirectory.culturalProtocol(planet);
    }

}
