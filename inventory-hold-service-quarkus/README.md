# Inventory Hold Service - Quarkus Migration

This is a Quarkus migration of the original Spring Boot inventory-hold-service. The service provides B2B Quote and Hold Management functionality for the Galaxium Travels booking system.

## Migration Summary

### Technology Stack Changes

| Component | Spring Boot | Quarkus |
|-----------|-------------|---------|
| **Framework** | Spring Boot 3.2.0 | Quarkus 3.x |
| **Database** | SQLite | H2 (file-based) |
| **ORM** | Spring Data JPA | Hibernate ORM with Panache |
| **REST** | Spring Web MVC | JAX-RS (Jakarta REST) |
| **HTTP Client** | Java HttpClient | MicroProfile REST Client |
| **Dependency Injection** | Spring IoC | CDI (Contexts and Dependency Injection) |
| **Scheduling** | Spring @Scheduled | Quarkus Scheduler |
| **Validation** | Spring Validation | Jakarta Validation |
| **Logging** | SLF4J/Logback | JBoss Logging |

### Key Migration Changes

#### 1. **Database Migration: SQLite → H2**
- Changed from SQLite to H2 file-based database
- Database file: `./holds-db` (H2 format)
- Configuration: `quarkus.datasource.jdbc.url=jdbc:h2:file:./holds-db;DB_CLOSE_DELAY=-1`

#### 2. **Entity Layer: Spring Data JPA → Panache**
- Entities now extend `PanacheEntityBase` (for custom ID types)
- Public fields instead of getters/setters (Panache convention)
- Repositories implement `PanacheRepositoryBase<Entity, ID>`
- Removed Lombok annotations (@Data, @Builder, etc.)
- Simplified entity lifecycle callbacks (@PrePersist, @PreUpdate)

#### 3. **REST Client: HttpClient → MicroProfile REST Client**
- `PythonBackendClient` renamed to `BookingSystemBackend`
- Declarative interface with JAX-RS annotations
- Configuration via `quarkus.rest-client.booking-system-backend.url`
- Automatic JSON serialization/deserialization
- Injection via `@RestClient` annotation

#### 4. **REST Controllers → JAX-RS Resources**
- `@RestController` → `@Path`
- `@RequestMapping` → `@Path` with method-level annotations
- `@GetMapping/@PostMapping` → `@GET/@POST`
- `ResponseEntity<T>` → `Response` with builders
- `@PathVariable` → `@PathParam`
- `@RequestBody` → method parameter (automatic)

#### 5. **Dependency Injection: Spring → CDI**
- `@Autowired/@RequiredArgsConstructor` → `@Inject`
- `@Service/@Component` → `@ApplicationScoped`
- `@Value` → `@ConfigProperty`
- Constructor injection replaced with field injection

#### 6. **Scheduler Migration**
- `@Scheduled(fixedDelayString = "...")` → `@Scheduled(every = "{property}s")`
- Configuration property format changed
- Maintained transactional behavior with `@Transactional`

#### 7. **Logging**
- `@Slf4j` and `log` → `Logger.getLogger(Class.class)` and `LOG`
- `log.info()` → `LOG.infof()` (formatted logging)
- `log.error()` → `LOG.errorf()`

## Project Structure

```
inventory-hold-service-quarkus/
├── src/main/java/com/galaxium/holdservice/
│   ├── api/
│   │   ├── dto/
│   │   │   └── CreateQuoteRequest.java
│   │   ├── HealthResource.java
│   │   ├── HoldResource.java
│   │   └── QuoteResource.java
│   ├── client/
│   │   ├── dto/
│   │   │   ├── BookingResponse.java
│   │   │   └── FlightResponse.java
│   │   ├── BookingCreationException.java
│   │   ├── BookingSystemBackend.java (MicroProfile REST Client)
│   │   └── FlightLookupException.java
│   ├── domain/
│   │   ├── AuditEvent.java
│   │   ├── Hold.java
│   │   └── Quote.java
│   ├── repository/
│   │   ├── AuditEventRepository.java
│   │   ├── HoldRepository.java
│   │   └── QuoteRepository.java
│   ├── scheduler/
│   │   └── HoldExpirationScheduler.java
│   └── service/
│       ├── HoldService.java
│       ├── PricingService.java
│       └── QuoteService.java
└── src/main/resources/
    └── application.properties
```

