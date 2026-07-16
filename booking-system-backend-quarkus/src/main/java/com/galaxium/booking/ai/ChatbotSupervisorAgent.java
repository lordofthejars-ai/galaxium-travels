package com.galaxium.booking.ai;

import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;


public interface ChatbotSupervisorAgent {

    //@InputGuardrails(BookingQuestionsInputGuardrail.class)
    @SupervisorAgent(
        responseStrategy = SupervisorResponseStrategy.SUMMARY,
        subAgents = { BookingAgent.class, TimeAgent.class, CancellationAgent.class})
    String chat(String request);
}
