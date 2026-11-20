# Insurance claim system

#### The aim of this system is to provide insurance companies and their associates with all the necessary tools to process insurance claims.

## Features

- Claim submission and tracking
- Document management
- Communication with claimants and collaborators
- Reporting and analytics
- Integration with other systems


##  Non-Functional Requirements

- customizability by configuration, 
- extendability
- securability
- scalability
- user-friendly

## Technical aspects

1. Programing language: Java Temurin implementation
2. Backend frameworks: Spring, Spring Boot, Hibernate
3. Frontend framework: Angular, TypeScript
3. Build tool: Maven, npm
4. Database: PostgreSQL
5. Container service provider: Docker


## Architecture


See [architecture/README.md](architecture/README.md) for the proposed architecture and [architecture/decisions/](architecture/decisions/) for the decision records.

Key decisions: Spring Modulith modular monolith, database per tenant (PostgreSQL), Keycloak realm per tenant, embedded Operaton (Camunda 7 fork) with one process engine per tenant, tenant claim processes assembled from building blocks, Spring Cloud Gateway as BFF, Angular + PrimeNG UI, UUIDv7 IDs with per-tenant human claim numbers.

## Repository structure

Target layout. Items marked *(planned)* don't exist yet.

```
ics/
├── pom.xml                         # (planned) Maven aggregator for backend + gateway (shared versions/BOMs)
├── architecture/                   # architecture overview + decisions/ (ADRs)
├── ics-backend/                    # Spring Modulith app
│   └── src/main/
│       ├── java/com/ics/backend/
│       │   ├── tenancy/            # (planned) split out of today's common/
│       │   ├── security/           # (planned) split out of today's common/
│       │   ├── process/            # (planned) Operaton engines per tenant, block registry, BPMN validation, task API
│       │   ├── claims/  documents/  communication/  integration/  reporting/  # communication, integration, reporting planned
│       └── resources/
│           ├── db/migration/       # domain Flyway scripts
│           ├── db/operaton/        # (planned) Operaton DDL vendored as Flyway scripts
│           └── processes/reference/  # (planned) reference BPMN + DMN copied to new tenants
├── ics-gateway/                    # (planned) Spring Cloud Gateway (BFF)
├── ics-webapp/                     # Angular + PrimeNG
│   └── src/app/
│       ├── core/                   # (planned) auth/session, tenant config, HTTP
│       ├── claims/  tasks/  documents/  admin/   # (planned)
│       └── forms/                  # (planned) user-task form components, registered by form ID
├── ics-containers/                 # local infra: postgres/, keycloak/ (one realm per tenant), gateway later
└── .github/workflows/              # (planned) CI filtered by path: backend, gateway, webapp
```

- **One monorepo.** A building block's Java code, element template, form component and API usually change together in one PR.
- **Backend modules are packages, not Maven modules.** Spring Modulith enforces their boundaries; split into artifacts only when a service is actually extracted.
- **Tenant processes are not stored here.** Each tenant's BPMN/DMN is tenant data, deployed through the admin endpoint into that tenant's database. The repo holds only the reference process and the block catalog.
- **Element templates should be generated from block code** and served by the admin API, so they can't drift from the Java implementation.
