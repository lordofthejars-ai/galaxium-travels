# Getting Started with Quarkus Migration

This guide helps you get started with the Galaxium Booking System Backend Quarkus migration.

## What Has Been Done

### ✅ Phase 1: Initial Scaffolding (Complete)

1. **Quarkus Project Created**
   - Group ID: `com.galaxium`
   - Artifact ID: `booking-system-backend-quarkus`
   - Quarkus Version: 3.35.3
   - Java Version: 21
   - Build Tool: Maven

2. **Extensions Included**
   - `quarkus-rest-jackson` - REST endpoints with JSON serialization
   - `quarkus-hibernate-orm-panache` - Simplified JPA/Hibernate with active record pattern
   - `quarkus-jdbc-postgresql` - PostgreSQL database driver
   - `quarkus-hibernate-validator` - Bean validation
   - `quarkus-smallrye-health` - Health check endpoints

3. **Documentation Created**
   - [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md) - Comprehensive migration requirements
   - [`README.md`](README.md) - Updated Quarkus-specific README
   - [`GETTING_STARTED.md`](GETTING_STARTED.md) - This file
   - [`application.properties`](src/main/resources/application.properties) - Base configuration

4. **Configuration Set Up**
   - HTTP port: 8081 (to avoid conflict with Python backend)
   - CORS enabled for development
   - PostgreSQL configured with dev services
   - Health checks at `/q/health`
   - REST API base path: `/api`

## What Needs to Be Done

### 📋 Phase 2: Data Layer (Next Steps)

1. **Create JPA Entities**
   - `User.java` - User entity with email normalization
   - `Flight.java` - Flight entity with three seat class counters
   - `Booking.java` - Booking entity with status and seat class

2. **Create Panache Repositories**
   - `UserRepository.java` - User queries with case-insensitive email
   - `FlightRepository.java` - Flight queries with filtering
   - `BookingRepository.java` - Booking queries by user

3. **Database Seeding**
   - Create seed data script or use `import.sql`
   - 10 users, 10 flights, 20 bookings

### 📋 Phase 3: Service Layer

1. **Implement Services**
   - `BookingService.java` - Booking logic with seat class handling
   - `FlightService.java` - Flight listing with advanced filters
   - `UserService.java` - User registration and lookup
   - `AddonsService.java` - Static add-ons catalog

2. **Create DTOs**
   - `FlightDto.java` - Flight response with computed prices
   - `BookingDto.java` - Booking response
   - `UserDto.java` - User response
   - `ErrorResponse.java` - Standardized error format

3. **Error Handling**
   - Implement Result/Either pattern for service returns
   - Define error codes enum

### 📋 Phase 4: REST API

1. **Create REST Resources**
   - `FlightResource.java` - Flight endpoints
   - `BookingResource.java` - Booking endpoints
   - `UserResource.java` - User endpoints

2. **Request/Response Validation**
   - Bean validation annotations
   - Custom validators for seat class

### 📋 Phase 5: MCP Integration

1. **Research MCP Options**
   - Evaluate Java MCP libraries
   - Consider custom SSE implementation
   - Plan tool registration

2. **Implement MCP Server**
   - Create MCP endpoint at `/mcp`
   - Expose service layer as MCP tools
   - Handle tool invocations

### 📋 Phase 6: Testing

1. **Unit Tests**
   - Service layer tests
   - Repository tests
   - DTO validation tests

2. **Integration Tests**
   - REST endpoint tests with REST Assured
   - Database integration tests
   - MCP tool tests

## Quick Commands

### Start Development

```bash
cd booking-system-backend-quarkus

# Start in dev mode (with hot reload)
./mvnw quarkus:dev

# Access Dev UI
open http://localhost:8081/q/dev/

# Check health
curl http://localhost:8081/q/health
```

### Run Tests

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=BookingServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Build

```bash
# Package as JAR
./mvnw package

# Build native executable
./mvnw package -Dnative

# Build native in container (no GraalVM needed)
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Development Tips

### 1. Use Quarkus Dev Mode

Dev mode provides:
- **Hot reload** - Code changes apply instantly
- **Dev UI** - Visual interface at `/q/dev/`
- **Dev Services** - Auto-starts PostgreSQL container
- **Continuous testing** - Tests run on code changes

### 2. Follow Panache Patterns

```java
// Active Record pattern
@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;
    
    public static User findByEmail(String email) {
        return find("email", email.toLowerCase()).firstResult();
    }
}

// Repository pattern
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByEmail(String email) {
        return find("email", email.toLowerCase()).firstResult();
    }
}
```

### 3. Use @Transactional

```java
@ApplicationScoped
public class BookingService {
    
    @Transactional
    public BookingDto bookFlight(BookingRequest request) {
        // Transaction managed automatically
        // Rollback on exception
    }
}
```

### 4. Implement Result Pattern

```java
public sealed interface Result<T> permits Success, Failure {
    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(ErrorResponse error) implements Result<T> {}
}

// Usage
public Result<BookingDto> bookFlight(BookingRequest request) {
    if (flight == null) {
        return new Failure<>(new ErrorResponse("FLIGHT_NOT_FOUND", "Flight not found"));
    }
    return new Success<>(bookingDto);
}
```

### 5. Use Bean Validation

```java
public class BookingRequest {
    @NotNull
    @Positive
    public Integer userId;
    
    @NotBlank
    public String name;
    
    @NotNull
    @Positive
    public Integer flightId;
    
    @Pattern(regexp = "economy|business|galaxium")
    public String seatClass = "economy";
}
```

## Project Structure

```
booking-system-backend-quarkus/
├── src/main/java/com/galaxium/booking/
│   ├── entity/          # JPA entities
│   ├── repository/      # Panache repositories
│   ├── service/         # Business logic
│   ├── dto/             # Data transfer objects
│   ├── rest/            # REST resources
│   ├── mcp/             # MCP server (to be implemented)
│   └── util/            # Enums and utilities
├── src/main/resources/
│   ├── application.properties
│   └── import.sql       # Optional seed data
├── src/test/java/       # Test suite
├── MIGRATION_REQUIREMENTS.md
├── README.md
├── GETTING_STARTED.md
└── pom.xml
```

## Useful Resources

### Quarkus Guides
- [Getting Started](https://quarkus.io/guides/getting-started)
- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [REST Guide](https://quarkus.io/guides/rest)
- [Validation Guide](https://quarkus.io/guides/validation)
- [Testing Guide](https://quarkus.io/guides/getting-started-testing)

### Migration References
- Python Backend: `../booking_system_backend/`
- Agent Rules: `../AGENTS.md`
- Migration Requirements: [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md)

## Next Steps

1. **Read the migration requirements**: [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md)
2. **Start Quarkus in dev mode**: `./mvnw quarkus:dev`
3. **Create the first entity**: Start with `User.java`
4. **Write a test**: Create `UserRepositoryTest.java`
5. **Iterate**: Build incrementally with continuous testing

## Questions?

- Check [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md) for detailed requirements
- Review Python backend at `../booking_system_backend/`
- Consult Quarkus guides at https://quarkus.io/guides/

---

**Status**: Ready to begin implementation  
**Next**: Create JPA entities in `src/main/java/com/galaxium/booking/entity/`