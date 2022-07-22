package example.graphql.web.controller.profile.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Profile {

  private final String id;
  private final List<SourceRecord> sourceRecords;

}
