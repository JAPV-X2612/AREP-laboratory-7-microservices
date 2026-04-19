# CLAUDE.md — Twitter-like Microservices Lab

This file provides Claude Code with the context needed to assist developers effectively on this project.

---

## Project Overview

A Twitter-like web application built as part of an AREP lab assignment. The project evolves through two architectures:

1. **Monolith** — Spring Boot REST API backed by H2 (dev) or PostgreSQL (prod), secured with Auth0 JWT.
2. **Microservices** — Three independent AWS Lambda functions backed by DynamoDB, each validating Auth0 JWTs independently.

A shared React (Vite) frontend consumes either architecture depending on the `VITE_API_BASE_URL` environment variable.

---

## Repository Structure

```
.
├── twitter-monolith/          # Spring Boot monolithic application (Java 17, Maven)
│   ├── src/main/java/edu/eci/arep/
│   │   ├── config/            # SecurityConfig, OpenApiConfig
│   │   ├── controller/        # PostController, StreamController, UserController
│   │   ├── dto/               # PostRequest, PostResponse, UserResponse
│   │   ├── entity/            # AppUser, Post (JPA entities)
│   │   ├── exception/         # GlobalExceptionHandler, UserNotFoundException
│   │   ├── repository/        # AppUserRepository, PostRepository (Spring Data JPA)
│   │   └── service/           # PostService, StreamService, UserService + Impl
│   └── src/main/resources/
│       └── application.properties
│
├── microservices/
│   ├── post-service/          # Lambda: GET /api/posts (public), POST /api/posts (protected)
│   ├── stream-service/        # Lambda: GET /api/stream (public)
│   └── user-service/          # Lambda: GET /api/me (protected)
│   Each service follows the same internal layout:
│       src/main/java/edu/eci/arep/
│           handler/           # AWS Lambda RequestHandler (entry point)
│           service/           # Business logic
│           model/ or entity/  # Domain model
│           dto/               # Response DTOs
│           util/              # Auth0TokenValidator
│
├── frontend/                  # React 18 + Vite SPA
│   ├── src/
│   │   ├── components/        # auth/, post/, stream/ UI components
│   │   ├── pages/             # HomePage, ProfilePage
│   │   └── services/          # apiService.js (axios calls to backend)
│   ├── .env.example           # Template — copy to .env and fill values
│   └── vite.config.js         # Dev server on port 3000
│
├── .claude/
│   └── settings.json          # Shared Claude Code permissions (committed)
└── CLAUDE.md                  # This file
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Monolith backend | Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA, H2 (dev), PostgreSQL (prod) |
| Microservices | Java 17, AWS Lambda, AWS SDK v2 (DynamoDB), auth0-java-jwt, jwks-rsa |
| API documentation | SpringDoc OpenAPI 2.5 (Swagger UI) |
| Frontend | React 18, Vite 5, Auth0 React SDK, Axios |
| Authentication | Auth0 (JWT / OAuth2) — mandatory for all protected endpoints |
| Cloud (prod) | AWS Lambda + API Gateway, AWS DynamoDB, Amazon S3 (frontend) |

---

## Prerequisites

- **Java 17** — `java -version`
- **Maven 3.8+** — `mvn -version`
- **Node.js 18+** — `node -v`
- **Auth0 account** with a configured SPA Application and API (see Auth0 Setup below)
- **Docker Desktop** — only required for running microservices locally via SAM
- **AWS SAM CLI** — only required for running microservices locally
- **AWS CLI** — only required for microservices and DynamoDB Local

---

## Environment Variables

### Monolith (set in shell before running)

```bash
export AUTH0_ISSUER_URI="https://YOUR-DOMAIN.auth0.com/"   # must end with /
export AUTH0_AUDIENCE="https://your-api-audience"
```

### Frontend (`frontend/.env` — copy from `frontend/.env.example`)

```
VITE_AUTH0_DOMAIN=your-domain.auth0.com
VITE_AUTH0_CLIENT_ID=your-spa-client-id
VITE_AUTH0_AUDIENCE=https://your-api-audience
VITE_API_BASE_URL=http://localhost:8080        # monolith; change to :8081 for microservices
```

### Microservices (Lambda environment variables)

Each function reads these at runtime:
- `AUTH0_ISSUER_URI`, `AUTH0_AUDIENCE`
- `POSTS_TABLE` (post-service, stream-service) — DynamoDB table name
- `USERS_TABLE` (user-service) — DynamoDB table name

**Never commit `.env` or credentials to the repository.**

---

## Build Commands

### Monolith

```bash
cd twitter-monolith
mvn clean package -DskipTests   # compile and package
mvn spring-boot:run              # run locally on port 8080
mvn test                         # run all tests
```

### Microservices (build each independently)

```bash
cd microservices/post-service    && mvn clean package -DskipTests
cd microservices/user-service    && mvn clean package -DskipTests
cd microservices/stream-service  && mvn clean package -DskipTests
```

### Frontend

```bash
cd frontend
npm install       # install dependencies (first time only)
npm run dev       # dev server on http://localhost:3000
npm run build     # production build → dist/
npm run preview   # preview production build locally
```

---

## Running Locally

### Option A — Monolith + Frontend

Terminal 1:
```bash
export AUTH0_ISSUER_URI="https://YOUR-DOMAIN.auth0.com/"
export AUTH0_AUDIENCE="https://your-api-audience"
cd twitter-monolith && mvn spring-boot:run
```

Terminal 2:
```bash
cd frontend && npm run dev
```

| URL | Description |
|---|---|
| `http://localhost:3000` | React frontend |
| `http://localhost:8080/api/stream` | Public stream (no auth) |
| `http://localhost:8080/api/posts` | Public post list (no auth) |
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/h2-console` | H2 database console (JDBC URL: `jdbc:h2:mem:twitterdb`, user: `sa`, password: empty) |

### Option B — Microservices + Frontend (requires Docker + SAM CLI)

Terminal 1 — DynamoDB Local:
```bash
docker run -p 8000:8000 amazon/dynamodb-local
```

Terminal 2 — create tables (first time only):
```bash
aws dynamodb create-table --table-name posts \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://localhost:8000

