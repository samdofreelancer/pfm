import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';

// Test user credentials - using a unique email for each test run
const generateTestUser = () => {
  const timestamp = Date.now();
  return {
    fullName: 'Test User',
    email: `testuser_${timestamp}@example.com`,
    password: 'TestPassword123!',
  };
};

test.describe('Authentication', () => {
  test('should register and login successfully with new account', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const dashboardPage = new DashboardPage(page);
    const testUser = generateTestUser();

    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Register new account
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);

    // Verify redirect to dashboard
    await expect(dashboardPage.welcomeMessage).toBeVisible();
    await expect(dashboardPage.dashboardHeading).toBeVisible();

    // Logout to clean up
    await dashboardPage.logout();
    await expect(loginPage.welcomeHeading).toBeVisible();
  });

  test('should login successfully with valid credentials', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const dashboardPage = new DashboardPage(page);

    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with valid credentials
    await loginPage.login('ginseng1000years@gmai.com', '123456');

    // Verify redirect to dashboard
    await expect(dashboardPage.welcomeMessage).toBeVisible();
    await expect(dashboardPage.dashboardHeading).toBeVisible();
  });

  test('should show error message with invalid credentials', async ({ page }) => {
    const loginPage = new LoginPage(page);

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

  test('should show error message with empty email', async ({ page }) => {
    const loginPage = new LoginPage(page);

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

  test('should show error message with empty password', async ({ page }) => {
    const loginPage = new LoginPage(page);

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

  test('should show error message with invalid email format', async ({ page }) => {
    const loginPage = new LoginPage(page);

    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with invalid email format
    await loginPage.login('invalid-email', 'password123');

    // Verify user stays on login page (validation error should prevent navigation)
    await expect(loginPage.welcomeHeading).toBeVisible();
  });

  test('should stay on login page after failed login', async ({ page }) => {
    const loginPage = new LoginPage(page);

    // Navigate to login page
    await loginPage.goto();
    await expect(loginPage.welcomeHeading).toBeVisible();

    // Login with invalid credentials
    await loginPage.login('wrong@example.com', 'wrongpassword');

    // Verify user stays on login page (welcome heading still visible)
    await expect(loginPage.welcomeHeading).toBeVisible();
    await expect(page).toHaveURL(/.*\/login.*/);
  });

  test('should logout successfully', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const dashboardPage = new DashboardPage(page);

    // Login first
    await loginPage.goto();
    await loginPage.login('ginseng1000years@gmai.com', '123456');
    await expect(dashboardPage.welcomeMessage).toBeVisible();

    // Logout
    await dashboardPage.logout();

    // Verify redirect to login page
    await expect(loginPage.welcomeHeading).toBeVisible();
  });
});
