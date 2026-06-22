package com.galaxium.booking.ai;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ChatMessagesClassificationTest {

    @Inject
    ChatMessagesClassification classification;

    @Test
    public void classifyInput() {

        System.out.println(classification.validConversation("List all my bookings"));

    }

}
