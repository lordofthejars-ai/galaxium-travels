package com.galaxium.booking.service;

import com.galaxium.booking.dto.BookingDto;
import com.galaxium.booking.dto.FlightDto;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * LangChain4j tool that allows the AI assistant to search for bookings by user ID.
 * This tool is automatically discovered and made available to the AI service.
 */
@ApplicationScoped
public class BookingSearchTool {

    @Inject
    BookingService bookingService;

    @Inject
    Logger logger;

    @Tool("Search booking information by booking id")
    public BookingDto findBookingById(Long bookingId) {
        logger.infof("Tool invoked for searching booking id " + bookingId);
        return bookingService.findBookingById(bookingId);
    }

    /**
     * Find all bookings for a specific user.
     * 
     * @param userId The ID of the user whose bookings to retrieve
     * @return A list of bookings for the specified user, or an empty list if none found
     */
    @Tool("Search for all bookings belonging to a specific user by their user ID")
    public List<BookingDto> findBookingsByUserId(Long userId) {
        logger.infof("Tool invoked for searching bookings for the user " + userId);
        return bookingService.getBookings(userId);
    }

}

// Made with Bob
