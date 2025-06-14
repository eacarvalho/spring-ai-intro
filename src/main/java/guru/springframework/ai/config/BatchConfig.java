
package guru.springframework.ai.config;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    Job pdfIngestJob(JobRepository jobRepository, Step ingestStep) {
        return new JobBuilder("pdfIngest", jobRepository)
                .start(ingestStep)
                .build();
    }
//
//    @Bean
//    Step ingestStep(JobRepository jobRepository,
//                    PlatformTransactionManager transactionManager,
//                    VectorStore store) {
//        return new StepBuilder("ingestStep", jobRepository)
//                .<Resource, List<Document>>chunk(1, transactionManager)
//                .reader(new PdfItemReader())
//                .processor(new ChunkProcessor())
//                .writer(new ChunkWriter(store))
//                .build();
//    }
}