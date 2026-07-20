package com.galaxium.booking.ai;

import com.galaxium.booking.rag.TermsOfUsageActions;
import com.galaxium.booking.service.BookingSearchTool;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ToolBox;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;

public interface CancellationAgent {

    @SystemMessage("""

                        You are a customer support agent of a Galaxium Travel agency for cancelling bookings.
                        You are friendly, polite and concise.
                
                            Rules that you must obey:
                
                            1. Before Canceling the booking,
                            you must make sure you know the customer's booking number to cancel, and that is possible to cancel a booking according to terms of usage.         
                
                            2. To cancel a booking use the provided tool. After cancelling the booking, always say "We hope to welcome you back again soon".
                
                            3. You should answer only questions related to the business of Galaxium Travel.
                            When asked about something not relevant to the company business,
                            apologize and say that you cannot help with that.
                
                            Today is {{current_date}}.
                """)
    @Agent(description = "You are a customer support agent of a Galaxium Travel agency for cancelling bookings")
    @ToolBox(BookingSearchTool.class)
    String cancel(@MemoryId Long userId, @UserMessage String message);

    @RetrievalAugmentorSupplier
    static RetrievalAugmentor rag(@CdiBean TermsOfUsageActions termsOfUsageActions) {
        return termsOfUsageActions.get();
    }

}
