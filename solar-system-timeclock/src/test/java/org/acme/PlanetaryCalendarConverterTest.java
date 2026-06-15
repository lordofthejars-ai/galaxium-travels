package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.planets.Planet;
import org.acme.planets.PlanetaryCalendarConverter;
import org.acme.planets.PlanetaryDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

@QuarkusTest
public class PlanetaryCalendarConverterTest {

    @Inject
    PlanetaryCalendarConverter planetaryCalendarConverter;

    @Test
    public void shouldConvertTime() {
        Instant earthDate =
            Instant.parse("2026-06-06T14:30:00Z");
        PlanetaryDate planetaryDate = planetaryCalendarConverter.convert(earthDate, Planet.MARS);
        Assertions.assertThat(planetaryDate.planet())
            .isEqualTo("09:23:25");
    }

}
