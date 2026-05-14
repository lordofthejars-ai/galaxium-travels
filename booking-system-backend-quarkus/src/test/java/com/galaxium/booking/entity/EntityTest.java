package com.galaxium.booking.entity;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for database entities using Panache Active Record pattern.
 * Tests verify CRUD operations, custom finders, and business logic.
 */
@QuarkusTest
class EntityTest {

    @Test
    @TestTransaction
    void testUserPersistAndFind() {
        // Create and persist a new user
        User user = new User();
        user.name = "Test User";
        user.email = "TEST@EXAMPLE.COM"; // Should be lowercased
        user.persist();

        assertNotNull(user.id);
        
        // Verify email was lowercased
        assertEquals("test@example.com", user.email);
        
        // Find by email (case-insensitive)
        User found = User.findByEmail("TEST@example.com");
        assertNotNull(found);
        assertEquals("Test User", found.name);
        assertEquals("test@example.com", found.email);
    }

    @Test
    @TestTransaction
    void testUserFindByNameAndEmail() {
        User user = new User();
        user.name = "Alice";
        user.email = "alice@test.com";
        user.persist();

        // Find by name and email
        User found = User.findByNameAndEmail("Alice", "ALICE@TEST.COM");
        assertNotNull(found);
        assertEquals("Alice", found.name);
        
        // Name is case-sensitive
        User notFound = User.findByNameAndEmail("alice", "alice@test.com");
        assertNull(notFound);
    }

    @Test
    @TestTransaction
    void testUserEmailExists() {
        User user = new User();
        user.name = "Bob";
        user.email = "bob@test.com";
        user.persist();

        assertTrue(User.emailExists("bob@test.com"));
        assertTrue(User.emailExists("BOB@TEST.COM")); // Case-insensitive
        assertFalse(User.emailExists("nonexistent@test.com"));
    }

    @Test
    @TestTransaction
    void testFlightPersistAndFind() {
        Flight flight = new Flight();
        flight.origin = "Earth";
        flight.destination = "Mars";
        flight.departureTime = "2026-06-01 10:00";
        flight.arrivalTime = "2026-06-01 18:00";
        flight.basePrice = 500;
        flight.economySeatsAvailable = 60;
        flight.businessSeatsAvailable = 30;
        flight.galaxiumSeatsAvailable = 10;
        flight.persist();

        assertNotNull(flight.id);
        
        // Test seat availability methods
        assertEquals(60, flight.getSeatsAvailable(SeatClass.ECONOMY));
        assertEquals(30, flight.getSeatsAvailable(SeatClass.BUSINESS));
        assertEquals(10, flight.getSeatsAvailable(SeatClass.GALAXIUM));
        assertEquals(100, flight.getTotalSeatsAvailable());
    }

    @Test
    @TestTransaction
    void testFlightSeatDecrement() {
        Flight flight = new Flight();
        flight.origin = "Earth";
        flight.destination = "Moon";
        flight.departureTime = "2026-06-01 08:00";
        flight.arrivalTime = "2026-06-01 10:00";
        flight.basePrice = 200;
        flight.economySeatsAvailable = 5;
        flight.businessSeatsAvailable = 2;
        flight.galaxiumSeatsAvailable = 1;
        flight.persist();

        // Decrement economy seats
        assertTrue(flight.decrementSeats(SeatClass.ECONOMY));
        assertEquals(4, flight.economySeatsAvailable);
        
        // Decrement business seats
        assertTrue(flight.decrementSeats(SeatClass.BUSINESS));
        assertEquals(1, flight.businessSeatsAvailable);
        
        // Decrement galaxium seats to zero
        assertTrue(flight.decrementSeats(SeatClass.GALAXIUM));
        assertEquals(0, flight.galaxiumSeatsAvailable);
        
        // Try to decrement when no seats available
        assertFalse(flight.decrementSeats(SeatClass.GALAXIUM));
        assertEquals(0, flight.galaxiumSeatsAvailable);
    }

