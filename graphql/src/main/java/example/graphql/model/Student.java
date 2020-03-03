package example.graphql.model;

import lombok.Getter;

@Getter
public class Student {

  private String firstName;
  private String lastName;

  Student(final String firstName, final String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public static StudentBuilder builder() {
    return new StudentBuilder();
  }

  public static class StudentBuilder {

    private String firstName;
    private String lastName;

    StudentBuilder() {
    }

    public StudentBuilder firstName(final String firstName) {
      this.firstName = firstName;
      return this;
    }

    public StudentBuilder lastName(final String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Student build() {
      return new Student(firstName, lastName);
    }

    @Override
    public String toString() {
      return "Student.StudentBuilder(firstName=" + firstName + ", lastName=" + lastName + ")";
    }
  }
}
