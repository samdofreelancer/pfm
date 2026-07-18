# 🌐 API Overview

## Purpose

This document provides an overview of the PFM REST API, its structure, and the main resources it exposes.

## API Versioning

The API uses versioned endpoints following the pattern:

- `/api/v1/...`

## Core Resources

- `Auth` – authentication and user session management
- `Accounts` – financial account management
- `Transactions` – create, update, search transactions
- `Budgets` – manage spending plans and alerts
- `Goals` – manage savings goals and progress
- `Reports` – retrieve summaries and dashboard data
- `Chat` – AI-based chat and assistant interactions

## General API Principles

- Use HTTP status codes consistently
- Validate request payloads with DTO constraints
- Return structured responses with metadata when needed
- Use pagination for list endpoints
- Protect APIs with authentication and authorization

## Example Patterns

### Create resource
- `POST /api/v1/accounts`
- `201 Created`

### Read resource
- `GET /api/v1/accounts/{id}`
- `200 OK`

### Search / list resources
- `GET /api/v1/transactions?from=2026-01-01&to=2026-01-31`
- `200 OK`

### Update resource
- `PUT /api/v1/budgets/{id}`
- `200 OK`

### Delete resource
- `DELETE /api/v1/goals/{id}`
- `204 No Content`

## Error Handling

Errors should return consistent JSON payloads such as:

```json
{
  "code": 400,
  "message": "Validation failed",
  "details": [ ... ]
}
```

## Security

- All endpoints require a valid JWT token, except authentication and registration
- Use HTTPS in production
- Do not expose internal implementation details in error messages
