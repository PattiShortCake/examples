package example.graphql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3ClientConfiguration;
import software.amazon.awssdk.transfer.s3.SizeConstant;

@Configuration
public class AwsConfiguration {

  @Bean
  public DynamoDbAsyncClient dynamoDbAsyncClient() {
    return DynamoDbAsyncClient.builder()
        .credentialsProvider(awsCredentialsProvider())
        .httpClient(sdkAsyncHttpClient())
        .build();
  }

  @Bean
  public AwsCredentialsProvider awsCredentialsProvider() {
    return DefaultCredentialsProvider.create();
  }

  private SdkAsyncHttpClient sdkAsyncHttpClient() {
    return NettyNioAsyncHttpClient.builder()
        .maxConcurrency(100)
        .maxPendingConnectionAcquires(10_000)
        .build();
  }

  @Bean
  public S3ClientConfiguration s3ClientConfiguration(
      final AwsCredentialsProvider credentialsProvider) {
    return S3ClientConfiguration.builder()
//        .region(region)
        .minimumPartSizeInBytes(10 * SizeConstant.MB)
        .targetThroughputInGbps(20.0)
        .credentialsProvider(credentialsProvider)
        .maxConcurrency(100)
        .build();
  }

  @Bean
  public S3Client s3Client(final AwsCredentialsProvider credentialsProvider) {

    return S3Client.builder()
        .credentialsProvider(credentialsProvider)
        .httpClient(sdkHttpClient())
        .build();
  }

  private SdkHttpClient sdkHttpClient() {
    return ApacheHttpClient.builder()
        .maxConnections(100)
        .build();
  }

  @Bean
  public S3AsyncClient s3AsyncClient(final AwsCredentialsProvider credentialsProvider) {
    return S3AsyncClient.builder()
        .credentialsProvider(credentialsProvider)
        .httpClient(sdkAsyncHttpClient())
        .build();
  }

}