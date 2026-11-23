# ADR-0002: Spring Modulith modular monolith

Status: accepted

## Context

The team is small and the domain boundaries (claims, documents, communication, process, integration) are still settling. Process steps call several domains inside one claim workflow.

## Decision

Build one deployable Spring Boot application structured as Spring Modulith application modules. Each module exposes only its root package, and implementation lives in `<module>/internal`. Modules communicate through their public API or through application events (`@ApplicationModuleListener` with the JPA event publication registry). The `ApplicationModules.verify()` test enforces the boundaries.

## Consequences

- One transaction can span a process step and its domain writes, which is how the embedded engine works best.
- Modules can be extracted later along event boundaries if scaling demands it. The gateway already hides the topology from the frontend.
- The event publication table is per tenant (ADR-0001). Incomplete publications must be resubmitted per tenant with `TenantContext` set.
- `ModularityTest` runs `modules.verify()`, so violations fail the build. Still to do: generate module docs with `Documenter` into this folder.
