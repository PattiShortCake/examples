package example.oauth.client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@Slf4j
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http

        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()
        )

        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)

        .oauth2Login(
            Customizer.withDefaults()
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
    config.addAllowedHeader(CorsConfiguration.ALL);
    config.addAllowedMethod(CorsConfiguration.ALL);
    config.addAllowedOriginPattern(CorsConfiguration.ALL);

    config.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public CustomAuthoritiesOpaqueTokenIntrospector opaqueTokenIntrospector(
      final OAuth2ResourceServerProperties properties) {
    return new CustomAuthoritiesOpaqueTokenIntrospector(properties);
  }

  @Bean
  public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
    // Changing ID Token Signature Verification
    final OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
    idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> SignatureAlgorithm.RS256);
    return idTokenDecoderFactory;
  }

  private static class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OpaqueTokenIntrospector delegate;

    public CustomAuthoritiesOpaqueTokenIntrospector(
        final OAuth2ResourceServerProperties properties) {
      final OAuth2ResourceServerProperties.Opaquetoken opaqueToken = properties.getOpaquetoken();
      delegate = new SpringOpaqueTokenIntrospector(opaqueToken.getIntrospectionUri(),
          opaqueToken.getClientId(),
          opaqueToken.getClientSecret());
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(final String token) {
      final OAuth2AuthenticatedPrincipal principal = delegate.introspect(token);
      return new DefaultOAuth2AuthenticatedPrincipal(
          principal.getName(), principal.getAttributes(), extractAuthorities(principal));
    }

    private Collection<GrantedAuthority> extractAuthorities(
        final OAuth2AuthenticatedPrincipal principal) {
      final List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
      log.info("These scopes[{}] would be mapped to GrantedAuthorities here", scopes);
      return scopes.stream()
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }
  }
}
