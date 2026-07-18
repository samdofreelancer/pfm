# 6. System Architecture Overview

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│ Frontend (React)                                                │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│ │Dashboard │ │Transac. │ │ Budget   │ │ Chat     │           │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTPS (JWT)
┌───────────────────────────▼─────────────────────────────────────┐
│ API Gateway (Spring Cloud Gateway)                              │
└───────────────────────────┬─────────────────────────────────────┘
                            │
┌───────────────────┼───────────────────┐
▼                   ▼                   ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ Auth Module   │ │ Core PFM      │ │ AI Module     │
│ (JWT, OAuth)  │ │(Transac.,Acct,│ │(Chat, Intent, │
│               │ │ Budget,Goal)  │ │ Executor)     │
└───────────────┘ └───────────────┘ └───────────────┘
                            │
┌───────────────────┼───────────────────┘
▼                   ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ PostgreSQL    │ │ Redis         │ │ LLM APIs      │
│ (Primary DB)  │ │ (Cache,       │ │(OpenAI/Gemini)│
│               │ │ Session)      │ │               │
└───────────────┘ └───────────────┘ └───────────────┘
```

## Architecture Layers

### 1. Presentation Layer (Frontend)
- **Technology**: React 18+ with MUI 5
- **Responsibilities**:
  - User interface and user experience
  - Client-side validation
  - State management
  - Real-time updates via WebSocket/SSE
- **Components**:
  - Dashboard
  - Transaction management
  - Budget & Goals
  - AI Chat widget
  - Reports & Analytics

### 2. API Gateway Layer
- **Technology**: Spring Cloud Gateway
- **Responsibilities**:
  - Request routing
  - Authentication/Authorization
  - Rate limiting
  - Load balancing
  - API versioning
- **Features**:
  - JWT validation
  - CORS handling
  - Request/response logging
  - Circuit breaker for AI module

### 3. Application Layer (Backend Modules)

#### 3.1 Auth Module
- **Purpose**: User authentication and authorization
- **Features**:
  - Registration/Login
  - JWT token management
  - Password reset
  - OAuth2 integration (future)
- **Security**: BCrypt, JWT, refresh tokens

#### 3.2 Core PFM Module
- **Purpose**: Main business logic for finance management
- **Bounded Contexts**:
  - Account Context
  - Transaction Context
  - Category Context
  - Budget Context
  - Goal Context
  - Notification Context
- **Pattern**: DDD with Aggregate Roots

#### 3.3 AI Module
- **Purpose**: AI-powered chat and automation
- **Components**:
  - Chat Controller (SSE streaming)
  - Intent Parser (LLM integration)
  - Action Executor (execute user requests)
  - Context Manager (Redis session)
  - Privacy Filter (data anonymization)
- **Integration**: OpenAI/Gemini APIs with fallback

### 4. Data Layer

#### 4.1 PostgreSQL (Primary Database)
- **Purpose**: Persistent data storage
- **Data**:
  - Users, accounts, transactions
  - Categories, budgets, goals
  - Chat history, notifications
- **Features**:
  - ACID compliance
  - Connection pooling (HikariCP)
  - Read replicas (future)

#### 4.2 Redis (Cache & Session)
- **Purpose**: High-performance caching and session management
- **Usage**:
  - Dashboard cache (5 min TTL)
  - AI conversation context (30 min TTL)
  - User sessions
  - Rate limiting counters
- **Features**: Clustering for scalability

#### 4.3 LLM APIs (External)
- **Purpose**: AI capabilities
- **Providers**:
  - OpenAI GPT-4 Turbo (primary)
  - Google Gemini Pro (alternative)
- **Features**:
  - Streaming responses
  - Token usage tracking
  - Fallback mechanism

## Design Patterns

### Domain-Driven Design (DDD)
- **Aggregate Roots**: Account, User, Goal
- **Entities**: Transaction, Budget, Category
- **Value Objects**: Money, Currency, Percentage
- **Domain Events**: TransactionCreated, BudgetExceeded, GoalAchieved
- **Repositories**: Interface-based, implementation in infrastructure layer

### Hexagonal Architecture (Ports & Adapters)
- **Domain Layer**: Core business logic (inner)
- **Application Layer**: Use cases and orchestration
- **Infrastructure Layer**: External adapters (DB, LLM, Email)
- **Presentation Layer**: Controllers and UI (outer)

## Data Flow Examples

### Transaction Creation Flow
1. User submits transaction form (Frontend)
2. API Gateway validates JWT
3. Transaction Controller receives request
4. Transaction Service validates business rules
5. Account Repository checks balance (with optimistic lock)
6. Transaction Repository saves transaction
7. Account Repository updates balance
8. Cache Service invalidates dashboard cache
9. Notification Service checks budget alerts
10. Response returned to user

### AI Chat Flow
1. User sends message in chat widget
2. Message sent to Chat Controller via SSE
3. Context Manager retrieves session from Redis
4. Privacy Filter anonymizes user data
5. LLM Service sends prompt to OpenAI/Gemini
6. Intent Parser extracts structured intent
7. If action required:
   - Show confirmation to user
   - Wait for user confirmation
   - Execute action via appropriate service
8. Response formatted and streamed back
9. Chat history saved to PostgreSQL
10. Context updated in Redis