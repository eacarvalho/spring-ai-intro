package guru.springframework.ai.bootstrap;

import guru.springframework.ai.config.VectorStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sfg.aiapp", name = "vector-store-local", havingValue = "false")
public class LoadVectorStore implements CommandLineRunner {

    private final VectorStore vectorStore;
    private final VectorStoreProperties vectorStoreProperties;

    public LoadVectorStore(VectorStore vectorStore, VectorStoreProperties vectorStoreProperties) {
        this.vectorStore = vectorStore;
        this.vectorStoreProperties = vectorStoreProperties;
    }

    @Override
    public void run(String... args) throws Exception {

        if (vectorStore.similaritySearch("Sportsman").isEmpty()){
            log.info("Loading documents into vector store");

            vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
                log.info("Loading document: {}", document.getFilename());

                TikaDocumentReader documentReader = new TikaDocumentReader(document);
                List<Document> documents = documentReader.get();

                TextSplitter textSplitter = new TokenTextSplitter();

                List<Document> splitDocuments = textSplitter.apply(documents);

                vectorStore.add(splitDocuments);
            });
        }

        log.info("Vector store loaded");
    }
}