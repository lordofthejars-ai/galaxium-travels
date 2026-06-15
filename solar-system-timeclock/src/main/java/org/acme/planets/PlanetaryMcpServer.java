package org.acme.planets;


import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;

@ApplicationScoped
public class PlanetaryMcpServer {

    @Inject
    PlanetaryCalendarConverter converter;

    @Inject
    Logger logger;

    @Tool(description = """
        Convert the Earth instant date and time to the time of another planet  
        """)
    public String convert(
            @ToolArg(description = """
            This is the earth current instant time in UTC format like 2011-12-03T10:15:30Z
            """)
            String instant,
            @ToolArg(description = """
               The planet to convert the instant time  
                """)
            String planet
        ) {

        logger.infof("");

        return converter.convert(
            Instant.parse(instant),
            Planet.fromName(planet))
            .time().toString();
    }

}
