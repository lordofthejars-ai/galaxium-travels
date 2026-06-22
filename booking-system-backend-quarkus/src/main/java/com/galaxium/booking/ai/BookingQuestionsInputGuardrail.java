package com.galaxium.booking.ai;

import dev.langchain4j.classification.ScoredLabel;
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

    @Inject
    ChatMessagesClassification classification;

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText();
        boolean isValid = classification.validConversation(text);
        logger.infof("Guarding input result %s", isValid);

        return isValid ? success() :
            failure("It is not a valid question. " +
                "Try again with a question regarding your booking");
    }
}
