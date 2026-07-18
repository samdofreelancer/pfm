# 📈 Report API

## Overview

This document describes reporting and dashboard endpoints for PFM.

## Endpoints

### Get Dashboard Summary
- `GET /api/v1/reports/dashboard`
- Response: `DashboardSummaryResponse`

### Get Spending Report
- `GET /api/v1/reports/spending`
- Query parameters: `from`, `to`, `categoryId`
- Response: `SpendingReportResponse`

### Get Income Report
- `GET /api/v1/reports/income`
- Query parameters: `from`, `to`, `accountId`
- Response: `IncomeReportResponse`

### Get Budget Status
- `GET /api/v1/reports/budgets`
- Response: list of `BudgetStatusResponse`

### Get Goal Progress
- `GET /api/v1/reports/goals`
- Response: list of `GoalProgressResponse`

## Notes

- Reporting endpoints aggregate transaction, budget, and goal data
- Use query filters for date ranges and categories
- Responses should be optimized for dashboard consumption
