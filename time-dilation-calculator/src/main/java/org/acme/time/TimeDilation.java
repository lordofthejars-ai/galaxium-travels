package org.acme.time;

import java.time.Duration;

public record TimeDilation(double travelHours, double earthHours, double bypassedHours) {
}
