# Shareholder Management System: Bhogavati Sugar Factory

A full-stack web application for managing shareholders of a cooperative sugar factory: registration, share allotment, sugar entitlement and billing, share transfers, and document management.

This project is a modern rebuild of a legacy **.NET WinForms + MS Access + Crystal Reports** desktop application. The original business rules (Class A / Class C membership, entitlement calculations, transfer on death) were preserved, and the architecture, security and user interface were redesigned.

## Problem Statement
The original desktop system had no real authentication (one hardcoded login), built SQL by string concatenation, kept no rate history, had no approval step for share transfers, and stored documents only as Yes/No flags. This project replaces it with a secure, role-based web application backed by MySQL.

## Features
- **Authentication and authorization:** JWT login with refresh tokens, BCrypt password hashing, and three roles (Admin, Staff, Shareholder)
- **Shareholder management:** registration, update, delete, and search/filter/pagination by ID, name, village, class and status (JPA Specifications)
- **Share allotment:** Class A (land owner with verified evidence) and Class C (no land) classification, preserved from the legacy system
- **Sugar entitlement and billing:**
  - Class A: monthly quota × 12, with a running balance
  - Class C: tonnage-based entitlement, with a running balance
- **Sugar allocation bonus:** an admin-triggered, audited top-up for all Class A members
- **Rate management with history:** effective-dated rates, so old transactions keep the rate that applied at the time
- **Share transfer workflow:** PENDING → APPROVED / REJECTED, supporting death, sale and gift
- **Document management:** upload, download and delete (PDF/JPEG/PNG, 10 MB limit), stored privately and served through authenticated endpoints
- **Audit log:** every important action is recorded (admin-only view)
- **Dashboard:** shareholder counts by class, sugar supplied, transfer status charts
- **API quality:** global exception handling, Bean Validation, DTOs, Swagger/OpenAPI documentation

## Tech Stack
| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Spring Web, Spring Data JPA, Hibernate, Spring Security, JWT (JJWT), Lombok, Maven |
| Database | MySQL 8 |
| Frontend | React 18, Vite, React Router, Axios, Bootstrap 5, Recharts |
| API docs | Swagger / OpenAPI (springdoc) |

## Architecture
```
React (Vite)  ──HTTP/JSON──>  Controller  ->  Service  ->  Repository  ->  MySQL
   JWT in header               DTOs          business      Spring Data JPA
                               validation    rules
```
Layered Spring Boot backend (`controller`, `service`, `repository`, `entity`, `dto`, `mapper`, `security`, `exception`, `specification`, `util`). Business logic lives in services only, and entities are never exposed directly.

## Project Structure
```
├── shms-backend/     Spring Boot REST API
└── shms-frontend/    React + Vite web app
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8 (or Docker, see below)
- Node.js 18+

### 1. Database
Either install MySQL yourself, or run the bundled MySQL container:
```bash
cd shms-backend
docker compose up -d
```
The database `shms_db` is created automatically.

### 2. Backend
```bash
cd shms-backend
cp .env.example .env     # then edit values if needed
mvn spring-boot:run
```
The API runs at `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`

Running from Eclipse: import as an **Existing Maven Project**, install the Lombok plugin, then run `ShmsApplication.java` as a Java Application.

### 3. Frontend
```bash
cd shms-frontend
npm install
npm run dev
```
Open `http://localhost:5173`. The dev server proxies `/api` to the backend on port 8080.

### Default login (development only)
```
username: admin
password: admin123
```
A default admin account is seeded on first run. **Change this password before using the app anywhere other than your own machine.**

## Environment Variables
Set in `shms-backend/.env` (see `.env.example`). Never commit real values.

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/shms_db...` |
| `DB_USERNAME` / `DB_PASSWORD` | Database credentials | `root` / `root` |
| `JWT_SECRET` | Token signing secret (**set a long random value**) | placeholder |
| `JWT_ACCESS_EXPIRATION_MS` | Access token lifetime | `3600000` (1 hour) |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh token lifetime | `604800000` (7 days) |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origin | `http://localhost:5173` |
| `FILE_UPLOAD_DIR` | Where uploaded documents are stored | `./uploads` |

## API Overview
| Area | Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `/login`, `/refresh` |
| Shareholders | `GET/POST /api/shareholders`, `GET/PUT/DELETE /api/shareholders/{memberId}`, `GET /api/shareholders/search` |
| Rates | `POST /api/rates`, `GET /api/rates`, `GET /api/rates/current` |
| Allotments | `POST /api/allotments`, `GET /api/allotments/member/{memberId}` |
| Share sugar (Class A) | `POST /api/share-sugar`, `GET /api/share-sugar/member/{memberId}` |
| Tonnes sugar (Class C) | `POST /api/tonnes-sugar`, `GET /api/tonnes-sugar/member/{memberId}` |
| Sugar allocation | `POST /api/sugar-allocations`, `GET /api/sugar-allocations` |
| Transfers | `POST /api/transfers`, `PUT /api/transfers/{id}/decision`, `GET /api/transfers` |
| Documents | `POST /api/documents`, `GET /api/documents/member/{memberId}`, `GET /api/documents/{id}/download` |
| Audit / Dashboard | `GET /api/audit-logs`, `GET /api/dashboard/summary` |

The full interactive reference is in Swagger UI.

### Example: login
```http
POST /api/auth/login
Content-Type: application/json

{ "username": "admin", "password": "admin123" }
```
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "tokenType": "Bearer",
    "username": "admin",
    "roles": ["ADMIN"]
  }
}
```
Send the token on every other request as `Authorization: Bearer <accessToken>`.

### Typical workflow
1. Admin creates a **rate** (`POST /api/rates`)
2. Staff **registers a shareholder** (gets a temporary ID ending in `U`)
3. Staff **allots shares**: the member becomes Class A or Class C and receives a new ID ending in `A` or `C`
4. Staff **records sugar supplied** (share sugar for Class A, tonnes sugar for Class C)
5. Staff **requests a transfer**, and an Admin approves or rejects it

## Screenshots
Add screenshots to a `docs/` folder and link them here:

| Login | Dashboard |
|---|---|
| `docs/login.png` | `docs/dashboard.png` |

| Shareholder detail | Swagger UI |
|---|---|
| `docs/shareholder-detail.png` | `docs/swagger.png` |

## Future Enhancements
- PDF and Excel reports (replacing the legacy Crystal Reports)
- QR code per shareholder for quick staff lookup
- Email notifications for transfer decisions
- Self-service shareholder portal (`/api/shareholders/me`)
- Automated unit and integration tests
- Dockerizing the backend and frontend into a single `docker-compose` setup

## Author
**Pratiksha Kirulkar**: MCA student, Pune
GitHub: [pratikshakirulkar-9340](https://github.com/pratikshakirulkar-9340)
