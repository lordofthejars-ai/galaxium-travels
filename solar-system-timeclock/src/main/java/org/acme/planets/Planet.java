package org.acme.planets;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Planet {

    MERCURY("Mercury", 1407.6, 88),
    VENUS("Venus", 5832.5, 225),
    EARTH("Earth", 24.0, 365.25),
    MARS("Mars", 24.6229, 687),
    JUPITER("Jupiter", 9.925, 4333),
    SATURN("Saturn", 10.7, 10759),
    URANUS("Uranus", 17.2, 30687),
    NEPTUNE("Neptune", 16.1, 60190);

    private final String displayName;
    private final double dayLengthHours;
    private final double orbitalPeriodEarthDays;

    Planet(
        String displayName,
        double dayLengthHours,
        double orbitalPeriodEarthDays) {
        this.displayName = displayName;
        this.dayLengthHours = dayLengthHours;
        this.orbitalPeriodEarthDays = orbitalPeriodEarthDays;
    }

    public String displayName() {
        return displayName;
    }

    public double dayLengthHours() {
        return dayLengthHours;
    }

    public double orbitalPeriodEarthDays() {
        return orbitalPeriodEarthDays;
    }

    private static final Map<String, Planet> BY_NAME =
        Arrays.stream(values())
            .collect(Collectors.toMap(
                Planet::displayName,
                Function.identity()
            ));

    public static Planet fromName(String name) {
        return BY_NAME.get(name);
    }
}