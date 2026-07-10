package org.acme.time;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TimeDilationMcpServer {

    @Inject
    TimeDilationCalculator timeDilationCalculator;

    @Inject
    Logger logger;

    @Tool(description = """
        Calculate the Hours you bypassed during an intergalactic flight  
        """)
    public TimeDilation calculate(
            @ToolArg(description = """
                This is the amount of hours between the arrival and the departure time. It is the duration of the trip.
            """)
            double durationOfTripInHours) {

        logger.infof("Bypassed Hours for a trip of duration %s", durationOfTripInHours);

        return timeDilationCalculator
                .calculate(durationOfTripInHours);

    }
}
