# 2.9 AI Assistant (Chat)

| ID | Description | Priority |
|----|-------------|----------|
| **FR-AI-01** | Users can chat with AI Assistant via embedded chat widget. AI understands Vietnamese and English natural language. | **Critical** |
| **FR-AI-02** | AI can **execute actions** based on user requests: <br> • Add/Edit/Delete transactions (income/expense/transfer) <br> • Create new budgets <br> • Create goals <br> • Contribute to goals <br> • Export reports (return file or summary) <br> • Set reminders (temporary) | **Critical** |
| **FR-AI-03** | AI can **answer queries** without modifying data: <br> • "What's my current balance?" <br> • "How much did I spend on food this month?" <br> • "Show my biggest expense categories" <br> • "Budget status" <br> • "Goal progress" <br> • "Recent transactions in category X" | **Critical** |
| **FR-AI-04** | AI provides **personalized financial advice**: <br> • Suggest spending cuts based on actual data <br> • Recommend reasonable budgets for each category <br> • Early warning if budget will be exceeded <br> • Create savings plan to achieve goals | **High** |
| **FR-AI-05** | AI maintains **conversation context** (per session). It can ask clarifying questions if information is missing (e.g., "Which account should I use?"). | **Critical** |
| **FR-AI-06** | Voice input (speech-to-text) and voice output (TTS) on mobile (future release). | **High** |
| **FR-AI-07** | Chat history is stored and searchable (recall past conversations). | **High** |
| **FR-AI-08** | Users can choose "Expert Mode" for in-depth analysis with charts, or "Simple Mode" for quick guidance. | **High** |
| **FR-AI-09** | **All AI actions MUST be confirmed by the user** before execution (prevent errors). | **Critical** |
| **FR-AI-10** | AI must maintain **privacy**: No sensitive data (balance, transactions) stored in third-party LLM. Data anonymized before sending. | **Critical** |
| **FR-AI-11** | Users can disable AI chat or limit permissions (e.g., query only, no actions). | **Medium** |

## Detailed Specifications

### FR-AI-01: Chat Interface
- **Widget**: Embedded chat panel in web app
- **Languages**: Vietnamese and English (auto-detect or user selection)
- **Input**: Text input with send button
- **Output**: Streaming response (typewriter effect)
- **UI**: Modern chat interface with message bubbles

### FR-AI-02: Action Execution
- **Supported actions**:
  - ADD_INCOME, ADD_EXPENSE, ADD_TRANSFER
  - EDIT_TRANSACTION, DELETE_TRANSACTION
  - CREATE_BUDGET, UPDATE_BUDGET
  - CREATE_GOAL, CONTRIBUTE_GOAL
  - EXPORT_REPORT
  - SET_REMINDER
- **Confirmation flow**:
  1. AI parses intent and parameters
  2. Shows confirmation message with details
  3. User confirms or modifies
  4. Execute action
  5. Return result

### FR-AI-03: Query Responses
- **Read-only operations**: No data modification
- **Examples**:
  - Balance queries: Sum of all accounts
  - Spending analysis: By category, time period
  - Budget status: Current vs limit
  - Goal progress: Percentage and remaining
  - Transaction history: Filtered lists
- **Response format**: Natural language with optional data tables

### FR-AI-04: Financial Advice
- **Spending analysis**: Identify top spending categories
- **Budget recommendations**: Based on historical data
- **Savings suggestions**: Identify areas to cut back
- **Goal planning**: Calculate required monthly savings
- **Early warnings**: Predict budget overruns

### FR-AI-05: Conversation Context
- **Session storage**: Redis (30 min TTL)
- **Context includes**:
  - Last 10 messages
  - User's recent transactions
  - Active budgets and goals
  - Clarification questions asked
- **Clarification**: Ask for missing information (account, category, amount)

### FR-AI-06: Voice Features (v2.0)
- **Speech-to-text**: Convert voice input to text
- **Text-to-speech**: Read AI responses aloud
- **Platform**: Mobile only (React Native/Flutter)
- **Languages**: Vietnamese and English

### FR-AI-07: Chat History
- **Storage**: PostgreSQL chat_history table
- **Search**: Full-text search across conversations
- **Recall**: "Show me last week's conversation about budgets"
- **Retention**: Keep indefinitely (user can delete)

### FR-AI-08: Expert vs Simple Mode
- **Simple Mode**:
  - Quick answers
  - Basic actions
  - No charts or detailed analysis
- **Expert Mode**:
  - Detailed analysis with charts
  - Advanced insights
  - MoM comparisons
  - Predictive analytics

### FR-AI-09: User Confirmation
- **Mandatory**: All actions require explicit user confirmation
- **Confirmation UI**: 
  - Show what will be executed
  - Allow editing before confirmation
  - Cancel button
- **Safety**: Prevent accidental data modification

### FR-AI-10: Privacy & Security
- **Data anonymization**: 
  - Replace real names with generic terms
  - Use category IDs instead of names
  - Send only necessary context
- **No PII**: Don't send email, phone, full name to LLM
- **Token minimization**: Only send relevant transaction data
- **Audit log**: Track what data was sent to LLM

### FR-AI-11: User Controls
- **Disable chat**: User can turn off AI assistant
- **Permission levels**:
  - Full access (queries + actions)
  - Query only (no data modification)
  - Disabled
- **Settings**: Per-user preference stored in database