package com.ics.backend.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            SecurityProperties securityProperties) throws Exception {

        JwtIssuerAuthenticationManagerResolver issuerAuthenticationManagerResolver = JwtIssuerAuthenticationManagerResolver
                .fromTrustedIssuers(securityProperties.getTrustedIssuers());

        AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver = request -> {
            AuthenticationManager delegate = issuerAuthenticationManagerResolver.resolve(request);
            return authentication -> {
                var authenticated = delegate.authenticate(authentication);
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

    @Bean
    public OncePerRequestFilter tenantContextFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain
            ) throws ServletException, IOException {
                try {
                    var authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof IcsJwtToken jwt) {
                        TenantContext.setCurrentTenant(jwt.getTenantId());
                    }
                    filterChain.doFilter(request, response);
                } finally {
                    TenantContext.clear();
                }
            }
        };
    }
}

