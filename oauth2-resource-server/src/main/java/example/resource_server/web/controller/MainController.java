package example.resource_server.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//import org.springframework.security.oauth2.jwt.Jwt;

@RestController
public class MainController {
//  @GetMapping("/")
//  public String index(@AuthenticationPrincipal Jwt jwt) {
//    return String.format("Hello, %s!", jwt.getSubject());
//  }

  @GetMapping("/message")
  public String message() {
    return "secret message";
  }
}
