# BookBridge — Spring Boot + MySQL Backend

This is the real backend for BookBridge. It matches the routes your frontend's
`js/api.js` already calls — the moment this is running and reachable, the
frontend's localStorage fallback (`js/store.js`) stops being used automatically.
No frontend code changes needed.

## Tech stack
- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security
- MySQL (via `mysql-connector-j`)
- JWT auth (`jjwt`)
- Lombok

## Project layout
```
src/main/java/com/bookbridge/
├── BookbridgeApplication.java
├── config/          SecurityConfig, CorsConfig, DataSeeder (seeds admin + categories on first boot)
├── controller/       one per resource — matches js/api.js routes exactly
├── dto/              request/response records
├── entity/           JPA entities (User, Book, BorrowRequest, Category, Review, ...)
├── exception/        ApiException + a global @RestControllerAdvice handler
├── repository/       Spring Data JPA repositories
├── security/         JwtUtil, JwtAuthFilter, CurrentUser helper
└── service/          business logic (mirrors your original store.js logic 1:1)
```

## Running locally

1. Install a local MySQL server (or use Docker: `docker run -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=bookbridge mysql:8`).
2. Set these environment variables (or edit `src/main/resources/application.properties` directly for local testing):
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=bookbridge
   DB_USER=root
   DB_PASSWORD=root
   JWT_SECRET=<any long random string>
   CORS_ORIGINS=http://127.0.0.1:5500,http://localhost:5500
   ```
3. Run: `mvn spring-boot:run` (or open the project in IntelliJ/Eclipse and run `BookbridgeApplication`).
4. On first boot, `DataSeeder` automatically creates:
   - An admin account: `admin@bookbridge.com` / `admin123` — **change this password immediately after your first login in production.**
   - The 6 default categories and 2 sample events.
5. Tables are auto-created by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no manual SQL needed.

## Point your frontend at it

In `js/api.js`, change:
```js
const API_BASE = "http://localhost:8080/api";
```
to your deployed backend's URL (e.g. `https://bookbridge-backend.onrender.com/api`) once deployed.

## Security notes for a real deployment
- Passwords are hashed with BCrypt (`SecurityConfig.passwordEncoder()`) — never stored in plain text.
- `jwt.secret` **must** be changed to a long random value before deploying — never reuse the placeholder in `application.properties`.
- `/api/admin/**` requires the `ADMIN` role (enforced by `SecurityConfig`, not just by hiding the UI).
- CORS is restricted to the origins listed in `CORS_ORIGINS` — update this to your deployed frontend's URL.

See the root-level `DEPLOYMENT.md` for the full free-tier deployment walkthrough (Render/Railway + MySQL + Netlify).
