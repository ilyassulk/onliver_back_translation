package ru.onliver.translation_streamer.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public CommandLineRunner createMinioBucket(MinioClient minioClient) {
        return args -> {
            try {
                boolean isExist = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(bucketName)
                                .build()
                );

                if (!isExist) {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucketName)
                                    .build()
                    );
                    System.out.println("Minio bucket created: " + bucketName);
                }
            } catch (Exception e) {
                System.err.println("Error creating Minio bucket: " + e.getMessage());
                throw new RuntimeException("Failed to initialize Minio bucket", e);
            }
        };
    }
}