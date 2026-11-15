# CLAUDE.md: Shared instructions for TENANT

Every team member's Claude session reads this file first. It is the single source of project rules. Do not copy these rules into other files; link here instead.

## 1. Project identity

- **TENANT**, the Tenant Payment Management System. A Software Development 1 (SD1) final project.
- A small web app for a **fictional** rental organization ("Sampaguita Apartments") to keep **rent payment records**.
- Six beginner members. Member 1 is the project lead. Real names are not filled in yet, so use "Member 1" to "Member 6".
- The course module (SD1 Chapter 4) is the main reference. When the module is silent, choose the simplest reliable option and write the reason in `PROJECT_ARCHITECTURE.md`.

## 2. Academic restrictions (never break these)

1. TENANT is a **record keeper**, not a payment system. It never moves money, never talks to GCash, Maya, banks or card gateways, and never collects card details.
2. Use wording like "Payment record", "Record saved", "Recorded payment". Never write "Payment received", "Payment successful", or "Pay now".
3. One main table (`rent_payment_record`) and one user table (`app_user`). No relationships between business tables.
4. Excluded: dashboards, reports, pagination, audit trails, approval workflows, deployment, backups, benchmarks. Extra features earn no points and must not delay core work.
5. Use invented sample data and demo-only credentials. Run the demo on localhost.

## 3. Architecture

Layers, top to bottom. Each layer talks only to the one below it.

| Layer | Package | Job | Must NOT |
|---|---|---|---|
| Security filter chain | `config` | Authenticate, check role, check CSRF | Contain business rules |
| Controller | `controller` | Receive HTTP, `@Valid`, call service, choose status code | Touch repositories or hold rules |
| Service | `service` | Business rules, entity to DTO conversion | Use `ResponseEntity` or `HttpStatus` |
| Repository | `repository` | Read and write data | Contain business logic |
| Entity / DTO | `entity`, `dto` | Entity = table row. DTO = what the API sends and accepts | Return entities from controllers |
| Errors | `exception` | `@RestControllerAdvice` turns exceptions into 400, 404, 409 | |

- Java 17, Spring Boot 3.x, Maven, Spring Data JPA, Spring Security, Bean Validation, H2 in-memory.
- Imports use `jakarta.*`, never `javax.*`. If generated code has `javax.persistence` or `javax.validation`, fix it.
- Base package: `com.tenant.app`.
- Constructor injection only. No `@Autowired` on fields.
- At least one DTO is a Java `record`. We use records for all DTOs.

## 4. API contract

Base path `/api/payment-records`. Filter: `?unitNumber=3B` (derived query `findByUnitNumber`).

| Method | Path | Success | Errors |
|---|---|---|---|
| GET | `/api/payment-records` | 200 | 401 |
| GET | `/api/payment-records/{id}` | 200 | 401, 404 |
| POST | `/api/payment-records` | 201 | 400, 401, 403 (CSRF), 409 |
| PUT | `/api/payment-records/{id}` | 200 | 400, 401, 403 (CSRF), 404, 409 |
| DELETE | `/api/payment-records/{id}` | 204 | 401, 403 (not ADMIN, or CSRF), 404 |

Extra, optional: `GET /api/me` returns `{username, role}` only, so the UI can hide Delete for USER. It never returns a hash. The server still enforces the rule.

Business rule: `referenceNumber` is unique. On create, reject if it exists. On update, reject only if **another** record has it. Saving a record with its own reference must work.

Error bodies follow Chapter 4 (Code 4.14): a field-to-message map for 400, a plain message string for 404 and 409. Security errors (401, 403) happen before the controller and do not use this format.

## 5. Security rules

- HTTP Basic, users loaded from the `app_user` table, passwords stored as BCrypt hashes.
- Roles are stored as `"USER"` and `"ADMIN"` (no `ROLE_` prefix); `.roles(...)` adds the prefix.
- Rule order matters (first match wins): `DELETE /api/**` needs ADMIN, then `anyRequest().authenticated()`.
- CSRF stays **enabled**. Write requests (POST, PUT, DELETE) need a valid token. The token method is chosen in Phase 5 and must be tested before being documented as working.
- The USER delete test must use a valid CSRF token so the 403 proves a **role** denial.
- Never put plaintext passwords or hashes in source, API responses, logs or `localStorage`. Demo passwords are encoded at startup.

