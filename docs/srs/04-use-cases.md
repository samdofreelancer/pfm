# 4. Use Cases

This section describes key use cases that illustrate how users interact with the system.

## 4.1 AI Use Case: "Add Expense via Chat"

| Field | Details |
|-------|---------|
| **Actor** | Logged-in User |
| **Precondition** | User has at least one account and expense category |
| **Main Flow** | 1. User types: *"I spent 500k on shopping today, deduct from Momo wallet"* <br> 2. AI analyzes intent → identifies: type (EXPENSE), category (Shopping), amount (500,000), account (Momo), date (today). <br> 3. AI displays confirmation: *"I'll create an expense of 500,000 VND in 'Shopping' from 'Momo' account today. Correct?"* <br> 4. User confirms. <br> 5. System creates transaction, updates balance, budget, cache. <br> 6. AI responds: *"Done! Your Momo balance is now X VND."* |
| **Exception Flow** | • Missing information → AI asks clarifying questions. <br> • Insufficient balance → AI warns and suggests another account. |
| **Result** | Transaction created without manual form filling. |

## 4.2 Use Case: "Transfer Between Accounts"

| Field | Details |
|-------|---------|
| **Actor** | Logged-in User |
| **Precondition** | User has at least two active accounts |
| **Main Flow** | 1. User selects "Transfer" on dashboard. <br> 2. Enters: from account, to account, amount. <br> 3. System validates: sufficient balance, different accounts. <br> 4. Transaction created with type TRANSFER. <br> 5. Balances updated atomically. <br> 6. Success message shown. |
| **Exception Flow** | • Insufficient balance → Error message. <br> • Same account selected → Validation error. |
| **Result** | Funds transferred between accounts. |

## 4.3 Use Case: "Set Budget and Get Alert"

| Field | Details |
|-------|---------|
| **Actor** | Logged-in User |
| **Precondition** | User has expense categories |
| **Main Flow** | 1. User creates budget: "Food - 2,000,000 VND/month". <br> 2. System tracks spending on Food category. <br> 3. At 80% (1,600,000 VND): Yellow warning shown. <br> 4. At 100% (2,000,000 VND): Red alert + email notification. <br> 5. User adjusts spending or increases budget. |
| **Exception Flow** | • Category deleted → Budget auto-deleted. <br> • No transactions → No alerts sent. |
| **Result** | User stays within budget or gets notified. |

## 4.4 Use Case: "Achieve Savings Goal"

| Field | Details |
|-------|---------|
| **Actor** | Logged-in User |
| **Precondition** | User has active account with balance |
| **Main Flow** | 1. User creates goal: "New Laptop - 15,000,000 VND by Dec 2026". <br> 2. User contributes 5,000,000 VND from Bank account. <br> 3. Progress bar shows 33% completion. <br> 4. User contributes another 10,000,000 VND. <br> 5. System detects goal reached (100%). <br> 6. Celebration message shown, notification sent. |
| **Exception Flow** | • Insufficient account balance → Error. <br> • Goal overdue → Reminder notification. |
| **Result** | Goal achieved, user motivated. |

## 4.5 Use Case: "Ask AI for Financial Advice"

| Field | Details |
|-------|---------|
| **Actor** | Logged-in User |
| **Precondition** | AI chat enabled, user has transaction history |
| **Main Flow** | 1. User asks: *"How can I save more money this month?"* <br> 2. AI analyzes: spending patterns, budgets, goals. <br> 3. AI identifies: Food spending 30% over budget, unused subscriptions. <br> 4. AI suggests: *"Cut food delivery by 500k, cancel unused gym membership (200k). You can save 700k/month."* <br> 5. User can create budget or goal based on advice. |
| **Exception Flow** | • Insufficient data → AI asks for more context. <br> • AI disabled → Show message to enable in settings. |
| **Result** | User receives personalized financial advice. |