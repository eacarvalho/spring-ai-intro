package com.spring.eac.ai.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "sfg.aiapp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationProperties {

    private String ninjasApiKey;

    @Builder.Default
    private boolean vectorStoreLocal = true;

    @Builder.Default
    private boolean vectorStoreEnabled = true;

    @Builder.Default
    private String vectorStorePath = "vector-store/vectorstore.json";

    @Builder.Default
    private List<Resource> documentsToLoad = new ArrayList<>();
}