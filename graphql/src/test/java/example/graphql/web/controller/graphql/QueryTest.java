package example.graphql.web.controller.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@GraphQLTest(
    profiles = "integration-test"
)
public class QueryTest {

  @Autowired
  private GraphQLTestTemplate graphQLTestTemplate;

  @Test
  void profiles() throws IOException {
    final GraphQLResponse response = graphQLTestTemplate.postForResource("get-with-post.graphql");
    assertNotNull(response);
    assertTrue(response.isOk());
    assertEquals("SpongeBob", response.get("$.data.profiles[0].sourceRecords[0].firstName"));
  }
}
