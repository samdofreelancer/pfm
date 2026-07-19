import { test, expect } from './fixtures/auth.fixture';

test.describe('Authentication', () => {
  test('should register and login successfully with new account', async ({ page, loginPage, dashboardPage, testUser }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Register new account
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);

    // Verify redirect to dashboard (more resilient in Docker/CI)
    await expect(page).toHaveURL(/.*\/dashboard.*/);
    await expect(dashboardPage.welcomeText).toBeVisible({ timeout: 15000 });
    await expect(dashboardPage.heading).toBeVisible();

    // Logout
    await dashboardPage.logout();
    await expect(loginPage.welcomeHeading).toBeVisible();
    // Cleanup is handled automatically by the testUser fixture
  });

  test('should login successfully with valid credentials', async ({ page, loginPage, dashboardPage, testUser }) => {
    // Register a new user first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.welcomeText).toBeVisible({ timeout: 15000 });

    // Logout to test login
    await dashboardPage.logout();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with the registered credentials
    await loginPage.login(testUser.email, testUser.password);

    // Verify redirect to dashboard
    await expect(dashboardPage.welcomeText).toBeVisible();
    await expect(dashboardPage.heading).toBeVisible();
  });

  test('should show error message with invalid credentials', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with invalid credentials
    await loginPage.login('wrong@example.com', 'wrongpassword');

    // Verify error message is displayed
    await expect(loginPage.errorMessage).toBeVisible();
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).toBeTruthy();
    if (errorText) {
      expect(errorText.length).toBeGreaterThan(0);
    }
  });

  test('should show error message with empty email', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with empty email
    await loginPage.login('', 'password123');

    // Verify error message is displayed
    await expect(loginPage.errorMessage).toBeVisible();
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).toContain('Email');
  });

  test('should show error message with empty password', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with empty password
    await loginPage.login('test@example.com', '');

    // Verify error message is displayed
    await expect(loginPage.errorMessage).toBeVisible();
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).toContain('Password');
  });

  test('should show error message with invalid email format', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with invalid email format
    await loginPage.login('invalid-email', 'password123');

    // Verify user stays on login page (validation error should prevent navigation)
    await expect(loginPage.welcomeHeading).toBeVisible();
  });

  test('should stay on login page after failed login', async ({ loginPage, page }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with invalid credentials
    await loginPage.login('wrong@example.com', 'wrongpassword');

    // Verify user stays on login page (welcome heading still visible)
    await expect(loginPage.welcomeHeading).toBeVisible();
    await expect(page).toHaveURL(/.*\/login.*/);
  });

  test('should logout successfully', async ({ page, loginPage, dashboardPage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.welcomeText).toBeVisible({ timeout: 15000 });

    // Logout
    await dashboardPage.logout();

    // Verify redirect to login page
    await expect(loginPage.welcomeHeading).toBeVisible();
  });

  test('should show error message with short password on register', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Switch to register tab
    await loginPage.registerTabButton.click();

    // Try to register with short password
    await loginPage.register('Test User', 'test@example.com', '123');

    // Verify error message is displayed
    await expect(loginPage.errorMessage).toBeVisible();
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).toContain('6 characters');
  });

  test('should show error message with empty full name on register', async ({ loginPage }) => {
    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Switch to register tab
    await loginPage.registerTabButton.click();

    // Try to register with empty full name
    await loginPage.register('', 'test@example.com', 'password123');

    // Verify error message is displayed
    await expect(loginPage.errorMessage).toBeVisible();
    const errorText = await loginPage.getErrorMessage();
    expect(errorText).toContain('Full name');
  });
});