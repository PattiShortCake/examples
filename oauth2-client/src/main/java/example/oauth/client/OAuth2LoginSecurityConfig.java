package example.oauth.client;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http
//        .antMatcher("/name").anonymous()
//    ;
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(c -> c.jwt())

//        .oauth2Login(Customizer.withDefaults())

        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    ;

    return http.build();
  }

  private static CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addAllowedOriginPattern("*");

    config.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
