package example.oauth.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http
//        .antMatcher("/graphql")
//        .antMatcher("/graphql**")
//        .antMatcher("/graphql/**")
//        .anonymous()
//
//        .and()

        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(c -> c.jwt())

        .oauth2Login(Customizer.withDefaults()
//            .userInfoEndpoint(userInfo -> userInfo
//                .userAuthoritiesMapper(userAuthoritiesMapper())
//
//            )
            // For OAuth 2.0 UserService
//            .userInfoEndpoint(userInfo -> userInfo
//                .userService(this.oauth2UserService()
//            )
            // For OpenID Connect 1.0 UserService
//            .userInfoEndpoint(userInfo -> userInfo
//                .oidcUserService(this.oidcUserService()
//            )
        )

        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        .csrf(c -> c.disable())
    ;

    return http.build();
  }

  private CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.addAllowedOriginPattern("*");

    config.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
    // Changing ID Token Signature Verification
    final OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
    idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> SignatureAlgorithm.RS256);
    return idTokenDecoderFactory;
  }
}
