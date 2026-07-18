# E2E Tests

This directory contains end-to-end tests using Playwright with Page Object Model.

## Setup

```bash
cd e2e
npm install
```

## Running Tests

```bash
# Run all tests
npm test

# Run tests with UI mode
npm run test:ui

# Show test report
npm run test:report
```

## Project Structure

```
e2e/
├── pages/
│   ├── LoginPage.ts      # Login page object
│   └── DashboardPage.ts  # Dashboard page object
├── tests/
│   └── login.spec.ts     # Authentication test cases
├── playwright.config.ts  # Playwright configuration
├── package.json
├── tsconfig.json
└── .gitignore
```

## Test Cases

- `should login successfully with valid credentials` - Tests login flow
- `should logout successfully` - Tests logout and redirect to login page