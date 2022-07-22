package example.graphql.web.controller.gretting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class GreetingControllerTest {

  @Autowired
  private WebApplicationContext context;

  void greeting() {

    final WebTestClient client =
        MockMvcWebTestClient.bindToApplicationContext(context)
            .configureClient()
            .baseUrl("/graphql")
            .build();

    final HttpGraphQlTester tester = HttpGraphQlTester.create(client);

//    tester.documentName()
  }

}