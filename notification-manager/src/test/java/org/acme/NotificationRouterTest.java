package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusTest
@TestProfile(NotificationRouterTest.Profile.class)
class NotificationRouterTest {

    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                "notification.kafka.topic", "test-notifications",
                "notification.telegram.authorization-token", "fake-token",
                "notification.telegram.chat-id", "12345",
                "quarkus.camel.routes-discovery.enabled", "false"
            );
        }
    }

    @Inject
    CamelContext camelContext;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    NotificationRouter notificationRouter;

    @BeforeEach
    void setUp() throws Exception {
        if (camelContext.getRouteController().getRouteStatus("kafka-to-telegram") == null) {
            // Use the CDI-managed bean so @Inject fields (e.g. passengerService) are populated
            camelContext.addRoutes(notificationRouter);
        }

        // Replace kafka source with a direct endpoint for testing
        AdviceWith.adviceWith(camelContext, "kafka-to-telegram", advice -> {
            advice.replaceFromWith("direct:test-notifications");
            advice.mockEndpointsAndSkip("telegram:*");
        });

        camelContext.start();
    }

    @Test
    void routeSendsKafkaMessageToTelegram() throws Exception {
        MockEndpoint mockTelegram = camelContext.getEndpoint("mock:telegram:bots", MockEndpoint.class);
        mockTelegram.expectedMessageCount(1);
        mockTelegram.expectedBodiesReceived("Hello Telegram");

        producerTemplate.sendBody("direct:test-notifications",
            "{\"flightId\": 1, \"message\": \"Hello Telegram\"}");

        mockTelegram.assertIsSatisfied();
    }
}
