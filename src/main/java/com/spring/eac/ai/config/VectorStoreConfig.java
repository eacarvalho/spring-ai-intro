package com.spring.eac.ai.config;

import com.spring.eac.ai.property.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-local", havingValue = "true")
public class VectorStoreConfig {

    @Bean
    @ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-enabled", havingValue = "true")
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel, ApplicationProperties applicationProperties) {
        return getSimpleVectorStore(embeddingModel, applicationProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-enabled", havingValue = "false", matchIfMissing = true)
    public SimpleVectorStore fallbackVectorStore(EmbeddingModel embeddingModel) {
        return mockVectorStore(embeddingModel);
    }

    private static SimpleVectorStore getSimpleVectorStore(EmbeddingModel embeddingModel, ApplicationProperties applicationProperties) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // Get the vector store path
        String vectorStorePath = applicationProperties.getVectorStorePath();

        // Create parent directories if they don't exist
        try {
            Path directory = Paths.get(vectorStorePath).getParent();
            if (directory != null) {
                Files.createDirectories(directory);
                log.debug("Created directory: {}", directory);
            }
        } catch (IOException e) {
            log.error("Failed to create directory for vector store: {}", e.getMessage());
            throw new RuntimeException("Failed to create directory for vector store", e);
        }

        File vectorStoreFile = new File(vectorStorePath);

        if (vectorStoreFile.exists()) {
            store.load(vectorStoreFile);
        } else {
            log.debug("Loading documents into vector store");
            applicationProperties.getDocumentsToLoad().forEach(document -> {
                log.debug("Loading document: " + document.getFilename());
                TikaDocumentReader documentReader = new TikaDocumentReader(document);
                List<Document> docs = documentReader.get();
                // TextSplitter textSplitter = new TokenTextSplitter();
                TextSplitter textSplitter = TokenTextSplitter.builder()
                        .withChunkSize(7000)
                        .withMinChunkSizeChars(500)
                        .withMinChunkLengthToEmbed(5)
                        .withMaxNumChunks(10000)
                        .withKeepSeparator(true)
                        .build();

                List<Document> splitDocs = textSplitter.apply(docs);
                store.add(splitDocs);

//                TokenCountBatchingStrategy batchingStrategy = new TokenCountBatchingStrategy();
//                batchingStrategy.batch(splitDocs).forEach(documents -> {
//                    log.info("Storing {} documents in vector store:", documents.size());
//                    store.add(documents);
//                });
            });

            store.save(vectorStoreFile);
        }

        return store;
    }

    private static SimpleVectorStore mockVectorStore(EmbeddingModel embeddingModel) {
        log.warn("****************************************************************************************************************");
        log.warn("VECTOR STORE IS DISABLED! Enable the full vector store with sfg.aiapp.vector-store-enabled=true in application.yaml");
        log.warn("****************************************************************************************************************");

        return new SimpleVectorStore(SimpleVectorStore.builder(embeddingModel)) {
            @Override
            public List<Document> similaritySearch(SearchRequest request) {
                log.warn("****************************************************************************************************************");
                log.warn("VECTOR STORE IS DISABLED! No documents will be returned. RAG functionality will not work properly.");
                log.warn("****************************************************************************************************************");
                return Collections.emptyList();
            }

            @Override
            public void add(List<Document> documents) {
                // Do nothing
            }

            @Override
            public void save(File file) {
                // Do nothing
            }

            @Override
            public void load(File file) {
                // Do nothing
            }

            @Override
            public void delete(String id) {
                // Do nothing
            }

            @Override
            public void delete(List<String> ids) {
                // Do nothing
            }
        };
    }

}