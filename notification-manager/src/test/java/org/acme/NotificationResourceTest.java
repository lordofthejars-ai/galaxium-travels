package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.QuarkusTestProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestProfile(NotificationResourceTest.Profile.class)
class NotificationResourceTest {

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

    @Test
    void postNotificationReturnsAccepted() {
        given()
            .contentType("text/plain")
            .body("Hello Kafka")
            .when().post("/notifications")
            .then()
                .statusCode(202)
                .body(is("Hello Kafka"));
    }

    @Test
    void postEmptyBodyIsAccepted() {
        given()
            .contentType("text/plain")
            .body("")
            .when().post("/notifications")
            .then()
                .statusCode(202);
    }
}
