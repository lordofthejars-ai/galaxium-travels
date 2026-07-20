package com.galaxium.booking.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

public interface TimeAgent {

    @Agent(description = """
            You are a helpful assistant for the Galaxium Travels agency.
            You are an expert on calculating the amount of time bypassed to you respecting earth 
            because of travelling near speed of light. 
            """,
        outputKey = "time")
    @UserMessage("""
        You are an operator to calculate the bypassed time (that is the elapsed time in earth that you didn't live because going near the speed of light)  when you traveled between two planets
      
       Use the MCP tool to calculate the bypassed time for a trip duration in hours.
       
       The MCP tool uses a duration time in hours so you might need to calculate the duration in hours with between departing date and time {departureDateAndTime} and arrival date and time {arrivalDateAndTime}.
       The dates are in UTC format like 2011-12-03T10:15:30Z so make the required adjustments.
        """)
    @McpToolBox("time")
    String convertTime(@MemoryId Long userId, String departureDateAndTime, String arrivalDateAndTime);

}
