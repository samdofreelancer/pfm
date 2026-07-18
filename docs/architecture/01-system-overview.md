# 🏛️ System Overview

## Purpose

This document explains the overall architecture of the Personal Finance Manager (PFM), including the key modules and how they interact to fulfill business goals.

## Architecture Summary

PFM is built using a layered architecture inspired by Domain-Driven Design (DDD) and Clean Architecture. The main layers are:

- **API Layer** (`pfm-api`): REST controllers, request/response DTOs, and exception handling
- **Application Layer** (`pfm-application`): Use case orchestration, command/query handlers, and application services
- **Domain Layer** (`pfm-domain`): Core business logic, aggregates, value objects, domain services, and repository interfaces
- **Infrastructure Layer** (`pfm-infrastructure`): Persistence, security, external integration adapters, and implementation details
- **Bootstrap Module** (`pfm-bootstrap`): Application startup and runtime configuration

## High-Level Flow

1. A client sends a request to the REST API
2. Controllers translate HTTP input into application commands or queries
3. Application services coordinate domain logic and repository interactions
4. Domain aggregates enforce invariants and perform business operations
5. Infrastructure implementations persist data and publish events
6. Responses are mapped back to API DTOs and returned to the client

## Core Concepts

- **Aggregate Roots** enforce business rules and consistency boundaries
- **Value Objects** encapsulate primitives and validation logic
- **Domain Events** communicate important state changes across modules
- **Application Services** orchestrate use cases without containing business rules
- **Infrastructure** remains isolated from domain implementation details

## System Diagram

```
Client --> API Layer --> Application Layer --> Domain Layer
                             ^                   |
                             |                   v
                       Infrastructure Layer <-- External Services
```

## Design Goals

- Maintainable code structure
- Strong separation of concerns
- Testable domain logic
- Scalable integration points for external services
- Secure, standards-based API design
