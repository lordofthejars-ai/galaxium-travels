package com.galaxium.booking.ai;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BookingQuestionsInputGuardrail implements InputGuardrail {

    @Inject
    Logger logger;

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText();
        logger.info("**** Guardrail: " + text);
        return success();
    }
}
