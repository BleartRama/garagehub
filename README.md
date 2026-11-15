![CI](https://github.com/BleartRama/GarageHub/actions/workflows/ci.yml/badge.svg)

# GarageHub

A backend-focused fullstack project: a workshop and booking system for car
owners and repair shops. Customers register their vehicles, book service
appointments, view their service history and get service reminders. Mechanics
manage bookings and service records, and administrators have full control.

Built with Java and Spring Boot as a REST API with JWT authentication,
role-based access control and ownership checks, so each customer only ever
sees their own data. The project has unit tests, runs with Docker, and is
built and tested automatically in GitHub Actions.

**Run the whole thing with one command:**

```bash
docker compose up --build
```

The app is then available at `http://localhost:8080`.

---

## Contents

- [Tech stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [Tests](#tests)
- [CI/CD](#cicd)
- [API overview](#api-overview)
- [Roles and permissions](#roles-and-permissions)
- [Security](#security)
- [Frontend](#frontend)
- [Possible next steps](#possible-next-steps)

---

## Tech stack

- **Java 25** and **Spring Boot 4** (Spring Web, Spring Data JPA, Spring Security)
- **MySQL** as the database, Hibernate as ORM
- **JWT** (jjwt) for stateless authentication
- **BCrypt** for password hashing
- **Bean Validation** for validating incoming data
- **JUnit 5** and **Mockito** for unit tests
- **Docker** and **docker-compose** to run the app and its database
- **GitHub Actions** for CI
- Built with **Maven**

---

## Features

- Full CRUD for vehicles, bookings and service records
- Registration and login with JWT
- Role-based access control: `USER`, `MECHANIC`, `ADMIN`
- Ownership checks – customers only see and manage their own vehicles,
  bookings and service records
- Booking flow: a booking is created as `PENDING` and is either approved
  (`APPROVED`) or cancelled (`CANCELLED`) by a mechanic; customers can cancel
  their own
- Service history per vehicle
- Service reminder: a calculated value that decides whether a vehicle needs
  service based on mileage or time since the last service
- Global exception handling with custom exceptions and correct HTTP status
  codes
- Error messages are in Swedish, since the application targets Swedish users

---

## Architecture

The project follows a strict three-layer architecture with clear
responsibilities:

```
Controller  →  Service  →  Repository  →  Database
```

- **Controller** – receives HTTP requests, returns `ResponseEntity`, delegates
  all work to the service layer. Contains no business logic.
- **Service** – all business logic, ownership checks, transaction handling,
  throws named custom exceptions.
- **Repository** – database access through Spring Data JPA derived queries.
- **Entity** – JPA entities mirroring the database tables.
- **DTO** – separate `XxxRequestDto` and `XxxResponseDto` so entities are
  never exposed directly through the API.
- **Mapper** – translates between entity and DTO.

The domain has four main entities: `User`, `Vehicle`, `Booking` and
`ServiceRecord`. A mechanic is not a separate entity but a `User` with the
`MECHANIC` role.

The frontend is served by Spring Boot from `src/main/resources/static`, so the
entire application runs on a single origin.

---

## Getting started

The application reads two secrets from environment variables. They are never
present in the code or in version control:

| Variable      | Description                                        |
|---------------|----------------------------------------------------|
| `DB_PASSWORD` | Password for the MySQL user                        |
| `JWT_SECRET`  | A long, random secret key (at least 32 characters) |

Create a `.env` file in the project root. It is gitignored and must never be
committed:

```bash
cp .env.example .env
```

Fill in the values. A suitable key can be generated with:

```bash
openssl rand -base64 48
```

### Option 1: Docker (recommended)

Docker is the only prerequisite. Neither Java nor MySQL needs to be installed.

```bash
docker compose up --build
```

This starts two containers: the application and a MySQL database. The app runs
on `http://localhost:8080`, and the database is exposed on port `3307` if you
want to inspect it with a tool such as MySQL Workbench.

```bash
docker compose down      # stop everything, keep the database
docker compose down -v   # stop everything and delete the database
```

### Option 2: Locally with Maven

Requires Java 25 and a running MySQL server.

Create the database:

```sql
CREATE DATABASE garagehub;
```

Tables are created automatically by Hibernate on startup
(`spring.jpa.hibernate.ddl-auto=update`).

Then run:

```bash
./mvnw spring-boot:run
```

---

## Tests

Unit tests written with JUnit 5 and Mockito. The tests are isolated and
require neither a database nor network access, which means they run anywhere
in a couple of seconds.

```bash
./mvnw test
```

What is covered:

- `ServiceRecordMapperTest` – entity to DTO mapping, including the case where
  a service record has no mechanic and the name should be `null`.
- `ServiceRecordServiceTest` – the service reminder logic, with one test per
  branch: no service recorded, mileage threshold reached, more than twelve
  months since the last service, and the case where no service is due.
  Repositories are replaced with Mockito mocks.

---

## CI/CD

Every push and pull request against `main` runs a GitHub Actions pipeline
that:

1. checks out the code and installs Java 25,
2. runs all unit tests,
3. builds the Docker image to verify that it builds from a clean state.

The configuration lives in `.github/workflows/ci.yml`. The badge at the top of
this file shows the current status.

---

## API overview

Every endpoint except `/auth/**` requires a valid JWT in the `Authorization`
header:

```
Authorization: Bearer <token>
```

### Authentication

| Method | Endpoint         | Description                            |
|--------|------------------|----------------------------------------|
| POST   | `/auth/register` | Register a new user (becomes `USER`)   |
| POST   | `/auth/login`    | Log in, returns a JWT                  |

### Vehicles

| Method | Endpoint                          | Description                            |
|--------|-----------------------------------|----------------------------------------|
| GET    | `/vehicles`                       | List vehicles (filtered by ownership)  |
| GET    | `/vehicles/{id}`                  | Get a single vehicle                   |
| POST   | `/vehicles`                       | Create a vehicle (owner set automatically) |
| PUT    | `/vehicles/{id}`                  | Update a vehicle                       |
| DELETE | `/vehicles/{id}`                  | Delete a vehicle (admin only)          |
| GET    | `/vehicles/{id}/service-records`  | Service history for a vehicle          |
| GET    | `/vehicles/{id}/service-reminder` | Service reminder for a vehicle         |

### Bookings

| Method | Endpoint                  | Description                                 |
|--------|---------------------------|---------------------------------------------|
| GET    | `/bookings`               | List bookings (filtered by ownership)       |
| GET    | `/bookings/{id}`          | Get a single booking                        |
| POST   | `/bookings`               | Create a booking (becomes `PENDING`)        |
| PUT    | `/bookings/{id}/approve`  | Approve a booking (mechanic/admin)          |
| PUT    | `/bookings/{id}/cancel`   | Cancel a booking (owner, mechanic, admin)   |
| DELETE | `/bookings/{id}`          | Delete a booking (admin only)               |

### Service records

| Method | Endpoint                | Description                                      |
|--------|-------------------------|--------------------------------------------------|
| GET    | `/service-records`      | List service records (filtered by ownership)     |
| GET    | `/service-records/{id}` | Get a single service record                      |
| POST   | `/service-records`      | Create a service record (for your own vehicle)   |
| DELETE | `/service-records/{id}` | Delete a service record (owner or admin)         |

---

## Roles and permissions

| Role       | Permissions                                                          |
|------------|----------------------------------------------------------------------|
| `USER`     | Sees and manages only their own vehicles, bookings and service records. Can book appointments and cancel their own bookings. Can add service records for their own vehicles. |
| `MECHANIC` | Sees all vehicles, bookings and service records. Can approve and cancel bookings. |
| `ADMIN`    | Full access to everything, including deletion.                       |

Access is enforced in two ways:

- **Role-based** with `@PreAuthorize` where a plain role check is enough
  (for example, only mechanics and admins may approve bookings).
- **Ownership-based** in the service layer, where the authenticated user is
  compared against the resource owner. This requires a database lookup and is
  therefore business logic rather than a simple annotation.

---

## Security

- **Passwords** are hashed with BCrypt before being stored, never in plain
  text.
- **JWT** is used for stateless authentication. The token is signed with a
  secret key and verified on every request, so the server keeps no session.
  The token carries the user's email and role.
- **Secrets** (database password and JWT key) are read from environment
  variables or a `.env` file that is never version controlled. `.env` is also
  listed in `.dockerignore`, so secrets never end up inside the Docker image.
- **Static files** (the frontend) are public; everything else except
  `/auth/**` requires a valid token.
- Status codes: `401` (not authenticated), `403` (authenticated but not
  authorized), `409` (conflict, for example a resource still in use).

---

## Frontend

The frontend is built with HTML, CSS and vanilla JavaScript and consumes the
API. It handles login, stores the JWT in the browser and renders separate
views for vehicles, bookings and service records.

It lives in `src/main/resources/static` and is served by Spring Boot, so it
starts together with the backend at `http://localhost:8080`. No separate
server and no CORS configuration is needed.

---

## Possible next steps

The following were deliberately left out to keep the project at a reasonable
scope, but would be natural next steps:

- Cloud deployment with a public demo URL
- Integration tests against a real database using Testcontainers
- Per-component service intervals (oil, timing belt, brake fluid and so on)
  with their own mileage and time intervals
- Configurable service plans per vehicle
- Vehicle dropdowns in the frontend forms instead of entering ids manually
- Editing (PUT) of bookings and service records