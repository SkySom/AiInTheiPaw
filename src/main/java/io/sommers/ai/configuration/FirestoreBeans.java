package io.sommers.ai.configuration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FirestoreBeans {
    @Bean
    @ConditionalOnProperty(value = "spring.cloud.gcp.firestore.emulator.enabled", havingValue = "true")
    public CredentialsProvider googleCredentials() {
        return NoCredentialsProvider.create();
    }
}
