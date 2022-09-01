package example.oauth2.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OAuth2GatewayApplication {

  public static void main(final String[] args) {
    SpringApplication.run(OAuth2GatewayApplication.class, args);
  }

}
