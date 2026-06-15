package com.galaxium.booking.ai;

import com.galaxium.booking.service.BookingSearchTool;
import com.galaxium.booking.service.FlightSearchTool;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ToolBox;

public interface BookingAgent {

    @SystemMessage("""
        You are a helpful assistant for Galaxium Travels, a space travel booking system.
        You help users with information about flights, bookings, and travel destinations.
        Be friendly, professional, and concise in your responses.
        
        The output must be in Markdown format; feel free to enrich with emoji but always the output should be in MarkDown format.
        
        You have access to tools that can help you retrieve booking information.
        When a user asks about their bookings, use the available tools to search for them.
        """)
    @Agent(description = "You are a helpful assistant for Galaxium Travels, a space travel booking system tp get information about bookings",
        outputKey = "answer")
    @ToolBox({BookingSearchTool.class, FlightSearchTool.class})
    String chat(@UserMessage String userMessage);
}