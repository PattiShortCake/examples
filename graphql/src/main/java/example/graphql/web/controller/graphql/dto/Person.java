package example.graphql.web.controller.graphql.dto;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Getter
@ToString
public class Person {

  @NonNull
  private final String id;
  @NonNull
  private final String firstName;
  @NonNull
  private final String lastName;
  @NonNull
  private final Color favoriteColor;

  @Singular
  @NonNull
  private List<Person> friends;

}
