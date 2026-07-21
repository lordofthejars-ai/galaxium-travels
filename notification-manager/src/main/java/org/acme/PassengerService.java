package org.acme;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PassengerService {

    public List<Long> getChatIds(FlightNotification notification) {
        return List.of();
    }

}
