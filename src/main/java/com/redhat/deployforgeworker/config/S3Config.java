package com.redhat.deployforgeworker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final  S3Properties s3Properties;

    @Bean
    public AwsCredentialsProvider getCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public Region getRegion() {
        String region = (s3Properties.region()==null || s3Properties.region().isBlank())
                ? "ap-south-1" : s3Properties.region();

        return Region.of(region);
    }

    @Bean
    public S3Client getS3Client() {
        return S3Client.builder()
                .credentialsProvider(getCredentialsProvider())
                .region(getRegion())
                .build();
    }



}

