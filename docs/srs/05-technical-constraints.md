# 5. Technical Constraints

This section defines the technical stack and constraints for the project.

## Technology Stack

| Category | Requirement |
|----------|-------------|
| **Backend** | Java 17+, Spring Boot 3.2+, Spring Security, Spring Data JPA, PostgreSQL 15+, Redis 7+ |
| **LLM Integration** | OpenAI API (GPT-4 Turbo) or Gemini Pro (switchable). Optional: local models (Llama, Vicuna) for offline use. |
| **Frontend** | React 18+, MUI 5, Recharts, Axios, React Router, Formik + Yup, WebSocket/SSE for streaming chat |
| **Build & Deploy** | Maven/Gradle, Docker, CI/CD (GitHub Actions), Kubernetes (future) |
| **API Documentation** | OpenAPI 3 (Swagger) |
| **Testing** | JUnit 5, Mockito, Testcontainers (integration), Cypress (E2E) |
| **Logging** | SLF4J + Logback, ELK Stack (future) |
| **Monitoring** | Prometheus + Grafana for metrics, track LLM token usage and cost |

## Detailed Constraints

### Backend Constraints
- **Java version**: 17 or higher (LTS)
- **Spring Boot**: 3.2+ (requires Java 17)
- **Database**: PostgreSQL 15+ for optimal performance
- **Cache**: Redis 7+ for session and data caching
- **API style**: RESTful with JSON
- **Architecture**: DDD with hexagonal/clean architecture

### AI/LLM Constraints
- **Primary LLM**: OpenAI GPT-4 Turbo
- **Alternative**: Google Gemini Pro
- **Fallback**: Local models (Llama 2, Vicuna) for offline mode
- **Streaming**: Server-Sent Events (SSE) for real-time responses
- **Token limit**: Context window management for cost control
- **Fallback**: Graceful degradation if LLM unavailable

### Frontend Constraints
- **Framework**: React 18+ with functional components and hooks
- **UI Library**: MUI 5 (Material UI)
- **Charts**: Recharts for data visualization
- **State management**: React Context + hooks (Redux Toolkit for complex state)
- **Forms**: Formik + Yup for validation
- **HTTP client**: Axios with interceptors
- **Routing**: React Router v6
- **Real-time**: WebSocket or SSE for chat streaming

### Infrastructure Constraints
- **Containerization**: Docker for all services
- **Orchestration**: Docker Compose (v1.0), Kubernetes (v2.0)
- **CI/CD**: GitHub Actions for automated testing and deployment
- **Environments**: Development, Staging, Production
- **Secrets management**: Environment variables, vault (future)

### Performance Constraints
- **Database connection pool**: HikariCP with optimized settings
- **Cache strategy**: Redis for hot data (dashboard, sessions)
- **CDN**: Static assets via CDN (future)
- **Compression**: Gzip/Brotli for API responses
- **Lazy loading**: Frontend code splitting

### Security Constraints
- **Authentication**: JWT with refresh token rotation
- **Authorization**: Role-based access control (RBAC)
- **Password hashing**: BCrypt with strength 12
- **CORS**: Whitelist specific origins
- **Rate limiting**: Per-user and per-IP limits
- **Input validation**: Server-side validation for all inputs
- **SQL injection prevention**: Parameterized queries via JPA
- **XSS prevention**: Input sanitization, CSP headers

### Monitoring Constraints
- **Metrics**: Prometheus for application metrics
- **Visualization**: Grafana dashboards
- **Logging**: Structured logging with ELK stack (future)
- **Tracing**: Distributed tracing for microservices (future)
- **Alerts**: PagerDuty/OpsGenie integration (future)
- **Cost tracking**: Monitor LLM token usage and costs