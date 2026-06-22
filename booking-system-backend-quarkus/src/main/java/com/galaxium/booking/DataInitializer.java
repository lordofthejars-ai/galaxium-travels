package com.galaxium.booking;

import com.galaxium.booking.entity.Booking;
import com.galaxium.booking.entity.BookingStatus;
import com.galaxium.booking.entity.Flight;
import com.galaxium.booking.entity.SeatClass;
import com.galaxium.booking.entity.User;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Data initializer that seeds the database with demo data on application startup.
 * Replaces the import.sql file with programmatic data insertion using Panache entities.
 * 
 * This approach provides better control over entity relationships and allows for
 * proper handling of ManyToOne associations.
 */
@ApplicationScoped
public class DataInitializer {

    @Inject
    Logger logger;

    @Startup
    @Transactional
    public void init() {
        // Check if data already exists to avoid duplicate inserts
        if (User.count() > 0) {
            logger.warn("Database already contains data, skipping initialization");
            return;
        }

        logger.info("Starting database initialization with demo data...");

        // Step 1: Create and persist users
        List<User> users = createUsers();
        logger.info("Created " + users.size() + " users");

        // Step 2: Create and persist flights
        List<Flight> flights = createFlights();
        logger.info("Created " + flights.size() + " flights");

        // Step 3: Create and persist bookings (with proper relationships)
        List<Booking> bookings = createBookings(users, flights);
        logger.info("Created " + bookings.size() + " bookings");

        // Step 4: Update flight seat availability based on bookings
        updateFlightSeats(flights, bookings);
        logger.info("Updated flight seat availability");

        logger.info("Database initialization complete!");
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        
        String[][] userData = {
            {"Alice", "alice@example.com"},
            {"Bob", "bob@example.com"},
            {"Charlie", "charlie@example.com"},
            {"Diana", "diana@example.com"},
            {"Eve", "eve@example.com"},
            {"Frank", "frank@example.com"},
            {"Grace", "grace@example.com"},
            {"Heidi", "heidi@example.com"},
            {"Ivan", "ivan@example.com"},
            {"Judy", "judy@example.com"}
        };

        for (String[] data : userData) {
            User user = new User();
            user.name = data[0];
            user.email = data[1];
            user.persist();
            users.add(user);
        }

        return users;
    }

    private List<Flight> createFlights() {
        List<Flight> flights = new ArrayList<>();

        Object[][] flightData = {
            {"Earth", "Mars", "2026-06-01 10:00:00", "2026-06-01 18:00:00", 500},
            {"Mars", "Earth", "2026-06-02 09:00:00", "2026-06-02 17:00:00", 500},
            {"Earth", "Moon", "2026-06-03 08:00:00", "2026-06-03 10:00:00", 200},
            {"Moon", "Earth", "2026-06-04 14:00:00", "2026-06-04 16:00:00", 200},
            {"Earth", "Venus", "2026-06-05 11:00:00", "2026-06-05 19:00:00", 600},
            {"Mars", "Jupiter", "2026-06-06 07:00:00", "2026-06-07 15:00:00", 1200},
            {"Jupiter", "Europa", "2026-06-08 10:00:00", "2026-06-08 12:00:00", 300},
            {"Europa", "Jupiter", "2026-06-09 13:00:00", "2026-06-09 15:00:00", 300},
            {"Earth", "Pluto", "2026-06-10 06:00:00", "2026-06-12 18:00:00", 2000},
            {"Pluto", "Earth", "2026-06-13 08:00:00", "2026-06-15 20:00:00", 2000}
        };

        for (Object[] data : flightData) {
            Flight flight = new Flight();
            flight.origin = (String) data[0];
            flight.destination = (String) data[1];
            flight.departureTime = (String) data[2];
            flight.arrivalTime = (String) data[3];
            flight.basePrice = (Integer) data[4];
            // Initial seat distribution: 60% Economy, 30% Business, 10% Galaxium
            flight.economySeatsAvailable = 60;
            flight.businessSeatsAvailable = 30;
            flight.galaxiumSeatsAvailable = 10;
            flight.persist();
            flights.add(flight);
        }

        return flights;
    }

