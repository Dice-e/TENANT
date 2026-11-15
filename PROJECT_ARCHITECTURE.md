# PROJECT_ARCHITECTURE.md: How TENANT is built

Status: **design only**. No code has been written, compiled or run yet. Items marked *unverified* must be tested before anyone calls them working.

## 1. Big picture

```
Browser (HTML/CSS/JS, served by Spring Boot)      Postman / curl
                 │                                      │
                 └─────── HTTP + Basic auth + CSRF ─────┘
                                  │
        ┌─────────────────────────▼──────────────────────────┐
        │ Security filter chain   (config)                   │
        │  HTTP Basic → user details service → BCrypt        │
        │  CSRF check · DELETE needs ADMIN · rest need login │
        └─────────────────────────┬──────────────────────────┘
                                  │ passes
 Controller   PaymentRecordController     HTTP only, @Valid, status codes
      │              ▲ request / response DTOs (records)
      ▼              │
 Service      PaymentRecordService        unique-reference rule, entity ↔ DTO
      │              ▲
      ▼              │
 Repository   PaymentRecordRepository     JpaRepository + findByUnitNumber
      │
      ▼
 H2 (memory)  rent_payment_record · app_user

 GlobalExceptionHandler (@RestControllerAdvice) → 400 / 404 / 409
```

## 2. Request flow (every member must be able to trace this)

Example: `POST /api/payment-records`

1. The client sends JSON with Basic credentials and a CSRF token.
2. The **security filter chain** checks the login (401 if bad), the CSRF token (403 if bad), and the role rules.
3. Spring's dispatcher finds `PaymentRecordController.create(...)`.
4. `@Valid` checks the request DTO. Failure becomes **400** through the exception handler.
5. The controller calls `PaymentRecordService.create(request)`.
6. The service checks `existsByReferenceNumber`. A duplicate throws `DuplicateReferenceException` and becomes **409**.
7. The service builds a `RentPaymentRecord` entity and calls `repository.save(...)`.
8. Hibernate writes a row to H2 and the generated ID comes back.
9. The service converts the entity to `PaymentRecordResponse`. The controller wraps it in **201 Created**.
10. Jackson turns the response record into JSON.

## 3. Packages and files (base package `com.tenant.app`)

| Package | Files | Owner |
|---|---|---|
| (root) | `TenantApplication` (`@SpringBootApplication`, `main`) | 1 |
| `entity` | `RentPaymentRecord` | 2 |
| `entity` | `AppUser` | 5 |
| `dto` | `PaymentRecordRequest`, `PaymentRecordResponse` (records) | 2 |
| `repository` | `PaymentRecordRepository` | 2 |
| `repository` | `AppUserRepository` | 5 |
| `service` | `PaymentRecordService`, sample-data seeder | 3 |
| `controller` | `PaymentRecordController` | 4 |
| `config` | `SecurityConfig`, `DbUserDetailsService`, `DemoAccountSeeder`, `MeController` (`/api/me`) | 5 |
| `exception` | `RecordNotFoundException`, `DuplicateReferenceException`, `GlobalExceptionHandler` | 6 |
| `resources` | `application.properties` | 1 |
| `resources/static` | `index.html`, `css/tenant.css`, `js/api.js`, `js/auth.js`, `js/ui.js`, `js/app.js` | 1 |
| `postman` | `TENANT-API-Collection.json` | 6 |
| `docs` | `API_ENDPOINTS.md` (real responses only) | 4 |
| `docs/evidence` | Screenshots and `CHECK-RESULTS.md` | 6 |

## 4. Data model

**rent_payment_record**

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | primary key, generated |
| tenant_name | VARCHAR(100) | not null |
| unit_number | VARCHAR(20) | not null |
| amount | DECIMAL(12,2) | not null |
| payment_date | DATE | not null |
| reference_number | VARCHAR(50) | not null, **unique** |

The database unique constraint is a last line of defense. The friendly 409 comes from the service check.

**app_user**: `id`, `username` (unique), `password_hash`, `role` (`"USER"` or `"ADMIN"`). No relationship to the payment table.

## 5. DTOs

