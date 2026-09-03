# 🔧 BookBridge — Backend

> The Spring Boot + MySQL engine powering BookBridge.

**🔗 Live Site:** [https://mybookbridge.netlify.app](https://mybookbridge.netlify.app)
**🔗 Frontend Repo:** [bookbridge-frontend](https://github.com/SadhviShastry/bookbridge-frontend)
**🔗 Live API:** [https://bookbridge-backend-vrkx.onrender.com/api](https://bookbridge-backend-vrkx.onrender.com/api)

![Status](https://img.shields.io/badge/status-live-brightgreen)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Deployed on](https://img.shields.io/badge/deployed%20on-Render-46E3B7)

---

## ✨ Overview

This is the REST API backend for **BookBridge**, a community book-sharing platform where users can lend, borrow, donate, exchange, or sell books. It's built with **Java 17 + Spring Boot 3**, secured with **JWT authentication**, and backed by a **MySQL** database. It powers the [BookBridge frontend](https://github.com/SadhviShastry/bookbridge-frontend) end-to-end  auth, book listings, requests, messaging, notifications, reviews, donations, memberships, and a full admin dashboard.

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Security) |
| Auth | JWT (jjwt) + BCrypt password hashing |
| Database | MySQL 8 (via Hibernate/JPA) |
| Build Tool | Maven |
| Hosting | Render (Docker-based web service) |
| Database Hosting | Railway |

## 🏗️ Architecture

```
Controller  →  Service  →  Repository  →  MySQL
     ↑
 JWT Auth Filter + Spring Security (role-based: USER / ADMIN)
```

Every response DTO is enriched at the service layer — e.g. a borrow request response includes the full book, owner, and requester details, not just their IDs — so the frontend never has to make extra round-trips.

## 📁 Project Structure

```
bookbridge-backend/
└── src/main/java/com/bookbridge/
    ├── BookbridgeApplication.java
    ├── config/          # SecurityConfig, CorsConfig, DataSeeder (seeds admin + categories on first boot)
    ├── controller/       # REST endpoints — one per resource
    ├── dto/               # request/response records & enrichment DTOs
    ├── entity/             # JPA entities (User, Book, BorrowRequest, Review, Donation, ...)
    ├── exception/           # ApiException + global @RestControllerAdvice handler
    ├── repository/           # Spring Data JPA repositories
    ├── security/              # JwtUtil, JwtAuthFilter, CurrentUser helper
    └── service/                 # business logic
```

## 🚀 Features

- 🔐 JWT auth with BCrypt-hashed passwords and role-based access control (`USER` / `ADMIN`)
- 📚 Book CRUD — Lend, Donate, Exchange, or Sell listings with category, condition, and cover image
- 📩 Full request lifecycle — create, accept, reject, complete, with automatic due-date and availability handling
- 💬 Threaded messaging between owner and requester
- 🔔 In-app notifications for every request/review/message event
- ⭐ Reviews tied to completed exchanges, with live rating recalculation
- 💰 Donations with optional message, tracked platform-wide
- 🏅 Auto-computed badges based on user activity
- 👑 Membership tiers
- 📅 Community events/offers
- 🛠️ Admin endpoints — stats, user/listing moderation, exchange & delivery coordination, category management, donation records, full activity log
- 🌱 `DataSeeder` auto-creates an admin account, default categories, and sample events on first boot — no manual SQL required

## 🖥️ Running Locally

### Prerequisites
- Java 17 (JDK)
- Maven
- A MySQL instance (local or cloud — e.g. Railway)

### Steps

1. Clone this repo:
   ```bash
   git clone https://github.com/SadhviShastry/bookbridge-backend.git
   cd bookbridge-backend
   ```
2. Set the required environment variables:
   ```bash
   DB_HOST=<your-mysql-host>
   DB_PORT=3306
   DB_NAME=bookbridge
   DB_USER=<your-mysql-user>
   DB_PASSWORD=<your-mysql-password>
   JWT_SECRET=<a long random string>
   CORS_ORIGINS=http://127.0.0.1:5500,http://localhost:5500
   ```
3. Run:
   ```bash
   mvn spring-boot:run
   ```
4. Tables are auto-created by Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no manual schema setup needed.
5. Verify it's running:
   ```
   http://localhost:8080/api/categories
   ```

## 🔑 Default Admin Account (seeded on first boot)

| Field | Value |
|---|---|
| Email | `admin@bookbridge.com` |
| Password | `admin123` — **change immediately after first login in production** |

## 🌐 Deployment

This backend is deployed as a **Docker web service on Render**, connected to a **MySQL instance on Railway**. See the [`Dockerfile`](./Dockerfile) for the build (Maven build stage → lightweight JRE runtime stage).

Key environment variables to configure on your host:

| Variable | Purpose |
|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | MySQL connection |
| `JWT_SECRET` | Signing key for JWTs — must be long & random in production |
| `CORS_ORIGINS` | Comma-separated list of allowed frontend origins |

> ⚠️ Render's free tier spins down after inactivity, which can delay the first request by up to ~50 seconds. An uptime monitor (e.g. UptimeRobot) is recommended to keep the instance warm.

## 📦 Related Repository

The frontend that consumes this API:
👉 **[github.com/SadhviShastry/bookbridge-frontend](https://github.com/SadhviShastry/bookbridge-frontend)**

## 👩‍💻 Author

Built by **Sadhvi Shastry** as a full-stack mini project — K. C. College of Engineering and Management Studies & Research, Department of Information Technology.

---

<p align="center">Made with ☕ and Spring Boot — BookBridge, 2026</p>
