# 2.7 Goal Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-32** | Create goals: name, target amount, deadline, description. | **Critical** |
| **FR-33** | Users can contribute to goals from any account (deduct from account, add to goal's saved amount). | **Critical** |
| **FR-34** | Display progress bar and percentage completion. | **Critical** |
| **FR-35** | Celebrate when goal is achieved. | **Critical** |

## Detailed Specifications

### FR-32: Create Goal
- **Input**: 
  - Name (required)
  - Target amount (required, > 0)
  - Deadline (required, future date)
  - Description (optional)
- **Validation**: 
  - Target amount > 0
  - Deadline must be in the future
  - Name unique per user
- **Initial state**: saved_amount = 0, status = IN_PROGRESS
- **Currency**: Goal amount in user's default currency

### FR-33: Contribute to Goal
- **Input**: Goal ID, amount, source account
- **Validation**: 
  - Goal exists and is IN_PROGRESS
  - Amount > 0
  - Account has sufficient balance
  - Account is active
- **Process**: 
  1. Deduct amount from selected account
  2. Add amount to goal's saved_amount
  3. Update goal status if target reached
- **Multiple accounts**: User can contribute from any of their accounts
- **Partial contributions**: Allowed (any amount <= remaining target)

### FR-34: Progress Display
- **Progress bar**: Visual indicator of completion
- **Percentage**: (saved_amount / target_amount) * 100
- **Remaining**: target_amount - saved_amount
- **Time remaining**: Days until deadline
- **Status**: 
  - IN_PROGRESS: < 100%
  - COMPLETED: >= 100%
  - OVERDUE: Past deadline and < 100%

### FR-35: Goal Achievement
- **Trigger**: saved_amount >= target_amount
- **Actions**: 
  - Mark goal as COMPLETED
  - Show celebration animation/message
  - Send notification
  - Option to create new goal
- **Overdue handling**: 
  - Send reminder notification
  - Allow continued contributions
  - Option to extend deadline