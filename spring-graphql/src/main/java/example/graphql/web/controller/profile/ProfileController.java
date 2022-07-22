package example.graphql.web.controller.profile;

import example.graphql.web.controller.profile.dto.Color;
import example.graphql.web.controller.profile.dto.Profile;
import example.graphql.web.controller.profile.dto.SourceRecord;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ProfileController {

  @QueryMapping
  public List<Profile> profiles() {

    final List<Profile> profiles = buildProfiles().collect(Collectors.toList());

    log.info("profiles[{}]", profiles);

    return profiles;
  }

  private Stream<Profile> buildProfiles() {
    return Stream.of(
        new Profile("1",
            Arrays.asList(new SourceRecord("A", "SpongeBob", "SquarePants", Color.YELLOW))),
        new Profile("2", Arrays.asList(new SourceRecord("B", "Patrick", "StarFish", Color.PINK))),
        new Profile("3", Arrays.asList(new SourceRecord("C", "Squidward", "Tentacles", Color.BLUE)))
    );
  }

  @QueryMapping
  public List<Profile> findByFirstName(@Argument final String firstName) {
    return buildProfiles()
        .filter(p -> p.getSourceRecords()
            .stream()
            .filter(sr -> sr.getFirstName().equalsIgnoreCase(firstName))
            .findAny().isPresent()
        )
        .peek(p -> log.info("profile[{}]", p))
        .collect(Collectors.toList());
  }

  @QueryMapping
  public List<Profile> findByLastName(@Argument final String lastName) {
    return buildProfiles()
        .filter(p -> p.getSourceRecords()
            .stream()
            .filter(sr -> sr.getLastName().equalsIgnoreCase(lastName))
            .findAny().isPresent()
        )
        .peek(p -> log.info("profile[{}]", p))
        .collect(Collectors.toList());
  }

}
