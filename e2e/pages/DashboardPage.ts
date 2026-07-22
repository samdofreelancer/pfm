import { Page, Locator } from '@playwright/test';

export class DashboardPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly welcomeText: Locator;
  readonly userAvatar: Locator;
  readonly userName: Locator;

  // Sidebar
  readonly sidebar: Locator;
  readonly sidebarDashboard: Locator;
  readonly sidebarTransactions: Locator;
  readonly sidebarBudgets: Locator;
  readonly sidebarGoals: Locator;
  readonly sidebarSettings: Locator;
  readonly sidebarLogo: Locator;
  readonly sidebarMobileClose: Locator;
  readonly mobileMenuButton: Locator;

  // User dropdown
  readonly userDropdownToggle: Locator;
  readonly userDropdownMenu: Locator;
  readonly profileMenuItem: Locator;
  readonly logoutMenuItem: Locator;

  // Total balance card
  readonly totalBalanceLabel: Locator;
  readonly totalBalanceAmount: Locator;
  readonly incomeLabel: Locator;
  readonly expenseLabel: Locator;

  // Sections
  readonly incomeExpenseOverviewHeading: Locator;
  readonly monthlySpendingCalendarHeading: Locator;
  readonly incomeExpenseHeading: Locator;
  readonly recentRecordsHeading: Locator;
  readonly accountsHeading: Locator;
  readonly incomeHeading: Locator;
  readonly expenseHeading: Locator;

  constructor(page: Page) {
    this.page = page;

    // Main header
    this.heading = page.getByRole('heading', { name: 'Dashboard' });
    this.welcomeText = page.locator('text=Welcome back');
    this.userAvatar = page.locator('header .rounded-full').first();
    this.userName = page.locator('header span.font-medium');
    this.mobileMenuButton = page.locator('header button').first();

    // User dropdown
    this.userDropdownToggle = page.locator('header button').filter({ has: page.locator('.rounded-full') });
    this.userDropdownMenu = page.locator('.absolute.right-0.mt-2');
    this.profileMenuItem = page.getByRole('button', { name: 'Profile' });
    this.logoutMenuItem = page.getByRole('button', { name: 'Logout' });

    // Sidebar
    this.sidebar = page.locator('aside');
    this.sidebarDashboard = page.getByRole('link', { name: 'Dashboard' });
    this.sidebarTransactions = page.getByRole('link', { name: 'Transactions' });
    this.sidebarBudgets = page.getByRole('link', { name: 'Budgets' });
    this.sidebarGoals = page.getByRole('link', { name: 'Goals' });
    this.sidebarSettings = page.getByRole('link', { name: 'Settings' });
    this.sidebarLogo = page.locator('aside').locator('text=PFM');
    this.sidebarMobileClose = page.locator('aside button').first();

    // Total balance
    this.totalBalanceLabel = page.locator('text=Total Balance');
    this.totalBalanceAmount = page.locator('text=18.000.000');
    this.incomeLabel = page.locator('text=Income:');
    this.expenseLabel = page.locator('text=Expense:');

    // Sections
    this.incomeExpenseOverviewHeading = page.getByRole('heading', { name: 'Income & Expense Overview', exact: true });
    this.monthlySpendingCalendarHeading = page.getByRole('heading', { name: 'Monthly Spending Calendar', exact: true });
    this.incomeExpenseHeading = page.getByRole('heading', { name: 'Income & Expense', exact: true });
    this.recentRecordsHeading = page.getByRole('heading', { name: 'Recent Records', exact: true });
    this.accountsHeading = page.getByRole('heading', { name: 'Accounts', exact: true });
    this.incomeHeading = page.getByRole('heading', { name: 'Income', exact: true });
    this.expenseHeading = page.getByRole('heading', { name: 'Expense', exact: true });
  }

  async isOnDashboard() {
    return await this.heading.isVisible();
  }

  async isLoggedIn() {
    return await this.welcomeText.isVisible();
  }

  async waitForDashboardLoaded() {
    await this.totalBalanceLabel.waitFor({ state: 'visible', timeout: 15000 });
    await this.page.waitForLoadState('networkidle');
  }

  async logout() {
    await this.userDropdownToggle.click();
    await this.logoutMenuItem.waitFor({ state: 'visible', timeout: 5000 });
    await this.logoutMenuItem.click();
    await this.page.waitForLoadState('networkidle');
  }

  async openProfile() {
    await this.userDropdownToggle.click();
    await this.profileMenuItem.waitFor({ state: 'visible', timeout: 5000 });
    await this.profileMenuItem.click();
  }

  async navigateToTransactions() {
    await this.sidebarTransactions.click();
  }

  async navigateToBudgets() {
    await this.sidebarBudgets.click();
  }

  async navigateToGoals() {
    await this.sidebarGoals.click();
  }

  async navigateToSettings() {
    await this.sidebarSettings.click();
  }

  async getTotalBalance() {
    return await this.totalBalanceAmount.textContent();
  }

  async getActiveSidebarItem() {
    const activeLink = this.sidebar.locator('a.bg-primary-50');
    return await activeLink.textContent();
  }

  async isSidebarVisible() {
    return await this.sidebar.isVisible();
  }

  // Mobile-specific
  async openMobileSidebar() {
    const isMobile = await this.mobileMenuButton.isVisible();
    if (isMobile) {
      await this.mobileMenuButton.click();
      await this.page.waitForTimeout(300);
    }
  }

  async closeMobileSidebar() {
    try {
      await this.sidebarMobileClose.click({ timeout: 1000 });
    } catch {
      // sidebar close button may not be visible on desktop
    }
  }
}