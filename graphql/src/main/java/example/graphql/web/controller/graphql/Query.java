package example.graphql.web.controller.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import example.graphql.web.controller.graphql.dto.Color;
import example.graphql.web.controller.graphql.dto.Profile;
import example.graphql.web.controller.graphql.dto.SourceRecord;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {

  public List<Profile> profiles() {
    return buildProfiles().collect(Collectors.toList());
  }

  public List<Profile> findByFirstName(String firstName) {
    return buildProfiles()
        .filter(p -> p.getSourceRecords()
            .stream()
            .filter(sr -> sr.getFirstName().equalsIgnoreCase(firstName))
            .findAny().isPresent()
        )
        .collect(Collectors.toList());
  }

  public List<Profile> findByLastName(String lastName) {
    return buildProfiles()
        .filter(p -> p.getSourceRecords()
            .stream()
            .filter(sr -> sr.getLastName().equalsIgnoreCase(lastName))
            .findAny().isPresent()
        )
        .collect(Collectors.toList());
  }

  private Stream<Profile> buildProfiles() {
    return Stream.of(
      new Profile("1",new SourceRecord("A", "SpongeBob", "SquarePants", Color.YELLOW)),
        new Profile("2",new SourceRecord("B", "Patrick", "StarFish", Color.PINK)),
        new Profile("3", new SourceRecord("C", "Squidward", "Tentacles", Color.BLUE))
    );
  }
}
