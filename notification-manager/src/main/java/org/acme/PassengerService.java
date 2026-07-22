package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class PassengerService {

    @RestClient
    PassengerClient passengerClient;

    public List<Long> getChatIds(FlightNotification notification) {
        long flightId = notification.flightId();
        return passengerClient.getPassengersByFlight(flightId)
                .stream()
                .filter(p -> p.telegramId() > 0)
                .map(Passenger::telegramId)
                .toList();
    }

}
