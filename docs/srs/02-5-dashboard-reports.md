# 2.5 Dashboard & Reports

| ID | Description | Priority |
|----|-------------|----------|
| **FR-23** | Dashboard displays: total balance, monthly income, monthly expense, remaining budget (if any). | **Critical** |
| **FR-24** | Bar chart comparing income vs expense by month (last 6 months). | **Critical** |
| **FR-25** | Pie chart showing expense distribution by category (current month, top 5 categories). | **Critical** |
| **FR-26** | Detailed report: select month/year, display summary table by category with chart. Export PDF (download or email). | **Critical** |
| **FR-27** | Dashboard cached in Redis (5 min TTL). Auto-refresh on transaction changes. | **High** |

## Detailed Specifications

### FR-23: Dashboard Summary
- **Total Balance**: Sum of all account balances
- **Monthly Income**: Sum of INCOME transactions for current month
- **Monthly Expense**: Sum of EXPENSE transactions for current month
- **Remaining Budget**: Sum of (budget limit - spent) for all active budgets
- **Display**: Card-based layout with icons and trend indicators

### FR-24: Income vs Expense Bar Chart
- **Period**: Last 6 months
- **Data**: Monthly income and expense totals
- **Chart type**: Grouped bar chart
- **Interactivity**: Hover to see exact amounts, click to drill down
- **Currency**: Display in user's default currency

### FR-25: Expense Distribution Pie Chart
- **Period**: Current month
- **Categories**: Top 5 expense categories by amount
- **Others**: Group remaining categories as "Others"
- **Chart type**: Pie/donut chart
- **Interactivity**: Click slice to see category details

### FR-26: Detailed Reports
- **Selection**: Month and year picker
- **Summary table**: 
  - Category name
  - Number of transactions
  - Total amount
  - Percentage of total
- **Chart**: Bar or pie chart visualization
- **Export options**:
  - Download PDF
  - Email PDF (requires email service)
- **PDF content**: Summary, charts, transaction list

### FR-27: Dashboard Caching
- **Cache key**: `dashboard:{userId}:{month}`
- **TTL**: 5 minutes
- **Invalidation**: 
  - On transaction create/update/delete
  - On budget change
  - Manual refresh button
- **Fallback**: Calculate from database if cache miss