import { Page, Locator } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly fullNameInput: Locator;
  readonly submitButton: Locator;
  readonly signUpTabButton: Locator;
  readonly signUpToggleButton: Locator;
  readonly errorMessage: Locator;
  readonly welcomeHeading: Locator;

  constructor(page: Page) {
    this.page = page;
    this.emailInput = page.locator('input[type="email"]');
    this.passwordInput = page.locator('input[type="password"]');
    this.fullNameInput = page.locator('input[name="fullName"]');
    // Submit button - works for both Sign In and Create Account
    this.submitButton = page.locator('form').getByRole('button', { name: /Sign In|Create Account/ });
    // Tab button for switching to Sign Up mode (first one in the tab group)
    this.signUpTabButton = page.locator('.flex.bg-gray-100.rounded-lg').getByRole('button', { name: 'Sign Up' });
    // Toggle button for switching to Sign Up mode (at the bottom)
    this.signUpToggleButton = page.locator('p').getByRole('button', { name: 'Sign up' });
    this.errorMessage = page.locator('.bg-red-50');
    this.welcomeHeading = page.getByRole('heading', { name: 'Welcome Back' });
  }

  async goto() {
    await this.page.goto('/login');
  }

  async login(email: string, password: string) {
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }

  async register(fullName: string, email: string, password: string) {
    // Switch to Sign Up tab
    await this.signUpTabButton.click();
    // Fill registration form
    await this.fullNameInput.fill(fullName);
    await this.emailInput.fill(email);
    await this.passwordInput.fill(password);
    await this.submitButton.click();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }

  async isLoginPageVisible() {
    return await this.welcomeHeading.isVisible();
  }
}
