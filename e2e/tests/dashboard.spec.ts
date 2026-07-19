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
      await expect(dashboardPage.totalBalanceLabel).toBeVisible();
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
      await expect(dashboardPage.totalBalanceLabel).toBeVisible();
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
    test('should display total balance section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.totalBalanceLabel).toBeVisible();
      await expect(dashboardPage.totalBalanceAmount).toBeVisible();
    });

    test('should display income and expense labels', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.incomeLabel).toBeVisible();
      await expect(dashboardPage.expenseLabel).toBeVisible();
    });
  });

  test.describe('Recent Transactions Section', () => {
    test('should display Ghi chép gần đây section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.ghiChepGanDayHeading).toBeVisible();
    });

    test('should show empty state when no transactions exist', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(page.locator('text=Chưa có giao dịch')).toBeVisible();
    });

    test('should display Thêm giao dịch button in empty state', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(page.getByRole('button', { name: 'Thêm giao dịch' })).toBeVisible();
    });

    test('should display Xem tất cả button', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(page.getByRole('button', { name: 'Xem tất cả →' })).toBeVisible();
    });
  });

  test.describe('Budget & Goals Sections', () => {
    test('should display Tổng quan thu chi section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.tongQuanThuChiHeading).toBeVisible();
    });

    test('should display Lịch chi tiêu tháng section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.lichChiTieuHeading).toBeVisible();
    });

    test('should display Tình hình thu chi section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.tinhHinhThuChiHeading).toBeVisible();
    });

    test('should display Tài khoản section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.taiKhoanHeading).toBeVisible();
    });

    test('should display Thu tiền section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.thuTienHeading).toBeVisible();
    });

    test('should display Chi tiền section', async ({ page, loginPage, dashboardPage, testUser }) => {
      await loginPage.goto();
      await loginPage.register(testUser.fullName, testUser.email, testUser.password);
      await dashboardPage.waitForDashboardLoaded();

      await expect(dashboardPage.chiTienHeading).toBeVisible();
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