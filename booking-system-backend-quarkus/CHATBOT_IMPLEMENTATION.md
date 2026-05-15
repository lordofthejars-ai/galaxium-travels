# Chatbot Implementation with LangChain4j

## Overview

This document describes the implementation of the `/chatbot` REST endpoint using LangChain4j AI Service in the Quarkus backend.

## Architecture

The chatbot implementation consists of the following components:

### 1. Dependencies

Added to [`pom.xml`](pom.xml:69):
```xml
<dependency>
    <groupId>io.quarkiverse.langchain4j</groupId>
    <artifactId>quarkus-langchain4j-openai</artifactId>
    <version>1.10.0</version>
</dependency>
```

### 2. DTOs

#### ChatRequest ([`ChatRequest.java`](src/main/java/com/galaxium/booking/dto/ChatRequest.java))
```java
public record ChatRequest(
    @NotBlank(message = "Message cannot be blank")
    String message
) {}
```

#### ChatResponse ([`ChatResponse.java`](src/main/java/com/galaxium/booking/dto/ChatResponse.java))
```java
public record ChatResponse(
    String response
) {}
```

### 3. AI Service Interface

[`ChatbotAiService.java`](src/main/java/com/galaxium/booking/service/ChatbotAiService.java):
```java
@RegisterAiService
public interface ChatbotAiService {

    @SystemMessage("""
        You are a helpful assistant for Galaxium Travels, a space travel booking system.
        You help users with information about flights, bookings, and travel destinations.
        Be friendly, professional, and concise in your responses.
        """)
    String chat(@UserMessage String userMessage);
}
```

The `@RegisterAiService` annotation tells Quarkus to automatically implement this interface at build time, connecting it to the configured LLM provider.

The `tools` parameter registers LangChain4j tools that the AI can use to perform actions like searching for bookings.

### 4. LangChain4j Tools

[`BookingSearchTool.java`](src/main/java/com/galaxium/booking/service/BookingSearchTool.java):
```java
@ApplicationScoped
public class BookingSearchTool {

    @Inject
    BookingService bookingService;

    @Tool("Search for all bookings belonging to a specific user by their user ID")
    public List<BookingDto> findBookingsByUserId(Long userId) {
        return bookingService.getBookings(userId);
    }
}
```

The `@Tool` annotation makes this method available to the AI assistant. When a user asks about their bookings, the AI can automatically call this tool with the appropriate user ID.

**How it works:**
1. User asks: "Show me my bookings for user ID 1"
2. AI recognizes it needs booking information
3. AI automatically calls `findBookingsByUserId(1)`
4. AI receives the booking data and formats a response for the user

### 5. REST Endpoint

[`ChatbotResource.java`](src/main/java/com/galaxium/booking/resource/ChatbotResource.java):
```java
@Path("/chatbot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatbotResource {

    @Inject
    ChatbotAiService chatbotAiService;

    @POST
    public ChatResponse chat(@Valid ChatRequest request) {
        String response = chatbotAiService.chat(request.message());
        return new ChatResponse(response);
    }
}
```

### 5. Configuration

Added to [`application.properties`](src/main/resources/application.properties:70):
```properties
# LangChain4j Configuration
# OpenAI API Configuration
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY:demo}
quarkus.langchain4j.openai.timeout=PT60S

# For development/testing without OpenAI, you can use a mock provider
# or configure to use Ollama (local LLM) instead:
# quarkus.langchain4j.ollama.base-url=http://localhost:11434
# quarkus.langchain4j.ollama.chat-model.model-id=llama2
```

## Usage

### API Endpoint

**POST** `/chatbot`

**Request Body:**
```json
{
  "message": "What destinations do you offer?"
}
```

**Response:**
```json
{
  "response": "Galaxium Travels offers exciting space travel destinations including Mars colonies, lunar resorts, and orbital stations. How can I help you plan your journey?"
}
```

### Example cURL Request

