package com.ics.backend.common;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IcsJwtConverterTest {

    private final IcsJwtConverter converter = new IcsJwtConverter();

    @Test
    void shouldExtractTenantIdFromIssuerUrl() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iss", "http://localhost:8180/realms/ics_tenant1")
                .claim("given_name", "John")
                .claim("family_name", "Doe")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertEquals("ics_tenant1", token.getTenantId());
        assertEquals("John", token.getFirstname());
        assertEquals("Doe", token.getLastname());
    }

    @Test
    void shouldHandleIssuerWithoutSlash() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("iss", "ics_tenant2")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertEquals("ics_tenant2", token.getTenantId());
    }

    @Test
    void shouldHandleNullIssuer() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("given_name", "Jane")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        IcsJwtToken token = converter.convert(jwt);

        assertNull(token.getTenantId());
    }
}
