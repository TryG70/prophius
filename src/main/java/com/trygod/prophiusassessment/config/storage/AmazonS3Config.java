package com.trygod.prophiusassessment.config.storage;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${aws.storage.access-key}")
    private String AWS_ACCESS_KEY;

    @Value("${aws.storage.secret-key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.storage.region}")
    private String AWS_STORAGE_REGION;

    @Bean
    public AmazonS3 s3() {

        AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);

        return AmazonS3ClientBuilder.standard().withRegion(AWS_STORAGE_REGION).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
