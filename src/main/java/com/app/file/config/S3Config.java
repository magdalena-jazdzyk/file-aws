package com.app.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {


    @Value("${aws.region}")
    private String awsRegion;
    @Bean
    public S3Client client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
              //  .endpointOverride(URI.create("https://s3.us-west-1.amazonaws.com"))
              //  .forcePathStyle(true)
                .build();
    }


    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
//                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
