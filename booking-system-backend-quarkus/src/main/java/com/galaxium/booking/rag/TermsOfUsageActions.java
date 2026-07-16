package com.galaxium.booking.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.function.Supplier;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@ApplicationScoped
public class TermsOfUsageActions implements Supplier<RetrievalAugmentor> {

    @Inject
    AllMiniLmL6V2EmbeddingModel embeddingModel;

    EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

    @Inject
    Logger logger;

    @Startup
    public void ingest() {

        logger.infof("Ingesting %s document", "terms-of-use.txt");

        Document termsOfUsage = ClassPathDocumentLoader
            .loadDocument("/terms-of-use.txt", new TextDocumentParser());

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
            .embeddingStore(store)
            .embeddingModel(embeddingModel)
            .documentSplitter(recursive(100, 0, tokenCountEstimator()))
            .build();

        ingestor.ingest(termsOfUsage);

    }

    TokenCountEstimator tokenCountEstimator() {
        return new OpenAiTokenCountEstimator(GPT_4_O_MINI);
    }

    @Override
    public RetrievalAugmentor get() {
        EmbeddingStoreContentRetriever retriever =
            EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .maxResults(2)
                .build();
        return DefaultRetrievalAugmentor.builder()
            .contentRetriever(retriever)
            .build();
    }
}
