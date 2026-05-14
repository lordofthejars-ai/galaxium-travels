# Python to Quarkus Migration Requirements

## Overview

This document outlines the requirements for migrating the Galaxium Booking System Backend from Python (FastAPI + FastMCP) to Quarkus (Java).

**Source Project**: `booking_system_backend/` (Python)  
**Target Project**: `booking-system-backend-quarkus/` (Quarkus)  
**Migration Status**: Initial scaffolding complete - implementation pending

---

## Core Requirements

### 1. Dual Protocol Support
The system must expose the same business logic through **two protocols**:
- **REST API** at `/api/*` endpoints
- **MCP (Model Context Protocol)** at `/mcp` endpoint

Both protocols must share the same service layer implementation.

### 2. Database Schema

#### Entities

**User**
- `user_id` (Integer, Primary Key, Auto-increment)
- `name` (String, Not Null)
- `email` (String, Unique, Not Null, Lowercase)

**Flight**
- `flight_id` (Integer, Primary Key, Auto-increment)
- `origin` (String, Not Null)
- `destination` (String, Not Null)
- `departure_time` (String, Not Null) - Format: ISO 8601 or "YYYY-MM-DD HH:MM"
- `arrival_time` (String, Not Null) - Format: ISO 8601 or "YYYY-MM-DD HH:MM"
- `base_price` (Integer, Not Null) - Economy price (1x multiplier)
- `economy_seats_available` (Integer, Not Null) - 60% of total seats
- `business_seats_available` (Integer, Not Null) - 30% of total seats
- `galaxium_seats_available` (Integer, Not Null) - 10% of total seats

**Booking**
- `booking_id` (Integer, Primary Key, Auto-increment)
- `user_id` (Integer, Foreign Key → users.user_id, Not Null)
- `flight_id` (Integer, Foreign Key → flights.flight_id, Not Null)
- `status` (String, Not Null) - Values: "booked", "cancelled", "completed"
- `booking_time` (String, Not Null) - ISO 8601 timestamp
- `seat_class` (String, Not Null, Default: "economy") - Values: "economy", "business", "galaxium"
- `price_paid` (Integer, Not Null) - Actual price at booking time
- `addons` (JSON, Nullable) - Array of selected add-on objects

### 3. Business Logic

#### Seat Class Pricing
**Hardcoded multipliers** (not configurable):
- Economy: 1.0x (base price)
- Business: 2.5x
- Galaxium: 5.0x

Price calculation: `int(base_price * multiplier)`

#### Booking Flow
1. Validate seat class (must be "economy", "business", or "galaxium")
2. Check flight exists
3. Check seat availability for the specific class
4. Verify user exists and name matches user_id
5. Calculate price: `base_price * seat_class_multiplier + addons_total`
6. Validate add-ons against catalog (prevent price tampering)
7. Decrement the appropriate seat counter (economy/business/galaxium)
8. Create booking with UTC timestamp
9. Return booking details

#### Cancellation Flow
1. Check booking exists
2. Verify booking is not already cancelled
3. Restore seat to the correct class counter
4. Update booking status to "cancelled"
5. Return updated booking

#### User Management
- Email addresses are **automatically lowercased** for case-insensitive lookups
- Email validation using regex pattern
- Name matching is **case-sensitive** and **exact**

#### Add-ons System
Static catalog of 7 add-ons:
- Extra Cargo Allowance (150 credits)
- Gourmet Space Meal (85 credits)
- Interstellar Wi-Fi (45 credits)
- Cosmic Travel Insurance (200 credits)
- Zero-G Experience Package (500 credits)
- Window Seat Upgrade (120 credits)
- Spaceport Lounge Access (95 credits)

Add-ons validation:
- Only selected add-ons are included
- Prices must match catalog (prevent tampering)
- Invalid add-on IDs are rejected

### 4. REST API Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/` | Health check | - |
| GET | `/api/flights` | List flights with filters | Query params |
| POST | `/api/book` | Book a flight | `{user_id, name, flight_id, seat_class, addons?}` |
| GET | `/api/bookings/{user_id}` | Get user's bookings | - |
| POST | `/api/cancel/{booking_id}` | Cancel a booking | - |
| POST | `/api/register` | Register new user | `{name, email}` |
| GET | `/api/user?name=...&email=...` | Get user by name and email | Query params |
| GET | `/api/addons` | Get add-ons catalog | - |

#### Flight Filtering (Query Parameters)
- `origin` - Case-insensitive partial match
- `destination` - Case-insensitive partial match
- `departure_date_from` - ISO format or YYYY-MM-DD
- `departure_date_to` - ISO format or YYYY-MM-DD
- `min_price` - Minimum economy price
- `max_price` - Maximum economy price
- `has_economy` - Boolean, only flights with economy seats
- `has_business` - Boolean, only flights with business seats
- `has_galaxium` - Boolean, only flights with galaxium seats
- `seat_class` - Filter by specific class availability
- `departure_time_period` - "morning", "afternoon", "evening", "night"
- `min_duration` - Minimum flight duration in hours
- `max_duration` - Maximum flight duration in hours
- `min_seats_available` - Minimum total seats
- `route_category` - "inner_planets", "outer_planets", "moons"
- `sort` / `sort_by` - "price", "departure_time", "duration", "seats_available"
- `order` / `sort_order` - "asc" or "desc"

