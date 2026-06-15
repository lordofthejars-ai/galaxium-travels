package com.galaxium.booking.service;

import com.galaxium.booking.dto.FlightDto;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FlightSearchTool {

    @Inject
    FlightService flightService;

    @Inject
    Logger logger;

    @Tool("Search for Flight information for given flight ID")
    public FlightDto searchFlightById(Long flightId) {
        logger.infof("Tool invoked for searching flight with id %s", flightId);
        return flightService.findFlightById(flightId);
    }

}