    private List<Booking> createBookings(List<User> users, List<Flight> flights) {
        List<Booking> bookings = new ArrayList<>();

        // Economy bookings
        bookings.add(createBooking(users.get(0), flights.get(0), BookingStatus.BOOKED, 
            "2026-05-01 10:00:00", SeatClass.ECONOMY, 500));
        bookings.add(createBooking(users.get(1), flights.get(1), BookingStatus.BOOKED, 
            "2026-05-01 11:00:00", SeatClass.ECONOMY, 500));
        bookings.add(createBooking(users.get(2), flights.get(2), BookingStatus.BOOKED, 
            "2026-05-01 12:00:00", SeatClass.ECONOMY, 200));
        bookings.add(createBooking(users.get(3), flights.get(3), BookingStatus.BOOKED, 
            "2026-05-01 13:00:00", SeatClass.ECONOMY, 200));
        bookings.add(createBooking(users.get(4), flights.get(4), BookingStatus.BOOKED, 
            "2026-05-01 14:00:00", SeatClass.ECONOMY, 600));
        bookings.add(createBooking(users.get(5), flights.get(5), BookingStatus.BOOKED, 
            "2026-05-01 15:00:00", SeatClass.ECONOMY, 1200));
        bookings.add(createBooking(users.get(6), flights.get(6), BookingStatus.BOOKED, 
            "2026-05-01 16:00:00", SeatClass.ECONOMY, 300));
        bookings.add(createBooking(users.get(7), flights.get(7), BookingStatus.BOOKED, 
            "2026-05-01 17:00:00", SeatClass.ECONOMY, 300));

        // Business bookings
        bookings.add(createBooking(users.get(8), flights.get(8), BookingStatus.BOOKED, 
            "2026-05-02 10:00:00", SeatClass.BUSINESS, 5000));
        bookings.add(createBooking(users.get(9), flights.get(9), BookingStatus.BOOKED, 
            "2026-05-02 11:00:00", SeatClass.BUSINESS, 5000));
        bookings.add(createBooking(users.get(0), flights.get(0), BookingStatus.BOOKED, 
            "2026-05-02 12:00:00", SeatClass.BUSINESS, 1250));
        bookings.add(createBooking(users.get(1), flights.get(1), BookingStatus.BOOKED, 
            "2026-05-02 13:00:00", SeatClass.BUSINESS, 1250));
        bookings.add(createBooking(users.get(2), flights.get(2), BookingStatus.BOOKED, 
            "2026-05-02 14:00:00", SeatClass.BUSINESS, 500));
        bookings.add(createBooking(users.get(3), flights.get(3), BookingStatus.BOOKED, 
            "2026-05-02 15:00:00", SeatClass.BUSINESS, 500));

        // Galaxium bookings
        bookings.add(createBooking(users.get(4), flights.get(4), BookingStatus.BOOKED, 
            "2026-05-03 10:00:00", SeatClass.GALAXIUM, 3000));
        bookings.add(createBooking(users.get(5), flights.get(5), BookingStatus.BOOKED, 
            "2026-05-03 11:00:00", SeatClass.GALAXIUM, 6000));
        bookings.add(createBooking(users.get(6), flights.get(6), BookingStatus.BOOKED, 
            "2026-05-03 12:00:00", SeatClass.GALAXIUM, 1500));
        bookings.add(createBooking(users.get(7), flights.get(7), BookingStatus.BOOKED, 
            "2026-05-03 13:00:00", SeatClass.GALAXIUM, 1500));

        // Cancelled bookings
        bookings.add(createBooking(users.get(8), flights.get(8), BookingStatus.CANCELLED, 
            "2026-05-04 10:00:00", SeatClass.ECONOMY, 2000));
        bookings.add(createBooking(users.get(9), flights.get(9), BookingStatus.CANCELLED, 
            "2026-05-04 11:00:00", SeatClass.ECONOMY, 2000));

        return bookings;
    }

    private Booking createBooking(User user, Flight flight, BookingStatus status, 
                                   String bookingTime, SeatClass seatClass, Integer pricePaid) {
        Booking booking = new Booking();
        booking.user = user;
        booking.flight = flight;
        booking.status = status;
        booking.bookingTime = bookingTime;
        booking.seatClass = seatClass;
        booking.pricePaid = pricePaid;
        booking.addons = null;
        booking.persist();
        return booking;
    }

    private void updateFlightSeats(List<Flight> flights, List<Booking> bookings) {
        // Count bookings per flight and seat class (excluding cancelled bookings)
        for (Booking booking : bookings) {
            if (booking.status != BookingStatus.CANCELLED) {
                Flight flight = booking.flight;
                switch (booking.seatClass) {
                    case ECONOMY -> flight.economySeatsAvailable--;
                    case BUSINESS -> flight.businessSeatsAvailable--;
                    case GALAXIUM -> flight.galaxiumSeatsAvailable--;
                }
            }
        }

        // Persist the updated seat counts
        for (Flight flight : flights) {
            flight.persist();
        }
    }
}

// Made with Bob
