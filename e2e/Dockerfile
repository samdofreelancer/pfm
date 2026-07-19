FROM mcr.microsoft.com/playwright:v1.47.0-focal AS base
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./
COPY tsconfig.json ./

# Install dependencies including Playwright browsers
RUN npm ci
RUN npx playwright install --with-deps chromium

# Copy source code
COPY . .

# Create directories for test results and reports
RUN mkdir -p /app/test-results /app/playwright-report

# Run tests with HTML reporter for CI
CMD ["npm", "test"]