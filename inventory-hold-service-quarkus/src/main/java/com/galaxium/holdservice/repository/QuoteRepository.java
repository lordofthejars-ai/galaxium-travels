package com.galaxium.holdservice.repository;

import com.galaxium.holdservice.domain.Quote;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuoteRepository implements PanacheRepositoryBase<Quote, String> {
    
    public long countAll() {
        return count();
    }
}

// Made with Bob
