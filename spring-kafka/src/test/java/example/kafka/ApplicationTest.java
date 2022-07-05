package example.kafka;

import static example.kafka.Application.TOPIC_NAME;

import example.kafka.Application.Api;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(topics = "topic1", bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class ApplicationTest {

  @Autowired
  private KafkaTemplate<String, String> template;

  @Captor
  private ArgumentCaptor<String> stringCaptor;

  @MockBean
  private Api mockApi;

  @Test
  void test() {
    // Given
    final String message = "hello world";
    BDDMockito.doThrow(new RuntimeException("error")).when(mockApi).doSomething(message);

    // When
    for (int i = 0; i < 1_000_000; i++) {
      template.send(TOPIC_NAME, message + "-" + i);
    }

    // Then
    Mockito.verify(mockApi, Mockito.timeout(5_000L)).doSomething(stringCaptor.capture());
    Assertions.assertThat(stringCaptor.getValue()).isEqualTo(message);
  }

}