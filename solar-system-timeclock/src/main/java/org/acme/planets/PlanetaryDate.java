package org.acme.planets;

import java.time.LocalTime;

public record PlanetaryDate(
    String planet,
    long year,
    long dayOfYear,
    LocalTime time
) {

    @Override
    public String toString() {
        return String.format(
            "%s Year %d Day %d %s",
            planet,
            year,
            dayOfYear,
            time
        );
    }
}
