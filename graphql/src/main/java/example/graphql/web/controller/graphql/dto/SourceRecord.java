package example.graphql.web.controller.graphql.dto;

public class SourceRecord {

  private String id;
  private String firstName;
  private String lastName;
  private Color color;

  public SourceRecord(String id, String firstName, String lastName, Color color) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.color = color;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
