# 🤖 AI Chat API

## Overview

This document describes the AI chat endpoints used for conversational interactions and assistant features in PFM.

## Endpoints

### Send Chat Message
- `POST /api/v1/chat/message`
- Request: `ChatMessageRequest`
- Response: `ChatMessageResponse`

### Get Chat History
- `GET /api/v1/chat/sessions/{sessionId}`
- Response: list of `ChatMessageResponse`

### Create Chat Session
- `POST /api/v1/chat/sessions`
- Request: `CreateChatSessionRequest`
- Response: `ChatSessionResponse`

## Request / Response Shapes

### ChatMessageRequest
- `sessionId` (string, required)
- `message` (string, required)
- `language` (string, optional)

### ChatMessageResponse
- `messageId` (string)
- `sessionId` (string)
- `sender` (string)
- `message` (string)
- `createdAt` (datetime)

## Notes

- Chat endpoints may call external AI services to interpret intent and generate responses
- The AI chat feature can support commands such as adding an expense or querying balances
- Maintain session context so the assistant can understand follow-up queries
