import { test as base, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';
import { DashboardPage } from '../../pages/DashboardPage';

type TestUser = {
  fullName: string;
  email: string;
  password: string;
};

type TestFixtures = {
  loginPage: LoginPage;
  dashboardPage: DashboardPage;
  testUser: TestUser;
};

// Generate unique test user for each test
const generateTestUser = (): TestUser => {
  const timestamp = Date.now();
  return {
    fullName: 'Test User',
    email: `testuser_${timestamp}@example.com`,
    password: 'TestPassword123!',
  };
};

// Get API base URL based on environment
const getApiBaseUrl = () => {
  return process.env.CI ? 'http://backend:8080' : 'http://localhost:8080';
};

export const test = base.extend<TestFixtures>({
  loginPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    await use(loginPage);
  },
  dashboardPage: async ({ page }, use) => {
    const dashboardPage = new DashboardPage(page);
    await use(dashboardPage);
  },
  testUser: async ({ request }, use) => {
    const testUser = generateTestUser();
    await use(testUser);
    // Cleanup: delete the test user after the test
    try {
      await request.delete(`${getApiBaseUrl()}/api/v1/auth/users`, {
        data: { email: testUser.email },
      });
    } catch (e) {
      // Ignore cleanup errors
    }
  },
});

export { expect };