# 8. API Design (RESTful)

## Overview

This section defines the RESTful API endpoints for the Personal Finance Manager.

### Base URL
```
https://api.pfm.com/api/v1
```

### Authentication
All endpoints (except auth endpoints) require JWT token in Authorization header:
```
Authorization: Bearer {access_token}
```

---

## Core Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | Login → JWT | No |
| POST | `/auth/register` | Register user | No |
| POST | `/auth/refresh` | Refresh token | Yes |
| POST | `/auth/logout` | Logout (invalidate refresh token) | Yes |
| POST | `/auth/forgot-password` | Request password reset | No |
| POST | `/auth/reset-password` | Reset password with OTP | No |
| GET | `/users/me` | Get current user profile | Yes |
| PUT | `/users/me` | Update user profile | Yes |
| DELETE | `/users/me` | Delete user account | Yes |
| GET | `/accounts` | List user accounts | Yes |
| POST | `/accounts` | Create new account | Yes |
| GET | `/accounts/{id}` | Get account details | Yes |
| PUT | `/accounts/{id}` | Update account | Yes |
| DELETE | `/accounts/{id}` | Delete account | Yes |
| GET | `/categories` | List categories (system + custom) | Yes |
| POST | `/categories` | Create custom category | Yes |
| PUT | `/categories/{id}` | Update category | Yes |
| DELETE | `/categories/{id}` | Delete category | Yes |
| GET | `/transactions` | List transactions (pagination, filters) | Yes |
| POST | `/transactions` | Create transaction | Yes |
| GET | `/transactions/{id}` | Get transaction details | Yes |
| PUT | `/transactions/{id}` | Update transaction | Yes |
| DELETE | `/transactions/{id}` | Delete transaction | Yes |
| GET | `/transactions/export/csv` | Export transactions as CSV | Yes |
| GET | `/dashboard/summary` | Dashboard summary data | Yes |
| GET | `/reports/monthly` | Monthly report by category | Yes |
| GET | `/reports/export/pdf` | Export report as PDF | Yes |
| GET | `/budgets` | List user budgets | Yes |
| POST | `/budgets` | Create budget | Yes |
| PUT | `/budgets/{id}` | Update budget | Yes |
| DELETE | `/budgets/{id}` | Delete budget | Yes |
| GET | `/goals` | List user goals | Yes |
| POST | `/goals` | Create goal | Yes |
| GET | `/goals/{id}` | Get goal details | Yes |
| PUT | `/goals/{id}` | Update goal | Yes |
| DELETE | `/goals/{id}` | Delete goal | Yes |
| POST | `/goals/{id}/contribute` | Contribute to goal | Yes |
| GET | `/notifications` | List notifications | Yes |
| PUT | `/notifications/{id}/read` | Mark notification as read | Yes |
| DELETE | `/notifications/{id}` | Delete notification | Yes |
| POST | `/chat` | Send chat message → AI response | Yes |
| GET | `/chat/history` | Get chat history | Yes |
| DELETE | `/chat/clear` | Clear chat session | Yes |

---

## Detailed Endpoint Specifications

### Authentication Endpoints

#### POST /auth/login
**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 900,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "full_name": "John Doe"
  }
}
```

#### POST /auth/register
**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "full_name": "John Doe"
}
```

**Response (201 Created):**
```json
{
  "access_token": "...",
  "refresh_token": "...",
  "expires_in": 900,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "full_name": "John Doe"
  }
}
```

### Transaction Endpoints

#### GET /transactions
**Query Parameters:**
- `page` (default: 0)
- `size` (default: 20, max: 100)
- `sort` (default: date,desc)
- `startDate` (format: YYYY-MM-DD)
- `endDate` (format: YYYY-MM-DD)
- `type` (INCOME, EXPENSE, TRANSFER)
- `accountId` (UUID)
- `categoryId` (UUID)
- `search` (keyword in note)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "uuid",
      "type": "EXPENSE",
      "amount": 500000,
      "date": "2026-07-18",
      "note": "Lunch at restaurant",
      "category": {
        "id": "uuid",
        "name": "Food",
        "type": "EXPENSE"
      },
      "account": {
        "id": "uuid",
        "name": "Momo Wallet",
        "currency": "VND"
      }
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 0
}
```

#### POST /transactions
**Request:**
```json
{
  "type": "EXPENSE",
  "amount": 500000,
  "categoryId": "uuid",
  "accountId": "uuid",
  "date": "2026-07-18",
  "note": "Lunch at restaurant"
}
```

**Response (201 Created):**
```json
{
  "id": "uuid",
  "type": "EXPENSE",
  "amount": 500000,
  "date": "2026-07-18",
  "note": "Lunch at restaurant",
  "category": { "id": "uuid", "name": "Food" },
  "account": { "id": "uuid", "name": "Momo Wallet", "balance": 1500000 }
}
```

### Dashboard Endpoints

#### GET /dashboard/summary
**Response (200 OK):**
```json
{
  "totalBalance": 15000000,
  "monthlyIncome": 5000000,
  "monthlyExpense": 3200000,
  "remainingBudget": 1800000,
  "currency": "VND",
  "lastUpdated": "2026-07-18T10:30:00Z"
}
```

### AI Chat Endpoints

#### POST /chat
**Request:**
```json
{
  "message": "I spent 500k on food today",
  "sessionId": "uuid" // optional, maintained by frontend
}
```

**Response (Streaming SSE):**
```
data: {"type": "intent", "data": {"intent": "ADD_EXPENSE", "confidence": 0.95}}

data: {"type": "confirmation", "data": {"message": "I'll create an expense of 500,000 VND in 'Food' from 'Momo' account today. Correct?"}}

data: {"type": "result", "data": {"response": "Done! Your Momo balance is now X VND.", "action_executed": true, "transaction_id": "uuid"}}
```

**Or non-streaming JSON response:**
```json
{
  "response": "Done! Your Momo balance is now X VND.",
  "intent": "ADD_EXPENSE",
  "confidence": 0.95,
  "action_executed": true,
  "confirmation_required": false,
  "data": {
    "transaction_id": "uuid"
  }
}
```

#### GET /chat/history
**Query Parameters:**
- `sessionId` (UUID, optional)
- `limit` (default: 50, max: 100)
- `offset` (default: 0)

**Response (200 OK):**
```json
{
  "messages": [
    {
      "id": "uuid",
      "message": "I spent 500k on food",
      "response": "Done! Transaction created.",
      "intent": "ADD_EXPENSE",
      "timestamp": "2026-07-18T10:30:00Z"
    }
  ],
  "total": 42
}
```

---

## Error Responses

All errors follow this format:

```json
{
  "timestamp": "2026-07-18T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient balance",
  "details": {
    "current_balance": 300000,
    "required_amount": 500000
  },
  "path": "/api/v1/transactions"
}
```

### Common HTTP Status Codes
- `200 OK` - Successful GET
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Missing or invalid JWT
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation (e.g., insufficient balance)
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Server error
- `503 Service Unavailable` - LLM service unavailable

---

## Rate Limiting

| Endpoint Type | Limit | Window |
|---------------|-------|--------|
| Auth endpoints | 5 requests | 15 minutes |
| Standard API | 100 requests | 1 minute |
| AI Chat | 20 requests | 1 minute |
| Report export | 5 requests | 1 hour |

---

## Pagination

All list endpoints support pagination:

**Request:**
```
GET /transactions?page=0&size=20&sort=date,desc
```

**Response:**
```json
{
  "content": [...],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": {"field": "date", "direction": "DESC"}
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {"field": "date", "direction": "DESC"},
  "numberOfElements": 20
}