# 🐦 Twitter-like Application — Monolith to Microservices

A simplified Twitter-like web application that evolves from a **Spring Boot monolith** into **serverless AWS Lambda microservices**, secured end-to-end with **Auth0** JWT authentication.

<img src="assets/images/architecture.png" alt="Architecture Diagram" width="70%">

---

## 📋 Table of Contents

- [Project Description](#-project-description)
- [Architecture Overview](#-architecture-overview)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Local Setup & Execution](#-local-setup--execution)
- [API Reference](#-api-reference)
- [Auth0 Configuration](#-auth0-configuration)
- [AWS Deployment](#-aws-deployment)
- [Test Report](#-test-report)
- [Authors](#-authors)
- [License](#-license)
- [Additional Resources](#-additional-resources)

---

## 📝 Project Description

This project implements a public microblogging feed where **authenticated users** can post messages of up to **140 characters**, and **anyone** can read the global stream. The system is built in two phases:

1. **Phase 1 — Monolith**: A Spring Boot application with a RESTful API, JPA persistence, Swagger UI, and Auth0 JWT security.
2. **Phase 2 — Microservices**: The monolith is decomposed into three independent AWS Lambda functions behind API Gateway, each backed by DynamoDB.

---

## 🏗️ Architecture Overview

### Phase 1 — Monolith

```
Browser (React SPA on S3)
        │
        │  HTTPS + Auth0 JWT
        ▼
Spring Boot Monolith (EC2 / local)
  ├── SecurityConfig (OAuth2 Resource Server)
  ├── POST /api/posts     → protected
  ├── GET  /api/posts     → public
  ├── GET  /api/stream    → public
  ├── GET  /api/me        → protected
  └── H2 / PostgreSQL
```

### Phase 2 — Microservices

```
Browser (React SPA on S3)
        │
        │  HTTPS + Auth0 JWT
        ▼
AWS API Gateway
  ├── /api/me      → user-service  (Lambda) → DynamoDB: Users
  ├── /api/posts   → post-service  (Lambda) → DynamoDB: Posts
  └── /api/stream  → stream-service(Lambda) → DynamoDB: Posts (read-only)
```

### Auth0 Security Flow

```
User → Auth0 Login → JWT Access Token
     → SPA attaches Bearer token to API calls
     → Backend validates: issuer + audience + signature
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18, Vite, Auth0 React SDK, Axios |
| **Monolith backend** | Spring Boot 3.2, Spring Security OAuth2, Spring Data JPA |
| **Microservices** | AWS Lambda (Java 17), API Gateway, DynamoDB |
| **Authentication** | Auth0 (JWT, JWKS, RS256) |
| **Database (monolith)** | H2 (dev), PostgreSQL (prod) |
| **Database (microservices)** | Amazon DynamoDB |
| **API Docs** | SpringDoc OpenAPI 3 / Swagger UI |
| **Build tool** | Maven 3.x |
| **Static hosting** | Amazon S3 |

---

## 📁 Project Structure

```
AREP-laboratory-7-microservices/
├── twitter-monolith/               ← Phase 1: Spring Boot monolith
│   ├── pom.xml
│   └── src/main/java/edu/eci/arep/
│       ├── config/                 ← SecurityConfig, OpenApiConfig
│       ├── controller/             ← PostController, StreamController, UserController
│       ├── dto/                    ← PostRequest, PostResponse, UserResponse
│       ├── entity/                 ← AppUser, Post
│       ├── exception/              ← GlobalExceptionHandler, UserNotFoundException
│       ├── repository/             ← AppUserRepository, PostRepository
│       └── service/                ← PostService, StreamService, UserService (+ Impls)
├── microservices/
│   ├── user-service/               ← GET /api/me (Lambda)
│   ├── post-service/               ← GET|POST /api/posts (Lambda)
│   └── stream-service/             ← GET /api/stream (Lambda)
└── frontend/                       ← React SPA (deployed on S3)
    └── src/
        ├── components/{auth,post,stream}
        ├── pages/
        └── services/apiService.js
```

---

## 🚀 Local Setup & Execution

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+
- An [Auth0](https://auth0.com) account

### 1. Configure Auth0

1. Create a **Single Page Application** in Auth0 → copy the *Domain* and *Client ID*.
2. Create an **API** in Auth0 → set a custom *Audience* (e.g. `https://twitter-app/api`).
3. Add `http://localhost:3000` to the SPA's **Allowed Callback / Logout / Web Origins** URLs.

### 2. Run the Monolith

```bash
cd twitter-monolith
export AUTH0_ISSUER_URI=https://your-domain.auth0.com/
export AUTH0_AUDIENCE=https://twitter-app/api
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Run the Frontend

```bash
cd frontend
cp .env.example .env
# Fill in VITE_AUTH0_DOMAIN, VITE_AUTH0_CLIENT_ID, VITE_AUTH0_AUDIENCE
npm install
npm run dev
```

The app will be available at `http://localhost:3000`.

### 4. Run Tests (Monolith)

```bash
cd twitter-monolith
mvn test
```

### 5. Run Tests (Microservices)

```bash
cd microservices/user-service && mvn test
cd ../post-service && mvn test
cd ../stream-service && mvn test
```

---

## 📡 API Reference

### Public Endpoints (no authentication required)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/posts` | List all posts (newest first) |
| `GET` | `/api/stream` | Global public post stream |

### Protected Endpoints (require `Authorization: Bearer <token>`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/posts` | Create a new post (max 140 chars) |
| `GET` | `/api/me` | Get the authenticated user's profile |

#### POST `/api/posts` — Request Body

```json
{
  "content": "Hello, world!"
}
```

#### POST `/api/posts` — Response `201 Created`

```json
{
  "id": 1,
  "content": "Hello, world!",
  "authorNickname": "alice",
  "createdAt": "2026-04-17T12:00:00Z"
}
```

---

## 🔒 Auth0 Configuration

| Setting | Value |
|---|---|
| Application Type | Single Page Application |
| Token Endpoint Auth Method | None |
| Allowed Callback URLs | `http://localhost:3000`, `https://your-s3-bucket.s3-website.amazonaws.com` |
| API Identifier (Audience) | `https://twitter-app/api` |
| Signing Algorithm | RS256 |

Environment variables required by the backend:

```properties
AUTH0_ISSUER_URI=https://your-domain.auth0.com/
AUTH0_AUDIENCE=https://twitter-app/api
```

> ⚠️ **Never commit** `.env` files or Auth0 secrets to the repository.

---

## ☁️ AWS Deployment

### Frontend → S3

```bash
cd frontend
npm run build
aws s3 sync dist/ s3://your-bucket-name --delete
aws s3 website s3://your-bucket-name --index-document index.html
```

### Microservices → Lambda

```bash
# Build fat JARs
cd microservices/user-service  && mvn package -DskipTests
cd ../post-service              && mvn package -DskipTests
cd ../stream-service            && mvn package -DskipTests

# Deploy via AWS CLI
aws lambda create-function --function-name user-service \
  --runtime java17 \
  --handler edu.eci.arep.handler.UserHandler::handleRequest \
  --zip-file fileb://target/user-service-1.0-SNAPSHOT.jar \
  --environment Variables="{AUTH0_ISSUER_URI=...,AUTH0_AUDIENCE=...,USERS_TABLE=users}"
```

Repeat for `post-service` (handler: `PostHandler`, table env: `POSTS_TABLE`) and `stream-service` (handler: `StreamHandler`, table env: `POSTS_TABLE`).

---

## 🧪 Test Report

### Monolith Unit Tests

| Test Class | Scenario | Expected | Status |
|---|---|---|---|
| `PostServiceTest` | Create post with existing user | Post persisted | ✅ |
| `PostServiceTest` | Create post with unknown user | `UserNotFoundException` | ✅ |
| `PostServiceTest` | Get all posts | Ordered list returned | ✅ |
| `StreamServiceTest` | Get stream | Delegates to repository | ✅ |
| `StreamServiceTest` | Empty stream | Empty list returned | ✅ |
| `PostControllerTest` | GET `/api/posts` without token | 200 OK | ✅ |
| `PostControllerTest` | POST `/api/posts` without token | 401 Unauthorized | ✅ |
| `PostControllerTest` | POST `/api/posts` with valid JWT | 201 Created | ✅ |
| `PostControllerTest` | POST blank content | 400 Bad Request | ✅ |
| `StreamControllerTest` | GET `/api/stream` | 200 OK, all posts | ✅ |
| `StreamControllerTest` | Empty stream | 200 OK, empty array | ✅ |

### Lambda Unit Tests

| Test Class | Scenario | Expected | Status |
|---|---|---|---|
| `UserHandlerTest` | Valid JWT → resolves user | 200 + profile | ✅ |
| `UserHandlerTest` | Missing header | 401 | ✅ |
| `UserHandlerTest` | Invalid token | 401 | ✅ |
| `PostHandlerTest` | GET (public) | 200 + posts | ✅ |
| `PostHandlerTest` | POST without token | 401 | ✅ |
| `PostHandlerTest` | POST valid | 201 | ✅ |
| `PostHandlerTest` | POST blank content | 400 | ✅ |
| `StreamHandlerTest` | GET stream | 200 + posts | ✅ |
| `StreamHandlerTest` | Empty stream | 200 + empty | ✅ |

---

## 👥 **Authors**

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/DavidVCAI">
        <img src="https://github.com/DavidVCAI.png" width="100px;" alt="David Velásquez"/>
        <br />
        <sub><b>David Velásquez</b></sub>
      </a>
      <br />
      <sub>Full Stack Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/JAPV-X2612">
        <img src="https://github.com/JAPV-X2612.png" width="100px;" alt="Jesús Alfonso Pinzón Vega"/>
        <br />
        <sub><b>Jesús Alfonso Pinzón Vega</b></sub>
      </a>
      <br />
      <sub>Full Stack Developer</sub>
    </td>
    <td align="center">
      <a href="https://github.com/buba-0511">
        <img src="https://github.com/buba-0511.png" width="100px;" alt="Santiago Díaz"/>
        <br />
        <sub><b>Santiago Díaz</b></sub>
      </a>
      <br />
      <sub>Full Stack Developer</sub>
    </td>
  </tr>
</table>

---

## 📄 License

This project is licensed under the **Apache License, Version 2.0**. See the [LICENSE](LICENSE) file for details.

---

## 🔗 **Additional Resources**

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Auth0 Spring Boot Quickstart](https://auth0.com/docs/quickstart/backend/java-spring-security5)
- [Auth0 React SDK Documentation](https://auth0.com/docs/libraries/auth0-react)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [AWS Lambda Java Runtime](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html)
- [AWS SDK for Java v2 — DynamoDB](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-dynamodb.html)
- [Amazon S3 Static Website Hosting](https://docs.aws.amazon.com/AmazonS3/latest/userguide/WebsiteHosting.html)
- [Vite Documentation](https://vitejs.dev/guide/)
