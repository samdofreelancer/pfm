import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 1,
  workers: process.env.CI ? 2 : undefined,
  // Use list + HTML reporter in CI for better visibility, html for local
  reporter: process.env.CI 
    ? [['list'], ['html', { outputFolder: 'playwright-report', open: 'never' }]]
    : 'html',
  use: {
    baseURL: process.env.CI ? 'http://frontend:3000' : 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    // Performance optimizations for CI
    actionTimeout: 10000,
    navigationTimeout: 30000,
  },
  // In CI/Docker, only run chromium for faster execution
  // For full cross-browser testing, run locally without CI env var
  projects: process.env.CI
    ? [
        {
          name: 'chromium',
          use: { ...devices['Desktop Chrome'] },
        },
      ]
    : [
        {
          name: 'chromium',
          use: { ...devices['Desktop Chrome'] },
        },
        {
          name: 'firefox',
          use: { ...devices['Desktop Firefox'] },
        },
        {
          name: 'webkit',
          use: { ...devices['Desktop Safari'] },
        },
      ],
  // In Docker, frontend is already running as a separate service
  // webServer is only used for local development
  webServer: process.env.CI
    ? undefined
    : {
        command: 'npm run dev',
        url: 'http://localhost:3000',
        reuseExistingServer: !process.env.CI,
        cwd: '../frontend',
      },
});