```java
public record PaymentRecordRequest(
    @NotBlank String tenantName,
    @NotBlank String unitNumber,
    @NotNull @Positive BigDecimal amount,
    @NotNull LocalDate paymentDate,
    @NotBlank String referenceNumber) { }

public record PaymentRecordResponse(
    Long id, String tenantName, String unitNumber,
    BigDecimal amount, LocalDate paymentDate, String referenceNumber) { }
```

The request has no `id` (the client must not choose it). The response adds it. Final annotations, messages and size limits are set in Phase 4 to 5 by Members 2 and 6. `AppUser` is never turned into a response.

## 6. Security design

Follows Chapter 4, Codes 4.9 and 4.10.

- `authorizeHttpRequests`: `DELETE /api/**` needs `ADMIN`, then `anyRequest().authenticated()`. Order matters because the first match wins.
- `httpBasic` with defaults, `BCryptPasswordEncoder` bean, CSRF **left enabled**.
- `DbUserDetailsService` loads `AppUser` by username and builds the user with `.roles(role)`.
- `DemoAccountSeeder` (a `CommandLineRunner`) creates `staff` (USER) and `admin` (ADMIN) at startup and encodes their passwords with the encoder, so no hash is stored in source.

## 7. Decisions where the module was silent

| Topic | Choice | Reason |
|---|---|---|
| CSRF token delivery | Cookie-based token repository (readable by our JavaScript) plus the matching handler. Fallback: a tiny token endpoint. ***Unverified***, so Member 5 tests it in Phase 5. | The module only says "keep CSRF on". The default session-based token is awkward for Postman and plain JavaScript. |
| `GET /api/me` | Add it, returns `{username, role}` only | Lets the UI hide Delete for USER. Low risk. The server still enforces the rule. Optional. |
| Error bodies | Module style: map for 400, string for 404 and 409 | The module gives an answer, and graders map the work to Chapter 4. |
| H2 console | Disabled | Not in the module. It needs extra security exceptions. |
| Frontend | Plain static HTML/CSS/JS | One command runs everything. No build tools for beginners. |
| Frontend login | A small in-page sign-in form. Credentials kept in JS memory only, so a refresh means signing in again. | HTTP Basic needs credentials on each call. The prompt forbids storing them. |
| Package name | `com.tenant.app` | Short and clear. |
| Money type | `BigDecimal` | `double` can give rounding errors with money. |
| Security starter timing | The skeleton `pom.xml` has no `spring-boot-starter-security`. Member 5 adds it in T-15. | The starter locks every endpoint at once (Chapter 4.5). Without this, Members 2 to 4 could not test. |
| Public static files | `/`, `/index.html`, `/css/**`, `/js/**` are public; `/api/**` is protected | The sign-in page must load before anyone can sign in. The chapter's example does not cover a frontend. |
| Malformed JSON handler | Extra `HttpMessageNotReadableException` handler | Gives a helpful 400 for bad dates or missing bodies (SD1 Check 5 asks for a helpful message). |

## 8. Chapter 4 mapping

| Section | Concept | Where in TENANT |
|---|---|---|
| 4.1 | Boot, starters, `application.properties`, constructor injection | `TenantApplication`, `pom.xml`, service and controller constructors |
| 4.2 | REST controller, verbs, status codes | `PaymentRecordController` |
| 4.3 | DTOs, records | `dto/*`, mapping method in the service |
| 4.4 | Entity, `JpaRepository`, `Optional`, derived query | `entity/*`, `repository/*` |
| 4.5 | HTTP Basic, BCrypt, roles, CSRF | `config/*` |
| 4.6 | Layers | controller, service, repository packages |
| 4.7 | Validation, `@Valid`, `@RestControllerAdvice` | `dto/*`, `exception/*` |

## 9. Tools and versions

- Java 17, Spring Boot 3.x (latest stable at project creation), Maven. The exact versions are recorded in `pom.xml` when it is generated and then copied into `docs/SETUP_GUIDE.md`.
- Free tools: VS Code or another Java IDE, Git, GitHub, GitHub Desktop, Postman, Claude.