## Configuration

### Application Properties

```properties
# Application
quarkus.application.name=inventory-hold-service-quarkus
quarkus.http.port=8080

# H2 Database
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:file:./holds-db;DB_CLOSE_DELAY=-1
quarkus.datasource.username=sa
quarkus.datasource.password=

# Hibernate ORM
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=false

# BookingSystemBackend REST Client
quarkus.rest-client.booking-system-backend.url=${PYTHON_BACKEND_URL:http://localhost:8001}

# Hold Configuration
hold.duration.minutes=15
hold.expiration.check.interval.seconds=60
```

## Running the Application

### Development Mode

```bash
./mvnw quarkus:dev
```

The application will start on `http://localhost:8080` with hot reload enabled.

### Production Mode

```bash
# Build
./mvnw package

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

### Native Build (Optional)

```bash
./mvnw package -Dnative
./target/inventory-hold-service-quarkus-1.0.0-runner
```

## API Endpoints

All endpoints remain the same as the original Spring Boot version:

### Health
- `GET /api/v1/health` - Health check

### Quotes
- `POST /api/v1/quotes` - Create a new quote
- `GET /api/v1/quotes/{quoteId}` - Get quote by ID

### Holds
- `POST /api/v1/quotes/{quoteId}/holds` - Create hold from quote
- `GET /api/v1/holds/{holdId}` - Get hold by ID
- `POST /api/v1/holds/{holdId}/confirm` - Confirm hold and create booking
- `POST /api/v1/holds/{holdId}/release` - Release hold

## Dependencies

### Quarkus Extensions Used

- `quarkus-rest-jackson` - JAX-RS with Jackson for JSON
- `quarkus-hibernate-orm-panache` - Simplified Hibernate ORM
- `quarkus-jdbc-h2` - H2 database driver
- `quarkus-rest-client-jackson` - MicroProfile REST Client
- `quarkus-scheduler` - Scheduled tasks

## Testing

The migration does not include tests as per requirements. To add tests:

```bash
./mvnw test
```

## Migration Notes

### Breaking Changes
1. **Database format changed** - Data from SQLite cannot be directly imported to H2
2. **Client name changed** - `PythonBackendClient` → `BookingSystemBackend`
3. **Configuration keys changed** - Spring properties → Quarkus properties

### Behavioral Differences
1. **Startup time** - Quarkus starts significantly faster than Spring Boot
2. **Memory footprint** - Quarkus uses less memory
3. **Hot reload** - Quarkus dev mode provides faster hot reload

### Compatibility
- All REST API endpoints remain unchanged
- Request/response formats are identical
- Business logic is preserved

## Environment Variables

- `PYTHON_BACKEND_URL` - URL of the Python booking backend (default: `http://localhost:8001`)

## Development

### Adding New Extensions

```bash
./mvnw quarkus:add-extension -Dextensions="extension-name"
```

### Quarkus Dev UI

Access the Dev UI at `http://localhost:8080/q/dev` when running in dev mode.

## Migration Checklist

- [x] Database migration (SQLite → H2)
- [x] Entity layer (Panache)
- [x] Repository layer (Panache repositories)
- [x] Service layer (CDI)
- [x] REST controllers (JAX-RS resources)
- [x] REST client (MicroProfile REST Client)
- [x] Scheduler (Quarkus Scheduler)
- [x] Configuration (application.properties)
- [x] Exception handling
- [x] Logging
- [ ] Tests (not included per requirements)

## Additional Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Hibernate ORM with Panache Guide](https://quarkus.io/guides/hibernate-orm-panache)
- [MicroProfile REST Client Guide](https://quarkus.io/guides/rest-client)
- [Quarkus Scheduler Guide](https://quarkus.io/guides/scheduler)
