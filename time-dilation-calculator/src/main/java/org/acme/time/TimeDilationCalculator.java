package org.acme.time;

import jakarta.inject.Singleton;

@Singleton
public class TimeDilationCalculator {

    private static double SPACECRAFT_SPEED = 0.999;

    public TimeDilation calculate(double travelInHours) {

        double travelerHours = travelInHours;
        double speedPercentage = SPACECRAFT_SPEED;

        // 2. Calculate the Lorentz factor: 1 / sqrt(1 - v^2/c^2)
        double lorentzFactor = 1.0 / Math.sqrt(1.0 - Math.pow(speedPercentage, 2));

        // 3. Calculate time passed on Earth (in hours)
        double earthHours = travelerHours * lorentzFactor;

        // 4. Calculate the difference (hours "skipped" or "saved")
        double hoursDifference = earthHours - travelerHours;
        return new TimeDilation(travelerHours, earthHours, hoursDifference);
    }

}
