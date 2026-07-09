package com.galaxium.booking.ai;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

@Description("Planet facts")
public record PlanetInformation(
        @Description("The synodic orbital period in days")
        double orbitalPeriod,
        @Description("The synodic rotation period in days")
        double rotationPeriod,
        @Description("Average surface temperature in Celsius")
        double averageSurfaceTemperature,
        @Description("List of interesting facts about the planet")
        List<String> interestingFacts) {
}
