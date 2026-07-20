import { Page, Locator } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly fullNameInput: Locator;
  readonly submitButton: Locator;
  readonly loginTabButton: Locator;
  readonly registerTabButton: Locator;
  readonly errorMessage: Locator;
  readonly welcomeHeading: Locator;

  constructor(page: Page) {
    this.page = page;
    // Use data-testid for stable locators
    this.emailInput = page.getByTestId('email-input');
    this.passwordInput = page.getByTestId('password-input');
    this.fullNameInput = page.getByTestId('fullName-input');
    this.submitButton = page.getByTestId('submit-button');
    this.loginTabButton = page.getByTestId('login-tab');
    this.registerTabButton = page.getByTestId('register-tab');
    this.errorMessage = page.getByTestId('error-message');
    this.welcomeHeading = page.getByRole('heading', { name: 'Welcome Back' });
  }

  async goto() {
    await this.page.goto('/login');
  }

  async login(email: string, password: string) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
    // Wait for navigation to complete - either to dashboard (success) or stay on login (error)
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async register(fullName: string, email: string, password: string) {
    // Switch to Sign Up tab
    await this.registerTabButton.click();
    // Fill registration form
    await this.fullNameInput.fill(fullName);
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    // Click submit and wait for navigation
    await this.submitButton.click();
    // Wait for navigation to complete - either to dashboard (success) or stay on login (error)
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }

  async isLoginPageVisible() {
    return await this.welcomeHeading.isVisible();
  }
}
