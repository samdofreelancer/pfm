import { Page, Locator } from '@playwright/test';

export class DashboardPage {
  readonly page: Page;
  readonly welcomeMessage: Locator;
  readonly logoutButton: Locator;
  readonly dashboardHeading: Locator;

  constructor(page: Page) {
    this.page = page;
    this.welcomeMessage = page.locator('text=Welcome to PFM! You are logged in.');
    this.logoutButton = page.getByRole('button', { name: 'Logout' });
    this.dashboardHeading = page.getByRole('heading', { name: 'Dashboard' });
  }

  async isLoggedIn() {
    return await this.welcomeMessage.isVisible();
  }

  async logout() {
    await this.logoutButton.click();
  }
}