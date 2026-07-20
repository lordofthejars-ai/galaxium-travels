package com.galaxium.booking.ai;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.service.MemoryId;


public interface ChatbotSupervisorAgent extends AgenticScopeAccess {

    //@InputGuardrails(BookingQuestionsInputGuardrail.class)
    @SupervisorAgent(
        responseStrategy = SupervisorResponseStrategy.SUMMARY,
        subAgents = { BookingAgent.class, TimeAgent.class,
                CancellationAgent.class, HiveMindAgent.class})
    String chat(@MemoryId Long userId,  String request);
}
