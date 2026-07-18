# 🏦 Account API

## Overview

This document describes the account management endpoints available in the PFM API.

## Endpoints

### Create Account
- `POST /api/v1/accounts`
- Request: `CreateAccountRequest`
- Response: `AccountResponse`

### Get Account
- `GET /api/v1/accounts/{accountId}`
- Response: `AccountResponse`

### List Accounts
- `GET /api/v1/accounts`
- Response: list of `AccountResponse`

### Update Account
- `PUT /api/v1/accounts/{accountId}`
- Request: `UpdateAccountRequest`
- Response: `AccountResponse`

### Delete Account
- `DELETE /api/v1/accounts/{accountId}`
- Response: `204 No Content`

## Request / Response Shapes

### CreateAccountRequest
- `name` (string, required)
- `currency` (string, optional, default `VND`)
- `initialBalance` (decimal, optional)

### UpdateAccountRequest
- `name` (string, optional)
- `currency` (string, optional)
- `status` (string, optional)

### AccountResponse
- `id` (string)
- `userId` (string)
- `name` (string)
- `currency` (string)
- `balance` (decimal)
- `status` (string)
- `createdAt` (datetime)

## Notes

- Account balance updates should be performed through transaction endpoints when possible
- The account status may control whether the account can participate in new transactions
