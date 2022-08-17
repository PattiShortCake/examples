package example.oauth.client.web.controller;

import com.github.javafaker.Faker;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
class HelloController {

  @GetMapping
  public List<String> get() {

    final Faker faker = new Faker();

    return IntStream.range(0, 10)
        .mapToObj(i -> faker.funnyName().name())
        .collect(Collectors.toList());
  }
}
