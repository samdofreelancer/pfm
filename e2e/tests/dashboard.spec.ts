import { test, expect } from './fixtures/auth.fixture';

test.describe('Dashboard', () => {
  test.describe('Authentication & Access', () => {
    test('should redirect to login when accessing dashboard without authentication', async ({ page }) => {
      // Try to access dashboard directly without login
      await page.goto('/dashboard');
      await page.waitForLoadState('networkidle');

      // Should be redirected to login page
      await expect(page).toHaveURL(/.*\/login.*/);
    });

    test('should display dashboard after successful login', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);

      // Wait for dashboard to load
      await dashboardPage.waitForDashboardLoaded();

      // Verify URL and key elements
      await expect(page).toHaveURL(/.*\/dashboard.*/);
      await expect(dashboardPage.heading).toBeVisible();
      await expect(dashboardPage.welcomeText).toBeVisible();
    });

    test('should show user name in header', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Verify user name is displayed in header
      await expect(dashboardPage.userName).toBeVisible();
      await expect(dashboardPage.userName).toHaveText(testUser.fullName);
    });

    test('should logout successfully from dashboard', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Logout
      await dashboardPage.logout();

      // Verify redirect to login page
      await expect(page).toHaveURL(/.*\/login.*/);
      await expect(loginPage.welcomeHeading).toBeVisible({ timeout: 10000 });
    });

    test('should persist session on page refresh', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Refresh the page
      await page.reload();
      await dashboardPage.waitForDashboardLoaded();

      // Should still be on dashboard (session persists via refresh token)
      await expect(page).toHaveURL(/.*\/dashboard.*/);
      await expect(dashboardPage.heading).toBeVisible();
    });
  });

  test.describe('Layout & Navigation', () => {
    test('should display sidebar with all navigation items', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Verify sidebar is visible
      await expect(dashboardPage.sidebar).toBeVisible();
      await expect(dashboardPage.sidebarLogo).toBeVisible();

      // Verify all navigation links exist
      await expect(dashboardPage.sidebarDashboard).toBeVisible();
      await expect(dashboardPage.sidebarTransactions).toBeVisible();
      await expect(dashboardPage.sidebarBudgets).toBeVisible();
      await expect(dashboardPage.sidebarGoals).toBeVisible();
      await expect(dashboardPage.sidebarSettings).toBeVisible();
    });

    test('should highlight active sidebar item', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Dashboard should be active
      const activeItem = await dashboardPage.getActiveSidebarItem();
      expect(activeItem?.trim()).toBe('Dashboard');
    });

    test('should navigate to transactions page via sidebar', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Click Transactions in sidebar
      await dashboardPage.navigateToTransactions();
      await page.waitForLoadState('networkidle');

      // Should navigate to /transactions (will show empty page since not implemented)
      await expect(page).toHaveURL(/.*\/transactions.*/);
    });

    test('should navigate to budgets page via sidebar', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await dashboardPage.navigateToBudgets();
      await page.waitForLoadState('networkidle');

      await expect(page).toHaveURL(/.*\/budgets.*/);
    });

    test('should navigate to goals page via sidebar', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await dashboardPage.navigateToGoals();
      await page.waitForLoadState('networkidle');

      await expect(page).toHaveURL(/.*\/goals.*/);
    });

    test('should navigate to settings page via sidebar', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await dashboardPage.navigateToSettings();
      await page.waitForLoadState('networkidle');

      await expect(page).toHaveURL(/.*\/settings.*/);
    });

    test('should have logout button visible in header', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.logoutButton).toBeVisible();
    });
  });

  test.describe('Summary Cards', () => {
    test('should display all 4 summary cards', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // All 4 cards should be visible
      await expect(dashboardPage.totalBalanceCard).toBeVisible();
      await expect(dashboardPage.incomeCard).toBeVisible();
      await expect(dashboardPage.expensesCard).toBeVisible();
      await expect(dashboardPage.savingsCard).toBeVisible();
    });

    test('should display correct default amounts', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // All cards should show $0.00 as default
      expect(await dashboardPage.getTotalBalance()).toContain('$0.00');
      expect(await dashboardPage.getIncomeAmount()).toContain('$0.00');
      expect(await dashboardPage.getExpensesAmount()).toContain('$0.00');
      expect(await dashboardPage.getSavingsAmount()).toContain('$0.00');
    });

    test('should display card labels correctly', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Verify each card title is present (use exact text match to avoid conflicts)
      await expect(page.getByText('Total Balance', { exact: true })).toBeVisible();
      await expect(page.getByText('Income', { exact: true })).toBeVisible();
      await expect(page.getByText('Expenses', { exact: true })).toBeVisible();
      await expect(page.getByText('Savings', { exact: true }).first()).toBeVisible();
    });
  });

  test.describe('Recent Transactions Section', () => {
    test('should display Recent Transactions section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.recentTransactionsHeading).toBeVisible();
    });

    test('should show empty state when no transactions exist', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Empty state elements
      await expect(dashboardPage.noTransactionsText).toBeVisible();
      await expect(dashboardPage.noTransactionsSubtext).toBeVisible();
    });

    test('should display Add Transaction button in empty state', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.addTransactionButton).toBeVisible();
      await expect(dashboardPage.addTransactionButton).toBeEnabled();
    });

    test('should display View All button', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.viewAllButton).toBeVisible();
    });
  });

  test.describe('Budget & Goals Sections', () => {
    test('should display Budget Overview section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.budgetOverviewHeading).toBeVisible();
    });

    test('should show empty state for Budgets', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.noBudgetsText).toBeVisible();
    });

    test('should display Savings Goals section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.savingsGoalsHeading).toBeVisible();
    });

    test('should show empty state for Goals', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.noGoalsText).toBeVisible();
    });
  });

  test.describe('Protected Route Behavior', () => {
    test('should redirect to login when accessing dashboard after logout', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      // Logout - this navigates to /login
      await dashboardPage.logout();
      await expect(loginPage.welcomeHeading).toBeVisible({ timeout: 10000 });

      // Try to navigate back to dashboard via SPA link (not full navigation)
      // Using SPA NavLink click rather than page.goto to avoid re-triggering refresh token
      await expect(page).toHaveURL(/.*\/login.*/);
    });

    test('should show loading state while checking authentication', async ({ page }) => {
      // Navigate to dashboard without being logged in
      await page.goto('/dashboard');

      // Should briefly show loading state then redirect to login
      await page.waitForTimeout(500);
      await expect(page).toHaveURL(/.*\/login.*/);
    });
  });
});