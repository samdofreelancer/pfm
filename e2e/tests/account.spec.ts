import { test, expect } from './fixtures/auth.fixture';

test.describe('Profile Management', () => {
  test('should view profile settings page', async ({ page, loginPage, dashboardPage, profilePage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to profile page
    await page.goto('/profile');
    await expect(profilePage.accountHeading).toBeVisible();
  });

  test('should update profile successfully', async ({ page, loginPage, dashboardPage, profilePage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to profile page
    await profilePage.goto();
    await expect(profilePage.accountHeading).toBeVisible();

    // Update profile
    const newFullName = 'Updated Name';
    await profilePage.updateProfile(newFullName);

    // Verify success message
    const successMessage = await profilePage.getSuccessMessage();
    expect(successMessage).toContain('Profile updated successfully');
  });

  test('should change password successfully', async ({ page, loginPage, dashboardPage, profilePage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to profile page
    await profilePage.goto();
    await expect(profilePage.accountHeading).toBeVisible();

    // Change password
    const newPassword = 'newPassword123';
    await profilePage.changePassword(testUser.password, newPassword, newPassword);

    // Verify success message
    const successMessage = await profilePage.getSuccessMessage();
    expect(successMessage).toContain('Password changed successfully');

    // Logout and login with new password
    await dashboardPage.logout();
    await expect(loginPage.welcomeHeading).toBeVisible();

    await loginPage.login(testUser.email, newPassword);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible();
  });

  test('should show error when new passwords do not match', async ({ page, loginPage, dashboardPage, profilePage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to profile page
    await profilePage.goto();
    await expect(profilePage.accountHeading).toBeVisible();

    // Try to change password with mismatched passwords
    await profilePage.currentPasswordInput.fill(testUser.password);
    await profilePage.newPasswordInput.fill('newPassword123');
    await profilePage.confirmPasswordInput.fill('differentPassword123');
    await profilePage.changePasswordButton.click();
    await page.waitForLoadState('networkidle', { timeout: 10000 });

    // Verify error message
    const errorMessage = await profilePage.getErrorMessage();
    expect(errorMessage).toContain('do not match');
  });

  test('should show error with incorrect current password', async ({ page, loginPage, dashboardPage, profilePage, testUser }) => {
    // Register and login first
    await loginPage.goto();
    await loginPage.register(testUser.fullName, testUser.email, testUser.password);
    await expect(dashboardPage.totalBalanceLabel).toBeVisible({ timeout: 15000 });

    // Navigate to profile page
    await profilePage.goto();
    await expect(profilePage.accountHeading).toBeVisible();

    // Try to change password with wrong current password
    await profilePage.changePassword('wrongPassword', 'newPassword123', 'newPassword123');

    // Verify error message
    const errorMessage = await profilePage.getErrorMessage();
    expect(errorMessage).toContain('incorrect');
  });
});
