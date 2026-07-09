package com.galaxium.booking.ai;

import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;

public class SchemaDefinitions {

    static ResponseFormat createPlanetSchema() {
        return ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchema.builder()
                        .name("PlanetInformation") // Required by some underlying providers like OpenAI
                        .rootElement(JsonObjectSchema.builder()
                                .addNumberProperty("orbitalPeriod")
                                .addNumberProperty("rotationPeriod")
                                .addNumberProperty("averageSurfaceTemperature")
                                .addProperty("interestingFacts", JsonArraySchema.builder()
                                        .items(JsonStringSchema.builder().build())
                                        .build())
                                // Specifying all fields as required for strict schema compliance
                                .required("orbitalPeriod",
                                        "rotationPeriod",
                                        "averageSurfaceTemperature",
                                        "interestingFacts")
                                .build())
                        .build())
                .build();
    }

    public static ChatRequestParameters schemaValidation() {
        return ChatRequestParameters.builder()
                .responseFormat(createPlanetSchema()).build();
    }

}
