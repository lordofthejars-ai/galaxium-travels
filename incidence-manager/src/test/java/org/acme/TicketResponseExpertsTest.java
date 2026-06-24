package org.acme;


import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.ai.TicketResponseExperts;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TicketResponseExpertsTest {

    @Inject
    TicketResponseExperts.ResponseGeneratorAgent responseGeneratorAgent;

    @Test
    public void shouldConnectToRemoteAgent() {
        System.out.println(responseGeneratorAgent.generateTicketResponse("Hello"));
    }

}
