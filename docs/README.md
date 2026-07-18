# 📚 Personal Finance Manager Documentation

Welcome to the PFM documentation. This guide follows **Domain-Driven Design (DDD)** principles to ensure a clean, maintainable, and scalable architecture.

## 📖 Documentation Structure

### [DDD Documentation](./ddd/)
- [Domain Model](./ddd/01-domain-model.md) – Core entities, aggregates, value objects
- [Bounded Contexts](./ddd/02-bounded-contexts.md) – Context boundaries and mapping
- [Value Objects](./ddd/03-value-objects.md) – Immutable objects and validation
- [Repositories](./ddd/04-repositories.md) – Data access patterns
- [Domain Services](./ddd/05-domain-services.md) – Business logic implementation
- [Domain Events](./ddd/06-domain-events.md) – Event-driven communication
- [Application Services](./ddd/07-application-services.md) – Use case orchestration
- [Dependency Rules](./ddd/08-dependency-rules.md) – Layer dependencies
- [Aggregate Design](./ddd/09-aggregate-design.md) – Aggregate design principles
- [Coding Standards](./ddd/10-coding-standards.md) – Code style and conventions

### [Architecture](./architecture/)
- [System Overview](./architecture/01-system-overview.md)
- [Module Structure](./architecture/02-module-structure.md)
- [Deployment](./architecture/03-deployment.md)
- [Security](./architecture/04-security.md)

### [API Documentation](./api/)
- [API Overview](./api/01-api-overview.md)
- [Authentication API](./api/02-auth-api.md)
- [Account API](./api/03-account-api.md)
- [Transaction API](./api/04-transaction-api.md)
- [Budget API](./api/05-budget-api.md)
- [Goal API](./api/06-goal-api.md)
- [Report API](./api/07-report-api.md)
- [AI Chat API](./api/08-chat-api.md)

## 📌 What’s Included

- **Architecture docs** explain the system design, module structure, deployment, and security
- **API docs** describe endpoints, request/response shapes, and authentication flows
- **DDD docs** cover domain models, aggregates, value objects, repositories, services, events, and coding standards

## 🏗️ Quick Start

```bash
# Clone the repository
git clone https://github.com/your-org/pfm-backend.git

# Build all modules
mvn clean install

# Run the application
cd pfm-backend
mvn spring-boot:run
```

## 📌 Key Principles

- **Domain-Driven Design** – Business logic is the heart of the application
- **Clean Architecture** – Dependency inversion, independent of frameworks
- **Event-Driven** – Loose coupling via domain events
- **Test-First** – High test coverage for domain models
- **Continuous Delivery** – Automated CI/CD pipeline

## 🔗 Useful Links
- SRS Document – Software Requirements Specification
- Issue Tracker
- Project Board
