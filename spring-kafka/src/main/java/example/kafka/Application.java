package example.kafka;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.util.backoff.ExponentialBackOff;

@SpringBootApplication
@Slf4j
public class Application {

  public static final String TOPIC_NAME = "topic1";
  @Autowired
  private Api api;

  public static void main(final String[] args) {
    SpringApplication.run(Application.class);
  }

  @Bean
  public NewTopic topic() {
    return TopicBuilder.name(TOPIC_NAME)
        .partitions(10)
        .replicas(1)
        .build();
  }

  @Bean
  public DefaultErrorHandler errorHandler() {
    final ExponentialBackOff backOff = new ExponentialBackOff();
    return new DefaultErrorHandler(new MyConsumerRecordRecoverer(), backOff);
  }

  @Bean
  public RecordInterceptor<String, String> commonRecordInterceptor() {
    return new RecordInterceptor<>() {

      @Override
      public ConsumerRecord<String, String> intercept(final ConsumerRecord<String, String> record) {
        return record;
      }

      @Override
      public void failure(final ConsumerRecord<String, String> record, final Exception exception,
          final Consumer<String, String> consumer) {
        log.error("Error processing record topic[{}] key[{}]. Reason: {}", record.topic(),
            record.key(), exception.getMessage());
      }
    };
  }

  @KafkaListener(id = "myId", topics = TOPIC_NAME)
  public void listen(final List<String> records) {
    log.info("messages[{}]", records);

    for (final String record : records) {
      api.doSomething(record);
    }

    throw new RuntimeException("something bad happen");
  }

  static interface Api {

    void doSomething(String value);
  }

  static class MyConsumerRecordRecoverer implements ConsumerRecordRecoverer {

    @Override
    public void accept(final ConsumerRecord<?, ?> consumerRecord, final Exception e) {
      log.info("Something bad happen with message[{}]", consumerRecord.value());
    }
  }
//  @Bean
//  public ApplicationRunner runner(final KafkaTemplate<String, String> template) {
//    return args -> {
//      template.send("topic1", "test");
//    };
//  }
}
