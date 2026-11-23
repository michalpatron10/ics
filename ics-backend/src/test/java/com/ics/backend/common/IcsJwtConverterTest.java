package com.ics.backend.common;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class IcsJwtConverterTest {

    private final IcsJwtConverter converter = new IcsJwtConverter();

    @Test
    void extractsTenantIdFromIssuerUrl() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iss", "http://localhost:8180/realms/ics_tenant1")
                .claim("given_name", "John")
                .claim("family_name", "Doe")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertThat(token.getTenantId()).isEqualTo("ics_tenant1");
        assertThat(token.getFirstname()).isEqualTo("John");
        assertThat(token.getLastname()).isEqualTo("Doe");
    }

    @Test
    void usesWholeIssuerAsTenantIdWhenItHasNoSlash() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iss", "ics_tenant2")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertThat(token.getTenantId()).isEqualTo("ics_tenant2");
    }

    @Test
    void leavesTenantIdEmptyWhenIssuerIsMissing() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("given_name", "Jane")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertThat(token.getTenantId()).isNull();
    }

    @Test
    void mapsRealmRolesToAuthorities() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iss", "http://localhost:8180/realms/ics_tenant1")
                .claim("realm_access", Map.of("roles", List.of("adjuster", "admin")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting(Object::toString)
                .containsExactlyInAnyOrder("ROLE_adjuster", "ROLE_admin");
    }
}
