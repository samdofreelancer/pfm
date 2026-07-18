# 1. Introduction

## 1.1 Purpose

This document defines the complete requirements for **Personal Finance Manager (PFM)** – a next-generation personal finance application that combines traditional financial tracking with an **AI-powered conversational assistant**. Users can manage their finances through both manual input and natural language chat interactions.

## 1.2 Scope

- **Backend:** RESTful API with Spring Boot, DDD architecture
- **Frontend:** React + MUI (Web), Flutter (Mobile - future)
- **AI Integration:** OpenAI GPT-4 / Gemini / Claude via API
- **Database:** PostgreSQL + Redis cache + optional Vector DB
- **Authentication:** JWT with refresh tokens, OAuth2 (Google) optional

## 1.3 Definitions & Abbreviations

| Term | Definition |
|------|------------|
| **PFM** | Personal Finance Manager |
| **SRS** | Software Requirements Specification |
| **DDD** | Domain-Driven Design |
| **LLM** | Large Language Model (GPT-4, Gemini, Claude) |
| **JWT** | JSON Web Token |
| **Intent** | User's intention extracted from chat messages |
| **Action** | System operation triggered by AI (add transaction, generate report, etc.) |
| **MoM** | Month-over-Month comparison |