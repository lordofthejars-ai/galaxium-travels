package com.galaxium.holdservice.repository;

import com.galaxium.holdservice.domain.AuditEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuditEventRepository implements PanacheRepositoryBase<AuditEvent, String> {
}

// Made with Bob
