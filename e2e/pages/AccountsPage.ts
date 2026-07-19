import { Page, Locator } from '@playwright/test';

export class AccountsPage {
  readonly page: Page;
  readonly heading: Locator;
  readonly addAccountButton: Locator;
  readonly createAccountForm: Locator;
  readonly accountTypeSelect: Locator;
  readonly accountNameInput: Locator;
  readonly accountDescriptionInput: Locator;
  readonly accountBalanceInput: Locator;
  readonly accountCurrencySelect: Locator;
  readonly createAccountSubmit: Locator;
  readonly accountsList: Locator;
  readonly noAccountsMessage: Locator;
  readonly accountsItems: Locator;
  readonly successMessage: Locator;
  readonly errorMessage: Locator;

  constructor(page: Page) {
    this.page = page;

    this.heading = page.getByTestId('accounts-heading');
    this.addAccountButton = page.getByTestId('add-account-button');
    this.createAccountForm = page.getByTestId('create-account-form');
    this.accountTypeSelect = page.getByTestId('account-type-select');
    this.accountNameInput = page.getByTestId('account-name-input');
    this.accountDescriptionInput = page.getByTestId('account-description-input');
    this.accountBalanceInput = page.getByTestId('account-balance-input');
    this.accountCurrencySelect = page.getByTestId('account-currency-select');
    this.createAccountSubmit = page.getByTestId('create-account-submit');
    this.accountsList = page.getByTestId('accounts-list');
    this.noAccountsMessage = page.getByTestId('no-accounts-message');
    this.accountsItems = page.getByTestId('accounts-items');
    this.successMessage = page.getByTestId('success-message');
    this.errorMessage = page.getByTestId('error-message');
  }

  async goto() {
    await this.page.goto('/accounts');
  }

  async isOnAccountsPage() {
    return await this.heading.isVisible();
  }

  async clickAddAccount() {
    await this.addAccountButton.click();
  }

  async createAccount(data: {
    type?: string;
    name: string;
    description?: string;
    initialBalance?: string;
    currency?: string;
  }) {
    await this.clickAddAccount();
    await this.createAccountForm.waitFor({ state: 'visible' });

    if (data.type) {
      await this.accountTypeSelect.selectOption(data.type);
    }
    await this.accountNameInput.fill(data.name);
    if (data.description) {
      await this.accountDescriptionInput.fill(data.description);
    }
    if (data.initialBalance) {
      await this.accountBalanceInput.fill(data.initialBalance);
    }
    if (data.currency) {
      await this.accountCurrencySelect.selectOption(data.currency);
    }

    await this.createAccountSubmit.click();
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async getAccountItemLocator(accountId: string) {
    return this.page.getByTestId(`account-item-${accountId}`);
  }

  async getAccountName(accountId: string) {
    return await this.page.getByTestId(`account-name-${accountId}`).textContent();
  }

  async getAccountBalance(accountId: string) {
    return await this.page.getByTestId(`account-balance-${accountId}`).textContent();
  }

  async deleteAccount(accountId: string) {
    // Handle the confirmation dialog
    this.page.once('dialog', (dialog) => {
      dialog.accept();
    });
    await this.page.getByTestId(`delete-account-${accountId}`).click();
    await this.page.waitForLoadState('networkidle', { timeout: 10000 });
  }

  async getSuccessMessage() {
    return await this.successMessage.textContent();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }

  async isNoAccountsMessageVisible() {
    return await this.noAccountsMessage.isVisible();
  }

  async getAccountsCount() {
    if (await this.noAccountsMessage.isVisible()) {
      return 0;
    }
    return await this.accountsItems.locator('> div').count();
  }
}