    @Test
    @TestTransaction
    void testFlightSeatIncrement() {
        Flight flight = new Flight();
        flight.origin = "Mars";
        flight.destination = "Earth";
        flight.departureTime = "2026-06-02 09:00";
        flight.arrivalTime = "2026-06-02 17:00";
        flight.basePrice = 500;
        flight.economySeatsAvailable = 58;
        flight.businessSeatsAvailable = 29;
        flight.galaxiumSeatsAvailable = 9;
        flight.persist();

        // Increment seats (for cancellation)
        flight.incrementSeats(SeatClass.ECONOMY);
        assertEquals(59, flight.economySeatsAvailable);
        
        flight.incrementSeats(SeatClass.BUSINESS);
        assertEquals(30, flight.businessSeatsAvailable);
        
        flight.incrementSeats(SeatClass.GALAXIUM);
        assertEquals(10, flight.galaxiumSeatsAvailable);
    }

    @Test
    @TestTransaction
    void testFlightPriceCalculation() {
        Flight flight = new Flight();
        flight.basePrice = 1000;
        flight.persist();

        // Test price multipliers
        assertEquals(1000, flight.calculatePrice(SeatClass.ECONOMY));    // 1x
        assertEquals(2500, flight.calculatePrice(SeatClass.BUSINESS));   // 2.5x
        assertEquals(5000, flight.calculatePrice(SeatClass.GALAXIUM));   // 5x
    }

    @Test
    @TestTransaction
    void testFlightFindByOrigin() {
        Flight flight1 = new Flight();
        flight1.origin = "Earth";
        flight1.destination = "Mars";
        flight1.departureTime = "2026-06-01 10:00";
        flight1.arrivalTime = "2026-06-01 18:00";
        flight1.basePrice = 500;
        flight1.economySeatsAvailable = 60;
        flight1.businessSeatsAvailable = 30;
        flight1.galaxiumSeatsAvailable = 10;
        flight1.persist();

        Flight flight2 = new Flight();
        flight2.origin = "Mars";
        flight2.destination = "Jupiter";
        flight2.departureTime = "2026-06-02 10:00";
        flight2.arrivalTime = "2026-06-03 18:00";
        flight2.basePrice = 1200;
        flight2.economySeatsAvailable = 60;
        flight2.businessSeatsAvailable = 30;
        flight2.galaxiumSeatsAvailable = 10;
        flight2.persist();

        // Case-insensitive partial match
        var earthFlights = Flight.findByOrigin("earth");
        assertEquals(1, earthFlights.size());
        assertEquals("Earth", earthFlights.get(0).origin);
        
        var marsFlights = Flight.findByOrigin("MARS");
        assertEquals(1, marsFlights.size());
        assertEquals("Mars", marsFlights.get(0).origin);
    }

    @Test
    @TestTransaction
    void testBookingPersistAndFind() {
        // Create user and flight first
        User user = new User();
        user.name = "Test User";
        user.email = "test@example.com";
        user.persist();

        Flight flight = new Flight();
        flight.origin = "Earth";
        flight.destination = "Mars";
        flight.departureTime = "2026-06-01 10:00";
        flight.arrivalTime = "2026-06-01 18:00";
        flight.basePrice = 500;
        flight.economySeatsAvailable = 60;
        flight.businessSeatsAvailable = 30;
        flight.galaxiumSeatsAvailable = 10;
        flight.persist();

        // Create booking
        Booking booking = new Booking();
        booking.userId = user.id;
        booking.flightId = flight.id;
        booking.status = BookingStatus.BOOKED;
        booking.bookingTime = "2026-05-01T10:00:00Z";
        booking.seatClass = SeatClass.BUSINESS;
        booking.pricePaid = 1250;
        booking.persist();

        assertNotNull(booking.id);
        
        // Find by user ID
        var userBookings = Booking.findByUserId(user.id);
        assertEquals(1, userBookings.size());
        assertEquals(BookingStatus.BOOKED, userBookings.get(0).status);
        assertEquals(SeatClass.BUSINESS, userBookings.get(0).seatClass);
    }

