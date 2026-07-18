# 9. AI Integration Flow

This section details the AI integration architecture, intent processing, and prompt engineering for the AI Assistant feature.

## 9.1 Intent Processing Pipeline

```
User Message → LLM (with prompt template) → Parse JSON Intent
    ↓
Validate parameters → If missing → Ask clarifying question
    ↓
Generate confirmation message → Wait for user confirmation
    ↓
Execute Action → Call appropriate Service
    ↓
Format response → Update chat history → Return to UI
```

### Pipeline Stages

#### Stage 1: Message Reception
- **Input**: User message from chat widget
- **Context**: Retrieve session from Redis (last 10 messages, user context)
- **Privacy**: Anonymize sensitive data before sending to LLM

#### Stage 2: Intent Parsing
- **LLM Call**: Send message + context + prompt template to OpenAI/Gemini
- **Prompt Engineering**: Structured prompt with examples and constraints
- **Response**: JSON with intent, parameters, confidence, clarifying question
- **Fallback**: If LLM fails, return UNKNOWN intent with error message

#### Stage 3: Parameter Validation
- **Required params**: Check if all necessary parameters are present
- **Validation rules**: 
  - Amount > 0
  - Account exists and is active
  - Category exists and matches type
  - Date format valid
- **Missing params**: Generate clarifying question

#### Stage 4: Confirmation Generation
- **Action preview**: Show user what will be executed
- **Confirmation message**: Natural language summary
- **UI**: Display confirmation dialog with Edit/Cancel buttons

#### Stage 5: Action Execution
- **Wait for confirmation**: User confirms or modifies
- **Execute**: Call appropriate domain service
- **Transaction**: All DB operations in single transaction
- **Cache invalidation**: Update dashboard, budgets, etc.

#### Stage 6: Response Formatting
- **Success**: Natural language confirmation with details
- **Error**: Friendly error message with suggestions
- **Streaming**: Send response in chunks for typewriter effect

---

## 9.2 Prompt Template Structure

### System Prompt

```
You are a personal finance assistant for a Vietnamese user. Your job is to parse the user's message and extract their intent to manage their finances.

You must respond ONLY with valid JSON in this exact format:
{
  "intent": "ADD_INCOME | ADD_EXPENSE | ADD_TRANSFER | GET_BALANCE | GET_REPORT | GET_BUDGET | GET_GOAL | DELETE_TRANSACTION | UPDATE_TRANSACTION | ADVICE | UNKNOWN",
  "params": {
    "amount": number or null,
    "category": "category name or null",
    "account": "account name or null",
    "date": "YYYY-MM-DD or null",
    "note": "string or null",
    "target_category": "for transfers, destination category or null"
  },
  "confidence": 0.0 to 1.0,
  "clarifying_question": "Ask if info is missing, or null if complete"
}

Rules:
1. Today's date is: {current_date}
2. User's accounts: {accounts_list}
3. User's categories: {categories_list}
4. Currency: VND (Vietnamese Dong)
5. If amount mentioned as "500k", convert to 500000
6. If date mentioned as "today", use {current_date}
7. If date mentioned as "yesterday", use yesterday's date
8. If category not found, use closest match or ask
9. If account not found, use closest match or ask
10. Confidence should reflect how certain you are

Examples:

User: "I spent 500k on food today"
Response:
{
  "intent": "ADD_EXPENSE",
  "params": {
    "amount": 500000,
    "category": "Food",
    "account": null,
    "date": "2026-07-18",
    "note": null
  },
  "confidence": 0.9,
  "clarifying_question": "Which account should I deduct from?"
}

User: "Transfer 1 million from Momo to Bank"
Response:
{
  "intent": "ADD_TRANSFER",
  "params": {
    "amount": 1000000,
    "category": null,
    "account": "Momo",
    "date": "2026-07-18",
    "note": null,
    "target_account": "Bank"
  },
  "confidence": 0.95,
  "clarifying_question": null
}

User: "How much did I spend on food this month?"
Response:
{
  "intent": "GET_REPORT",
  "params": {
    "amount": null,
    "category": "Food",
    "account": null,
    "date": "2026-07-18",
    "note": null,
    "report_type": "category_spending",
    "period": "month"
  },
  "confidence": 0.9,
  "clarifying_question": null
}
```

### Intent Types

