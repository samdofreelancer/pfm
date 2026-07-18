FROM mcr.microsoft.com/playwright:v1.47.0-focal AS base
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./
COPY tsconfig.json ./

# Install dependencies including Playwright browsers
RUN npm ci
RUN npx playwright install --with-deps chromium firefox webkit

# Copy source code
COPY . .

# Run tests
CMD ["npm", "test"]
