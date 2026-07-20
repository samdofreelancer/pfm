FROM mcr.microsoft.com/playwright:v1.61.0-noble AS base
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./
COPY tsconfig.json ./

# Install dependencies (browsers are already included in the base image)
RUN npm ci

# Copy source code
COPY . .

# Create directories for test results and reports
RUN mkdir -p /app/test-results /app/playwright-report

# Run tests with HTML reporter for CI
CMD ["npm", "test"]
