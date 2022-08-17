package example.graphql.web.controller.profile;

import example.graphql.web.controller.profile.dto.Color;
import example.graphql.web.controller.profile.dto.Person;
import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class PeopleController {

  public static final Person PATRICK = Person.builder().id("2").firstName("Patrick")
      .lastName("StarFish").favoriteColor(Color.PINK)
      .build();
  public static final Person SQUIDWARD = Person.builder().id("3").firstName("Squidward")
      .lastName("Tentacles")
      .favoriteColor(Color.BLUE).build();
  public static final Person SPONGEBOB = Person.builder().id("1").firstName("SpongeBob")
      .lastName("SquarePants")
      .favoriteColor(Color.YELLOW)
      .friend(PATRICK)
      .friend(SQUIDWARD)
      .build();

  @QueryMapping
  public Connection<Person> people(
      @Argument final Optional<String> after,
      @Argument final Optional<Integer> first,
      final DataFetchingEnvironment dataFetchingEnvironment) {
    final List<Person> people = buildPeople().collect(Collectors.toList());
    return new SimpleListConnection<>(people).get(dataFetchingEnvironment);
  }

  private Stream<Person> buildPeople() {
    return Stream.of(
        SPONGEBOB,
        PATRICK,
        SQUIDWARD
    );
  }

  @QueryMapping
  public List<Person> peopleByFirstName(@Argument final String firstName) {
    return buildPeople()
        .filter(p -> StringUtils.equalsAnyIgnoreCase(p.getFirstName(), firstName))
        .collect(Collectors.toList());
  }

  @QueryMapping
  public List<Person> peopleByLastName(@Argument final String lastName) {
    return buildPeople()
        .filter(p -> StringUtils.equalsAnyIgnoreCase(p.getLastName(), lastName))
        .collect(Collectors.toList());
  }

}
