package org.acme.planets;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

@ApplicationScoped
public class PlanetaryCalendarConverter {

    private static final Instant EPOCH =
        Instant.parse("2000-01-01T00:00:00Z"); // JD2000 approach

    public PlanetaryDate convert(
        Instant earthInstant,
        Planet targetPlanet) {

        double elapsedEarthDays =
            Duration.between(EPOCH, earthInstant)
                .toSeconds() / 86400.0;

        long planetaryYear =
            (long) (elapsedEarthDays
                / targetPlanet.orbitalPeriodEarthDays());

        double daysInsideCurrentYear =
            elapsedEarthDays %
                targetPlanet.orbitalPeriodEarthDays();

        long dayOfYear =
            (long) daysInsideCurrentYear + 1;

        double elapsedSeconds =
            Duration.between(EPOCH, earthInstant)
                .toSeconds();

        double planetDaySeconds =
            targetPlanet.dayLengthHours() * 3600.0;

        double secondsInsidePlanetDay =
            elapsedSeconds % planetDaySeconds;

        double dayFraction =
            secondsInsidePlanetDay / planetDaySeconds;

        long localClockSeconds =
            Math.round(dayFraction * 86400);

        LocalTime localTime =
            LocalTime.ofSecondOfDay(
                localClockSeconds % 86400
            );

        return new PlanetaryDate(
            targetPlanet.name(),
            planetaryYear,
            dayOfYear,
            localTime
        );
    }
}
