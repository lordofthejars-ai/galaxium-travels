package com.galaxium.booking.ai;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ChatbotSupervisorAgentTest {

    @Inject
    ChatbotSupervisorAgent agent;

    @Test
    public void shouldBlockNoneBookingQuestions() {
        agent.chat("My user id is 1, Can you list my bookings?");
    }

}
