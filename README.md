# PFM - Personal Finance Manager

A modern personal finance management application built with Spring Boot (backend) and React (frontend), following Domain-Driven Design (DDD) architecture.

## 🏗️ Architecture

### Backend - DDD Multi-Module Maven Project

```
backend/
├── pfm-common/           # Shared Kernel (DTOs, Exceptions, Constants)
├── pfm-domain/           # Core Domain (Entities, Events, Repository Interfaces)
├── pfm-application/      # Use Cases (Command Handlers, Mappers)
├── pfm-infrastructure/   # External Concerns (JPA, JWT, Security)
├── pfm-api/              # REST Controllers, Exception Handlers
└── pfm-bootstrap/        # Main Application, Configuration, Flyway Migrations
```

### Frontend - React + Vite + TailwindCSS

```
frontend/
├── src/
│   ├── components/auth/  # AuthPage (Login/Signup)
│   ├── services/         # API client with JWT interceptor
│   ├── App.jsx           # Routes with ProtectedRoute
│   └── main.jsx          # Entry point
└── dist/                 # Production build
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (Temurin/OpenJDK)
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **Docker** and **Docker Compose** (for database)

### Option 1: Docker (Recommended - One Command)

```bash
# Start everything (PostgreSQL, Redis, Backend, Frontend)
./start.sh --docker
```

Or on Windows:
```cmd
start.bat --docker
```

Then open:
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **API Docs:** http://localhost:8080/swagger-ui.html

### Option 2: Local Development with Hot Reload

```bash
# Start database + backend (with hot reload) + frontend (with HMR)
./start.sh --dev
```

Or on Windows:
```cmd
start.bat --dev
```

**Hot Reload Features:**
- **Backend:** Spring Boot DevTools automatically restarts when Java files change
- **Frontend:** Vite HMR (Hot Module Replacement) updates the browser instantly

### Option 3: Standard Local Development

```bash
# Start database + backend (local) + frontend (local)
./start.sh
```

Or on Windows:
```cmd
start.bat
```

## 📋 Available Scripts

### `start.sh` / `start.bat`

| Command | Description |
|---------|-------------|
| `./start.sh` | Start DB (Docker) + Backend (local) + Frontend (local) |
| `./start.sh --dev` | Start with hot reload enabled (backend + frontend) |
| `./start.sh --docker` | Start everything in Docker |
| `./start.sh --backend-only` | Start only backend |
| `./start.sh --frontend-only` | Start only frontend |
| `./start.sh --help` | Show help |

## 🐳 Docker Services

| Service | Container Name | Port | Description |
|---------|---------------|------|-------------|
| PostgreSQL | `pfm-postgres` | 5432 | Primary database |
| Redis | `pfm-redis` | 6379 | Cache (for future use) |
| Backend | `pfm-backend` | 8080 | Spring Boot API |
| Frontend | `pfm-frontend` | 3000 | Nginx + React |

### Docker Commands

```bash
# Start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Check status
docker-compose ps
```

## 🔧 Backend Setup

### Build

```bash
cd backend

# Compile all modules
mvn compile

# Install to local .m2 repository
mvn install -DskipTests

# Run tests
mvn test

# Package
mvn clean package -DskipTests
```

### Run Locally

```bash
# From backend directory
mvn spring-boot:run -pl pfm-bootstrap

# Or from project root
cd backend && mvn spring-boot:run -pl pfm-bootstrap
```

### Database Migrations

Flyway migrations are in `backend/pfm-bootstrap/src/main/resources/db/migration/`

```bash
# Migrations run automatically on startup
# To run manually:
mvn flyway:migrate -pl pfm-bootstrap
```

## 🎨 Frontend Setup

### Install Dependencies

```bash
cd frontend
npm install
```

### Development

```bash
npm run dev
# Opens at http://localhost:3000
```

### Build for Production

```bash
npm run build
# Output in frontend/dist/
```

### Preview Production Build

```bash
npm run preview
```

## 🔐 Authentication

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login with email/password |
| POST | `/api/v1/auth/refresh` | Refresh access token |

### Request Examples

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Response Example

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 900000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john@example.com",
    "fullName": "John Doe",
    "avatarUrl": null
  }
}
```

