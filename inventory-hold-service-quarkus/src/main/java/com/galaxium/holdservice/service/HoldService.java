package com.galaxium.holdservice.service;

import com.galaxium.holdservice.client.BookingCreationException;
import com.galaxium.holdservice.client.BookingSystemBackend;
import com.galaxium.holdservice.client.dto.BookingResponse;
import com.galaxium.holdservice.domain.AuditEvent;
import com.galaxium.holdservice.domain.Hold;
import com.galaxium.holdservice.domain.Quote;
import com.galaxium.holdservice.repository.AuditEventRepository;
import com.galaxium.holdservice.repository.HoldRepository;
import com.galaxium.holdservice.repository.QuoteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class HoldService {

    @Inject
    Logger log;

    @Inject
    HoldRepository holdRepository;

    @Inject
    QuoteRepository quoteRepository;

    @Inject
    AuditEventRepository auditEventRepository;

    @RestClient
    BookingSystemBackend bookingSystemBackend;

    @ConfigProperty(name = "hold.duration.minutes", defaultValue = "15")
    int holdDurationMinutes;

    @Transactional
    public Hold createHold(String quoteId) {
        log.infof("Creating hold for quote %s", quoteId);

        // Verify quote exists
        Quote quote = quoteRepository.findByIdOptional(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found: " + quoteId));

        // Check if quote is expired
        if (quote.expiresAt.isBefore(Instant.now())) {
            throw new IllegalStateException("Quote has expired");
        }

        // Generate hold ID
        String holdId = generateHoldId();

        // Create hold
        Hold hold = new Hold();
        hold.holdId = holdId;
        hold.quoteId = quoteId;
        hold.status = Hold.HoldStatus.HELD;
        hold.reservedUntil = Instant.now().plus(holdDurationMinutes, ChronoUnit.MINUTES);

        holdRepository.persist(hold);

        // Audit event
        createAuditEvent("HOLD", holdId, "CREATED",
                String.format("Hold created for quote %s, expires at %s", quoteId, hold.reservedUntil));

        log.infof("Hold %s created successfully", holdId);
        return hold;
    }

    public Optional<Hold> getHold(String holdId) {
        return holdRepository.findByIdOptional(holdId);
    }

    @Transactional
    public Hold confirmHold(String holdId, List<Map<String, Object>> addons) {
        log.infof("Confirming hold %s", holdId);

        Hold hold = holdRepository.findByIdOptional(holdId)
                .orElseThrow(() -> new IllegalArgumentException("Hold not found: " + holdId));

        // Check if already confirmed
        if (hold.status == Hold.HoldStatus.CONFIRMED) {
            log.infof("Hold %s already confirmed, returning existing booking reference", holdId);
            return hold;
        }

        // Check if hold is still valid
        if (hold.status != Hold.HoldStatus.HELD) {
            throw new IllegalStateException("Hold is not in HELD status: " + hold.status);
        }

        if (hold.reservedUntil.isBefore(Instant.now())) {
            hold.status = Hold.HoldStatus.EXPIRED;
            holdRepository.persist(hold);
            throw new IllegalStateException("Hold has expired");
        }

        // Get quote details
        Quote quote = quoteRepository.findByIdOptional(hold.quoteId)
                .orElseThrow(() -> new IllegalStateException("Quote not found: " + hold.quoteId));

        try {
            // Call Python backend to create booking
            Map<String, Object> holdData = new HashMap<>();
            holdData.put("user_id", quote.travelerId);
            holdData.put("traveler_name", quote.travelerName);
            holdData.put("flight_id", quote.flightId);
            holdData.put("seat_class", quote.seatClass);

            if (addons != null && !addons.isEmpty()) {
                holdData.put("addons", addons);
            }

            BookingResponse booking = bookingSystemBackend.createBookingFromHold(holdData);

            // Update hold with booking reference
            hold.status = Hold.HoldStatus.CONFIRMED;
            hold.externalBookingReference = String.valueOf(booking.bookingId);
            holdRepository.persist(hold);

            // Audit event
            createAuditEvent("HOLD", holdId, "CONFIRMED",
                    String.format("Hold confirmed, booking ID: %s", booking.bookingId));

            log.infof("Hold %s confirmed successfully with booking %s", holdId, booking.bookingId);
            return hold;

        } catch (Exception e) {
            log.errorf(e, "Failed to create booking for hold %s", holdId);
            hold.status = Hold.HoldStatus.CONFIRMATION_FAILED;
            hold.errorMessage = e.getMessage();
            holdRepository.persist(hold);

            createAuditEvent("HOLD", holdId, "CONFIRMATION_FAILED", e.getMessage());

            throw new BookingCreationException("Failed to confirm hold: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Hold releaseHold(String holdId) {
        log.infof("Releasing hold %s", holdId);

        Hold hold = holdRepository.findByIdOptional(holdId)
                .orElseThrow(() -> new IllegalArgumentException("Hold not found: " + holdId));

        if (hold.status != Hold.HoldStatus.HELD) {
            throw new IllegalStateException("Hold cannot be released, current status: " + hold.status);
        }

        hold.status = Hold.HoldStatus.RELEASED;
        holdRepository.persist(hold);

        createAuditEvent("HOLD", holdId, "RELEASED", "Hold manually released");

        log.infof("Hold %s released successfully", holdId);
        return hold;
    }

    private String generateHoldId() {
        int year = Year.now().getValue();
        long count = holdRepository.countAll() + 1;
        return String.format("H-%d-%06d", year, count);
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
