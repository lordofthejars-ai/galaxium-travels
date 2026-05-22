package com.galaxium.holdservice.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PricingService {

    @Inject
    Logger logger;

    public long calculatePrice(int basePrice, String seatClass) {
        double multiplier = switch (seatClass.toLowerCase()) {
            case "economy" -> 1.0;
            case "business" -> 2.5;
            case "galaxium" -> 5.0;
            default -> 1.0;
        };

        Long finalPrice = (long) (basePrice * multiplier);

        logger.debugf("Calculated price from base price %d in %s: %d", basePrice, seatClass, finalPrice);
        return finalPrice;
    }
}

// Made with Bob
