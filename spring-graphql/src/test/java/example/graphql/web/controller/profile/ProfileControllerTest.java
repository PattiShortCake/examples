package example.graphql.web.controller.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ProfileControllerTest {

  @Autowired
  private WebApplicationContext context;

  private HttpGraphQlTester tester;

  @BeforeEach
  void beforeEach() {
    final WebTestClient client =
        MockMvcWebTestClient.bindToApplicationContext(context)
            .configureClient()
            .baseUrl("/graphql")
            .build();

    tester = HttpGraphQlTester.create(client);
  }

  @Test
  void profiles() {
    tester.documentName("profile")
        .execute()
        .path("profiles[*].sourceRecords[*].firstName")
        .entityList(String.class)
        .contains("SpongeBob");
  }

  @Test
  void profiles_givenPatrick_expectStarfish() {
    tester.documentName("profile-by-first-name")
        .variable("firstName", "Patrick")
        .execute()
        .path("findByFirstName[*].sourceRecords[*].lastName")
        .entityList(String.class)
        .contains("StarFish");
  }

  @Test
  void profiles_givenStarfish_expectPatrick() {
    tester.documentName("profile-by-last-name")
        .variable("lastName", "Starfish")
        .execute()
        .path("findByLastName[*].sourceRecords[*].firstName")
        .entityList(String.class)
        .contains("Patrick");
  }

}