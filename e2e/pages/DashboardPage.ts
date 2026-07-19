import { Page, Locator } from '@playwright/test';

export class DashboardPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly welcomeText: Locator;
  readonly logoutButton: Locator;
  readonly sidebarDashboard: Locator;
  readonly sidebarTransactions: Locator;
  readonly sidebarBudgets: Locator;
  readonly sidebarGoals: Locator;
  readonly sidebarSettings: Locator;
  readonly totalBalanceCard: Locator;
  readonly incomeCard: Locator;
  readonly expensesCard: Locator;
  readonly savingsCard: Locator;

  constructor(page: Page) {
    this.page = page;
    this.heading = page.getByRole('heading', { name: 'Dashboard' });
    this.welcomeText = page.locator('text=Welcome back');
    this.logoutButton = page.locator('button[title="Logout"]');
    this.sidebarDashboard = page.getByRole('link', { name: 'Dashboard' });
    this.sidebarTransactions = page.getByRole('link', { name: 'Transactions' });
    this.sidebarBudgets = page.getByRole('link', { name: 'Budgets' });
    this.sidebarGoals = page.getByRole('link', { name: 'Goals' });
    this.sidebarSettings = page.getByRole('link', { name: 'Settings' });
    this.totalBalanceCard = page.locator('text=Total Balance');
    this.incomeCard = page.locator('text=Income');
    this.expensesCard = page.locator('text=Expenses');
    this.savingsCard = page.locator('text=Savings');
  }

  async isOnDashboard() {
    return await this.heading.isVisible();
  }

  async isLoggedIn() {
    return await this.welcomeText.isVisible();
  }

  async logout() {
    await this.logoutButton.click();
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
}