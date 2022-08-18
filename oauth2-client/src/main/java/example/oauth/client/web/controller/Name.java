package example.oauth.client.web.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@AllArgsConstructor
public class Name {

  private final long id;
  private final String firstName;
  private final String lastName;

}
