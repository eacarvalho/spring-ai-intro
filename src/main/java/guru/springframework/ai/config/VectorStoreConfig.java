package guru.springframework.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-enabled", havingValue = "true")
public class VectorStoreConfig {

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel, VectorStoreProperties vectorStoreProperties) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        // Get the vector store path
        String vectorStorePath = vectorStoreProperties.getVectorStorePath();

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
            vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
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

}