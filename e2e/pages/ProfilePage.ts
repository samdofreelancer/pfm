import { Page, Locator } from '@playwright/test';

export class ProfilePage {
  readonly page: Page;
  readonly fullNameInput: Locator;
  readonly updateProfileButton: Locator;
  readonly currentPasswordInput: Locator;
  readonly newPasswordInput: Locator;
  readonly confirmPasswordInput: Locator;
  readonly changePasswordButton: Locator;
  readonly deleteAccountButton: Locator;
  readonly successMessage: Locator;
  readonly errorMessage: Locator;
  readonly accountHeading: Locator;

  constructor(page: Page) {
    this.page = page;
    this.fullNameInput = page.getByTestId('full-name-input');
    this.updateProfileButton = page.getByTestId('update-profile-button');
    this.currentPasswordInput = page.getByTestId('current-password-input');
    this.newPasswordInput = page.getByTestId('new-password-input');
    this.confirmPasswordInput = page.getByTestId('confirm-password-input');
    this.changePasswordButton = page.getByTestId('change-password-button');
    this.deleteAccountButton = page.getByTestId('delete-account-button');
    this.successMessage = page.getByTestId('success-message');
    this.errorMessage = page.getByTestId('error-message');
    this.accountHeading = page.getByRole('heading', { name: 'Profile Settings' });
  }

  async goto() {
    await this.page.goto('/profile');
  }

  async updateProfile(fullName: string) {
    await this.fullNameInput.fill(fullName);
    await this.updateProfileButton.click();
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async changePassword(currentPassword: string, newPassword: string, confirmPassword: string) {
    await this.currentPasswordInput.fill(currentPassword);
    await this.newPasswordInput.fill(newPassword);
    await this.confirmPasswordInput.fill(confirmPassword);
    await this.changePasswordButton.click();
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async getSuccessMessage() {
    return await this.successMessage.textContent();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }
}