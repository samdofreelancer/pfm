# 2.8 Notification Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-36** | Email alerts when budget is exceeded. | **Critical** |
| **FR-37** | Weekly/monthly report emails (v2.0). | **High** |
| **FR-38** | In-app notifications (bell icon) with alert list on Dashboard. | **Critical** |

## Detailed Specifications

### FR-36: Budget Alert Emails
- **Trigger**: Budget usage reaches 100%
- **Recipient**: Account owner
- **Content**: 
  - Category name
  - Budget limit
  - Amount spent
  - Percentage used
  - Link to app
- **Frequency**: Once per budget per month (prevent spam)
- **Template**: HTML email with branding

### FR-37: Periodic Reports (v2.0)
- **Weekly reports**: 
  - Summary of income/expense
  - Top spending categories
  - Budget status
  - Sent every Monday
- **Monthly reports**: 
  - Detailed breakdown by category
  - MoM comparison
  - Goal progress
  - Sent on 1st of next month
- **User control**: Enable/disable, choose frequency

### FR-38: In-App Notifications
- **Bell icon**: Shows unread notification count
- **Notification types**:
  - Budget alerts (80%, 100%)
  - Goal achievements
  - Account balance low
  - Recurring transaction created
  - System announcements
- **Notification list**: Dropdown with all notifications
- **Actions**: Mark as read, delete, navigate to related item
- **Persistence**: Store in database, keep for 90 days