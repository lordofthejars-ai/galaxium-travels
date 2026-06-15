package com.galaxium.booking.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

public interface TimeAgent {

    @Agent(description = "You are a helpful assistant for Galaxium Travels, you can convert the time from Earth to any other planet",
        outputKey = "time")
    @UserMessage("""
        You are an operator converting times from Earth to any other Solar System planet.
        
         Use the tool to convert the {{time}} in ISO 8601 format or "YYYY-MM-DD HH:MM" to the planet {{planet}}.
         
         The tool uses instant time in UTC format like 2011-12-03T10:15:30Z so make the required adjustments.
        """)
    @McpToolBox("time")
    String convertTime(String time, String planet);

}
