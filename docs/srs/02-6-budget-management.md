# 2.6 Budget Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-28** | Users can set monthly spending limits per expense category. | **Critical** |
| **FR-29** | System auto-calculates spent amount for the month and displays usage percentage. | **Critical** |
| **FR-30** | Warning when spending exceeds 80% (yellow). Alert when exceeds 100% (red) + email notification. | **Critical** |
| **FR-31** | Users can edit/delete budgets. | **Critical** |

## Detailed Specifications

### FR-28: Create Budget
- **Input**: Category, month/year, limit amount
- **Validation**: 
  - Category must be EXPENSE type
  - Limit amount > 0
  - One budget per category per month
- **Currency**: Budget in user's default currency
- **Output**: Budget created

### FR-29: Budget Tracking
- **Calculation**: Sum of EXPENSE transactions for category in selected month
- **Spent amount**: Real-time calculation with caching
- **Usage percentage**: (spent / limit) * 100
- **Remaining**: limit - spent
- **Display**: Progress bar with percentage

### FR-30: Budget Alerts
- **Warning threshold**: 80% usage
  - Visual: Yellow indicator
  - In-app notification
- **Alert threshold**: 100% usage
  - Visual: Red indicator
  - In-app notification
  - Email notification (if enabled)
- **Notification content**: Category name, spent amount, limit amount
- **Frequency**: Once per threshold crossing per budget

### FR-31: Budget Management
- **Edit**: Update limit amount, month
- **Delete**: Remove budget, clear alerts
- **History**: Keep track of budget changes
- **Rollover**: Option to roll over unused budget to next month (v2.0)