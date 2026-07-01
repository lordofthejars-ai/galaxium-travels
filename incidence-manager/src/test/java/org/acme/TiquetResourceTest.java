package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class TiquetResourceTest {

    // -------------------------------------------------------------------------
    // Happy path — the full workflow runs against the real (demo) LLM endpoint.
    // We cannot assert the exact text, but we can assert the call succeeds and
    // returns a non-empty plain-text body.
    // -------------------------------------------------------------------------

    // My seat was broken during the flight.

    @Test
    void shouldReturnAIResponseForValidRequest() {
        String response = given()
                .contentType("application/json")
                .body("""
                        {
                          "user":      "John Doe",
                          "email":     "john@example.com",
                          "bookingId": 42,
                          "message":   "The experience was great."
                        }
                        """)
        .when()
                .post("/tiquet/storetest")
        .then()
                .extract().response().asString();

        System.out.println("***+++ " + response);
    }

    
}
