# SHMS Backend — Shareholder Management System

Spring Boot backend for the Bhogavati Sugar Factory Shareholder Management System,
rebuilt from the original .NET/Access desktop application.

## What's implemented
- JWT authentication with ADMIN / STAFF / SHAREHOLDER roles (register/login/refresh)
- Shareholder registration, search (filter by name/village/class/status), update, delete
- Rate management with full history (effective-dated, so old transactions stay consistent)
- Share Allotment (Class A / Class C classification rule preserved from the legacy app)
- Share Sugar transactions (Class A entitlement, running balance — legacy carry-forward bug fixed)
- Tonnes Sugar transactions (Class C entitlement, same running-balance pattern)
- Sugar Allocation bonus batch (Class A only, fully audited)
- Share Transfer workflow: PENDING → APPROVED/REJECTED, supports DEATH/SALE/GIFT
- Document upload/download/delete (real file storage, replacing the legacy Yes/No flags)
- Audit log (every mutating action is recorded)
- Dashboard summary endpoint
- Global exception handling, request validation, Swagger/OpenAPI docs
- A default admin account is seeded automatically on first run (see below)

## Not yet implemented (flagged, not silently skipped)
- PDF/Excel report generation (Phase 14 in the original spec) — reporting endpoints aren't built yet
- QR code generation (Phase 10)
- Actual email sending (mail dependency is wired, but no notification service calls it yet)
- Automated tests (Phase 19)
- Docker image for the Spring Boot app itself (docker-compose.yml here only runs MySQL)
- React frontend (this repo is backend-only — see note below)

Ask to continue and I'll build any of these next.

## Requirements
- Java 17+
- Maven 3.9+ (or use Eclipse's bundled Maven support)
- MySQL 8+ (or `docker compose up -d` to run one locally)

## Running in Eclipse
1. **File → Import → Maven → Existing Maven Projects** → select this folder.
2. Right-click the project → **Maven → Update Project**.
3. **Install the Lombok Eclipse plugin** if you haven't already — download `lombok.jar` from
   https://projectlombok.org/download, run `java -jar lombok.jar`, point it at your Eclipse
   install, and restart Eclipse. Without this, Eclipse will show red errors on every
   `@Getter`/`@Setter`/`@Builder` even though the actual Maven build compiles fine.
4. Copy `.env.example` to `.env` (or just set env vars/edit `application.yml` directly) with
   your MySQL credentials.
5. Run `ShmsApplication.java` as a **Java Application** (or **Spring Boot App** if you have
   the Spring Tools plugin).

## Running from the command line
```bash
docker compose up -d          # starts local MySQL (skip if you have your own)
cp .env.example .env          # edit with real values if not using docker-compose defaults
mvn clean install
mvn spring-boot:run
```

## Default login (seeded automatically on first run)
```
username: admin
password: admin123
```
**Change this immediately if this ever runs anywhere beyond your local machine.**

## Verify it's running
```bash
curl http://localhost:8080/api/ping
```
Then log in:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```
Use the returned `accessToken` as `Authorization: Bearer <token>` on every other endpoint.

Swagger UI: http://localhost:8080/swagger-ui.html

## Why no React frontend in this delivery
A React app is a separate npm project and doesn't run "in Eclipse" the way a Maven project does.
This delivery is the complete, runnable backend so you can verify it works first. Say the word
and I'll build the React frontend as its own project next.
