# 🎯 Bounded Contexts & Ubiquitous Language

## Overview

The Personal Finance Manager is divided into separate bounded contexts, each with its own domain model and ubiquitous language.

## Context Map

- **User Context** – identity, authentication, profile management
- **Account Context** – financial accounts, balance management, transfers
- **Transaction Context** – recording income, expenses, and transfers
- **Budget Context** – spending limits and alerts
- **Goal Context** – savings goals and progress tracking

## Context Details

### 1. User Context
- **Purpose:** Manage user identity and access
- **Core Concepts:** User, Email, Password, Profile
- **Responsibilities:**
  - Register new users
  - Authenticate users (JWT)
  - Manage user profiles
  - Handle password reset

### 2. Account Context
- **Purpose:** Manage financial accounts and balances
- **Core Concepts:** Account, Balance, Currency, Transfer
- **Responsibilities:**
  - Create, update, delete accounts
  - Update balances via deposit and withdrawal
  - Transfer funds between accounts
  - Maintain account status and optimistic locking

### 3. Transaction Context
- **Purpose:** Record all financial activities
- **Core Concepts:** Transaction, Income, Expense, Category
- **Responsibilities:**
  - Create transactions
  - Validate transaction rules
  - Query and search transaction history
  - Export transaction data

### 4. Budget Context
- **Purpose:** Manage spending limits and triggers
- **Core Concepts:** Budget, Limit, Period, Alert
- **Responsibilities:**
  - Create, update, delete budgets
  - Track spend against budget limits
  - Trigger alerts when limits are exceeded

### 5. Goal Context
- **Purpose:** Manage savings goals
- **Core Concepts:** Goal, Target, Contribution, Progress
- **Responsibilities:**
  - Create, update, delete goals
  - Track contributions and progress
  - Notify users when goals are achieved

## Communication Between Contexts

### Event-Driven Communication

Transactions can drive updates in budgets and accounts using domain events such as `TransactionCreated` and `BudgetExceeded`.

### Anti-Corruption Layer

For external integrations like LLM APIs, use an Anti-Corruption Layer to:
- translate external responses into domain objects
- protect the domain model from external changes
- keep the domain isolated from third-party formats

## Ubiquitous Language Glossary

| Term | Definition | Context |
|------|------------|---------|
| **User** | Person using the application | User |
| **Account** | Container for money such as cash or bank accounts | Account |
| **Transaction** | Financial activity (income, expense, transfer) | Transaction |
| **Category** | Classification of transactions | Shared |
| **Budget** | Monthly spending limit per category | Budget |
| **Goal** | Savings target with deadline | Goal |
| **Balance** | Current amount available in an account | Account |
| **Transfer** | Moving funds between accounts | Account |

## Guidelines

- Never share domain models between contexts
- Use domain events for cross-context communication
- Define Anti-Corruption Layers for external services
- Maintain ubiquitous language consistently within each context
- Keep contexts loosely coupled and avoid direct dependencies
