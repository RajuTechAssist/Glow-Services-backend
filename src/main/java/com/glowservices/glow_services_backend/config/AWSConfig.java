package com.glowservices.glow_services_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AWSConfig {
    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region)) // Change to your region (e.g., US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // ✅ NEW: AWS SNS Client for SMS
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}