package example.oauth.client;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http
        .antMatcher("/name").anonymous()
    ;

//        .authorizeHttpRequests(authorize -> authorize
//            .anyRequest().authenticated()
//        )
//        .oauth2Login(Customizer.withDefaults())
    return http.build();
  }

}
