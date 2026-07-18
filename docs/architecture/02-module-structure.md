# 📦 Module Structure

## Overview

This document describes the module structure used in the PFM project. Each module has a clear responsibility aligned with the layered architecture.

## Modules

### `pfm-api`
- Exposes REST APIs
- Defines request and response DTOs
- Handles authentication and authorization
- Contains global exception handling and API documentation metadata

### `pfm-application`
- Implements use cases and business workflows
- Defines commands, queries, and handlers
- Orchestrates interactions between domain objects and repositories
- Publishes domain events after changes

### `pfm-domain`
- Contains domain entities, aggregates, value objects, and domain services
- Defines repository interfaces and domain events
- Encapsulates business rules and invariants
- Avoids framework-specific dependencies

### `pfm-infrastructure`
- Provides persistence implementations using JPA
- Contains repository implementations, mappers, and entities
- Implements security, caching, messaging, and external integrations
- Connects domain interfaces to concrete infrastructure

### `pfm-common`
- Stores shared utilities, constants, and custom exceptions
- Provides common DTOs and helper classes used across modules

### `pfm-bootstrap`
- Bootstraps the Spring Boot application
- Loads application configuration and runtime settings
- Integrates all modules into a running application

## Package Layout

Example package layout for domain and application modules:

- `com.pfm.domain.user`
- `com.pfm.domain.account`
- `com.pfm.domain.transaction`
- `com.pfm.application.auth`
- `com.pfm.application.account`
- `com.pfm.application.transaction`

## Dependency Rules

- `pfm-api` depends on `pfm-application`
- `pfm-application` depends on `pfm-domain` and `pfm-common`
- `pfm-infrastructure` depends on `pfm-domain`
- `pfm-bootstrap` depends on all runtime modules

## Benefits

- Clear separation between business rules and infrastructure
- Easier testing because domain logic is isolated
- Better maintainability and module ownership
- Flexible replacement of persistence or integration layers
