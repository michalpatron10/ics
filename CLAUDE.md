# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Insurance Claim System (ICS): a multi-tenant platform for insurance companies to process claims. Monorepo with three parts:

- `ics-backend/`: Spring Boot 4.2 (Java 26), Spring Modulith, JPA/Hibernate, Flyway, OAuth2 resource server
- `ics-webapp/`: Angular 22 (standalone, Vitest for tests), `angular-oauth2-oidc` for login
- `ics-containers/`: docker-compose for local infra (PostgreSQL 14 + Keycloak 22)

The target architecture is in `architecture/README.md`, with decision records in `architecture/decisions/`. Planned additions that are not yet in the code: Operaton (Camunda 7 fork) embedded with one engine per tenant, tenant claim processes built from registered building blocks, Spring Cloud Gateway as BFF, and PrimeNG. Read those docs before working on tenancy, processes or auth, and update the ADRs when a decision changes.

## Commands

Local infrastructure (run first; backend and webapp depend on it):
```
cd ics-containers && docker compose up -d
```
Postgres is on `localhost:5432` (postgres/postgres) and Keycloak on `localhost:8180` (admin/admin). `init-databases.sql` creates the `ics_tenant1` and `ics_tenant2` databases.

Backend (from the repo root, use `mvnw.cmd` on Windows). The root `pom.xml` is an aggregator holding the shared versions and BOMs; `ics-backend` is its only module today. Builds need JDK 26 or newer in `JAVA_HOME`.
```
./mvnw -pl ics-backend spring-boot:run
./mvnw test
./mvnw test -Dtest=ModularityTest
./mvnw test -Dtest=IcsJwtConverterTest#shouldExtractTenantIdFromIssuerUrl
```
`IcsApplicationTest` is a full `@SpringBootTest`, so it needs Postgres running. `ModularityTest` holds the Spring Modulith `ApplicationModules.verify()` check and needs no infrastructure.

Webapp (from `ics-webapp/`):
```
npm start        # ng serve on :4200
npm run build
npm test         # ng test (Vitest)
```
Prettier config is in `.prettierrc`.

## Backend architecture

**Code style.**
- Never use wildcard imports (`import jakarta.persistence.*;`, `import static ...Assertions.*;`). Import each class or static member explicitly.
- Exactly one blank line between the `package` statement and the imports.
- In tests, use static imports for assertions and other test helpers: `assertEquals(...)`, not `Assertions.assertEquals(...)`. Import each method explicitly (`import static org.junit.jupiter.api.Assertions.assertEquals;`), never with `*`. Outside tests, don't use static imports.

**Spring Modulith layout.** Each top-level package under `com.ics.backend` is a module (`claims`, `documents`, `common`). Only a module's root package is public: controllers sit there (e.g. `claims/ClaimsController`). Everything else lives under `<module>/internal/...` (datamodel, dto, mapper, repoository [sic], service). Other modules must not reach into `internal`; `ModularityTest` checks this with `ApplicationModules.verify()`. Modules are packages inside the single `ics-backend` Maven module, not separate Maven modules.

**Multi-tenancy: one database per tenant, with the tenant resolved from the JWT issuer.** Several files work together:
1. `SecurityConfig` uses `JwtIssuerAuthenticationManagerResolver` with the issuers listed in `ics.security.trusted-issuers` (`application.yaml`, bound by `SecurityProperties`). Each tenant is its own Keycloak realm.
2. Once a token validates, `IcsJwtConverter` turns it into `IcsJwtToken`. The tenant ID is the last path segment of the `iss` claim (`.../realms/ics_tenant1` → `ics_tenant1`). Realm roles from `realm_access.roles` become `ROLE_<role>` authorities.
3. A `tenantContextFilter`, registered after `BearerTokenAuthenticationFilter`, puts the tenant ID into the `TenantContext` ThreadLocal and clears it after the request.
4. `MultitenantConfiguration` builds a `MultitenantDataSource` (an `AbstractRoutingDataSource`) from every `classpath:db/connections/*.properties` file. Each file has a `name` (the tenant ID, which must match the realm name) and `datasource.*` keys. The default tenant, used when no tenant is set (for example at startup), is hardcoded to `ics_tenant1`.

Adding a tenant takes three things: a Keycloak realm, a `db/connections/<tenant>.properties` file, and an entry in `ics.security.trusted-issuers`. The database also has to exist (see `init-databases.sql`).

Flyway migrations are in `src/main/resources/db/migration`. They run through the routing DataSource with no tenant set, so by default only the default tenant's database is migrated.

## Webapp architecture

`app.config.ts` sets up OAuth in an app initializer: `loadDiscoveryDocumentAndLogin()` (code flow) runs before the app boots, and on failure the browser is redirected to `/error.html`. Keycloak settings (issuer, `clientId: ics-webapp-client`) are in `src/environments/`. The root component is `app.root.ts` (not `app.component.ts`).

The webapp's issuer (`realms/ics-realm`) is not in the backend's trusted tenant issuers (`ics_tenant1`/`ics_tenant2`). docker-compose imports only `ics-realm-realm.json`. Keep this mismatch in mind when wiring the frontend to the backend.
