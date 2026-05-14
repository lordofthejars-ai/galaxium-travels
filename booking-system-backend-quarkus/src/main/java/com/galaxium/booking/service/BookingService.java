package com.galaxium.booking.service;

import com.galaxium.booking.dto.AddOnDto;
import com.galaxium.booking.dto.BookingDto;
import com.galaxium.booking.dto.ErrorResponse;
import com.galaxium.booking.dto.Result;
import com.galaxium.booking.entity.Booking;
import com.galaxium.booking.entity.BookingStatus;
import com.galaxium.booking.entity.Flight;
import com.galaxium.booking.entity.SeatClass;
import com.galaxium.booking.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Booking service for flight reservations and cancellations.
 * Matches Python backend's booking.py service layer.
 */
@ApplicationScoped
public class BookingService {

    @Inject
    AddonsService addonsService;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Book a seat on a specific flight for a user in the specified seat class.
     * 
     * @param userId User ID
     * @param name User's name (must match the user ID)
     * @param flightId Flight ID
     * @param seatClass Seat class (economy, business, or galaxium)
     * @param addons Optional list of selected add-ons
     * @return Result containing BookingDto or ErrorResponse
     */
    @Transactional
    public Result<BookingDto> bookFlight(
            Long userId,
            String name,
            Long flightId,
            String seatClass,
            List<AddOnDto> addons) {

        // Parse seat class
        SeatClass seatClassEnum;
        try {
            seatClassEnum = SeatClass.fromString(seatClass);
        } catch (IllegalArgumentException e) {
            return Result.failure(
                "Invalid seat class",
                ErrorResponse.INVALID_SEAT_CLASS,
                String.format("Seat class '%s' is not valid. Valid options are: economy, business, galaxium.", seatClass)
            );
        }

        // Check flight exists
        Flight flight = Flight.findById(flightId);
        if (flight == null) {
            return Result.failure(
                "Flight not found",
                ErrorResponse.FLIGHT_NOT_FOUND,
                String.format("The specified flight_id %d does not exist in our system. Please check the flight_id or use list_flights to see available flights.", flightId)
            );
        }

        // Check seats available for the specific class
        int seatsAvailable = flight.getSeatsAvailable(seatClassEnum);
        if (seatsAvailable < 1) {
            return Result.failure(
                String.format("No %s seats available", seatClass),
                ErrorResponse.NO_SEATS_AVAILABLE,
                String.format("The flight has no available seats in %s class. Please try a different class or check other flights.", seatClass)
            );
        }

        // Check user exists and name matches
        User user = User.findById(userId);
        if (user == null) {
            return Result.failure(
                "User not found",
                ErrorResponse.USER_NOT_FOUND,
                String.format("User with ID %d is not registered in our system. The user might need to register first, or you may need to check if the user_id is correct.", userId)
            );
        }

        if (!user.name.equals(name)) {
            return Result.failure(
                "Name mismatch",
                ErrorResponse.NAME_MISMATCH,
                String.format("User ID %d exists but the name '%s' does not match the registered name '%s'. Please verify the user's name or use the correct name for this user ID.", userId, name, user.name)
            );
        }

        // Calculate base price for seat class
        int basePrice = flight.calculatePrice(seatClassEnum);

        // Validate and calculate add-ons total
        int addonsTotal = 0;
        List<AddOnDto> validatedAddons = new ArrayList<>();

        if (addons != null && !addons.isEmpty()) {
            Map<String, AddOnDto> catalogMap = addonsService.getAddonsCatalog()
                .stream()
                .collect(Collectors.toMap(a -> a.id, a -> a));

            for (AddOnDto addon : addons) {
                // Skip unselected add-ons
                if (addon.selected == null || !addon.selected) {
                    continue;
                }

                // Validate add-on exists in catalog
                AddOnDto catalogItem = catalogMap.get(addon.id);
                if (catalogItem == null) {
                    return Result.failure(
                        "Invalid add-on",
                        ErrorResponse.INVALID_ADDON,
                        String.format("Add-on '%s' not found in catalog.", addon.id)
                    );
                }

                // Validate price hasn't been tampered with
                if (!addon.price.equals(catalogItem.price)) {
                    return Result.failure(
                        "Price tampering detected",
                        ErrorResponse.PRICE_TAMPERING,
                        String.format("Add-on price mismatch for '%s'.", addon.id)
                    );
                }

                addonsTotal += catalogItem.price;
                validatedAddons.add(catalogItem);
            }
        }

        int pricePaid = basePrice + addonsTotal;

        // Decrement seat counter
        if (!flight.decrementSeats(seatClassEnum)) {
            return Result.failure(
                String.format("No %s seats available", seatClass),
                ErrorResponse.NO_SEATS_AVAILABLE,
                "Failed to reserve seat. Please try again."
            );
        }

        // Serialize add-ons to JSON
        String addonsJson = null;
        if (!validatedAddons.isEmpty()) {
            try {
                addonsJson = objectMapper.writeValueAsString(validatedAddons);
            } catch (JsonProcessingException e) {
                // Log error but continue - add-ons are optional
                addonsJson = "[]";
            }
        }

        // Create booking
        Booking booking = new Booking();
        booking.userId = userId;
        booking.flightId = flightId;
        booking.status = BookingStatus.BOOKED;
        booking.bookingTime = Instant.now().toString();
        booking.seatClass = seatClassEnum;
        booking.pricePaid = pricePaid;
        booking.addons = addonsJson;
        booking.persist();

        return Result.success(BookingDto.from(booking));
    }

    /**
     * Cancel an existing booking by its booking_id and restore seat to correct class.
     * 
     * @param bookingId Booking ID to cancel
     * @return Result containing BookingDto or ErrorResponse
     */
    @Transactional
    public Result<BookingDto> cancelBooking(Long bookingId) {
        Booking booking = Booking.findById(bookingId);
        if (booking == null) {
            return Result.failure(
                "Booking not found",
                ErrorResponse.BOOKING_NOT_FOUND,
                String.format("Booking with ID %d not found. The booking may have been deleted or the booking_id may be incorrect. Please verify the booking_id or check if the booking exists.", bookingId)
            );
        }

        if (booking.status == BookingStatus.CANCELLED) {
            return Result.failure(
                "Booking already cancelled",
                ErrorResponse.ALREADY_CANCELLED,
                String.format("Booking %d is already cancelled and cannot be cancelled again. The booking status is currently '%s'. If you need to make changes, please contact support.", bookingId, booking.status.getValue())
            );
        }

        // Restore seat to the correct class
        Flight flight = Flight.findById(booking.flightId);
        if (flight != null) {
            flight.incrementSeats(booking.seatClass);
        }

        // Update booking status
        booking.status = BookingStatus.CANCELLED;
        booking.persist();

        return Result.success(BookingDto.from(booking));
    }

    /**
     * Retrieve all bookings for a specific user.
     * 
     * @param userId User ID
     * @return List of BookingDto
     */
    public List<BookingDto> getBookings(Long userId) {
        return Booking.findByUserId(userId)
            .stream()
            .map(BookingDto::from)
            .collect(Collectors.toList());
    }
}

// Made with Bob
