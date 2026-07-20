package com.galaxium.booking.resource;

import com.galaxium.booking.ai.BookingAgent;
import com.galaxium.booking.ai.ChatbotSupervisorAgent;
import com.galaxium.booking.dto.Question;
import com.galaxium.booking.dto.Questions;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;

@Path("/chatbot")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatbotResource {

    @Inject
    ChatbotSupervisorAgent chatbotAiService;

    @Inject
    Logger logger;

    @GET
    @Path("/suggested-questions")
    public Questions getChatbotSuggestedQuestions() {
        List<Question> questions = List.of(
                new Question("1", "What are my booked flights?"),
                new Question("2", "Can you calculate the bypass time respect earth for my booking 1?"),
                new Question("3", "What is the cultural protocol for Neptune?")
        );
        return new Questions(questions);
    }

    @POST
    @Path("/{userId}")
    public String chat(@RestPath Long userId, String message) {

        logger.infof("User: %s Asked: %s", userId, message);

        String userMessage = """
            User Id: %s
            
            Message of the user: %s
            """.formatted(userId, message);

        String response =  chatbotAiService.chat(userId, userMessage);
        logger.info(response);

        return response;
    }
}

// Made with Bob
