# 💳 Transaction API

## Overview

This document describes the transaction endpoints used to record financial activity in PFM.

## Endpoints

### Create Transaction
- `POST /api/v1/transactions`
- Request: `CreateTransactionRequest`
- Response: `TransactionResponse`

### Get Transaction
- `GET /api/v1/transactions/{transactionId}`
- Response: `TransactionResponse`

### List Transactions
- `GET /api/v1/transactions`
- Query parameters: `from`, `to`, `type`, `categoryId`, `accountId`, `page`, `size`
- Response: paged list of `TransactionResponse`

### Update Transaction
- `PUT /api/v1/transactions/{transactionId}`
- Request: `UpdateTransactionRequest`
- Response: `TransactionResponse`

### Delete Transaction
- `DELETE /api/v1/transactions/{transactionId}`
- Response: `204 No Content`

## Request / Response Shapes

### CreateTransactionRequest
- `accountId` (string, required)
- `categoryId` (string, required)
- `amount` (decimal, required)
- `type` (string, required, `INCOME`, `EXPENSE`, `TRANSFER`)
- `note` (string, optional)
- `transactionDate` (date, optional)

### TransactionResponse
- `id` (string)
- `userId` (string)
- `accountId` (string)
- `categoryId` (string)
- `amount` (decimal)
- `type` (string)
- `note` (string)
- `transactionDate` (date)
- `status` (string)
- `createdAt` (datetime)

## Notes

- Expense transactions deduct from account balance
- Income transactions increase account balance
- Transfer transactions move funds between accounts
- Use query parameters for filtering and pagination
