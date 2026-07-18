import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';

test.describe('Authentication', () => {
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