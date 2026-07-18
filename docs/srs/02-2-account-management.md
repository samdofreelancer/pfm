# 2.2 Account Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-07** | Default account "Cash" with zero balance is auto-created upon registration. | **Critical** |
| **FR-08** | Users can create multiple accounts (e.g., Bank Card, E-wallet) with custom names and initial balance. | **Critical** |
| **FR-09** | Users can edit account name and toggle active/inactive status. | **Critical** |
| **FR-10** | Account balance auto-updates on transactions. Uses Optimistic Locking to prevent concurrent balance errors. | **Critical** |
| **FR-11** | Multi-currency support (VND, USD, EUR). Default VND. Exchange rates updated manually or via free API (v2.0). | **High** |

## Detailed Specifications

### FR-07: Default Account Creation
- **Trigger**: User registration
- **Process**: Automatically create "Cash" account with balance = 0
- **Currency**: Default to VND
- **Status**: Active by default

### FR-08: Create Multiple Accounts
- **Input**: Account name, initial balance, currency
- **Validation**: Name uniqueness per user, positive initial balance
- **Output**: New account created
- **Example accounts**: Bank Card, E-wallet (Momo, ZaloPay), Savings

### FR-09: Edit Account
- **Editable fields**: Account name, active/inactive status
- **Constraints**: Cannot delete default "Cash" account
- **Inactive accounts**: Excluded from transaction creation dropdown

### FR-10: Balance Management with Optimistic Locking
- **Mechanism**: Version field in accounts table
- **Process**: 
  1. Read account with current version
  2. Update balance with version check
  3. If version mismatch, retry or throw concurrency error
- **Auto-update**: Balance changes on transaction create/update/delete

### FR-11: Multi-Currency Support
- **Supported currencies**: VND, USD, EUR (extensible)
- **Default**: VND
- **Exchange rates**: 
  - Manual update by user (v1.0)
  - Auto-fetch from free API (v2.0)
- **Display**: Show balance in account currency