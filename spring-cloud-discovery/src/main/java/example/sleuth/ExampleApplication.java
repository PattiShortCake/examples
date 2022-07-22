package example.sleuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ExampleApplication {

  public static void main(final String[] args) {
    SpringApplication.run(ExampleApplication.class);
  }

  @GetMapping("/hello")
  public String home() {
    return "hello";
  }
}
