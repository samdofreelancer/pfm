import { test, expect } from './fixtures/account.fixture';

test.describe.configure({ mode: 'serial' });

test.describe('Accounts Management', () => {
  test.beforeEach(async ({ page, loginPage, dashboardPage, accountsPage, testUser }) => {
    // Register and login before each test
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to accounts page
    await page.goto('/accounts');
    await expect(accountsPage.heading).toBeVisible({ timeout: 15000 });
  });

  test('should display accounts page', async ({ accountsPage }) => {
    // Verify accounts page is displayed
    await expect(accountsPage.heading).toBeVisible();
    await expect(accountsPage.accountsList).toBeVisible();
  });

  test('should show create account form when clicking Add Account', async ({ accountsPage }) => {
    // Click Add Account button
    await accountsPage.clickAddAccount();

    // Verify form is displayed
    await expect(accountsPage.createAccountForm).toBeVisible();
    await expect(accountsPage.accountNameInput).toBeVisible();
    await expect(accountsPage.accountTypeSelect).toBeVisible();
    await expect(accountsPage.accountBalanceInput).toBeVisible();
    await expect(accountsPage.accountCurrencySelect).toBeVisible();
    await expect(accountsPage.createAccountSubmit).toBeVisible();
  });

  test('should create a new account successfully', async ({ page, accountsPage }) => {
    // Create a new account
    await accountsPage.createAccount({
      name: 'Test Bank Account',
      type: 'BANK_ACCOUNT',
      description: 'My test bank account',
      initialBalance: '1000000',
      currency: 'VND',
    });

    // Wait for success message
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });
    const successMessage = await accountsPage.getSuccessMessage();
    expect(successMessage).toContain('Account created successfully');

    // Verify form is hidden after creation
    await expect(accountsPage.createAccountForm).toBeHidden();

    // Verify account appears in the list
    await expect(accountsPage.accountsItems).toBeVisible();
    await expect(page.getByText('Test Bank Account').first()).toBeVisible();
  });

  test('should create multiple accounts and display them', async ({ page, accountsPage }) => {
    // Create first account
    await accountsPage.createAccount({
      name: 'Cash Wallet',
      type: 'CASH',
      initialBalance: '500000',
      currency: 'VND',
    });
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });

    // Create second account
    await accountsPage.createAccount({
      name: 'Vietcombank',
      type: 'BANK_ACCOUNT',
      initialBalance: '5000000',
      currency: 'VND',
    });
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });

    // Verify both accounts appear in the list
    await expect(accountsPage.accountsItems).toBeVisible();
    await expect(page.getByText('Cash Wallet').first()).toBeVisible();
    await expect(page.getByText('Vietcombank').first()).toBeVisible();
  });

  test('should show validation error when required fields are empty', async ({ page, accountsPage }) => {
    // Open form
    await accountsPage.clickAddAccount();
    await expect(accountsPage.createAccountForm).toBeVisible();

    // Try to submit without filling required fields
    await accountsPage.accountNameInput.fill('');
    await accountsPage.createAccountSubmit.click();

    // Check that the form is still visible (submission prevented by browser validation)
    await expect(accountsPage.createAccountForm).toBeVisible();
  });

  test('should delete an account successfully', async ({ page, accountsPage }) => {
    // Wait for any previous success message to auto-dismiss
    await page.waitForTimeout(4000);

    // First create an account to delete
    await accountsPage.createAccount({
      name: 'Account to Delete',
      type: 'CASH',
      initialBalance: '100000',
      currency: 'VND',
    });
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });

    // Verify account appears
    await expect(page.getByText('Account to Delete').first()).toBeVisible();

    // Wait for success message to auto-dismiss (3.5s auto-dismiss timer + buffer)
    await page.waitForTimeout(4000);
    await expect(accountsPage.successMessage).not.toBeVisible({ timeout: 5000 });

    // Set up dialog handler BEFORE clicking delete
    page.once('dialog', (dialog) => {
      expect(dialog.message()).toContain('delete');
      dialog.accept();
    });

    // Click delete button
    const deleteButton = page.locator('[data-testid^="delete-account-"]').first();
    await deleteButton.click();

    // Wait for the success message to appear after delete
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });
    const successMessage = await accountsPage.getSuccessMessage();
    expect(successMessage).toContain('Account deleted successfully');
  });

  test('should toggle add account form visibility', async ({ page, accountsPage }) => {
    // Form should be hidden initially
    await expect(accountsPage.createAccountForm).toBeHidden();

    // Click Add Account button to open the form
    await accountsPage.clickAddAccount();
    await expect(accountsPage.createAccountForm).toBeVisible();

    // Close the form by clicking the Cancel button inside the modal
    const cancelButton = page.getByRole('button', { name: 'Cancel' });
    await cancelButton.click();
    await expect(accountsPage.createAccountForm).toBeHidden();
  });

  test('should create account with different types', async ({ page, accountsPage }) => {
    // Create an E_WALLET account
    await accountsPage.createAccount({
      name: 'Momo Wallet',
      type: 'E_WALLET',
      description: 'My e-wallet',
      initialBalance: '200000',
      currency: 'VND',
    });
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });

    // Verify account was created
    await expect(accountsPage.accountsItems).toBeVisible();
    await expect(page.getByText('Momo Wallet').first()).toBeVisible();
  });

  test('should handle cancel in delete confirmation dialog', async ({ page, accountsPage }) => {
    // Wait for any previous success message to auto-dismiss
    await page.waitForTimeout(4000);

    // First create an account
    await accountsPage.createAccount({
      name: 'Test Account for Cancel',
      type: 'CASH',
      initialBalance: '50000',
      currency: 'VND',
    });
    await expect(accountsPage.successMessage).toBeVisible({ timeout: 15000 });

    // Verify account appears
    await expect(page.getByText('Test Account for Cancel').first()).toBeVisible();

    // Wait for success message to auto-dismiss
    await page.waitForTimeout(4000);

    // Set up dialog handler BEFORE clicking delete
    page.once('dialog', (dialog) => {
      dialog.dismiss();
    });

    // Try to delete but cancel the dialog
    const deleteButton = page.locator('[data-testid^="delete-account-"]').first();
    await deleteButton.click();

    // Account should still exist
    await expect(page.getByText('Test Account for Cancel').first()).toBeVisible();
  });
});