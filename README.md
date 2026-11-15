# TENANT: Tenant Payment Management System

A Software Development 1 (SD1) final project by a six-member team.

TENANT helps a small, **fictional** rental organization (Sampaguita Apartments) keep rent **payment records** in one place. Staff can add, view, search by unit, and correct records. Only an admin can delete.

> **Records only.** TENANT does not process payments. It never moves money, never connects to GCash, Maya, banks or cards, and never claims a payment was received. It stores what a staff member typed in. See `PROJECT_SCOPE.md`.

## Project status

| Area | Status |
|---|---|
| Planning and documentation | In progress (Phase 1) |
| Backend (Spring Boot, JPA, security, validation) | Not started |
| Frontend (Palette 2) | Not started |
| Ten SD1 checks | Not run |

The sections below describe the **planned** setup. They are filled in with real, tested commands as each phase finishes. Nothing here has been run yet.

## What it uses

Java 17, Spring Boot 3.x, Maven, Spring Data JPA, Spring Security (HTTP Basic, BCrypt), Bean Validation, H2 in-memory database, and a plain HTML/CSS/JavaScript frontend served by Spring Boot.

## Quick start (to be confirmed in Phase 3)

1. Install Java 17 or newer, Maven (or use the project's Maven wrapper), and Git.
2. Clone the repository and open the folder in your IDE.
3. Run the app from the project folder: `mvn spring-boot:run`
4. Open `http://localhost:8080`.
5. Sign in with a demo account (below).

## Demo accounts (demo-only, set up at startup in Phase 5)

| Username | Password | Role | Can delete? |
|---|---|---|---|
| `staff` | `staff123` | USER | No |
| `admin` | `admin123` | ADMIN | Yes |

These are invented demo credentials for localhost only. Never reuse them anywhere real.

## Sample data and data loss

The database is H2 **in memory**. When you stop the app, **all data is lost**. On every start, the seeding code recreates the two demo accounts and a few invented sample records (labeled demo data). To reset, stop and start the app.

## API at a glance

Base path `/api/payment-records`. Filter: `?unitNumber=3B`.

| Method | Path | Success | Who |
|---|---|---|---|
| GET | `/api/payment-records` | 200 | USER, ADMIN |
| GET | `/api/payment-records/{id}` | 200 | USER, ADMIN |
| POST | `/api/payment-records` | 201 | USER, ADMIN |
| PUT | `/api/payment-records/{id}` | 200 | USER, ADMIN |
| DELETE | `/api/payment-records/{id}` | 204 | ADMIN only |

Errors: 400 invalid input, 401 not logged in, 403 not allowed, 404 not found, 409 duplicate reference number. Write requests also need a valid CSRF token; how to get one is documented in `docs/API_TESTING_GUIDE.md` once tested.

## Repository map

| Path | What it is |
|---|---|
| `CLAUDE.md` | Shared rules for everyone's Claude session |
| `PROJECT_SCOPE.md` | What the project is and is not, schedule |
| `PROJECT_ARCHITECTURE.md` | Layers, files, data model, design decisions |
| `GITHUB_WORKFLOW.md`, `CONTRIBUTING.md` | How we branch, review and merge |
| `CONTRIBUTION_TRACKER.md` | Evidence of each member's work |
| `member-guides/MEMBER-01.md` to `06.md` | One learning guide per member |
| `docs/` | Setup, API testing, endpoint docs, defense, requirements checklist, AI-use note |
| `docs/evidence/` | Screenshots and results of the ten checks (created when they are run) |
| `src/main/java/com/tenant/app/` | Backend code |
| `src/main/resources/static/` | Frontend files |
| `postman/` | API collection |

## Team

| Member | Role |
|---|---|
| Member 1 (project lead) | Frontend, integration, documentation, reviews |
| Member 2 | Entity, DTOs, repository |
| Member 3 | Service layer and business rule |
| Member 4 | REST controllers |
| Member 5 | Security |
| Member 6 | Validation, error handling, QA |

Real names are added here by Member 1.

## Known limitations

- No real payments (by design).
- Data is lost when the app stops (H2 in memory).
- Signing in again is needed after a page refresh, because credentials are kept in memory only.
- Demo only: not hardened for the internet. Run on localhost.

## AI use

The team uses Claude as a coding mentor. Each member must be able to explain and test what they submit. See `docs/AI_USE_DISCLOSURE.md`.
