package com.galaxium.booking.resource;

import com.galaxium.booking.service.ChatbotAiService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

@Path("/chatbot")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatbotResource {

    @Inject
    ChatbotAiService chatbotAiService;

    @Inject
    Logger logger;

    @POST
    @Path("/{userId}")
    public String chat(@RestPath Long userId, String message) {

        logger.infof("User: %s Asked: %s", userId, message);

        String userMessage = """
            User Id: %s
            
            Message of the user: %s
            """.formatted(userId, message);

        String response =  chatbotAiService.chat(userMessage);
        logger.info(response);

        return response;
    }
}

// Made with Bob
