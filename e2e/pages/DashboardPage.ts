import { Page, Locator } from '@playwright/test';

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

  // Total balance card
  readonly totalBalanceLabel: Locator;
  readonly totalBalanceAmount: Locator;
  readonly incomeLabel: Locator;
  readonly expenseLabel: Locator;

  // Sections
  readonly tongQuanThuChiHeading: Locator;
  readonly lichChiTieuHeading: Locator;
  readonly tinhHinhThuChiHeading: Locator;
  readonly ghiChepGanDayHeading: Locator;
  readonly taiKhoanHeading: Locator;
  readonly thuTienHeading: Locator;
  readonly chiTienHeading: Locator;

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

    // Total balance
    this.totalBalanceLabel = page.locator('text=Tổng số dư');
    this.totalBalanceAmount = page.locator('text=18.000.000');
    this.incomeLabel = page.locator('text=Thu:');
    this.expenseLabel = page.locator('text=Chi:');

    // Sections
    this.tongQuanThuChiHeading = page.getByRole('heading', { name: 'Tổng quan thu chi' });
    this.lichChiTieuHeading = page.getByRole('heading', { name: 'Lịch chi tiêu tháng' });
    this.tinhHinhThuChiHeading = page.getByRole('heading', { name: 'Tình hình thu chi' });
    this.ghiChepGanDayHeading = page.getByRole('heading', { name: 'Ghi chép gần đây' });
    this.taiKhoanHeading = page.getByRole('heading', { name: 'Tài khoản' });
    this.thuTienHeading = page.getByRole('heading', { name: 'Thu tiền' });
    this.chiTienHeading = page.getByRole('heading', { name: 'Chi tiền' });
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