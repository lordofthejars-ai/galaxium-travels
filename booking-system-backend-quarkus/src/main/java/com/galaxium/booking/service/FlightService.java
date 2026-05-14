package com.galaxium.booking.service;

import com.galaxium.booking.dto.FlightDto;
import com.galaxium.booking.entity.Flight;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Flight service for listing available flights.
 * Matches Python backend's flight.py service layer.
 * 
 * Note: Advanced filtering (by origin, destination, date, price, etc.) 
 * will be implemented in the REST layer or as additional methods.
 */
@ApplicationScoped
public class FlightService {

    /**
     * List all available flights with computed prices for all seat classes.
     * 
     * @return List of FlightDto with economy, business, and galaxium prices
     */
    public List<FlightDto> listFlights() {
        return Flight.<Flight>listAll()
            .stream()
            .map(FlightDto::from)
            .collect(Collectors.toList());
    }
}

// Made with Bob
