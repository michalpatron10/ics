# ADR-0001: Database per tenant

Status: accepted

## Context

Tenants are insurance companies. They expect strong isolation of claim data and documents, some will ask for their data in a specific location or for separate backup/restore, and an offboarded tenant's data must be removable completely.

## Decision

Each tenant gets its own PostgreSQL database containing its domain tables, its Operaton tables and its Spring Modulith event publication table. The application routes connections with `MultitenantDataSource` (an `AbstractRoutingDataSource`) keyed by `TenantContext`. Connection details live in a tenant catalog (today: `db/connections/*.properties`; target: the `ics_platform` database).

## Consequences

- Strong isolation, per-tenant backup/restore, simple offboarding (drop the database), and noisy tenants can move to their own server.
- Every code path must know its tenant. Non-request threads (job executor, async listeners, schedulers) must set `TenantContext` explicitly. See README §4.2.
- Schema migrations run N times. Flyway must iterate tenants at startup and onboarding, and one tenant's failed migration must not block the others from starting.
- Connection pools multiply (N tenants × pool size). Keep per-tenant pools small and size Postgres `max_connections` accordingly.
- Cross-tenant queries are not possible by design.

## Alternatives considered

- **Shared schema with a `tenant_id` column**: cheapest and supported natively by Operaton, but isolation depends on every query being correct, and per-tenant restore is hard.
- **Schema per tenant**: a middle ground. It was rejected for consistency with the per-tenant backup/location requirement, but it remains a cheap fallback if the number of databases becomes a problem, because the routing code is the same.
