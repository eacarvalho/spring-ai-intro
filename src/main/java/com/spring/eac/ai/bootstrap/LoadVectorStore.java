package com.spring.eac.ai.bootstrap;

import com.spring.eac.ai.property.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-local", havingValue = "false")
public class LoadVectorStore implements CommandLineRunner {

    private final VectorStore vectorStore;
    private final ApplicationProperties applicationProperties;

    public LoadVectorStore(VectorStore vectorStore, ApplicationProperties applicationProperties) {
        this.vectorStore = vectorStore;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void run(String... args) throws Exception {

        if (vectorStore.similaritySearch("Sportsman").isEmpty()) {
            log.info("Loading documents into vector store");

            applicationProperties.getDocumentsToLoad().forEach(document -> {
                log.info("Loading document: {}", document.getFilename());

                TikaDocumentReader documentReader = new TikaDocumentReader(document);
                List<Document> documents = documentReader.get();

                TextSplitter textSplitter = TokenTextSplitter.builder()
                        .withChunkSize(500)
                        .withMinChunkSizeChars(100)
                        .withMinChunkLengthToEmbed(5)
                        .withMaxNumChunks(10000)
                        .withKeepSeparator(true)
                        .build();

                // Split documents into chunks
                List<Document> chunks = new ArrayList<>();
                for (Document doc : documents) {
                    List<Document> documentChunks = textSplitter.split(doc);

                    // Add metadata to each chunk
                    documentChunks.forEach(chunk -> {
                        Map<String, Object> chunkMetadata = new HashMap<>(chunk.getMetadata());
                        chunkMetadata.put("source_file", document.getFilename());
                        chunkMetadata.put("ingestion_timestamp", Instant.now().toString());
                        chunk.getMetadata().putAll(chunkMetadata);
                    });

                    chunks.addAll(documentChunks);
                }

                vectorStore.add(chunks);
            });
        }

        log.info("Vector store loaded");
    }
}