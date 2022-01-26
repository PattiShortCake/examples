package example.sleuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@RestController
public class ExampleApplication {

  private final ExampleService service;

  public ExampleApplication(final ExampleService service) {
    this.service = service;
  }

  public static void main(final String[] args) {
      SpringApplication.run(ExampleApplication.class);
  }

  @GetMapping("/hello")
  public String home() {
    log.info("Handling home");
    return service.hello();
  }
}