aws dynamodb create-table --table-name users \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url http://localhost:8000
```

Terminal 3 — SAM API (requires `microservices/template.yaml`):
```bash
cd microservices && sam local start-api --port 8081
```

Terminal 4 — Frontend (set `VITE_API_BASE_URL=http://localhost:8081` in `.env`):
```bash
cd frontend && npm run dev
```

---

## Auth0 Setup

The project requires two Auth0 entities:

### 1. Application (Single Page Application)
- **Type:** Single Page Application
- **Allowed Callback URLs:** `http://localhost:3000`
- **Allowed Logout URLs:** `http://localhost:3000`
- **Allowed Web Origins:** `http://localhost:3000`
- Provides: `Client ID` → `VITE_AUTH0_CLIENT_ID`

### 2. API
- **Identifier (Audience):** any unique URI, e.g. `https://twitter-api`
- Provides: `Audience` → `VITE_AUTH0_AUDIENCE` and `AUTH0_AUDIENCE`

Both the monolith and the microservices act as **OAuth2 Resource Servers** — they share the same `AUTH0_ISSUER_URI` and `AUTH0_AUDIENCE` values.

### Including `email` in the Access Token (required)

Auth0 does not include `email` in access tokens by default. Add a **Post Login Action** in Auth0:

```javascript
exports.onExecutePostLogin = async (event, api) => {
  if (event.authorization) {
    api.accessToken.setCustomClaim('email', event.user.email);
    api.accessToken.setCustomClaim('nickname', event.user.nickname);
  }
};
```

Deploy the Action and attach it to the **Login flow** in Auth0 Dashboard → Actions → Flows → Login.

---

## API Endpoints

### Public (no authentication required)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/stream` | Returns all posts ordered newest first |
| `GET` | `/api/posts` | Returns all posts |

### Protected (requires `Authorization: Bearer <JWT>`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/posts` | Creates a new post (max 140 characters) |
| `GET` | `/api/me` | Returns the authenticated user's profile |

**Testing protected endpoints:** use Swagger UI at `/swagger-ui.html` (click Authorize and paste your Bearer token), or pass the token via `curl -H "Authorization: Bearer <token>"`. The token can be obtained from the browser's DevTools → Network tab while the frontend is running.

---

## Code Conventions

All code in this project follows these rules (see `instrucciones.txt` for full details):

- All source code and comments must be written in **formal English**.
- Apply **SOLID, YAGNI, KISS, and DRY** principles throughout.
- Use appropriate **creational, behavioral, structural, and architectural design patterns**.
- Every class and method must have a **Javadoc comment** with description, `@param`, `@return`, and `@throws` where applicable, following this header:

```java
/**
 * [Brief description]
 *
 * @author David Velásquez, Jesús Pinzón, Santiago Díaz
 * @version 1.0
 * @since YYYY-MM-DD
 */
```

- Do not add inline comments unless the reason is genuinely non-obvious.
- Do not create attributes, methods, or abstractions beyond what the task requires.