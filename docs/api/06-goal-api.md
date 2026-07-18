# 🎯 Goal API

## Overview

This document describes the goal management endpoints for savings targets in PFM.

## Endpoints

### Create Goal
- `POST /api/v1/goals`
- Request: `CreateGoalRequest`
- Response: `GoalResponse`

### Get Goal
- `GET /api/v1/goals/{goalId}`
- Response: `GoalResponse`

### List Goals
- `GET /api/v1/goals`
- Response: list of `GoalResponse`

### Update Goal
- `PUT /api/v1/goals/{goalId}`
- Request: `UpdateGoalRequest`
- Response: `GoalResponse`

### Delete Goal
- `DELETE /api/v1/goals/{goalId}`
- Response: `204 No Content`

### Contribute to Goal
- `POST /api/v1/goals/{goalId}/contributions`
- Request: `GoalContributionRequest`
- Response: `GoalResponse`

## Request / Response Shapes

### CreateGoalRequest
- `name` (string, required)
- `targetAmount` (decimal, required)
- `deadline` (date, required)
- `initialContribution` (decimal, optional)

### GoalResponse
- `id` (string)
- `userId` (string)
- `name` (string)
- `targetAmount` (decimal)
- `currentAmount` (decimal)
- `deadline` (date)
- `progress` (integer)
- `status` (string)
- `createdAt` (datetime)

## Notes

- Goals represent savings milestones with deadlines
- Contributions update progress toward the target amount
- Status values may include `ACTIVE`, `ACHIEVED`, `CANCELLED`