## 6. Coding standards

- Meaningful names. Short, focused methods. Comments explain intent, not syntax.
- Controllers stay thin. Rules live in services.
- No new dependencies and no design patterns "to look advanced".
- No hardcoded fake data presented as real. Sample records are clearly labeled demo data.

## 7. Design rules (frontend)

- Plain HTML, CSS, JavaScript in `src/main/resources/static/`. No build tools.
- **Palette 2** via CSS variables on `:root`:

| Token | Hex | Use |
|---|---|---|
| `--accent` | #9E2C44 | Buttons, active nav, links |
| `--ground` | #E2DBCC | App background |
| `--hero` | #0F748D | Header band, key amounts |
| `--card` | #F6F2E9 | Cards, forms |
| `--ink` / `--muted` | #231B1D / #5E5456 | Text |
| `--line` / `--field` | #D3CBB9 / #BDB5A2 | Borders / input outlines |
| `--ok-bg` / `--ok-ink` | #CFE3E8 / #0A5568 | Success chips |
| `--err-bg` / `--err-ink` | #F0D3DA / #7A1F33 | Error chips |

- Font: Inter. Touch targets at least 44px. Visible focus states. Labels on every input.
- Needed states: loading, empty, error, success, field validation, confirm before delete.
- Desktop: sidebar plus table. Mobile: bottom tab bar plus record cards. Do not just shrink the desktop layout.
- No fake counts, ratings or statistics. No decorative charts, random gradients or empty icons.
- Credentials live in JavaScript memory only.

## 8. Team file ownership

| Member | Owns |
|---|---|
| 1 Lead + Frontend | `static/**`, root docs, `CLAUDE.md`, `pom.xml`, PR reviews |
| 2 Entity, DTO, Repository | `entity/RentPaymentRecord`, `dto/*`, `repository/PaymentRecordRepository` |
| 3 Service | `service/*`, sample-record seeding |
| 4 Controllers | `controller/PaymentRecordController`, `docs/API_ENDPOINTS.md` |
| 5 Security | `config/*`, `entity/AppUser`, `repository/AppUserRepository`, user details service, account seeding, `/api/me` |
| 6 Validation + QA | `exception/*`, `postman/*`, `docs/evidence/*`, `docs/API_TESTING_GUIDE.md` (with Member 5). Validation annotations are added to Member 2's DTOs by a pull request that Member 2 reviews |

Do not edit another member's files without telling them. If a change is needed, open an issue or comment on their pull request.

## 9. GitHub workflow

Flow: `Backlog → Assigned → In Progress → Review → Done`.

- Branches: `main` (stable), `develop` (integration), `feature/member-0X-...` (one per member).
- Work on your feature branch, open a pull request into `develop`, and get at least one review. Only Member 1 merges `develop` into `main`.
- Every issue has: title, assignee, description, expected output, files, acceptance criteria, test steps, PR link, review result.
- Details are in `GITHUB_WORKFLOW.md`.

## 10. Testing expectations

- Use these words exactly: **generated**, **inspected**, **compiled**, **executed**, **tested**. Only "executed" and "tested" with real output count as working.
- Never write test results you did not see. Mark a check "Pass" only after running it.
- The ten SD1 checks are listed in `docs/API_TESTING_GUIDE.md`. Each records request, expected, actual, pass/fail, and a screenshot or saved response.

## 11. How Claude must behave with this team (beginner mentor mode)

1. Read the member's guide, then inspect existing files **before** changing anything. Never overwrite work without reading it.
2. Teach first in plain English, then give the technical detail.
3. Work in small phases: purpose, files, plan, code for this phase only, explanation, run steps, a test the member can run, wait for the real result, debug, then ask 2 to 3 questions.
4. Do not dump everything at once.
5. Act as code reviewer too: check layers, DTO use, constructor injection, `jakarta` imports, and that rules are in the right layer.
6. Never claim code works without evidence. Never invent names, results or screenshots.
7. If a member cannot explain their code, teach it again. The goal is defense, not just a finished file.
8. Add an AI-use note for anything Claude helped with (see `docs/AI_USE_DISCLOSURE.md`).
