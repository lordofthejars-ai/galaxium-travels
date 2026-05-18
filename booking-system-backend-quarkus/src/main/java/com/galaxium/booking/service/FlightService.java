package com.galaxium.booking.service;

import com.galaxium.booking.dto.FlightDto;
import com.galaxium.booking.entity.Flight;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Inject
    Logger logger;

    /**
     * List all available flights with computed prices for all seat classes.
     * Supports dynamic filtering by origin, destination, departure_time, max_price, and seat_class.
     *
     * @param queryParameters Query parameters for filtering flights
     * @return List of FlightDto with economy, business, and galaxium prices
     */
    public List<FlightDto> listFlights(MultivaluedMap<String, String> queryParameters) {
        // If no filters provided, return all flights
        if (queryParameters == null || queryParameters.isEmpty()) {
            return Flight.<Flight>listAll()
                .stream()
                .map(FlightDto::from)
                .collect(Collectors.toList());
        }

        // Build dynamic query with filters
        StringBuilder queryBuilder = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        
        // Filter by origin (case-insensitive partial match)
        if (queryParameters.containsKey("origin")) {
            String origin = queryParameters.getFirst("origin");
            if (origin != null && !origin.isBlank()) {
                queryBuilder.append("LOWER(origin) LIKE LOWER(:origin)");
                params.put("origin", "%" + origin + "%");
            }
        }
        
        // Filter by destination (case-insensitive partial match)
        if (queryParameters.containsKey("destination")) {
            String destination = queryParameters.getFirst("destination");
            if (destination != null && !destination.isBlank()) {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("LOWER(destination) LIKE LOWER(:destination)");
                params.put("destination", "%" + destination + "%");
            }
        }
        
        // Filter by departure time (exact or prefix match for date filtering)
        if (queryParameters.containsKey("departure_time")) {
            String departureTime = queryParameters.getFirst("departure_time");
            if (departureTime != null && !departureTime.isBlank()) {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("departureTime LIKE :departureTime");
                params.put("departureTime", departureTime + "%");
            }
        }
        
        // Filter by max price (base price)
        if (queryParameters.containsKey("max_price")) {
            String maxPriceStr = queryParameters.getFirst("max_price");
            if (maxPriceStr != null && !maxPriceStr.isBlank()) {
                try {
                    Integer maxPrice = Integer.parseInt(maxPriceStr);
                    if (queryBuilder.length() > 0) {
                        queryBuilder.append(" AND ");
                    }
                    queryBuilder.append("basePrice <= :maxPrice");
                    params.put("maxPrice", maxPrice);
                } catch (NumberFormatException e) {
                    logger.warnf("Invalid max_price parameter: %s", maxPriceStr);
                }
            }
        }
        
        // Filter by seat class availability
        if (queryParameters.containsKey("seat_class")) {
            String seatClassStr = queryParameters.getFirst("seat_class");
            if (seatClassStr != null && !seatClassStr.isBlank()) {
                if (queryBuilder.length() > 0) {
                    queryBuilder.append(" AND ");
                }
                // Check which seat class has availability
                switch (seatClassStr.toUpperCase()) {
                    case "ECONOMY":
                        queryBuilder.append("economySeatsAvailable > 0");
                        break;
                    case "BUSINESS":
                        queryBuilder.append("businessSeatsAvailable > 0");
                        break;
                    case "GALAXIUM":
                        queryBuilder.append("galaxiumSeatsAvailable > 0");
                        break;
                    default:
                        logger.warnf("Invalid seat_class parameter: %s", seatClassStr);
                }
            }
        }
        
        // Execute query with filters or return all if no valid filters
        List<Flight> flights;
        if (queryBuilder.length() > 0) {
            logger.debugf("Executing flight query: %s with params: %s", queryBuilder.toString(), params);
            flights = Flight.find(queryBuilder.toString(), params).list();
        } else {
            flights = Flight.listAll();
        }
        
        return flights.stream()
            .map(FlightDto::from)
            .collect(Collectors.toList());
    }
}

// Made with Bob
