package com.barowoori.foodpinbackend.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.barowoori.foodpinbackend.config.factory.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:secrets/aws-sqs-config.yml", factory = YamlPropertySourceFactory.class)
public class SqsConfig {
    @Value("${sqs.credentials.access-key:}")
    private String accessKey;
    @Value("${sqs.credentials.secret-key:}")
    private String secretKey;
    @Value("${sqs.region:ap-northeast-2}")
    private String region;

    @Bean
    public AmazonSQS amazonSQS() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSQSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
