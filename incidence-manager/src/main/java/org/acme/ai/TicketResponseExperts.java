package org.acme.ai;


import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.A2AClientAgent;
import dev.langchain4j.agentic.declarative.ActivationCondition;
import dev.langchain4j.agentic.declarative.ConditionalAgent;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.UserMessage;

public class TicketResponseExperts {

    public interface ExpertTicketResponseAgent {

        @SequenceAgent(
            outputKey = "response",
            subAgents = { SentimentAnalyzer.class, TicketResponseExpertsAgent.class })
        ResultWithAgenticScope<String> ask(String request);
    }

    public interface SentimentAnalyzer {

        @Agent(description = "Analyze the sentiment of text and Categorize", outputKey = "sentiment")
        @UserMessage("""
            Analyze sentiment of {request} only between positive, neutral, or negative sentiment
        """)
        Sentiment analyzeSentimentOf(String request);

    }

    public interface TicketResponseExpertsAgent {

        @ConditionalAgent(
            outputKey = "response",
            subAgents = { ResponseGeneratorAgent.class, NoAgent.class })
        String askExpert(String request);

        @ActivationCondition(ResponseGeneratorAgent.class)
        static boolean activatePositive(Sentiment sentiment) {
            return sentiment == Sentiment.POSITIVE;
        }

        @ActivationCondition(NoAgent.class)
        static boolean activateNeutral(Sentiment sentiment) {
            return sentiment == Sentiment.NEUTRAL || sentiment == Sentiment.NEGATIVE;
        }
    }

    public class NoAgent {
        @Agent(description = "Agent that does nothing", outputKey = "response")
        public static String no(String request) {
            return "";
        }
    }

    public interface ResponseGeneratorAgent {

        @A2AClientAgent(a2aServerUrl = "http://localhost:7777", outputKey = "response")
        String generateTicketResponse(String request);
    }
}

