package com.galaxium.holdservice.scheduler;

import com.galaxium.holdservice.domain.AuditEvent;
import com.galaxium.holdservice.domain.Hold;
import com.galaxium.holdservice.repository.AuditEventRepository;
import com.galaxium.holdservice.repository.HoldRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class HoldExpirationScheduler {

    @Inject
    Logger log;

    @Inject
    HoldRepository holdRepository;

    @Inject
    AuditEventRepository auditEventRepository;

    @Scheduled(every = "{hold.expiration.check.interval.seconds}")
    @Transactional
    public void expireHolds() {
        Instant now = Instant.now();
        List<Hold> expiredHolds = holdRepository.findExpiredHolds(now);

        if (!expiredHolds.isEmpty()) {
            log.infof("Found %d expired holds to process", expiredHolds.size());

            for (Hold hold : expiredHolds) {
                hold.status = Hold.HoldStatus.EXPIRED;
                holdRepository.persist(hold);

                // Create audit event
                AuditEvent event = new AuditEvent();
                event.entityType = "HOLD";
                event.entityId = hold.holdId;
                event.eventType = "EXPIRED";
                event.details = String.format("Hold expired at %s", now);
                auditEventRepository.persist(event);

                log.infof("Hold %s marked as expired", hold.holdId);
            }

            log.infof("Processed %d expired holds", expiredHolds.size());
        }
    }
}

// Made with Bob
