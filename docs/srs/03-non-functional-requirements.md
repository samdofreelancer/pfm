# 3. Non-Functional Requirements

| ID | Description | Priority |
|----|-------------|----------|
| **NFR-01** | **Performance:** API response < 200ms for 95% requests (excluding reports & AI). AI response < 2 seconds. | **Critical** |
| **NFR-02** | **Availability:** System uptime ≥ 99.5% (excluding scheduled maintenance). | **High** |
| **NFR-03** | **Security:** HTTPS, JWT tokens, BCrypt, CORS restricted to frontend domain. AI data anonymized. | **Critical** |
| **NFR-04** | **Data Backup:** Daily automated PostgreSQL backups. | **Critical** |
| **NFR-05** | **Scalability:** DDD architecture supports easy feature addition. Modular design. | **Medium** |
| **NFR-06** | **Testability:** Unit test coverage ≥ 80% for domain layer. Integration tests for AI flows (LLM mocked). | **Critical** |
| **NFR-07** | **Internationalization:** UI and AI chat support Vietnamese and English (i18n). | **High** |
| **NFR-08** | **Responsive:** UI works on desktop, tablet, and mobile. | **Critical** |
| **NFR-09** | **Cost Optimization:** Minimize LLM token usage – only send necessary context, use caching for common questions. | **High** |

## Detailed Specifications

### NFR-01: Performance
- **API Response Time**:
  - P50: < 100ms
  - P95: < 200ms
  - P99: < 500ms
- **Exclusions**: Report generation, PDF export, AI chat
- **AI Response Time**: < 2 seconds for streaming response
- **Database**: Indexed queries, connection pooling
- **Caching**: Redis for frequent queries (dashboard, reports)

### NFR-02: Availability
- **Uptime target**: 99.5% (≈ 3.65 days downtime/year)
- **Maintenance window**: Sundays 2:00-4:00 AM UTC
- **Monitoring**: Health checks, uptime monitoring
- **Redundancy**: Database replication, load balancing

### NFR-03: Security
- **Transport**: HTTPS/TLS 1.3 only
- **Authentication**: JWT with short expiry, refresh tokens
- **Password storage**: BCrypt with strength 12
- **CORS**: Whitelist frontend domains only
- **AI Privacy**: 
  - Anonymize data before sending to LLM
  - No PII in prompts
  - Audit log for all AI requests
- **Rate limiting**: Prevent abuse of AI endpoint

### NFR-04: Data Backup
- **Frequency**: Daily automated backups
- **Retention**: 30 days
- **Storage**: Off-site cloud storage
- **Recovery**: Point-in-time recovery capability
- **Testing**: Monthly backup restoration test

### NFR-05: Scalability
- **Architecture**: DDD with bounded contexts
- **Modularity**: Independent deployable modules
- **Database**: Connection pooling, read replicas
- **Caching**: Redis cluster for horizontal scaling
- **Future**: Microservices migration path

### NFR-06: Testability
- **Unit tests**: ≥ 80% coverage for domain layer
- **Integration tests**: All API endpoints
- **AI tests**: Mock LLM responses
- **E2E tests**: Critical user flows
- **Performance tests**: Load testing for peak traffic

### NFR-07: Internationalization
- **Languages**: Vietnamese (primary), English
- **UI**: All text translatable
- **AI chat**: Multi-language understanding
- **Date/Time**: Localized formats
- **Currency**: Multi-currency support with localization

### NFR-08: Responsive Design
- **Breakpoints**:
  - Desktop: > 1024px
  - Tablet: 768px - 1024px
  - Mobile: < 768px
- **Touch-friendly**: Mobile gestures and buttons
- **Progressive Web App**: Offline capability (v2.0)

### NFR-09: Cost Optimization
- **LLM usage**: 
  - Cache common questions
  - Minimize context window
  - Use cheaper models for simple queries
- **Token budget**: Track and limit per user
- **Caching strategy**: 
  - Dashboard: 5 min TTL
  - Reports: 15 min TTL
  - Common queries: 1 hour TTL