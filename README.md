# PrimeTrade Task API

A production-grade REST API with JWT authentication, Role-Based Access Control, and a React frontend for testing.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Auth | JWT (jjwt), BCrypt password hashing |
| Database | PostgreSQL (prod) / H2 in-memory (dev) |
| Docs | Swagger / OpenAPI 3 (springdoc) |
| Frontend | Vanilla JS + HTML (zero build step) |

---

## Quick Start

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

API runs at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`
H2 Console at `http://localhost:8080/h2-console`

**Default admin user seeded on startup:**
- Username: `admin`
- Password: `admin123`

### Frontend

```bash
cd frontend
# Option 1: Open directly in browser
open index.html

# Option 2: Serve with any static server
npx serve .
# or
python -m http.server 5173
```

---

## API Reference

### Authentication — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/register` | None | Register new user |
| POST | `/login` | None | Login, returns JWT |

**Register:**
```json
POST /api/v1/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Login:**
```json
POST /api/v1/auth/login
{
  "username": "john",
  "password": "secret123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGci...",
    "username": "john",
    "role": "ROLE_USER"
  }
}
```

---

### Tasks — `/api/v1/tasks` (JWT required)

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/tasks` | USER/ADMIN | Create task |
| GET | `/tasks` | USER/ADMIN | Get my tasks |
| GET | `/tasks/{id}` | USER/ADMIN | Get task by ID |
| PUT | `/tasks/{id}` | USER/ADMIN | Update task |
| DELETE | `/tasks/{id}` | USER/ADMIN | Delete task |

**Create task:**
```json
POST /api/v1/tasks
Authorization: Bearer <token>
{
  "title": "Review PR",
  "description": "Check the auth module PR",
  "status": "TODO",
  "priority": "HIGH"
}
```

**Task status values:** `TODO` | `IN_PROGRESS` | `DONE`
**Priority values:** `LOW` | `MEDIUM` | `HIGH`

---

### Admin — `/api/v1/admin` (ADMIN role only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/users` | List all users |
| GET | `/admin/tasks` | List all tasks |
| DELETE | `/admin/users/{id}` | Delete user |
| PUT | `/admin/users/{id}/role?role=ROLE_ADMIN` | Change role |

---

## Database Schema

```sql
-- users
CREATE TABLE users (
  id         BIGSERIAL PRIMARY KEY,
  username   VARCHAR(30) UNIQUE NOT NULL,
  email      VARCHAR(100) UNIQUE NOT NULL,
  password   VARCHAR(255) NOT NULL,  -- BCrypt hashed
  role       VARCHAR(20) NOT NULL,   -- ROLE_USER | ROLE_ADMIN
  created_at TIMESTAMP
);

-- tasks
CREATE TABLE tasks (
  id          BIGSERIAL PRIMARY KEY,
  title       VARCHAR(100) NOT NULL,
  description TEXT,
  status      VARCHAR(20) NOT NULL,   -- TODO | IN_PROGRESS | DONE
  priority    VARCHAR(10) NOT NULL,   -- LOW | MEDIUM | HIGH
  owner_id    BIGINT REFERENCES users(id) ON DELETE CASCADE,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);
```

---

## Security Practices

- Passwords hashed with **BCrypt** (strength 10)
- JWT signed with **HS256**, 24-hour expiration
- **Stateless sessions** — no server-side session storage
- **RBAC** enforced at service layer via `@PreAuthorize`
- Input validation via **Jakarta Bean Validation** (`@NotBlank`, `@Size`, `@Email`)
- Input sanitisation — all user input validated before persistence
- **CORS** restricted to allowed origins only
- Global exception handler — no stack traces exposed to clients
- Admin endpoints protected by role check at both security config and method level

---

## Scalability Note

### Current architecture
Single Spring Boot service → PostgreSQL → Stateless JWT (horizontally scalable now)

### Path to scale

**Caching (Redis)**
```
GET /tasks → Redis cache (TTL: 60s) → DB on miss
Invalidate on create/update/delete
```

**Microservices decomposition**
```
auth-service     → handles JWT issuance/validation
task-service     → CRUD for tasks
user-service     → user management
api-gateway      → routing, rate limiting, auth
```

**Load balancing**
```
Nginx / AWS ALB → multiple Spring Boot instances
All instances share same Postgres + Redis
Works because JWT is stateless
```

**Docker deployment**
```bash
docker build -t primetrade-api .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/primetradedb \
  -e JWT_SECRET=your-secret \
  primetrade-api
```

**Kafka (async events)**
- Task creation → notification event → email/push service
- Decouples task service from notification concerns

---

## Environment Variables (Production)

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/primetradedb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=yourpassword
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION=86400000
APP_CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

---

## Author

**Rahul Yadav Bakkatatla**
- GitHub: [github.com/rahulbakkatatla](https://github.com/rahulbakkatatla)
- LinkedIn: [linkedin.com/in/rahul-yadav-bakkatatla](https://www.linkedin.com/in/rahul-yadav-bakkatatla/)
