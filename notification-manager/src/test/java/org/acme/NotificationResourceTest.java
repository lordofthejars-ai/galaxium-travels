package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

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
    @TestSecurity(user = "alice", roles = "admin")
    void adminCanPostNotification() {
        given()
            .contentType("application/json")
            .body("""
                {"flightId": 1, "message": "Your flight is delayed"}
                """)
            .when().post("/notifications")
            .then()
                .statusCode(202)
                .body(notNullValue());
    }

    @Test
    @TestSecurity(user = "bob", roles = "user")
    void nonAdminIsRejected() {
        given()
            .contentType("application/json")
            .body("""
                {"flightId": 1, "message": "Your flight is delayed"}
                """)
            .when().post("/notifications")
            .then()
                .statusCode(403);
    }

    @Test
    void unauthenticatedIsRejected() {
        given()
            .contentType("application/json")
            .body("""
                {"flightId": 1, "message": "Your flight is delayed"}
                """)
            .when().post("/notifications")
            .then()
                .statusCode(401);
    }
}