    @Test
    @TestTransaction
    void testBookingCancellation() {
        Booking booking = new Booking();
        booking.userId = 1L;
        booking.flightId = 1L;
        booking.status = BookingStatus.BOOKED;
        booking.bookingTime = "2026-05-01T10:00:00Z";
        booking.seatClass = SeatClass.ECONOMY;
        booking.pricePaid = 500;
        booking.persist();

        // Can be cancelled
        assertTrue(booking.canBeCancelled());
        
        // Cancel it
        booking.cancel();
        assertEquals(BookingStatus.CANCELLED, booking.status);
        
        // Cannot be cancelled again
        assertFalse(booking.canBeCancelled());
        assertThrows(IllegalStateException.class, booking::cancel);
    }

    @Test
    @TestTransaction
    void testBookingFindByStatus() {
        Booking booking1 = new Booking();
        booking1.userId = 1L;
        booking1.flightId = 1L;
        booking1.status = BookingStatus.BOOKED;
        booking1.bookingTime = "2026-05-01T10:00:00Z";
        booking1.seatClass = SeatClass.ECONOMY;
        booking1.pricePaid = 500;
        booking1.persist();

        Booking booking2 = new Booking();
        booking2.userId = 2L;
        booking2.flightId = 2L;
        booking2.status = BookingStatus.CANCELLED;
        booking2.bookingTime = "2026-05-02T10:00:00Z";
        booking2.seatClass = SeatClass.BUSINESS;
        booking2.pricePaid = 1250;
        booking2.persist();

        var bookedBookings = Booking.findByStatus(BookingStatus.BOOKED);
        assertTrue(bookedBookings.size() >= 1);
        
        var cancelledBookings = Booking.findByStatus(BookingStatus.CANCELLED);
        assertTrue(cancelledBookings.size() >= 1);
    }

    @Test
    void testSeatClassEnum() {
        assertEquals("economy", SeatClass.ECONOMY.getValue());
        assertEquals("business", SeatClass.BUSINESS.getValue());
        assertEquals("galaxium", SeatClass.GALAXIUM.getValue());
        
        assertEquals(1.0, SeatClass.ECONOMY.getMultiplier());
        assertEquals(2.5, SeatClass.BUSINESS.getMultiplier());
        assertEquals(5.0, SeatClass.GALAXIUM.getMultiplier());
        
        // Test fromString
        assertEquals(SeatClass.ECONOMY, SeatClass.fromString("economy"));
        assertEquals(SeatClass.BUSINESS, SeatClass.fromString("BUSINESS"));
        assertEquals(SeatClass.GALAXIUM, SeatClass.fromString("Galaxium"));
        
        // Test default
        assertEquals(SeatClass.ECONOMY, SeatClass.fromString(null));
        
        // Test invalid
        assertThrows(IllegalArgumentException.class, () -> SeatClass.fromString("invalid"));
    }

    @Test
    void testBookingStatusEnum() {
        assertEquals("booked", BookingStatus.BOOKED.getValue());
        assertEquals("cancelled", BookingStatus.CANCELLED.getValue());
        assertEquals("completed", BookingStatus.COMPLETED.getValue());
        
        // Test fromString
        assertEquals(BookingStatus.BOOKED, BookingStatus.fromString("booked"));
        assertEquals(BookingStatus.CANCELLED, BookingStatus.fromString("CANCELLED"));
        assertEquals(BookingStatus.COMPLETED, BookingStatus.fromString("Completed"));
        
        // Test American spelling
        assertEquals(BookingStatus.CANCELLED, BookingStatus.fromString("canceled"));
        
        // Test default
        assertEquals(BookingStatus.BOOKED, BookingStatus.fromString(null));
        
        // Test invalid
        assertThrows(IllegalArgumentException.class, () -> BookingStatus.fromString("invalid"));
    }
}

// Made with Bob
