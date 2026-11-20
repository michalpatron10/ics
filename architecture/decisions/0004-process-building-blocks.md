# ADR-0004: Tenant claim processes built from building blocks

Status: proposed

## Context

Each insurance company handles claims differently: different steps, approvals, thresholds, document requirements and SLAs. We do not want per-tenant code branches, and tenants' process authors must not be able to run arbitrary code in our JVM.

## Decision

- **Blocks are code; processes are configuration.** Every capability usable in a process is a `ProcessBlock` (a service-task behaviour) or a user-task form, implemented and versioned by us inside the owning Modulith module and registered by ID.
- BPMN references blocks by ID through a single dispatcher delegate (`operaton:delegateExpression="${block}"` plus a `blockId` input/field). Element templates describe each block's inputs and outputs for the modeler.
- Tenant-specific **rules** (amount limits, routing, fraud thresholds, SLAs) go in DMN decision tables, not in gateway expressions.
- Deployments go through our admin endpoint, which validates them before deploying into the tenant's engine. It rejects script tasks, `operaton:class`, unregistered block IDs, unregistered forms, delegate/listener expressions, and non-trivial condition expressions.
- A reference process is maintained by us and copied to each new tenant as a starting point.

## Consequences

- Onboarding a new insurer is mostly modeling and DMN work, not development.
- The block catalog becomes a product surface. Block contracts (variables, errors, idempotency) need versioning; a block change that breaks its contract becomes a new block version, with the old one kept while running instances use it.
- Every block must be idempotent, because the job executor retries.
- Validation is a security control and needs its own tests (malicious BPMN fixtures).
- Phase 2 (an embedded bpmn-js modeler in the admin UI) can reuse the same element templates and validation.
