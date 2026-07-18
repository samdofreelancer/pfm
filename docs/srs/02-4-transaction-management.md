# 2.4 Transaction Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-15** | Users can add transactions: type (INCOME/EXPENSE/TRANSFER), category, account, amount, date (default today), note. | **Critical** |
| **FR-16** | Expense transactions check account balance – insufficient balance throws error. | **Critical** |
| **FR-17** | Transfer transactions: deduct from account A, add to account B in a single ACID transaction. | **Critical** |
| **FR-18** | Users can edit/delete transactions. Balance is rolled back correctly. | **Critical** |
| **FR-19** | Recurring transactions: users can create templates (daily/weekly/monthly). System auto-generates transactions on schedule. | **High** |
| **FR-20** | Transaction list supports pagination, sorting by date (newest first). | **Critical** |
| **FR-21** | Search transactions by keyword (note, category), filter by date range, type, account. | **Critical** |
| **FR-22** | Export filtered transaction list as CSV. | **Critical** |

## Detailed Specifications

### FR-15: Create Transaction
- **Input**: Type (INCOME/EXPENSE/TRANSFER), category, account, amount, date, note
- **Validation**: 
  - Amount > 0
  - Account exists and is active
  - Category exists and matches type
  - Date not in future (for manual entry)
- **Default**: Date = today if not specified
- **Output**: Transaction created, balance updated

### FR-16: Balance Validation
- **Check**: Before creating EXPENSE transaction
- **Validation**: Account balance >= transaction amount
- **Error**: Insufficient balance exception with current balance shown
- **Currency**: Check in account's currency

### FR-17: Transfer Transactions
- **Type**: TRANSFER
- **Process**: 
  1. Deduct amount from source account
  2. Add amount to destination account
  3. Both operations in single ACID transaction
- **Validation**: 
  - Source and destination accounts must be different
  - Source account must have sufficient balance
  - Both accounts must be active
- **Fee**: Optional transfer fee (deducted from source)

### FR-18: Edit/Delete Transactions
- **Edit**: Update any field, recalculate balances
- **Delete**: Soft delete, reverse balance changes
- **Balance rollback**: 
  - EXPENSE: Add amount back to account
  - INCOME: Subtract amount from account
  - TRANSFER: Reverse both accounts
- **History**: Keep audit trail of changes

### FR-19: Recurring Transactions
- **Frequency**: Daily, Weekly, Monthly
- **Configuration**: 
  - Base transaction details
  - Start date, end date (optional)
  - Next occurrence date
- **Auto-generation**: Background job runs daily
- **Creation**: Generate transaction, update next occurrence
- **Skip option**: User can skip next occurrence

### FR-20: Transaction List
- **Pagination**: Page size 20, 50, 100
- **Sorting**: By date (newest first by default), amount, category
- **Performance**: Indexed queries, cached counts

### FR-21: Search and Filter
- **Search**: Keyword in note, category name
- **Filters**: 
  - Date range (from, to)
  - Type (INCOME, EXPENSE, TRANSFER)
  - Account
  - Category
  - Amount range
- **Combination**: Multiple filters can be applied

### FR-22: Export to CSV
- **Format**: CSV with headers
- **Columns**: Date, Type, Category, Account, Amount, Note
- **Filtered export**: Respects current search/filter criteria
- **Encoding**: UTF-8 with BOM for Excel compatibility