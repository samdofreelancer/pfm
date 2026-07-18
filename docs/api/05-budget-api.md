# 📊 Budget API

## Overview

This document describes the budget management endpoints in PFM.

## Endpoints

### Create Budget
- `POST /api/v1/budgets`
- Request: `CreateBudgetRequest`
- Response: `BudgetResponse`

### Get Budget
- `GET /api/v1/budgets/{budgetId}`
- Response: `BudgetResponse`

### List Budgets
- `GET /api/v1/budgets`
- Response: list of `BudgetResponse`

### Update Budget
- `PUT /api/v1/budgets/{budgetId}`
- Request: `UpdateBudgetRequest`
- Response: `BudgetResponse`

### Delete Budget
- `DELETE /api/v1/budgets/{budgetId}`
- Response: `204 No Content`

## Request / Response Shapes

### CreateBudgetRequest
- `categoryId` (string, required)
- `limit` (decimal, required)
- `period` (string, required, e.g. `2026-07`)
- `alertThreshold` (integer, optional)

### BudgetResponse
- `id` (string)
- `userId` (string)
- `categoryId` (string)
- `limit` (decimal)
- `spent` (decimal)
- `period` (string)
- `status` (string)
- `createdAt` (datetime)

## Notes

- Budgets track spending per category for a defined period
- Alerts may trigger when spending reaches threshold values
- Budget status should reflect current usage and whether limits are exceeded
