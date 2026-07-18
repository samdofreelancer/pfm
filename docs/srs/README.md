# Software Requirements Specification (SRS)

**Project:** Personal Finance Manager (PFM)  
**Version:** 2.0  
**Date:** July 18, 2026  
**Status:** Approved

---

## Table of Contents

This SRS is organized into modular documents for easier maintenance and navigation.

### Core Documents

1. **[01-introduction.md](01-introduction.md)** - Purpose, scope, and definitions
2. **[02-functional-requirements.md](02-functional-requirements.md)** - Functional requirements overview
   - [02-1-user-management.md](02-1-user-management.md) - User authentication and profiles
   - [02-2-account-management.md](02-2-account-management.md) - Financial accounts
   - [02-3-category-management.md](02-3-category-management.md) - Transaction categories
   - [02-4-transaction-management.md](02-4-transaction-management.md) - Income, expense, and transfer transactions
   - [02-5-dashboard-reports.md](02-5-dashboard-reports.md) - Dashboard and reporting features
   - [02-6-budget-management.md](02-6-budget-management.md) - Budget planning and alerts
   - [02-7-goal-management.md](02-7-goal-management.md) - Savings goals
   - [02-8-notification-management.md](02-8-notification-management.md) - Email and in-app notifications
   - [02-9-ai-assistant.md](02-9-ai-assistant.md) - AI-powered chat assistant
3. **[03-non-functional-requirements.md](03-non-functional-requirements.md)** - Performance, security, scalability
4. **[04-use-cases.md](04-use-cases.md)** - Key use case scenarios
5. **[05-technical-constraints.md](05-technical-constraints.md)** - Technology stack and constraints
6. **[06-system-architecture.md](06-system-architecture.md)** - System design and architecture
7. **[07-database-schema.md](07-database-schema.md)** - Database tables and relationships
8. **[08-api-design.md](08-api-design.md)** - RESTful API specifications
9. **[09-ai-integration.md](09-ai-integration.md)** - AI/LLM integration details

---

## Quick Navigation

### By Priority

**Critical Features:**
- User Management (FR-01 to FR-06)
- Account Management (FR-07 to FR-11)
- Category Management (FR-12 to FR-14)
- Transaction Management (FR-15 to FR-22)
- Dashboard & Reports (FR-23 to FR-27)
- Budget Management (FR-28 to FR-31)
- Goal Management (FR-32 to FR-35)
- Notification Management (FR-36 to FR-38)
- AI Assistant (FR-AI-01 to FR-AI-11)

**High Priority:**
- Google OAuth2 (FR-03)
- Account deletion with retention (FR-06)
- Multi-currency support (FR-11)
- Recurring transactions (FR-19)
- Dashboard caching (FR-27)
- Periodic report emails (FR-37)
- AI financial advice (FR-AI-04)
- Voice features (FR-AI-06)
- Chat history (FR-AI-07)
- Expert mode (FR-AI-08)

### By Component

**Backend Development:**
- Start with: [01-introduction.md](01-introduction.md)
- Then: [06-system-architecture.md](06-system-architecture.md)
- Then: [07-database-schema.md](07-database-schema.md)
- Then: [08-api-design.md](08-api-design.md)
- Then: [09-ai-integration.md](09-ai-integration.md)

**Frontend Development:**
- Start with: [01-introduction.md](01-introduction.md)
- Then: [08-api-design.md](08-api-design.md)
- Then: [02-functional-requirements.md](02-functional-requirements.md) (specific modules)

**AI/ML Development:**
- Start with: [02-9-ai-assistant.md](02-9-ai-assistant.md)
- Then: [09-ai-integration.md](09-ai-integration.md)
- Then: [06-system-architecture.md](06-system-architecture.md) (AI Module section)

**Database Design:**
- Start with: [07-database-schema.md](07-database-schema.md)
- Then: [06-system-architecture.md](06-system-architecture.md) (Data Layer section)

---

## Document Conventions

### Priority Levels
- **Critical**: Must be implemented in v1.0
- **High**: Should be implemented in v1.0 or early v2.0
- **Medium**: Nice to have, can be deferred
- **Low**: Future consideration

### Requirement IDs
- **FR-XX**: Functional requirements (e.g., FR-01, FR-02)
- **FR-AI-XX**: AI-specific functional requirements
- **NFR-XX**: Non-functional requirements

### Version History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2026-07-01 | Initial SRS | System Architect |
| 2.0 | 2026-07-18 | Added AI Assistant feature (FR-AI-01 to FR-AI-11), updated NFRs, architecture, API | System Architect |

---

## Related Documentation

- **Architecture Design Document (ADD)**: Detailed technical design
- **API Documentation**: Swagger/OpenAPI specs
- **Database Migration Scripts**: SQL scripts for schema changes
- **User Stories**: Detailed user scenarios
- **UI/UX Mockups**: Figma designs

---

## Next Steps

✅ Review this SRS with stakeholders

📐 Create Architecture Design Document (ADD)

🎨 Design UI/UX mockups (Figma)

💻 Start implementation: Backend (Domain models first), Frontend (component library), AI (integration tests)

📌 Repository Structure:
```
personal-finance-manager/
├── README.md
├── docs/
│   ├── SRS/
│   │   ├── README.md (this file)
│   │   ├── 01-introduction.md
│   │   ├── 02-functional-requirements.md
│   │   ├── 02-1-user-management.md
│   │   ├── 02-2-account-management.md
│   │   ├── 02-3-category-management.md
│   │   ├── 02-4-transaction-management.md
│   │   ├── 02-5-dashboard-reports.md
│   │   ├── 02-6-budget-management.md
│   │   ├── 02-7-goal-management.md
│   │   ├── 02-8-notification-management.md
│   │   ├── 02-9-ai-assistant.md
│   │   ├── 03-non-functional-requirements.md
│   │   ├── 04-use-cases.md
│   │   ├── 05-technical-constraints.md
│   │   ├── 06-system-architecture.md
│   │   ├── 07-database-schema.md
│   │   ├── 08-api-design.md
│   │   └── 09-ai-integration.md
│   ├── architecture/
│   ├── api/
│   └── database/
├── backend/
├── frontend/
└── docker/
```

---

## Contact

For questions or clarifications about this SRS, please contact the project team.

**Product Owner**: [Name]  
**Tech Lead (Backend)**: [Name]  
**Tech Lead (Frontend)**: [Name]  
**AI Integration Lead**: [Name]