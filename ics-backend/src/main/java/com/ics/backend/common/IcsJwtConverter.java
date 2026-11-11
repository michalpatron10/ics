package com.ics.backend.common;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IcsJwtConverter implements Converter<Jwt, IcsJwtToken> {

    @Override
    public IcsJwtToken convert(@NonNull Jwt jwt) {
        // Extract claims and authorities as needed
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // You can also map other information from the Jwt to the custom token
        var customJwt = new IcsJwtToken(jwt, authorities);
        customJwt.setFirstname(jwt.getClaimAsString("given_name"));
        customJwt.setLastname(jwt.getClaimAsString("family_name"));
        customJwt.setTenantId(extractTenantId(jwt.getClaimAsString("iss")));
        return customJwt;
    }

    private String extractTenantId(String iss) {
        if (iss == null) {
            return null;
        }
        return iss.substring(iss.lastIndexOf('/') + 1);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        var authorities = new ArrayList<GrantedAuthority>();

        // ... your logic to extract and map the claims to GrantedAuthority ...
        var realm_access = jwt.getClaimAsMap("realm_access");
        if (realm_access != null && realm_access.get("roles") != null) {
            var roles = realm_access.get("roles");
            if (roles instanceof List l) {
                l.forEach(role ->
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        }

        return authorities;
    }
}
