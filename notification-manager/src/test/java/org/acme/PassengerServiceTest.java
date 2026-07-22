package org.acme;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PassengerServiceTest {
    @Inject
    PassengerService passengerService;

    @Test
    void getChatIdsReturnsTelegramIdsForFlight() {
        System.out.println(passengerService.getChatIds(new FlightNotification(1, "Hello")));
    }
}
