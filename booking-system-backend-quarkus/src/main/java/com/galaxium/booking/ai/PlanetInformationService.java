package com.galaxium.booking.ai;

import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
@SystemMessage("""
        You are an assistant that gets information about solar system planets for a galaxy trip.
        This information can be the orbital period in days, average surface temperature in Celsius, or from 2 to 4 interesting facts about the planet. 
        """)
public interface PlanetInformationService {

    @UserMessage("""
            Find the required information for the {planet} planet.
            """)
    PlanetInformation findInformationAbout(String planet, ChatRequestParameters chatRequestParameters);

}
