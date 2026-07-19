import { Page, Locator, expect } from '@playwright/test';

export class DashboardPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly welcomeText: Locator;
  readonly logoutButton: Locator;
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

  // Summary cards
  readonly totalBalanceCard: Locator;
  readonly totalBalanceAmount: Locator;
  readonly incomeCard: Locator;
  readonly incomeAmount: Locator;
  readonly expensesCard: Locator;
  readonly expensesAmount: Locator;
  readonly savingsCard: Locator;
  readonly savingsAmount: Locator;

  // Recent Transactions
  readonly recentTransactionsHeading: Locator;
  readonly viewAllButton: Locator;
  readonly noTransactionsText: Locator;
  readonly noTransactionsSubtext: Locator;
  readonly addTransactionButton: Locator;

  // Budget Overview
  readonly budgetOverviewHeading: Locator;
  readonly noBudgetsText: Locator;

  // Savings Goals
  readonly savingsGoalsHeading: Locator;
  readonly noGoalsText: Locator;

  constructor(page: Page) {
    this.page = page;

    // Main header
    this.heading = page.getByRole('heading', { name: 'Dashboard' });
    this.welcomeText = page.locator('text=Welcome back');
    this.logoutButton = page.locator('button[title="Logout"]');
    this.userAvatar = page.locator('header .rounded-full').first();
    this.userName = page.locator('header span.font-medium');
    this.mobileMenuButton = page.locator('header button').first();

    // Sidebar
    this.sidebar = page.locator('aside');
    this.sidebarDashboard = page.getByRole('link', { name: 'Dashboard' });
    this.sidebarTransactions = page.getByRole('link', { name: 'Transactions' });
    this.sidebarBudgets = page.getByRole('link', { name: 'Budgets' });
    this.sidebarGoals = page.getByRole('link', { name: 'Goals' });
    this.sidebarSettings = page.getByRole('link', { name: 'Settings' });
    this.sidebarLogo = page.locator('aside').locator('text=PFM');
    this.sidebarMobileClose = page.locator('aside button').first();

    // Summary cards
    this.totalBalanceCard = page.getByText('Total Balance').first().locator('..');
    this.totalBalanceAmount = page.getByText('Total Balance').first().locator('..').locator('.text-2xl');
    this.incomeCard = page.getByText('Income', { exact: true }).first().locator('..');
    this.incomeAmount = page.getByText('Income', { exact: true }).first().locator('..').locator('.text-2xl');
    this.expensesCard = page.getByText('Expenses', { exact: true }).first().locator('..');
    this.expensesAmount = page.getByText('Expenses', { exact: true }).first().locator('..').locator('.text-2xl');
    this.savingsCard = page.getByText('Savings', { exact: true }).first().locator('..');
    this.savingsAmount = page.getByText('Savings', { exact: true }).first().locator('..').locator('.text-2xl');

    // Recent Transactions
    this.recentTransactionsHeading = page.getByRole('heading', { name: 'Recent Transactions' });
    this.viewAllButton = page.getByRole('button', { name: 'View All' });
    this.noTransactionsText = page.locator('text=No transactions yet');
    this.noTransactionsSubtext = page.locator('text=Start by adding your first transaction');
    this.addTransactionButton = page.getByRole('button', { name: 'Add Transaction' });

    // Budget Overview
    this.budgetOverviewHeading = page.getByRole('heading', { name: 'Budget Overview' });
    this.noBudgetsText = page.locator('text=No budgets set up yet');

    // Savings Goals
    this.savingsGoalsHeading = page.getByRole('heading', { name: 'Savings Goals' });
    this.noGoalsText = page.locator('text=No goals set up yet');
  }

  async isOnDashboard() {
    return await this.heading.isVisible();
  }

  async isLoggedIn() {
    return await this.welcomeText.isVisible();
  }

  async waitForDashboardLoaded() {
    await this.heading.waitFor({ state: 'visible', timeout: 15000 });
    await this.page.waitForLoadState('networkidle');
  }

  async logout() {
    await this.logoutButton.click();
    await this.page.waitForLoadState('networkidle');
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

  async getIncomeAmount() {
    return await this.incomeAmount.textContent();
  }

  async getExpensesAmount() {
    return await this.expensesAmount.textContent();
  }

  async getSavingsAmount() {
    return await this.savingsAmount.textContent();
  }

  async isSummaryCardVisible(cardName: 'Total Balance' | 'Income' | 'Expenses' | 'Savings') {
    const card = this.page.locator(`text=${cardName}`).locator('..');
    return await card.isVisible();
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
      await this.page.waitForTimeout(300); // wait for animation
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