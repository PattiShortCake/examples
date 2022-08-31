package example.oauth2.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@EnableWebFluxSecurity
@Configuration
@Slf4j
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityWebFilterChain filterChain(final ServerHttpSecurity http) {
    http

        .authorizeExchange(authorize -> authorize
            .anyExchange().authenticated()
        )

//        .oauth2ResourceServer(spec -> spec.opaqueToken())
//        .oauth2ResourceServer(spec -> spec.jwt())

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

//        .oauth2Client(Customizer.withDefaults())

//        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        .csrf(c -> c.disable())
    ;

    log.info("Configured " + OAuth2LoginSecurityConfig.class);
    return http.build();
  }

  //  @Bean
  public CorsWebFilter corsWebFilter() {

    final CorsConfiguration config = new CorsConfiguration();
    config.addAllowedHeader(CorsConfiguration.ALL);
    config.addAllowedMethod(CorsConfiguration.ALL);
    config.addAllowedOriginPattern(CorsConfiguration.ALL);
    config.setMaxAge(3600L);

    //config.setAllowCredentials(true);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
  }

  //  @Bean
//  public CustomAuthoritiesOpaqueTokenIntrospector opaqueTokenIntrospector(
//      final OAuth2ResourceServerProperties properties) {
//    return new CustomAuthoritiesOpaqueTokenIntrospector(properties);
//  }

  @Bean
  public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
    // Changing ID Token Signature Verification
    final OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
    idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> SignatureAlgorithm.RS256);
    return idTokenDecoderFactory;
  }

//  private static class CustomAuthoritiesOpaqueTokenIntrospector implements
//      ReactiveOpaqueTokenIntrospector {
//
//    private final ReactiveOpaqueTokenIntrospector delegate;
//
//    public CustomAuthoritiesOpaqueTokenIntrospector(
//        final OAuth2ResourceServerProperties properties) {
//      final OAuth2ResourceServerProperties.Opaquetoken opaqueToken = properties.getOpaquetoken();
//      delegate = new SpringReactiveOpaqueTokenIntrospector(opaqueToken.getIntrospectionUri(),
//          opaqueToken.getClientId(),
//          opaqueToken.getClientSecret());
//    }
//
//    @Override
//    public Mono<OAuth2AuthenticatedPrincipal> introspect(final String token) {
//      return delegate.introspect(token)
//          .map(principal -> new DefaultOAuth2AuthenticatedPrincipal(
//              principal.getName(), principal.getAttributes(), extractAuthorities(principal)))
//          ;
//    }
//
//    private Collection<GrantedAuthority> extractAuthorities(
//        final OAuth2AuthenticatedPrincipal principal) {
//      final List<String> scopes = principal.getAttribute(OAuth2TokenIntrospectionClaimNames.SCOPE);
//      log.info("These scopes[{}] would be mapped to GrantedAuthorities here", scopes);
//      return scopes.stream()
//          .map(SimpleGrantedAuthority::new)
//          .collect(Collectors.toList());
//    }
//  }
}
