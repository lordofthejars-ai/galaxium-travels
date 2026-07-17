package com.galaxium.booking.ai;

import com.galaxium.booking.hivemind.HiveMindCulturalProtocolTool;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.skills.Skills;

public interface HiveMindAgent {

    @SystemMessage("""
        You are an assistant to provide cultural protocol for planets.
        
        First activate the hive-mind-cultural-protocol skills before executing any tool, and then based on the skills content execute any tool you need.  
                """)
    @Agent(description = "Gets the cultural protocol for the given planet")
    @Skills("hive-mind-cultural-protocol")
    @ToolBox(HiveMindCulturalProtocolTool.class)
    String findCulturalProtocol(@UserMessage String planet);

}