#### Flight Response Enhancement
Each flight response includes computed prices for all classes:
- `economy_price` = base_price
- `business_price` = int(base_price * 2.5)
- `galaxium_price` = base_price * 5

### 5. MCP Tools

| Tool | Description | Parameters |
|------|-------------|------------|
| `list_flights` | List all available flights | - |
| `book_flight` | Book a seat | `user_id, name, flight_id, seat_class` |
| `get_bookings` | Get user's bookings | `user_id` |
| `cancel_booking` | Cancel a booking | `booking_id` |
| `register_user` | Register new user | `name, email` |
| `get_user_id` | Get user by name and email | `name, email` |

**MCP Implementation Notes**:
- MCP tools must convert service layer `ErrorResponse` unions to exceptions
- REST endpoints return `ErrorResponse` as JSON payloads
- Both use the same service layer functions

### 6. Error Handling

#### Service Layer Pattern
All service functions return **Union types**: `ModelOut | ErrorResponse`

**Never raise exceptions** - always return `ErrorResponse` with:
- `success: false`
- `error: string` - Human-readable error message
- `error_code: string` - Machine-readable code
- `details: string` (optional) - Additional context

#### Error Codes
- `INVALID_SEAT_CLASS` - Invalid seat class provided
- `FLIGHT_NOT_FOUND` - Flight doesn't exist
- `NO_SEATS_AVAILABLE` - No seats in requested class
- `NAME_MISMATCH` - User ID exists but name doesn't match
- `USER_NOT_FOUND` - User doesn't exist
- `INVALID_ADDON` - Add-on not in catalog
- `PRICE_TAMPERING` - Add-on price doesn't match catalog
- `BOOKING_NOT_FOUND` - Booking doesn't exist
- `ALREADY_CANCELLED` - Booking already cancelled
- `EMAIL_EXISTS` - Email already registered
- `INVALID_EMAIL` - Invalid email format

### 7. Configuration

#### Database
- **Development**: SQLite (`booking.db`)
- **Production**: PostgreSQL (via `DATABASE_URL` environment variable)
- Connection pooling required
- Auto-create tables on startup

#### Server
- **Port**: 8080 (Python), 8081 (Quarkus to avoid conflict during migration)
- **CORS**: Enabled for all origins (development)
- **Health Check**: Available at `/` and `/q/health`

#### Environment Variables
- `DATABASE_URL` - Database connection string (optional, defaults to SQLite)

### 8. Demo Data Seeding

On startup, seed the database with:
- **10 users**: Alice, Bob, Charlie, Diana, Eve, Frank, Grace, Heidi, Ivan, Judy
- **10 flights**: Interplanetary routes (Earth ↔ Mars, Moon, Venus, Jupiter, Europa, Pluto)
- **Seat distribution per flight**: 60% Economy, 30% Business, 10% Galaxium
- **20 bookings**: Random bookings across all seat classes

### 9. Testing Requirements

#### Service Layer Tests
- Flight listing with seat availability
- Booking creation with seat class validation
- Seat counter updates (all three classes)
- Booking cancellation and seat restoration
- User registration and retrieval
- Error handling for all error codes
- Add-ons validation and price tampering detection

#### REST API Tests
- All endpoints
- Request/response validation
- Error responses
- Integration with service layer

#### Test Isolation
- Use in-memory database for tests
- Mock external dependencies
- Each test should be independent

---

## Architecture Patterns

### Service Layer Architecture
```
┌─────────────────────────────────────┐
│         Transport Layer             │
│  ┌──────────┐      ┌──────────┐   │
│  │   REST   │      │   MCP    │   │
│  └────┬─────┘      └────┬─────┘   │
│       │                 │          │
│       └────────┬────────┘          │
└────────────────┼───────────────────┘
                 │
┌────────────────▼───────────────────┐
│         Service Layer               │
│  (Pure business logic)              │
│  - booking.py / BookingService      │
│  - flight.py / FlightService        │
│  - user.py / UserService            │
│  - addons.py / AddonsService        │
└────────────────┬───────────────────┘
                 │
┌────────────────▼───────────────────┐
│         Data Layer                  │
│  - models.py / JPA Entities         │
│  - db.py / Panache Repositories     │
└─────────────────────────────────────┘
```

