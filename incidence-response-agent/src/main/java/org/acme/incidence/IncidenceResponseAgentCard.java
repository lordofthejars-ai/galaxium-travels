package org.acme.incidence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.a2aproject.sdk.server.PublicAgentCard;
import org.a2aproject.sdk.spec.AgentCapabilities;
import org.a2aproject.sdk.spec.AgentCard;
import org.a2aproject.sdk.spec.AgentInterface;
import org.a2aproject.sdk.spec.AgentSkill;
import org.a2aproject.sdk.spec.TransportProtocol;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class IncidenceResponseAgentCard {

    @ConfigProperty(name = "agent.url")
    String agentUrl;

    @Inject
    Logger logger;

    @Produces
    @PublicAgentCard
    public AgentCard agentCard() {

        logger.infof("Creating AgentCard %s", agentUrl);

        return AgentCard.builder()
                .name("Email Creator")
                .description("Creates a response of an incident")
                .supportedInterfaces(List.of(
                        new AgentInterface(TransportProtocol.JSONRPC.asString(), agentUrl)))
                .version("1.0.0")
                .capabilities(AgentCapabilities.builder()
                        .streaming(true)
                        .pushNotifications(false)
                        .build())
                .defaultInputModes(Collections.singletonList("text"))
                .defaultOutputModes(Collections.singletonList("text"))
                .skills(Collections.singletonList(AgentSkill.builder()
                        .id("incidence_response_creator")
                        .name("Generates an incidence response")
                        .description("Generates response for a given incidence")
                        .tags(Collections.singletonList("incidence response generator"))
                        .examples(List.of("generate a response for the following incidence"))
                        .build()))
                .build();
    }
}
