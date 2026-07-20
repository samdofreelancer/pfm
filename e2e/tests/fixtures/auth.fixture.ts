import { test as base, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';
import { DashboardPage } from '../../pages/DashboardPage';
import { ProfilePage } from '../../pages/ProfilePage';
import { AccountsPage } from '../../pages/AccountsPage';

type TestUser = {
  fullName: string;
  email: string;
  password: string;
};

type TestFixtures = {
  loginPage: LoginPage;
  dashboardPage: DashboardPage;
  profilePage: ProfilePage;
  accountsPage: AccountsPage;
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

export const test = base.extend<TestFixtures>({
  loginPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    await use(loginPage);
  },
  dashboardPage: async ({ page }, use) => {
    const dashboardPage = new DashboardPage(page);
    await use(dashboardPage);
  },
  profilePage: async ({ page }, use) => {
    const profilePage = new ProfilePage(page);
    await use(profilePage);
  },
  accountsPage: async ({ page }, use) => {
    const accountsPage = new AccountsPage(page);
    await use(accountsPage);
  },
  testUser: async ({}, use) => {
    const testUser = generateTestUser();
    await use(testUser);
  },
});

export { expect };
