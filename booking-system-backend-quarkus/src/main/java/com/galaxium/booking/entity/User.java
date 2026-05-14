package com.galaxium.booking.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * User entity representing a customer in the booking system.
 * Uses Panache Active Record pattern for simplified data access.
 * 
 * Email addresses are stored in lowercase for case-insensitive lookups.
 */
@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    public String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true, nullable = false)
    public String email;

    /**
     * Find user by email (case-insensitive).
     * Email is automatically lowercased before storage.
     */
    public static User findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return find("LOWER(email) = LOWER(?1)", email).firstResult();
    }

    /**
     * Find user by name and email (case-insensitive email, case-sensitive name).
     */
    public static User findByNameAndEmail(String name, String email) {
        if (name == null || email == null) {
            return null;
        }
        return find("name = ?1 AND LOWER(email) = LOWER(?2)", name, email).firstResult();
    }

    /**
     * Check if email already exists (case-insensitive).
     */
    public static boolean emailExists(String email) {
        if (email == null) {
            return false;
        }
        return count("LOWER(email) = LOWER(?1)", email) > 0;
    }

    /**
     * Normalize email to lowercase before persisting.
     */
    @Override
    public void persist() {
        if (email != null) {
            email = email.toLowerCase();
        }
        super.persist();
    }
}

// Made with Bob
