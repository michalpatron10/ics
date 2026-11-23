# ADR-0005: Spring Cloud Gateway as entry point and BFF

Status: proposed

## Context

The SPA currently runs the OIDC code flow itself (`angular-oauth2-oidc`) and holds tokens in the browser. The tenant (realm) has to be selected before login. We also want one public origin for the SPA and the API, and room to split the backend later.

## Decision

Spring Cloud Gateway is the only public entry point:

- **Routing:** it serves the Angular app and proxies `/api/**` to the backend.
- **Tenant selection:** the host (`<tenant>.ics.example`) or a path prefix maps to a Keycloak realm. The gateway is an OAuth2 client with one client registration per realm, resolved dynamically from the tenant catalog.
- **BFF:** the gateway runs the code flow, keeps tokens in its session, and adds `Authorization: Bearer` to backend calls (`TokenRelay`). The browser gets only an HTTP-only, SameSite session cookie, and CSRF protection is applied at the gateway.
- **Backend:** stays a stateless resource server that validates tokens against trusted issuers. It does not trust the gateway blindly.

## Consequences

- No tokens in browser storage, following the OAuth 2.0 for Browser-Based Apps guidance. The SPA's auth code shrinks to "call `/api`, redirect to `/login` on 401".
- The gateway holds sessions. With more than one instance it needs a shared session store (Spring Session + Redis/JDBC).
- One more component to run locally. Add it to `ics-containers` or run it as a separate Spring Boot module.
- If the BFF is postponed, the gateway can still route while the SPA keeps `angular-oauth2-oidc`. In that case the SPA has to choose the realm/issuer per tenant, but `environment.ts` currently hardcodes `ics-realm`, which is not one of the backend's trusted issuers.
