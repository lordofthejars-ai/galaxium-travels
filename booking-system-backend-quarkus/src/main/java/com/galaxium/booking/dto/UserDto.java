package com.galaxium.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galaxium.booking.entity.User;

import java.util.Objects;

/**
 * User data transfer object.
 * Matches Python backend's UserOut schema.
 */
public class UserDto {
    @JsonProperty("user_id")
    public Long id;
    
    public String name;
    public long telegramId;
    public String email;

    public UserDto() {
    }

    public UserDto(Long id, String name, String email, long telegramId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegramId = telegramId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return telegramId == userDto.telegramId && Objects.equals(id, userDto.id) && Objects.equals(name, userDto.name) && Objects.equals(email, userDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, telegramId, email);
    }

    /**
     * Create DTO from entity.
     */
    public static UserDto from(User user) {
        return new UserDto(user.id, user.name, user.email, user.telegramId);
    }
}

// Made with Bob
