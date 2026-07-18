# 2.3 Category Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-12** | System provides default categories (Food, Transport, Shopping, Utilities, Entertainment, Health, Education, Income, Bonus, Investment, etc.) with type (INCOME/EXPENSE). | **Critical** |
| **FR-13** | Users can create sub-categories (2-level hierarchy). | **Critical** |
| **FR-14** | Users can edit and delete categories (cannot delete categories with existing transactions). | **Critical** |

## Detailed Specifications

### FR-12: Default Categories
- **System categories**: Pre-defined categories available to all users
- **Types**: INCOME or EXPENSE
- **Default categories**:
  - **Income**: Salary, Bonus, Investment, Gift, Other Income
  - **Expense**: Food, Transport, Shopping, Utilities, Entertainment, Health, Education, Housing, Other Expense
- **Immutable**: System categories cannot be deleted, only deactivated
- **Localization**: Support Vietnamese and English names

### FR-13: Sub-Categories (2-Level Hierarchy)
- **Structure**: Parent category → Child category
- **Example**: 
  - Parent: Food
  - Children: Breakfast, Lunch, Dinner, Snacks
- **Depth**: Maximum 2 levels (parent + children)
- **Creation**: Users can create custom sub-categories under any category
- **Validation**: Cannot create sub-category under another sub-category

### FR-14: Category Management
- **Editable fields**: Name, parent category, active/inactive status
- **Delete constraints**:
  - Cannot delete category with existing transactions
  - Must reassign or delete transactions first
  - System categories can only be deactivated, not deleted
- **Custom categories**: Users can create, edit, delete their own categories
- **Usage tracking**: Show transaction count per category