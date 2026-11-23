package com.ics.backend.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            SecurityProperties securityProperties) throws Exception {

        JwtIssuerAuthenticationManagerResolver issuerAuthenticationManagerResolver = JwtIssuerAuthenticationManagerResolver
                .fromTrustedIssuers(securityProperties.getTrustedIssuers());

        AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver = request -> {
            AuthenticationManager delegate = issuerAuthenticationManagerResolver.resolve(request);
            return authentication -> {
                Authentication authenticated = delegate.authenticate(authentication);
                if (authenticated instanceof JwtAuthenticationToken jwtAuthentication) {
                    return customJwtConverter().convert(jwtAuthentication.getToken());
                }
                return authenticated;
            };
        };

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver))
                .addFilterAfter(tenantContextFilter(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public Converter<Jwt, IcsJwtToken> customJwtConverter() {
        return new IcsJwtConverter();
    }

    // Deliberately not a @Bean: Spring Boot would also register it as a servlet filter that runs
    // before the security chain, finds no authentication, and is then skipped inside the chain
    // (OncePerRequestFilter), so the tenant would never be set.
    private OncePerRequestFilter tenantContextFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof IcsJwtToken jwt) {
                        String tenantId = jwt.getTenantId();
                        if (tenantId == null || tenantId.isBlank()) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token does not carry a tenant");
                            return;
                        }
                        TenantContext.setCurrentTenant(tenantId);
                    }
                    filterChain.doFilter(request, response);
                } finally {
                    TenantContext.clear();
                }
            }
        };
    }
}
