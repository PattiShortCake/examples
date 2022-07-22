package example.graphql.web.controller.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceRecord {

  private final String id;
  private final String firstName;
  private final String lastName;
  private final Color color;

}
