# Galaxium Booking System Backend - Quarkus

A Quarkus-based reimplementation of the Galaxium Travels booking system, migrated from Python (FastAPI + FastMCP).

## 🚀 Migration Status

**Current Phase**: Initial scaffolding complete  
**Original Backend**: `../booking_system_backend/` (Python)  
**Migration Guide**: See [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md) for detailed requirements

This project aims to provide the same dual-protocol functionality (REST + MCP) as the Python backend, with improved performance and native compilation capabilities.

## Features (Target)

- **Dual Protocol Support**: REST API and MCP (Model Context Protocol) from a single server
- **Three Seat Classes**: Economy, Business, Galaxium with independent availability tracking
- **Dynamic Pricing**: Class-based multipliers (1x, 2.5x, 5x) applied to base prices
- **Service Layer Architecture**: Pure business logic separated from transport layer
- **Type-Safe**: Full Java type safety with Bean Validation
- **Database Support**: SQLite (dev) and PostgreSQL (production)
- **Comprehensive Testing**: JUnit 5 with REST Assured
- **Native Compilation**: GraalVM native image support for fast startup

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL (for production) or SQLite (for development)

### Running in Dev Mode

```bash
./mvnw quarkus:dev
```

The application will start on port **8081** (to avoid conflict with Python backend on 8080) with:
- REST endpoints at `/api/*`
- MCP tools at `/mcp` (to be implemented)
- Health check at `/q/health`
- Dev UI at `http://localhost:8081/q/dev/`

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test
./mvnw test -Dtest=BookingServiceTest
```

## API Reference (Target)

### REST Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/q/health` | Health check | - |
| GET | `/api/flights` | List all available flights with filters | Query params |
| POST | `/api/book` | Book a flight with specific seat class | `{user_id, name, flight_id, seat_class, addons?}` |
| GET | `/api/bookings/{user_id}` | Get user's bookings | - |
| POST | `/api/cancel/{booking_id}` | Cancel a booking | - |
| POST | `/api/register` | Register a new user | `{name, email}` |
| GET | `/api/user?name=...&email=...` | Get user by name and email | Query params |
| GET | `/api/addons` | Get add-ons catalog | - |

### MCP Tools (To Be Implemented)

| Tool | Description | Parameters |
|------|-------------|------------|
| `list_flights` | List all available flights | - |
| `book_flight` | Book a seat on a flight | `user_id, name, flight_id, seat_class` |
| `get_bookings` | Get user's bookings | `user_id` |
| `cancel_booking` | Cancel a booking | `booking_id` |
| `register_user` | Register a new user | `name, email` |
| `get_user_id` | Get user by name and email | `name, email` |

## Project Structure

```
src/main/java/com/galaxium/booking/
├── entity/          # JPA entities (User, Flight, Booking)
├── repository/      # Panache repositories
├── service/         # Business logic layer
├── dto/             # Data transfer objects
├── rest/            # REST resources
├── mcp/             # MCP server implementation
└── util/            # Enums and utilities

src/main/resources/
├── application.properties    # Configuration
└── import.sql               # Demo data seeding (optional)

src/test/java/
└── com/galaxium/booking/    # Test suite
```

## Configuration

### Development (SQLite)

```properties
quarkus.datasource.db-kind=sqlite
quarkus.datasource.jdbc.url=jdbc:sqlite:booking.db
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.http.port=8081
```

### Production (PostgreSQL)

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=${DATABASE_URL}
quarkus.datasource.username=${DB_USER}
quarkus.datasource.password=${DB_PASSWORD}
quarkus.hibernate-orm.database.generation=update
quarkus.http.port=8080
```

## Building

### JVM Mode

```bash
# Package as JAR
./mvnw package

# Run the JAR
java -jar target/quarkus-app/quarkus-run.jar
```

### Native Mode

```bash
# Build native executable
./mvnw package -Dnative

# Or with container build (no GraalVM required)
./mvnw package -Dnative -Dquarkus.native.container-build=true

# Run native executable
./target/booking-system-backend-quarkus-1.0.0-SNAPSHOT-runner
```

## Docker

```bash
# Build JVM image
docker build -f src/main/docker/Dockerfile.jvm -t galaxium-booking-quarkus .

# Build native image
docker build -f src/main/docker/Dockerfile.native -t galaxium-booking-quarkus-native .

# Run
docker run -p 8081:8081 galaxium-booking-quarkus
```

## Migration Notes

### Key Differences from Python Backend

1. **Port**: 8081 (Quarkus) vs 8080 (Python) during migration
2. **Database**: Hibernate ORM with Panache vs SQLAlchemy
3. **Validation**: Bean Validation vs Pydantic
4. **Error Handling**: Result types vs Union types
5. **Transactions**: `@Transactional` vs manual session management
6. **MCP**: Custom implementation vs FastMCP library

### Compatibility

The Quarkus backend maintains API compatibility with:
- Frontend application (`booking_system_frontend/`)
- Java hold service (`inventory_hold_service/`)
- Existing database schema

### Performance Benefits

- **Faster startup**: ~0.05s (native) vs ~2s (Python)
- **Lower memory**: ~30MB (native) vs ~100MB (Python)
- **Better throughput**: Native compilation optimizations
- **Reactive support**: Optional reactive extensions

## Related Guides

- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache) - Simplified JPA/Hibernate
- [REST Jackson](https://quarkus.io/guides/rest#json-serialisation) - JSON serialization
- [SmallRye Health](https://quarkus.io/guides/smallrye-health) - Health checks
- [Hibernate Validator](https://quarkus.io/guides/validation) - Bean validation
- [JDBC PostgreSQL](https://quarkus.io/guides/datasource) - Database connectivity

## Development Workflow

1. **Start Quarkus in dev mode**: `./mvnw quarkus:dev`
2. **Make changes**: Code changes are automatically reloaded
3. **Run tests**: Tests run automatically or via `./mvnw test`
4. **Check Dev UI**: Visit `http://localhost:8081/q/dev/`
5. **View health**: Visit `http://localhost:8081/q/health`

## Troubleshooting

### Port Already in Use

If port 8081 is in use:
```bash
# macOS/Linux
lsof -ti:8081 | xargs kill -9

# Or change port in application.properties
quarkus.http.port=8082
```

### Database Errors

```bash
# Reset SQLite database
rm booking.db
./mvnw quarkus:dev  # Will recreate on startup
```

### Build Errors

```bash
# Clean and rebuild
./mvnw clean package
```

## Contributing

This is a migration project. Please refer to:
- [`MIGRATION_REQUIREMENTS.md`](MIGRATION_REQUIREMENTS.md) - Detailed requirements
- [`../AGENTS.md`](../AGENTS.md) - Agent rules and patterns
- [`../booking_system_backend/README.md`](../booking_system_backend/README.md) - Original Python backend

## License

See [LICENSE](../LICENSE) in the project root.

---

**Migration Status**: Initial scaffolding complete  
**Next Steps**: Implement entities, repositories, and service layer  
**Original Backend**: Python FastAPI + FastMCP  
**Target**: Quarkus with REST + MCP support
