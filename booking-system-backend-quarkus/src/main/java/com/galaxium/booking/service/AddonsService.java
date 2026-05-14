package com.galaxium.booking.service;

import com.galaxium.booking.dto.AddOnDto;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

/**
 * Add-ons service providing the static catalog of available add-ons.
 * Matches Python backend's addons.py service layer.
 */
@ApplicationScoped
public class AddonsService {

    /**
     * Static catalog of available add-ons.
     * Matches the Python backend's ADDONS_CATALOG.
     */
    private static final List<AddOnDto> ADDONS_CATALOG = Arrays.asList(
        new AddOnDto(
            "extra_cargo",
            "Extra Cargo Allowance",
            150,
            "Additional 20kg cargo allowance for moon rocks and souvenirs",
            "💼"
        ),
        new AddOnDto(
            "gourmet_meal",
            "Gourmet Space Meal",
            85,
            "Chef-prepared zero-gravity cuisine with asteroid-aged wine",
            "🍽️"
        ),
        new AddOnDto(
            "wifi",
            "Interstellar Wi-Fi",
            45,
            "High-speed quantum-entangled connectivity throughout your journey",
            "📡"
        ),
        new AddOnDto(
            "insurance",
            "Cosmic Travel Insurance",
            200,
            "Comprehensive coverage including meteor strikes and alien encounters",
            "🛡️"
        ),
        new AddOnDto(
            "zero_g",
            "Zero-G Experience Package",
            500,
            "30-minute guided zero-gravity experience with certified instructor",
            "🚀"
        ),
        new AddOnDto(
            "window_seat",
            "Window Seat Upgrade",
            120,
            "Guaranteed panoramic viewport for Earth/Mars views",
            "🪟"
        ),
        new AddOnDto(
            "lounge_access",
            "Spaceport Lounge Access",
            95,
            "Pre-departure access to luxury orbital lounge with anti-gravity bar",
            "👑"
        )
    );

    /**
     * Get the static add-ons catalog.
     * 
     * @return List of available add-ons
     */
    public List<AddOnDto> getAddonsCatalog() {
        return ADDONS_CATALOG;
    }
}

// Made with Bob
