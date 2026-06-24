package org.acme.incidence.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.function.Supplier;

@ApplicationScoped
public class SecuredChatModelSupplier implements Supplier<ChatModel> {

    @Inject
    Logger logger;

    @ConfigProperty(name = "key")
    String apiKey;

    @Override
    public ChatModel get() {

        logger.infof("Getting chat model with '%s' key", apiKey);

        ChatModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .modelName("gpt-4o-mini")
                .build();

        return model;
    }
}
