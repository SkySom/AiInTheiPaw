package io.sommers.aiintheipaw.awscore;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.sts.StsClient;

@Component
public class AWSBeans {
    @Bean
    public StsClient stsClient(AwsCredentialsProvider credentialsProvider, AwsRegionProvider regionProvider) {
        return StsClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(regionProvider.getRegion())
                .build();
    }

    @Bean
    public AccountProvider accountProvider(StsClient stsClient) {
        String account = stsClient.getCallerIdentity()
                .account();
        return () -> account;
    }
}
