package org.acme;

import org.acme.ai.SentimentAnalysis;

public record TiquetResponse(String draft, SentimentAnalysis sentiment) {
}
