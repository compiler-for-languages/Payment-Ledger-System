# Payment Ledger & Wallet System

Enterprise wallet management and immutable ledger platform using Spring Boot and React.

## Project Structure

- `Backend/project1` -> Spring Boot backend (Java 17, Spring Boot 3.5.15)
- `src` -> React frontend (Vite + TypeScript + Tailwind)

## Backend Run

1. Start dependencies:
   - `docker compose up -d`
2. Run backend:
   - `cd Backend/project1`
   - `mvn spring-boot:run`
3. Swagger:
   - `http://localhost:8080/swagger-ui.html`

### Seeded Accounts

- Admin: `admin@infotact.com` / `password`
- User: `user@infotact.com` / `password`

## Backend Test

- `cd Backend/project1`
- `mvn clean test`

### Admin API Additions

- `GET /api/admin/users/search?keyword=john`
- `GET /api/admin/users/paged?page=0&size=20`
- `GET /api/admin/wallets/filter?status=ACTIVE`
- `GET /api/admin/wallets/paged?page=0&size=20`
- `GET /api/admin/transactions/search?keyword=TXN-`
- `GET /api/admin/transactions/paged?page=0&size=20`
- `GET /api/admin/transactions/filter?type=TRANSFER&status=SUCCESS&from=2026-01-01&to=2026-12-31`
- `GET /api/admin/reports/summary?from=2026-01-01&to=2026-01-31`
- `GET /api/admin/reports/transactions.csv?from=2026-01-01&to=2026-01-31`

## Frontend Run

- `npm install`
- `npm run dev`
