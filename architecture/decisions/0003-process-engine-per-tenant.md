# ADR-0003: Operaton, embedded, one engine per tenant

Status: proposed (Operaton choice accepted; engine-per-tenant pending spike)

## Context

Claim processes are BPMN, and each tenant has its own (ADR-0004). We chose Operaton, the open-source continuation of Camunda 7, because it is an embeddable Java engine with a mature BPMN/DMN implementation and an Apache 2.0 licence. Camunda 8 (Zeebe) needs a separate cluster and has a different licence.

Operaton's built-in multi-tenancy uses a `TENANT_ID_` column in shared tables, which contradicts database per tenant (ADR-0001). A single engine on top of the routing DataSource does not work either: its job executor runs on background threads with no tenant and would poll only the default database.

## Decision

The `process` module creates one `ProcessEngine` per tenant, named with the tenant ID:

- **Configuration:** `SpringProcessEngineConfiguration`, built programmatically for each tenant in the catalog, at startup and on onboarding. The Spring Boot starter's auto-configured engine is disabled or used only as the template.
- **DataSource:** `TransactionAwareDataSourceProxy(multitenantDataSource)`, together with the application's **shared** `JpaTransactionManager`. Engine commands and JPA writes in the same thread therefore use one connection and one transaction.
- **Job executor:** one per engine. Its thread factory pins `TenantContext` to the engine's tenant for the lifetime of the thread, so every connection those threads acquire routes to the right database.
- **Guard:** a command interceptor checks `TenantContext.getCurrentTenant().equals(engineName)` and fails fast otherwise. This catches any path that reaches an engine with the wrong context.
- **Lookup:** a `TenantProcessEngines` bean resolves the engine for the current tenant. No bean injects `ProcessEngine` or its services directly.
- **Schema:** `databaseSchemaUpdate=false`. The Operaton DDL is applied by per-tenant Flyway.
- **Expressions:** the engine's exposed beans are restricted to the block dispatcher (`configuration.setBeans(...)`), so BPMN expressions cannot reach arbitrary Spring beans.

## Consequences

- True per-tenant process isolation: history, jobs, deployments and incidents stay in the tenant's database.
- Memory, threads and polling grow with the number of tenants. To be measured (README §7). If it becomes a problem, use one shared execution thread pool with per-engine acquisition, or lazy engine start for idle tenants.
- The Operaton webapps (Cockpit/Tasklist) are built for a single engine per app or a shared-engine container. We build tasklist and admin views in our Angular app and use Cockpit only for operations, if at all.
- A starter upgrade needs a check that the custom engine wiring still works, which is a reason to keep it small and well covered by an integration test running two tenants (Testcontainers Postgres).

## Open points

- Confirm Operaton support for Spring Boot 4.1 / Java 25. Fallback: Spring Boot 3.5.
- Confirm that the shared `JpaTransactionManager` plus the proxy behaves as expected when an async continuation is followed by a JPA write in a delegate.
