package example.graphql.web.controller.graphql;

import example.graphql.web.controller.graphql.dto.Color;
import example.graphql.web.controller.graphql.dto.Person;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.relay.Connection;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {

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

  public Connection<Person> people(final DataFetchingEnvironment dataFetchingEnvironment) {
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

  public List<Person> peopleByFirstName(final String firstName) {
    return buildPeople()
        .filter(p -> StringUtils.equalsAnyIgnoreCase(p.getFirstName(), firstName))
        .collect(Collectors.toList());
  }

  public List<Person> peopleByLastName(final String lastName) {
    return buildPeople()
        .filter(p -> StringUtils.equalsAnyIgnoreCase(p.getLastName(), lastName))
        .collect(Collectors.toList());
  }
}
