# HANDOFF.md: where TENANT stands

Written 2026-11-28 from what is on GitHub `main` (commit accdc8a, updated after Member 3's branch was found). Everything below was **inspected** only. Nothing here was compiled or run in this write-up. The only run on record is the Spring Boot skeleton starting on Member 1's laptop (2026-11-15, `Started TenantApplication`).

## What exists on `main`

| Area | Files | Owner | Status |
|---|---|---|---|
| Skeleton | `pom.xml`, `TenantApplication`, `application.properties`, context test | Member 1 | Started successfully on Member 1's laptop |
| Entity | `entity/RentPaymentRecord.java` | Member 2 | Merged. Not compiled or run by a reviewer |
| DTOs | `dto/PaymentRecordRequest.java`, `PaymentRecordResponse.java` (records, 10 to 11 lines) | Member 2 | Merged. No validation annotations seen yet |
| Repository | `repository/PaymentRecordRepository.java` (`findByUnitNumber`, `existsByReferenceNumber`, `existsByReferenceNumberAndIdNot`) | Member 2 | Merged. Not run |
| Errors | `exception/GlobalExceptionHandler`, `RecordNotFoundException`, `DuplicateReferenceException` | Member 6 | Merged. PR said "not tested yet" |
| Frontend | `static/index.html` (36 lines), `static/css/tenant.css` (76 lines) | Member 1 | Shell only: sidebar and bottom tab bar. No API calls yet |

## Newest activity (2026-11-28 check)

- PR #35 (AGENTS.md and HANDOFF.md) was merged straight into `main`, not `develop`. `develop` (d1ffb1f) is now behind `main`; sync it before new feature work.
- Member 3 pushed `service/PaymentRecordService.java` to `feature/member-03-service` (commit 471d893). **No pull request yet, not on `main`.**
- **That file will not compile as written.** The class closes after the constructor, and the imports plus the `create` and `toResponse` methods are pasted *after* the closing brace. Fix: move the imports to the top and put both methods inside the class. Do not merge it until it compiles.
- It only has `create`. Still missing: get by id, list with the unit filter, update (own reference allowed), delete, and 404 handling.
- Member 5's branch has no new work. Member 4's branch has no controller.

## What does not exist yet

- Service layer (Member 3): only a broken `create` draft on a branch. Also needs the unique-reference rule on update and sample-data seeding.
- Controller (Member 4). A Member 4 PR merged, but no controller file is on `main`.
- Security: `AppUser`, `AppUserRepository`, BCrypt, HTTP Basic, ADMIN-only delete, CSRF method (Member 5). `spring-boot-starter-security` is deliberately not in `pom.xml` yet.
- Bean Validation annotations on the DTOs (Member 6, by pull request that Member 2 reviews).
- Frontend pages and JavaScript that call the API (Member 1).
- Postman collection, the ten checks, evidence, and the write-up.

## Known open items

- The CSRF token method is undecided and untested. `docs/API_TESTING_GUIDE.md` leaves it blank on purpose until Member 5 tests it.
- Six members: Member 1 must confirm with the professor that six is allowed.
- Branch rules: PRs should go into `develop`, then Member 1 merges `develop` into `main`. Several PRs went straight into `main`, and some had no review. Record this honestly in `CONTRIBUTION_TRACKER.md`.
- Earlier commits by the AI assistant appear under Member 1's identity. Say so in `docs/AI_USE_DISCLOSURE.md`.
- Build tools (Maven downloads) were not reachable from the AI's cloud workspace, so no AI session has compiled the code. Run `mvn spring-boot:run` locally and paste the real output before calling anything working.

## Next steps, in order

1. Member 1: run the app on `main` and confirm it still starts with the new entity, repository and exception classes.
2. Member 3: fix the service file so it compiles, finish the remaining methods, open a PR into `develop`. Then Member 4: controller.
3. Member 5: security, after telling Member 1 (adds the starter to `pom.xml`).
4. Member 6: validation annotations, Postman collection, ten checks.
5. Member 1: connect the frontend to the real API.

Rules for every AI tool: [CLAUDE.md](CLAUDE.md). Teach first, work in small phases, never claim code works without evidence.
