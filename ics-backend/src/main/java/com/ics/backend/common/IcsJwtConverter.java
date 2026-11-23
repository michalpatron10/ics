package com.ics.backend.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class IcsJwtConverter implements Converter<Jwt, IcsJwtToken> {

    @Override
    public IcsJwtToken convert(@NonNull Jwt jwt) {
        var customJwt = new IcsJwtToken(jwt, extractAuthorities(jwt));
        customJwt.setFirstname(jwt.getClaimAsString("given_name"));
        customJwt.setLastname(jwt.getClaimAsString("family_name"));
        customJwt.setTenantId(extractTenantId(jwt.getClaimAsString("iss")));
        return customJwt;
    }

    private String extractTenantId(String issuer) {
        if (issuer == null) {
            return null;
        }
        return issuer.substring(issuer.lastIndexOf('/') + 1);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        return authorities;
    }
}
