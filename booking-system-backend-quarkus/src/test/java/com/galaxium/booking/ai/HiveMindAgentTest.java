package com.galaxium.booking.ai;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class HiveMindAgentTest {

    @Inject
    HiveMindAgent hiveMindAgent;

    @Test
    public void testCulturalProtocol() {
        String protocol = hiveMindAgent
            .findCulturalProtocol("Can you provide me the cultural protocol for Mars?");
        System.out.println(protocol);
    }

}
