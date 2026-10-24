# SHMS Frontend

React + Vite frontend for the Shareholder Management System, talking to the Spring Boot
backend (`shms-backend`) you already have running on `localhost:8080`.

## What's implemented
- Login (JWT, with auto-refresh on expiry)
- Role-based navigation (ADMIN / STAFF see different menu items)
- Dashboard with charts (shareholders by class, transfer status)
- Shareholder registration, search/filter, and a detail page with tabs:
  Profile, Allotments, Sugar History, Documents (upload/download/delete)
- Share Allotment form (shows the new Class A/C memberId after allotment)
- Share Sugar + Tonnes Sugar transaction recording
- Sugar Allocation (bonus) — ADMIN only
- Share Transfer request + approve/reject — ADMIN approves
- Rate management with history — ADMIN only
- Audit log viewer — ADMIN only

## Known limitation
**"My Profile" for SHAREHOLDER-role logins is a placeholder.** The backend's JWT doesn't
currently expose which Shareholder record a SHAREHOLDER-role user is linked to via a
dedicated endpoint (there's no `GET /api/shareholders/me`), so this page just says so
honestly rather than faking data. Happy to add that backend endpoint if you want
self-service login to actually work.

## Requirements
- Node.js 18+ and npm
- The backend running on `http://localhost:8080` (see `shms-backend` README)

## Running it
```bash
npm install
npm run dev
```
Then open http://localhost:5173

The dev server proxies all `/api/*` requests to `http://localhost:8080`, so there's no
CORS configuration needed on your end — just make sure the backend is running first.

## Login
Use the same seeded admin account as the backend:
```
username: admin
password: admin123
```

## Building for production
```bash
npm run build
```
Outputs static files to `dist/`. Note: the Vite proxy only works in dev mode — for a
production deployment you'd serve this behind the same origin as the API, or update
`app.cors.allowed-origins` in the backend and point this app's requests at the real
backend URL instead of relying on the dev proxy.
