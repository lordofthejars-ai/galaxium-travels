-- Galaxium Booking System - Demo Data Seeding
-- This file is automatically executed by Hibernate when database.generation=drop-and-create

-- Insert 10 users
INSERT INTO users (id, name, email) VALUES (1, 'Alice', 'alice@example.com');
INSERT INTO users (id, name, email) VALUES (2, 'Bob', 'bob@example.com');
INSERT INTO users (id, name, email) VALUES (3, 'Charlie', 'charlie@example.com');
INSERT INTO users (id, name, email) VALUES (4, 'Diana', 'diana@example.com');
INSERT INTO users (id, name, email) VALUES (5, 'Eve', 'eve@example.com');
INSERT INTO users (id, name, email) VALUES (6, 'Frank', 'frank@example.com');
INSERT INTO users (id, name, email) VALUES (7, 'Grace', 'grace@example.com');
INSERT INTO users (id, name, email) VALUES (8, 'Heidi', 'heidi@example.com');
INSERT INTO users (id, name, email) VALUES (9, 'Ivan', 'ivan@example.com');
INSERT INTO users (id, name, email) VALUES (10, 'Judy', 'judy@example.com');

-- Insert 10 flights with seat distribution: 60% Economy, 30% Business, 10% Galaxium
-- Total seats per flight: 100 (60 Economy, 30 Business, 10 Galaxium)
INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (1, 'Earth', 'Mars', '2026-06-01 10:00', '2026-06-01 18:00', 500, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (2, 'Mars', 'Earth', '2026-06-02 09:00', '2026-06-02 17:00', 500, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (3, 'Earth', 'Moon', '2026-06-03 08:00', '2026-06-03 10:00', 200, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (4, 'Moon', 'Earth', '2026-06-04 14:00', '2026-06-04 16:00', 200, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (5, 'Earth', 'Venus', '2026-06-05 11:00', '2026-06-05 19:00', 600, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (6, 'Mars', 'Jupiter', '2026-06-06 07:00', '2026-06-07 15:00', 1200, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (7, 'Jupiter', 'Europa', '2026-06-08 10:00', '2026-06-08 12:00', 300, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (8, 'Europa', 'Jupiter', '2026-06-09 13:00', '2026-06-09 15:00', 300, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (9, 'Earth', 'Pluto', '2026-06-10 06:00', '2026-06-12 18:00', 2000, 60, 30, 10);

INSERT INTO flights (id, origin, destination, departure_time, arrival_time, base_price, economy_seats_available, business_seats_available, galaxium_seats_available) 
VALUES (10, 'Pluto', 'Earth', '2026-06-13 08:00', '2026-06-15 20:00', 2000, 60, 30, 10);

-- Insert 20 bookings distributed across all seat classes
-- Economy bookings
INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (1, 1, 1, 'BOOKED', '2026-05-01T10:00:00Z', 'ECONOMY', 500, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (2, 2, 2, 'BOOKED', '2026-05-01T11:00:00Z', 'ECONOMY', 500, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (3, 3, 3, 'BOOKED', '2026-05-01T12:00:00Z', 'ECONOMY', 200, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (4, 4, 4, 'BOOKED', '2026-05-01T13:00:00Z', 'ECONOMY', 200, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (5, 5, 5, 'BOOKED', '2026-05-01T14:00:00Z', 'ECONOMY', 600, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (6, 6, 6, 'BOOKED', '2026-05-01T15:00:00Z', 'ECONOMY', 1200, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (7, 7, 7, 'BOOKED', '2026-05-01T16:00:00Z', 'ECONOMY', 300, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (8, 8, 8, 'BOOKED', '2026-05-01T17:00:00Z', 'ECONOMY', 300, NULL);

-- Business bookings
INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (9, 9, 9, 'BOOKED', '2026-05-02T10:00:00Z', 'BUSINESS', 5000, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (10, 10, 10, 'BOOKED', '2026-05-02T11:00:00Z', 'BUSINESS', 5000, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (11, 1, 1, 'BOOKED', '2026-05-02T12:00:00Z', 'BUSINESS', 1250, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (12, 2, 2, 'BOOKED', '2026-05-02T13:00:00Z', 'BUSINESS', 1250, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (13, 3, 3, 'BOOKED', '2026-05-02T14:00:00Z', 'BUSINESS', 500, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (14, 4, 4, 'BOOKED', '2026-05-02T15:00:00Z', 'BUSINESS', 500, NULL);

-- Galaxium bookings
INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (15, 5, 5, 'BOOKED', '2026-05-03T10:00:00Z', 'GALAXIUM', 3000, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (16, 6, 6, 'BOOKED', '2026-05-03T11:00:00Z', 'GALAXIUM', 6000, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (17, 7, 7, 'BOOKED', '2026-05-03T12:00:00Z', 'GALAXIUM', 1500, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (18, 8, 8, 'BOOKED', '2026-05-03T13:00:00Z', 'GALAXIUM', 1500, NULL);

-- Cancelled bookings
INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (19, 9, 9, 'CANCELLED', '2026-05-04T10:00:00Z', 'ECONOMY', 2000, NULL);

INSERT INTO bookings (id, user_id, flight_id, status, booking_time, seat_class, price_paid, addons) 
VALUES (20, 10, 10, 'CANCELLED', '2026-05-04T11:00:00Z', 'ECONOMY', 2000, NULL);

-- Update seat availability based on bookings (subtract booked seats)
-- Flight 1: 2 economy, 1 business booked
UPDATE flights SET economy_seats_available = 58, business_seats_available = 29 WHERE id = 1;

-- Flight 2: 2 economy, 1 business booked
UPDATE flights SET economy_seats_available = 58, business_seats_available = 29 WHERE id = 2;

-- Flight 3: 2 economy, 1 business booked
UPDATE flights SET economy_seats_available = 58, business_seats_available = 29 WHERE id = 3;

-- Flight 4: 2 economy, 1 business booked
UPDATE flights SET economy_seats_available = 58, business_seats_available = 29 WHERE id = 4;

-- Flight 5: 1 economy, 1 galaxium booked
UPDATE flights SET economy_seats_available = 59, galaxium_seats_available = 9 WHERE id = 5;

-- Flight 6: 1 economy, 1 galaxium booked
UPDATE flights SET economy_seats_available = 59, galaxium_seats_available = 9 WHERE id = 6;

-- Flight 7: 1 economy, 1 galaxium booked
UPDATE flights SET economy_seats_available = 59, galaxium_seats_available = 9 WHERE id = 7;

-- Flight 8: 1 economy, 1 galaxium booked
UPDATE flights SET economy_seats_available = 59, galaxium_seats_available = 9 WHERE id = 8;

-- Flight 9: 1 business booked (1 cancelled, so seats restored)
UPDATE flights SET business_seats_available = 29 WHERE id = 9;

-- Flight 10: 1 business booked (1 cancelled, so seats restored)
UPDATE flights SET business_seats_available = 29 WHERE id = 10;

-- Set sequence values for auto-increment (PostgreSQL specific)
SELECT setval('users_seq', 10, true);
SELECT setval('flights_seq', 10, true);
SELECT setval('bookings_seq', 20, true);

-- Made with Bob
