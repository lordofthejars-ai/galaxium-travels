package com.galaxium.holdservice.repository;

import com.galaxium.holdservice.domain.Hold;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class HoldRepository implements PanacheRepositoryBase<Hold, String> {
    
    public long countAll() {
        return count();
    }
    
    public List<Hold> findExpiredHolds(Instant now) {
        return list("status = ?1 and reservedUntil < ?2", Hold.HoldStatus.HELD, now);
    }
}

// Made with Bob