### Key Design Principles
1. **Service layer is transport-agnostic** - No HTTP/MCP concepts in services
2. **Union return types** - Services return success or error, never throw
3. **Email normalization** - Always lowercase before DB operations
4. **Manual session management** - Explicit transaction boundaries
5. **Integer pricing** - No decimal handling
6. **UTC timestamps** - ISO 8601 format strings
7. **No cascade deletes** - Bookings persist when flights/users deleted

---

## Quarkus-Specific Implementation Notes

### Extensions Required
✅ Already included:
- `quarkus-rest-jackson` - REST endpoints with JSON
- `quarkus-hibernate-orm-panache` - ORM with active record pattern
- `quarkus-jdbc-postgresql` - PostgreSQL driver
- `quarkus-hibernate-validator` - Bean validation
- `quarkus-smallrye-health` - Health checks

🔜 To be added:
- SQLite JDBC driver (for development)
- MCP server implementation (custom or library)
- CORS configuration

### Recommended Package Structure
```
com.galaxium.booking/
├── entity/
│   ├── User.java
│   ├── Flight.java
│   └── Booking.java
├── repository/
│   ├── UserRepository.java
│   ├── FlightRepository.java
│   └── BookingRepository.java
├── service/
│   ├── BookingService.java
│   ├── FlightService.java
│   ├── UserService.java
│   └── AddonsService.java
├── dto/
│   ├── FlightDto.java
│   ├── BookingDto.java
│   ├── UserDto.java
│   └── ErrorResponse.java
├── rest/
│   ├── FlightResource.java
│   ├── BookingResource.java
│   └── UserResource.java
├── mcp/
│   └── McpServer.java (to be implemented)
└── util/
    ├── SeatClass.java (enum)
    └── BookingStatus.java (enum)
```

### Database Configuration
```properties
# Development (SQLite)
quarkus.datasource.db-kind=sqlite
quarkus.datasource.jdbc.url=jdbc:sqlite:booking.db
quarkus.hibernate-orm.database.generation=drop-and-create

# Production (PostgreSQL)
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=${DATABASE_URL}
quarkus.hibernate-orm.database.generation=update
```

---

## Migration Checklist

### Phase 1: Core Infrastructure ✅
- [x] Create Quarkus project with extensions
- [x] Document migration requirements
- [ ] Configure database (SQLite + PostgreSQL)
- [ ] Set up package structure

### Phase 2: Data Layer
- [ ] Create JPA entities (User, Flight, Booking)
- [ ] Create Panache repositories
- [ ] Implement database seeding
- [ ] Write entity tests

### Phase 3: Service Layer
- [ ] Implement BookingService
- [ ] Implement FlightService (with all filters)
- [ ] Implement UserService
- [ ] Implement AddonsService
- [ ] Create DTOs and ErrorResponse
- [ ] Write service layer tests

### Phase 4: REST API
- [ ] Implement FlightResource
- [ ] Implement BookingResource
- [ ] Implement UserResource
- [ ] Configure CORS
- [ ] Write REST endpoint tests

### Phase 5: MCP Integration
- [ ] Research Quarkus MCP implementation options
- [ ] Implement MCP server
- [ ] Expose service layer via MCP tools
- [ ] Test MCP integration

### Phase 6: Testing & Documentation
- [ ] Complete test coverage
- [ ] Update README with Quarkus-specific instructions
- [ ] Performance testing
- [ ] Migration validation

---

## Critical Implementation Notes

### From AGENTS.md
1. **Test isolation**: Must patch both session factories to use in-memory DB
2. **MCP server lifecycle**: Create MCP server before application startup
3. **Service layer errors**: Return ErrorResponse unions, don't raise exceptions
4. **Booking confirmation**: Identity-checked by both ID and name
5. **Email normalization**: Lowercase before any DB query
6. **Frontend compatibility**: Use `isErrorResponse()` pattern for error detection
7. **Hold state split**: Java hold service + frontend localStorage (not in this service)
8. **Quote/hold IDs**: Demo-friendly sequential IDs, not production-safe

### Python-Specific Patterns to Translate
- `SessionLocal()` with try/finally → Quarkus `@Transactional`
- Pydantic `BaseModel` → Java records or POJOs with validation
- `Literal['economy', 'business', 'galaxium']` → Java enum
- `Union[ModelOut, ErrorResponse]` → Java sealed interfaces or custom Result type
- `datetime.utcnow().isoformat()` → `Instant.now().toString()`
- SQLAlchemy ORM → Hibernate ORM with Panache

---

## References

- **Python Backend**: `booking_system_backend/`
- **Python README**: `booking_system_backend/README.md`
- **Agent Rules**: `AGENTS.md`
- **Quarkus Guides**: https://quarkus.io/guides/
- **Hibernate Panache**: https://quarkus.io/guides/hibernate-orm-panache
- **Quarkus REST**: https://quarkus.io/guides/rest

---

**Migration Started**: 2026-05-14  
**Target Completion**: TBD  
**Status**: Initial scaffolding complete, implementation pending