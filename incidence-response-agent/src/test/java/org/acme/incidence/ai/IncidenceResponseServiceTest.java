package org.acme.incidence.ai;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class IncidenceResponseServiceTest {

    @Inject
    IncidenceResponseService incidenceResponseService;

    @Test
    public void shouldCreateAResponse() {
        String response = incidenceResponseService.generate("""
                Thank you very much for your trip, the experience of the trip to Mars was amazing. 
                """);
        System.out.println(response);
    }

}