## 🛠️ Tech Stack

### Backend
- **Java 17** (Temurin)
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Hibernate)
- **PostgreSQL 15**
- **Redis 7**
- **Flyway** (Database Migrations)
- **Lombok**
- **Maven** (Multi-module)

### Frontend
- **React 18**
- **Vite 5**
- **TailwindCSS 3.3**
- **React Router 6**
- **Axios** (HTTP client)
- **Lucide React** (Icons)
- **Nginx** (Production)

## 📁 Project Structure

```
pfm/
├── backend/
│   ├── pom.xml                    # Parent POM
│   ├── pfm-common/                # Shared kernel
│   ├── pfm-domain/                # Domain layer
│   ├── pfm-application/           # Application layer
│   ├── pfm-infrastructure/        # Infrastructure layer
│   ├── pfm-api/                   # API layer
│   ├── pfm-bootstrap/             # Bootstrap/Config
│   └── Dockerfile                 # Backend Docker image
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── Dockerfile                 # Frontend Docker image
│   ├── nginx.conf                 # Nginx config
│   └── src/
│       ├── components/auth/       # Auth components
│       ├── services/              # API services
│       ├── App.jsx                # Main app
│       └── main.jsx               # Entry point
├── docker-compose.yml             # Docker Compose config
├── start.sh                       # Linux/Mac startup script
└── start.bat                      # Windows startup script
```

## 🔒 Security

- JWT-based authentication (Access + Refresh tokens)
- BCrypt password encoding
- CORS configuration
- Stateless sessions
- Password visibility toggle in UI
- Form validation (client + server side)

## 🎯 Features

### Implemented
- ✅ User Registration
- ✅ User Login
- ✅ JWT Token Management
- ✅ Password Visibility Toggle
- ✅ Form Validation
- ✅ Social Login Buttons (UI ready)
- ✅ Error Handling
- ✅ Loading States
- ✅ Protected Routes
- ✅ Hot Reload (Backend + Frontend)

### Planned
- ⏳ Account Management
- ⏳ Transaction Tracking
- ⏳ Budget Management
- ⏳ Goal Setting
- ⏳ Reports & Analytics
- ⏳ AI Assistant

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 <PID>  # Mac/Linux
taskkill /PID <PID> /F  # Windows
```

### PostgreSQL Connection Refused

```bash
# Start PostgreSQL with Docker
docker-compose up -d postgres

# Check if it's running
docker-compose ps
```

### Timezone Error

If you see `FATAL: invalid value for parameter "TimeZone"`, the app is configured to use `Asia/Ho_Chi_Minh` timezone. This is set in:
- `docker-compose.yml` (container timezone)
- `application.yml` (JDBC connection)
- `pfm-bootstrap/pom.xml` (JVM timezone)

### Frontend Can't Connect to Backend

Make sure:
1. Backend is running on `http://localhost:8080`
2. CORS is configured in `SecurityConfig.java`
3. API base URL in `frontend/src/services/api.js` is correct

## 📝 Development Notes

### Backend

- **Domain Layer** is framework-agnostic (no Spring dependencies)
- **Application Layer** contains use cases and DTOs
- **Infrastructure Layer** handles JPA, Security, external APIs
- **API Layer** exposes REST endpoints
- **Hot Reload:** Spring Boot DevTools automatically restarts the application when classpath files change (Java, properties, etc.)

### Frontend

- Uses Vite for fast HMR (Hot Module Replacement)
- TailwindCSS for utility-first styling
- Axios interceptors for JWT token management
- React Router for client-side routing
- **Hot Reload:** Vite HMR updates the browser instantly when files change, without full page reload

## 📄 License

This project is licensed under the MIT License.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For issues and questions, please open an issue on GitHub.