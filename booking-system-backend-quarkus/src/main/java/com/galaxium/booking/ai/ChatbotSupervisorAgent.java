package com.galaxium.booking.ai;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.service.guardrail.InputGuardrails;

public interface ChatbotSupervisorAgent {

    @InputGuardrails(BookingQuestionsInputGuardrail.class)
    @SupervisorAgent(
        responseStrategy = SupervisorResponseStrategy.SUMMARY,
        subAgents = { BookingAgent.class, TimeAgent.class})
    String chat(String request);
}
