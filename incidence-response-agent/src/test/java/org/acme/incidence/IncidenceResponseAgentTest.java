package org.acme.incidence;

import io.quarkus.test.junit.QuarkusTest;
import org.a2aproject.sdk.A2A;
import org.a2aproject.sdk.client.*;
import org.a2aproject.sdk.client.config.ClientConfig;
import org.a2aproject.sdk.client.http.A2ACardResolver;
import org.a2aproject.sdk.client.transport.jsonrpc.JSONRPCTransport;
import org.a2aproject.sdk.client.transport.jsonrpc.JSONRPCTransportConfig;
import org.a2aproject.sdk.client.transport.spi.ClientTransportConfig;
import org.a2aproject.sdk.jsonrpc.common.json.JsonProcessingException;
import org.a2aproject.sdk.jsonrpc.common.json.JsonUtil;
import org.a2aproject.sdk.spec.AgentCard;
import org.a2aproject.sdk.spec.Message;
import org.a2aproject.sdk.spec.Part;
import org.a2aproject.sdk.spec.TextPart;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@QuarkusTest
public class IncidenceResponseAgentTest {

    String agentURl = "http://localhost:8081";
    String messageText = """
                Thank you very much for your trip, the experience of the trip to Mars was amazing.
                """;

    @Test
    public void shouldConnectToAgentUsingA2A() throws JsonProcessingException {
        System.out.println(agentURl);

        AgentCard publicAgentCard = A2ACardResolver.builder().baseUrl(agentURl).build().getAgentCard();
        System.out.println("Successfully fetched public agent card:");
        System.out.println(JsonUtil.toJson(publicAgentCard));
        System.out.println("Using public agent card for client initialization (default).");
        AgentCard finalAgentCard = publicAgentCard;

        final CompletableFuture<String> messageResponse = new CompletableFuture<>();

        // Create consumers list for handling client events
        List<BiConsumer<ClientEvent, AgentCard>> consumers = new ArrayList<>();
        consumers.add((event, agentCard) -> {
            if (event instanceof MessageEvent messageEvent) {
                System.out.println("Message Event");
                Message responseMessage = messageEvent.getMessage();
                StringBuilder textBuilder = new StringBuilder();
                if (responseMessage.parts() != null) {
                    for (Part<?> part : responseMessage.parts()) {
                        if (part instanceof TextPart textPart) {
                            textBuilder.append(textPart.text());
                        }
                    }
                }
                System.out.println("Completed");
                messageResponse.complete(textBuilder.toString());
            }
        });

        // Create error handler for streaming errors
        Consumer<Throwable> streamingErrorHandler = (error) -> {
            System.err.println("Streaming error occurred: " + error.getMessage());
            error.printStackTrace();
            messageResponse.completeExceptionally(error);
        };

        ClientConfig clientConfig = new ClientConfig.Builder()
                .setAcceptedOutputModes(List.of("text"))
                .build();
        Client client = Client
                .builder(finalAgentCard)
                .clientConfig(clientConfig)
                .withTransport(JSONRPCTransport.class, new JSONRPCTransportConfig())
                .addConsumers(consumers)
                .streamingErrorHandler(streamingErrorHandler)
                .build();

        Message message = A2A.toUserMessage(messageText); // the message ID will be automatically generated for you
        try {
            System.out.println("Sending message: " + messageText);
            client.sendMessage(message);
            System.out.println("Message sent successfully. Responses will be handled by the configured consumers.");

            String responseText = messageResponse.get();
            System.out.println("Response: " + responseText);
        } catch (Exception e) {
            System.err.println("Failed to get response: " + e.getMessage());
        }

    }

}