| Intent | Description | Required Params | Action |
|--------|-------------|-----------------|--------|
| **ADD_INCOME** | Add income transaction | amount, category, account | Create INCOME transaction |
| **ADD_EXPENSE** | Add expense transaction | amount, category, account | Create EXPENSE transaction |
| **ADD_TRANSFER** | Transfer between accounts | amount, account, target_account | Create TRANSFER transaction |
| **GET_BALANCE** | Query current balance | none | Return sum of all accounts |
| **GET_REPORT** | Generate report | category (optional), period | Return spending analysis |
| **GET_BUDGET** | Query budget status | category (optional) | Return budget usage |
| **GET_GOAL** | Query goal progress | none | Return goal status |
| **DELETE_TRANSACTION** | Delete transaction | transaction_id or date/amount/category | Soft delete transaction |
| **UPDATE_TRANSACTION** | Edit transaction | transaction_id, fields to update | Update transaction |
| **ADVICE** | Financial advice | none | Analyze data and suggest |
| **UNKNOWN** | Unrecognized intent | none | Ask user to rephrase |

---

## 9.3 Context & Memory

### Short-Term Memory (Redis)
- **Storage**: Redis with 30-minute TTL
- **Key**: `chat:context:{userId}:{sessionId}`
- **Data structure**:
```json
{
  "session_id": "uuid",
  "user_id": "uuid",
  "messages": [
    {"role": "user", "content": "..."},
    {"role": "assistant", "content": "..."}
  ],
  "last_intent": "ADD_EXPENSE",
  "last_params": {...},
  "clarification_count": 2,
  "created_at": "2026-07-18T10:00:00Z",
  "last_accessed": "2026-07-18T10:30:00Z"
}
```
- **Usage**: Maintain conversation context, remember previous intents

### Long-Term Memory (PostgreSQL)
- **Table**: chat_history
- **Retention**: Indefinite (user can delete)
- **Indexing**: Full-text search on message and response
- **Usage**: Search past conversations, analyze user behavior

### Semantic Memory (Optional - Vector DB)
- **Technology**: Pinecone, Weaviate, or pgvector
- **Storage**: Embeddings of chat messages
- **Usage**: Semantic search across conversations
- **Example**: "Find conversations about budgeting"

---

## 9.4 Privacy & Data Anonymization

### Data Minimization
Before sending to LLM, filter and anonymize:

**Include:**
- Transaction amounts (aggregated)
- Category names (generic)
- Account types (not specific names)
- Date ranges (not specific dates unless needed)

**Exclude:**
- User email, phone, full name
- Specific account numbers
- Exact transaction notes with PII
- User location (unless relevant)

### Anonymization Example

**Before:**
```
User: "I spent 500k on groceries at Co.opmart for my family"
Context: User has account "Vietcombank ****1234", email john@gmail.com
```

**After:**
```
Message: "I spent 500k on groceries"
Context: User has 3 accounts (Bank, E-wallet, Cash), 12 categories
```

### Audit Logging
Log all LLM requests for compliance:
```sql
CREATE TABLE llm_audit_log (
    id UUID PRIMARY KEY,
    user_id UUID,
    session_id UUID,
    prompt_tokens INTEGER,
    completion_tokens INTEGER,
    model VARCHAR(50),
    anonymized_data JSONB,
    created_at TIMESTAMP
);
```

---

## 9.5 Error Handling & Fallback

### LLM Service Unavailable
- **Fallback**: Return error message: "AI assistant is temporarily unavailable. Please try again later."
- **Alternative**: Show suggested quick actions based on common intents
- **Retry**: Exponential backoff, max 3 retries

### Intent Parsing Failure
- **Low confidence (< 0.5)**: Ask user to rephrase
- **Missing parameters**: Ask clarifying question
- **Invalid parameters**: Show error and ask for correction

### Action Execution Failure
- **Validation error**: Show specific error (e.g., "Insufficient balance")
- **System error**: Log error, show generic message
- **Rollback**: If action partially executed, rollback changes

---

## 9.6 Streaming Response

### Server-Sent Events (SSE)
```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive

data: {"type": "intent", "data": {"intent": "ADD_EXPENSE", "confidence": 0.95}}

data: {"type": "status", "data": {"message": "Creating transaction..."}}

data: {"type": "result", "data": {"response": "Done! Transaction created.", "transaction_id": "uuid"}}

data: {"type": "done"}
```

### Frontend Handling
- **Typewriter effect**: Display text as it arrives
- **Intermediate states**: Show "Thinking...", "Creating transaction..."
- **Error handling**: Display error message if stream interrupted

---

## 9.7 Cost Optimization

### Token Management
- **Context window**: Limit to last 10 messages (≈ 1000 tokens)
- **Prompt template**: Keep under 500 tokens
- **Response limit**: Max 500 tokens per response
- **Caching**: Cache common questions and responses

### Model Selection
- **Simple queries** (balance, budget): Use GPT-3.5 or Gemini Flash (cheaper)
- **Complex actions** (transactions, analysis): Use GPT-4 or Gemini Pro
- **Fallback**: If primary model fails, try alternative

### Caching Strategy
- **Common questions**: Cache for 1 hour
- **Dashboard data**: Cache for 5 minutes
- **User context**: Cache for 30 minutes
- **Cache key**: Hash of (user_id + message + context_hash)