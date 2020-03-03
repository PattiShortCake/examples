package example.graphql.model;

public class StudentService {


  public Student buildStudent() {
    return Student.builder()
        .firstName("first")
        .lastName("last")
        .build()
        ;

  }
}
