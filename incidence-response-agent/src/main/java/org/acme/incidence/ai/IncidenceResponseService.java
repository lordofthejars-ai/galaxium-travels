package org.acme.incidence.ai;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(chatLanguageModelSupplier = SecuredChatModelSupplier.class)
public interface IncidenceResponseService {

    @SystemMessage("""
            You are an expert on understanding comments of given by users and generate a response 
            either if they are positive comments to thankful him, or if it is a negative comment (but no question done) to 
            respond with a message telling him that you understand the situation and trying to calm down.
            
            Messages are for topics related for interplanetary travel agency flights. 
                    
            Always be polite, even though the tone of the message is unpolite.  
    """)
    String generate(String incidence);

}
