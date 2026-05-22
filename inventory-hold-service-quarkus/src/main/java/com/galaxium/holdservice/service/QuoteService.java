package com.galaxium.holdservice.service;

import com.galaxium.holdservice.api.dto.CreateQuoteRequest;
import com.galaxium.holdservice.client.BookingSystemBackend;
import com.galaxium.holdservice.client.FlightLookupException;
import com.galaxium.holdservice.client.dto.FlightResponse;
import com.galaxium.holdservice.domain.AuditEvent;
import com.galaxium.holdservice.domain.Quote;
import com.galaxium.holdservice.repository.AuditEventRepository;
import com.galaxium.holdservice.repository.QuoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ApplicationScoped
public class QuoteService {

    @Inject
    Logger log;

    @Inject
    QuoteRepository quoteRepository;

    @Inject
    AuditEventRepository auditEventRepository;

    @Inject
    PricingService pricingService;

    @RestClient
    BookingSystemBackend bookingSystemBackend;

    @Transactional
    public Quote createQuote(CreateQuoteRequest request) {
        log.infof("Creating quote for flight %d with %d %s seats",
                request.flightId, request.quantity, request.seatClass);

        String quoteId = generateQuoteId();

        FlightResponse flight = bookingSystemBackend.getFlights().stream()
                .filter(f -> f.flightId != null && f.flightId.equals(request.flightId))
                .findFirst()
                .orElseThrow(() -> new FlightLookupException("Flight not found: " + request.flightId));

        if (flight.basePrice == null) {
            throw new IllegalStateException("Flight base price missing for flight " + request.flightId);
        }

        long pricePerSeat = pricingService.calculatePrice(flight.basePrice, request.seatClass);
        long totalPrice = pricePerSeat * request.quantity;

        Quote quote = new Quote();
        quote.quoteId = quoteId;
        quote.flightId = request.flightId;
        quote.seatClass = request.seatClass;
        quote.quantity = request.quantity;
        quote.travelerId = request.travelerId;
        quote.travelerName = request.travelerName;
        quote.pricePerSeat = pricePerSeat;
        quote.totalPrice = totalPrice;
        quote.expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);
        quote.status = Quote.QuoteStatus.CREATED;

        quoteRepository.persist(quote);

        createAuditEvent("QUOTE", quoteId, "CREATED",
                String.format("Quote created for flight %d, %d %s seats",
                        request.flightId, request.quantity, request.seatClass));

        log.infof("Quote %s created successfully", quoteId);
        return quote;
    }

    public Optional<Quote> getQuote(String quoteId) {
        return quoteRepository.findByIdOptional(quoteId);
    }

    private String generateQuoteId() {
        int year = Year.now().getValue();
        long count = quoteRepository.countAll() + 1;
        return String.format("Q-%d-%06d", year, count);
    }

    @Transactional
    void createAuditEvent(String entityType, String entityId, String eventType, String details) {
        AuditEvent event = new AuditEvent();
        event.entityType = entityType;
        event.entityId = entityId;
        event.eventType = eventType;
        event.details = details;
        auditEventRepository.persist(event);
    }
}

// Made with Bob
