package guru.springframework.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Created by jt, Spring Framework Guru.
 */
@Configuration
@ConfigurationProperties(prefix = "sfg.aiapp")
@Setter
@Getter
public class VectorStoreProperties {

    private String vectorStorePath;
    private List<Resource> documentsToLoad;
}