```bash
curl -X POST http://localhost:8001/chatbot \
  -H "Content-Type: application/json" \
  -d '{"message": "Tell me about your services"}'
```

## Configuration Options

### Using OpenAI (Default)

Set the `OPENAI_API_KEY` environment variable:
```bash
export OPENAI_API_KEY=sk-your-api-key-here
```

### Using Ollama (Local LLM)

1. Install and start Ollama: https://ollama.ai/
2. Pull a model: `ollama pull llama2`
3. Update `application.properties`:
```properties
# Comment out OpenAI config
#quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY:demo}

# Enable Ollama
quarkus.langchain4j.ollama.base-url=http://localhost:11434
quarkus.langchain4j.ollama.chat-model.model-id=llama2
```

4. Change the dependency in `pom.xml`:
```xml
<dependency>
    <groupId>io.quarkiverse.langchain4j</groupId>
    <artifactId>quarkus-langchain4j-ollama</artifactId>
    <version>1.10.0</version>
</dependency>
```

## Testing

### Manual Testing

Start the application:
```bash
./mvnw quarkus:dev
```

Test the endpoint:
```bash
curl -X POST http://localhost:8001/chatbot \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello, what can you help me with?"}'
```

### Integration with Frontend

The frontend can call this endpoint from the ChatBot component:
```typescript
const response = await fetch('http://localhost:8001/chatbot', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ message: userMessage })
});
const data = await response.json();
console.log(data.response);
```

## LangChain4j Tools Integration

### Available Tools

The chatbot currently has access to the following tools:

1. **BookingSearchTool** - Search for bookings by user ID
   - Method: `findBookingsByUserId(Long userId)`
   - Description: Retrieves all bookings for a specific user

### Creating Additional Tools

To add more tools for the AI assistant:

1. Create a new `@ApplicationScoped` class
2. Add methods annotated with `@Tool("description")`
3. Register the tool in `ChatbotAiService`:

```java
@RegisterAiService(tools = {BookingSearchTool.class, FlightSearchTool.class})
public interface ChatbotAiService {
    // ...
}
```

Example - Flight Search Tool:
```java
@ApplicationScoped
public class FlightSearchTool {

    @Inject
    FlightService flightService;

    @Tool("Search for available flights between origin and destination")
    public List<FlightDto> searchFlights(String origin, String destination) {
        return flightService.searchFlights(origin, destination);
    }
}
```

### Tool Best Practices

1. **Clear Descriptions**: The `@Tool` annotation description helps the AI understand when to use the tool
2. **Simple Parameters**: Use primitive types or simple objects that the AI can easily provide
3. **Return Structured Data**: Return DTOs or simple types that the AI can interpret
4. **Error Handling**: Tools should handle errors gracefully and return meaningful messages

## Advanced Features

### Adding Memory/Context

To add conversation memory, modify the AI Service:
```java
@RegisterAiService
public interface ChatbotAiService {
    
    @SystemMessage("You are a helpful assistant for Galaxium Travels...")
    String chat(@MemoryId String userId, @UserMessage String message);
}
```

### Adding Tools/Functions

LangChain4j supports function calling to integrate with your business logic:
```java
@RegisterAiService(tools = FlightSearchTool.class)
public interface ChatbotAiService {
    String chat(@UserMessage String message);
}
```

## Troubleshooting

### Issue: "API key not configured"
**Solution:** Set the `OPENAI_API_KEY` environment variable or use Ollama for local testing.

### Issue: "Timeout errors"
**Solution:** Increase the timeout in `application.properties`:
```properties
quarkus.langchain4j.openai.timeout=PT120S
```

### Issue: "Model not found"
**Solution:** Verify the model name in configuration matches available models.

## References

- [Quarkus LangChain4j Extension](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)
- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [OpenAI API Documentation](https://platform.openai.com/docs/api-reference)
- [Ollama Documentation](https://ollama.ai/docs)