package example.oauth.client.web.controller;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
class NameGraphqlController {

  @QueryMapping
  public List<Name> names() {

    final Faker faker = new Faker();

    return IntStream.range(0, 10)
        .mapToObj(i -> {
              final com.github.javafaker.Name fakeName = faker.name();
              return new Name(i, fakeName.firstName(), fakeName.lastName());
            }
        )
        .collect(Collectors.toList());
  }
}

// A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and
// removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric
// characters include letters and numbers.
//
// Given a string s, return true if it is a palindrome, or false otherwise.
