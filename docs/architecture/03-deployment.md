# 🚀 Deployment Architecture

## Overview

This document describes how the PFM application is deployed and the main runtime components that support production operations.

## Deployment Targets

PFM is designed to run on a cloud platform or container environment. Typical deployment components include:

- Application server running Spring Boot
- Relational database for persistent storage
- External services for authentication, notifications, and AI integration
- Monitoring and logging infrastructure

## Deployment Components

### Application Service
- Spring Boot application packaged as a JAR
- Runs behind a reverse proxy or API gateway
- Supports environment-specific configuration via profiles

### Database
- Primary persistent store using PostgreSQL or MySQL
- Database migrations managed by Flyway or Liquibase
- Optimistic locking support via JPA `@Version`

### External Integrations
- AI/LLM service for chat and assistant features
- Email or push notification service for alerts
- Authentication provider if federated login is supported

## Deployment Considerations

### Scalability
- Deploy application instances behind a load balancer
- Use database connection pooling
- Scale read-heavy operations with caching where appropriate

### Security
- Enforce HTTPS for all external traffic
- Use strong authentication and authorization policies
- Secure secrets through environment variables or vaults

### Observability
- Collect logs centrally using ELK, Grafana Loki, or similar
- Monitor application health and metrics
- Track key business events and errors

## Example Deployment Topology

```
Client --> Load Balancer --> API Instances
                          |
                          +--> Database
                          +--> AI / Notification Services
```

## Environment Configuration

- `application.yml` for default settings
- `application-dev.yml` for development
- `application-prod.yml` for production
- Secret values should never be stored in source control
