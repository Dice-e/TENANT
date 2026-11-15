# SD1_REQUIREMENTS_CHECKLIST.md

Authoritative source: the SD1 Enterprise Web Application Project Guide. Chapter 4 supplies the technical details. Every row starts as **Not started**. Change it only when there is evidence (a merged PR, a run result, a saved file).

Status words: `Not started`, `In progress`, `Done (verified)`.

## A. Requirements traceability

| ID | Requirement (source) | Planned location | Owner | Evidence needed | Status |
|---|---|---|---|---|---|
| R1 | Spring Boot entry point, starters, `application.properties`, constructor injection; explain one injected dependency (4.1) | `TenantApplication`, `pom.xml`, service and controller constructors | 1, 3, 4 | App starts; explanation ready | Not started |
| R2 | REST URLs, JSON, GET, POST, PUT, DELETE, correct status codes (4.2) | `PaymentRecordController` | 4 | Checks 1 to 4, 6 | Not started |
| R3 | Request and response DTOs, at least one record, no generated id in create request, never expose passwords or hashes (4.3) | `dto/*` | 2 | Code review; inspect responses | Not started |
| R4 | `@Entity`, `@Id`, `JpaRepository`, missing-id handling, one derived query (4.4) | `entity/*`, `repository/*` | 2, 3 | Checks 2, 6, 10 | Not started |
| R5 | HTTP Basic, database-backed users, BCrypt hashes, ADMIN-only delete enforced on the server (4.5) | `config/*`, `AppUser` | 5 | Checks 8, 9 | Not started |
| R6 | Controller, service and repository layers; HTTP in controllers; rule in service (4.6) | all layers | 3, 4 | Request trace in defense | Not started |
| R7 | Bean Validation on request DTOs, `@Valid`, `@RestControllerAdvice` for validation, missing records, duplicates (4.7) | `dto/*`, `exception/*`, controller | 6, 4 | Checks 5, 6, 7 | Not started |
| R8 | Unique rule on create and update; updating without changing the unique value works | `PaymentRecordService` | 3 | Checks 3, 7 | Not started |
| R9 | CSRF kept enabled; valid token on write requests; 403 role test uses a valid token | `SecurityConfig`, `API_TESTING_GUIDE.md` | 5, 6 | Check 9 plus documented steps | Not started |
| R10 | H2 data-loss note; steps to recreate sample records and the two demo accounts | `README.md`, seeders | 1, 3, 5 | Fresh start from README works | Not started |
| R11 | Ten checks, each with request, expected, actual, pass/fail, screenshot or saved response | `postman/*`, `docs/evidence/*` | 6 | Executed results only | Not started |
| R12 | Source ZIP, 4 to 6 page write-up (editable and PDF), saved API requests, AI-use note | root, `docs/*` | 1, all | Files present | Not started |
| R13 | 10 to 15 minute demo; each member traces a request | `docs/DEFENSE_GUIDE.md` | all | Rehearsal log | Not started |
| R14 | Individual contribution evidence (15 points) | `CONTRIBUTION_TRACKER.md`, PRs | all | PRs, reviews, Q and A readiness | Not started |
| R15 | Payment processing excluded; no payment wording | wording rules in `CLAUDE.md`, UI text, README | 1 | UI and text audit | Not started |
| R16 | Palette 2, responsive desktop and mobile, real API data (team design requirement, not an SD1 rule) | `static/**` | 1 | Screenshots at about 375px and 1280px | Not started |

## B. Guide details to confirm

| Item from the guide | Where handled | Status |
|---|---|---|
| One main entity with 3 to 5 meaningful fields plus ID | `RentPaymentRecord` has five fields plus id | Not started |
| Small user-account table for the security example | `AppUser` | Not started |
| Two predefined demo roles; both read, create, update; only ADMIN deletes | `SecurityConfig` | Not started |
| No registration or account screen | Out of scope | Done by design |
| One basic filter using a derived query | `findByUnitNumber` | Not started |
| 401 for missing or invalid credentials; 403 for role denial when other checks pass | Checks 8, 9 | Not started |
| Useful messages for invalid input and duplicates | `GlobalExceptionHandler` | Not started |
| Security-filter errors need not use the controller-advice format | Documented in `API_TESTING_GUIDE.md` | Not started |
| Short proposal, about one page, early in Week 1 | `PROJECT_SCOPE.md` (Member 1 submits to the professor) | Not started |
| Run locally; localhost; invented data; demo-only credentials | README, SETUP_GUIDE | Not started |
| Six-member group allowed | Member 1 confirms with the professor | Not started |

## C. Ten checks status

Copy final results from `docs/API_TESTING_GUIDE.md` only after they were run.

| No. | Check | Status |
|---|---|---|
| 1 | Create valid record: 201 and generated ID | Not run |
| 2 | List, get by ID, filter: 200 and matching data | Not run |
| 3 | Update: 200; later GET shows change; unchanged unique value valid | Not run |
| 4 | Delete as ADMIN: 204; then 404 | Not run |
| 5 | Invalid input: 400 with helpful message | Not run |
| 6 | Nonexistent ID: 404 with clear message | Not run |
| 7 | Duplicate on create and update: 409; data unchanged | Not run |
| 8 | Protected GET: no credentials and wrong password: 401 | Not run |
| 9 | DELETE as USER, other checks satisfied: 403; record remains | Not run |
| 10 | Create then fetch separately: JPA stored it | Not run |

## D. Final submission checklist

- [ ] Runnable source ZIP with `pom.xml`, configuration, README (setup, run, sample data, demo accounts). `_journal/` is not included in the ZIP.
- [ ] README instructions tested on a clean clone by someone other than the author
- [ ] Short write-up, about 4 to 6 pages excluding cover and test evidence, editable copy and PDF copy
- [ ] Write-up contains: problem and scope; fields and tables and layer sketch; endpoint and role table; Chapter 4 mapping to source files; results, limitations, contributions, AI-use note
- [ ] Saved API requests (Postman collection) and the ten completed checks
- [ ] AI-use note naming the tool, the parts it assisted, and how output was reviewed and tested
- [ ] Demonstration rehearsed (10 to 15 minutes)
- [ ] Every member can explain their code and trace a request

## E. Rubric self-check (100 points)

| Criterion | Points | What earns it | Our confidence (fill honestly) |
|---|---:|---|---|
| Working API and appropriate scope | 25 | CRUD and filter work for a small, clear problem | |
| Chapter 4 implementation | 25 | Boot, REST, DTOs, JPA, constructor injection, layers used correctly | |
| Security, validation, business rule | 20 | Two roles, hashed passwords, input checks, useful errors | |
| Testing and documentation | 15 | Ten executed checks, accurate write-up, usable setup steps | |
| Individual contribution and understanding | 15 | Each member explains their work and AI-assisted parts (contribution 5, explanation 8, reflection 2) | |

Group criteria are scored 0 to 4 and converted: points = weight × rating ÷ 4. Individual points are for each student separately.
