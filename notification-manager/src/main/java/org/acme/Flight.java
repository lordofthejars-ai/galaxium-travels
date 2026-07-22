package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Flight(
        @JsonProperty("flight_id")
        long flightId,
        String origin,
        String destination) {
}
