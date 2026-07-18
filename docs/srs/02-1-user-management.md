# 2.1 User Management

| ID | Description | Priority |
|----|-------------|----------|
| **FR-01** | Users can register with email and password (min 6 characters). Password hashed with BCrypt. | **Critical** |
| **FR-02** | Users can login with email/password, receive JWT access token (15 min) and refresh token (7 days). | **Critical** |
| **FR-03** | Users can login via Google OAuth2 (future release v2.0). | **High** |
| **FR-04** | Users can request password reset via email OTP. | **Critical** |
| **FR-05** | Users can edit profile (full name, avatar). | **Critical** |
| **FR-06** | Users can delete account – all data (transactions, budgets, goals) permanently deleted (soft delete with 30-day retention optional). | **High** |

## Detailed Specifications

### FR-01: User Registration
- **Input**: Email, password (min 6 chars), full name (optional)
- **Validation**: Email format, password strength, unique email
- **Processing**: Hash password with BCrypt, create user record
- **Output**: User created, auto-login with JWT tokens

### FR-02: User Login
- **Input**: Email, password
- **Validation**: Verify credentials, check account status
- **Processing**: Generate JWT access token (15 min expiry) and refresh token (7 days)
- **Output**: Tokens returned to client

### FR-03: Google OAuth2 (v2.0)
- **Input**: Google OAuth2 authorization code
- **Processing**: Exchange code for user info, create/link account
- **Output**: JWT tokens

### FR-04: Password Reset
- **Input**: Email address
- **Processing**: Generate OTP, send email with reset link
- **Output**: Email sent confirmation
- **Security**: OTP expires in 15 minutes, max 3 attempts

### FR-05: Profile Management
- **Editable fields**: Full name, avatar URL
- **Validation**: Name length, avatar URL format
- **Output**: Updated user profile

### FR-06: Account Deletion
- **Process**: 
  - Option 1: Soft delete with 30-day retention (can restore)
  - Option 2: Permanent deletion (all data removed)
- **Confirmation**: Require password re-entry
- **Cascade**: Delete all associated data (transactions, budgets, goals, chat history)