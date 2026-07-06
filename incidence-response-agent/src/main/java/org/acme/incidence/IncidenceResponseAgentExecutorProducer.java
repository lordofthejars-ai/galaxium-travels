package org.acme.incidence;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.a2aproject.sdk.server.agentexecution.AgentExecutor;
import org.a2aproject.sdk.server.agentexecution.RequestContext;
import org.a2aproject.sdk.server.tasks.AgentEmitter;
import org.a2aproject.sdk.spec.*;
import org.acme.incidence.ai.IncidenceResponseService;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class IncidenceResponseAgentExecutorProducer {

    @Inject
    IncidenceResponseService incidenceResponseService;

    @Inject
    Logger logger;

    @Inject
    MeterRegistry registry;

    @Produces
    public AgentExecutor agentExecutor() {
        return new IncidenceResponseAgentExecutor(incidenceResponseService, logger, registry);
    }

    private static class IncidenceResponseAgentExecutor implements AgentExecutor {
        private final IncidenceResponseService incidenceResponseService;
        private final Logger logger;
        private final MeterRegistry registry;

        private IncidenceResponseAgentExecutor(IncidenceResponseService incidenceResponseService,
                                               Logger logger,
                                               MeterRegistry registry) {
            this.incidenceResponseService = incidenceResponseService;
            this.logger = logger;
            this.registry = registry;
        }

        @Override
        public void execute(RequestContext context, AgentEmitter agentEmitter) throws A2AError {

            registry.counter("agent.process.total").increment();

            if (context.getTask() == null) {
                agentEmitter.submit();
            }
            agentEmitter.startWork();

            // extract the text from the message
            String userMessage = extractTextFromMessage(context.getMessage());

            logger.infof("Received the following user message %s", userMessage);

            String response = incidenceResponseService.generate(userMessage);

            // create the response part
            TextPart responsePart = new TextPart(response);
            List<Part<?>> parts = List.of(responsePart);

            agentEmitter.addArtifact(parts);
            agentEmitter.complete();
        }

        /**
         * Extracts text content from a message.
         */
        private String extractTextFromMessage(Message message) {
            StringBuilder textBuilder = new StringBuilder();
            if (message.parts() != null) {
                for (Part<?> part : message.parts()) {
                    if (part instanceof TextPart textPart) {
                        textBuilder.append(textPart.text());
                    }
                }
            }
            return textBuilder.toString();
        }

        @Override
        public void cancel(RequestContext context, AgentEmitter agentEmitter) throws A2AError {
            Task task = context.getTask();

            if (task.status().state() == TaskState.TASK_STATE_CANCELED) {
                // task already cancelled
                throw new TaskNotCancelableError();
            }

            if (task.status().state() == TaskState.TASK_STATE_COMPLETED) {
                // task already completed
                throw new TaskNotCancelableError();
            }

            // cancel the task
            agentEmitter.cancel();
        }
    }

}
