package com.galaxium.booking.service;

import com.galaxium.booking.dto.ErrorResponse;
import com.galaxium.booking.dto.Result;
import com.galaxium.booking.dto.UserDto;
import com.galaxium.booking.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.regex.Pattern;

/**
 * User service for registration and lookup operations.
 * Matches Python backend's user.py service layer.
 */
@ApplicationScoped
public class UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Validate email address format.
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Register a new user with a name and unique email.
     * Email is automatically lowercased for case-insensitive lookups.
     * 
     * @param name User's name
     * @param email User's email (will be lowercased)
     * @return Result containing UserDto or ErrorResponse
     */
    @Transactional
    public Result<UserDto> registerUser(String name, String email) {
        // Normalize email to lowercase
        String normalizedEmail = email.toLowerCase();
        
        // Validate email format
        if (!isValidEmail(normalizedEmail)) {
            return Result.failure(
                "Invalid email format",
                ErrorResponse.INVALID_EMAIL,
                String.format("Email '%s' is not a valid email address. Please provide a valid email in the format: example@domain.com", email)
            );
        }

        // Check if email already exists
        if (User.emailExists(normalizedEmail)) {
            return Result.failure(
                "Email already registered",
                ErrorResponse.EMAIL_EXISTS,
                String.format("Email '%s' is already registered. A user with this email already exists in our system. If you're trying to access an existing account, use get_user with the correct name and email to get the user_id.", normalizedEmail)
            );
        }

        // Create and persist new user
        User user = new User();
        user.name = name;
        user.email = normalizedEmail;
        user.persist();

        return Result.success(UserDto.from(user));
    }

    /**
     * Retrieve a user's information by name and email.
     * Email lookup is case-insensitive, name matching is case-sensitive.
     * 
     * @param name User's name (case-sensitive)
     * @param email User's email (case-insensitive)
     * @return Result containing UserDto or ErrorResponse
     */
    public Result<UserDto> getUser(String name, String email) {
        // Normalize email to lowercase
        String normalizedEmail = email.toLowerCase();
        
        // Validate email format
        if (!isValidEmail(normalizedEmail)) {
            return Result.failure(
                "Invalid email format",
                ErrorResponse.INVALID_EMAIL,
                String.format("Email '%s' is not a valid email address. Please provide a valid email in the format: example@domain.com", email)
            );
        }

        // Find user by name and email
        User user = User.findByNameAndEmail(name, normalizedEmail);
        if (user == null) {
            return Result.failure(
                "User not found",
                ErrorResponse.USER_NOT_FOUND,
                String.format("User not found with name '%s' and email '%s'. The user may not be registered in our system. Please check the spelling of both name and email, or register the user first.", name, normalizedEmail)
            );
        }

        return Result.success(UserDto.from(user));
    }
}

// Made with Bob
