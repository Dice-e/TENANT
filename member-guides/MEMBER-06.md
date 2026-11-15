# MEMBER-06: Validation, Error Handling and QA

Branch: `feature/member-06-validation-qa`. Issues: T-18 to T-21.

Read `CLAUDE.md` first. All code below is a **plan or example**. Nothing has been compiled or run. You are also the team's tester, so your most important rule is: **write only results you really saw.**

---

## 1. Your role and mission

You make TENANT **fail politely** and you **prove it works**. You add the input rules, write the code that turns errors into clear messages (400, 404, 409), build the Postman collection, and run and document the ten SD1 checks.

## 2. Level 1: beginner explanation

**What am I building?** Two things.

1. **A bouncer for bad data.** If someone tries to save a record with an empty name or a negative amount, the bouncer stops it at the door and says exactly what is wrong. No crash, no mystery.
2. **A test report.** The professor wants proof. You run ten specific tests and record what really happened, with screenshots.

**Why does it exist?** Without validation, bad data reaches the database. Without the error handler, users see confusing server crashes (500) or empty responses. Without your test evidence, nobody can show the project works.

**What happens when someone uses it?** A bad request arrives, a rule fails, and the user gets `{"amount": "Amount must be greater than zero"}` with status 400. A request for a missing record gets "Payment record with id 99 not found" with status 404.

**If it did not exist?** The app would work only when everyone is perfect, and the team could not prove anything to the professor.

## 3. Level 2: technical explanation

### 3.1 Exceptions (T-19). These merge **first**

Member 3's service throws them, so the service cannot compile without them.

```java
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(Long id) {
        super("Payment record with id " + id + " not found");
    }
}

public class DuplicateReferenceException extends RuntimeException {
    public DuplicateReferenceException(String reference) {
        super("Reference number '" + reference + "' is already used by another record");
    }
}
```

`RuntimeException` means callers do not have to catch them. The handler catches them for the whole application.

### 3.2 The global handler (Chapter 4.7, Code 4.14)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);                         // 400
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<String> handleNotFound(RecordNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // 404
    }

    @ExceptionHandler(DuplicateReferenceException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateReferenceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());  // 409
    }
}
```

| Part | Meaning |
|---|---|
| `@RestControllerAdvice` | One class that handles errors for **all** controllers |
| `@ExceptionHandler(X.class)` | "When X is thrown anywhere, run this method" |
| `MethodArgumentNotValidException` | What Spring throws when `@Valid` finds a bad DTO, **before** the controller method runs |
| Map of field to message | Each field gets its own message, like the chapter's example |

**Recommended small addition (the chapter is silent, reason below).** A malformed body, such as `"paymentDate": "abc"` or a missing body, throws `HttpMessageNotReadableException`. By default this gives a 400 with little help. SD1 Check 5 asks for a *helpful* message, so add:

```java
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("request", "Request body is missing or malformed. Check the JSON and the date format (YYYY-MM-DD)."));
}
```

Optional: handle `MethodArgumentTypeMismatchException` for `/api/payment-records/abc` (400, "id must be a number"). Skip it if time is short.

**Important limit:** 401 and 403 come from Spring Security **before** any controller runs. This handler never sees them, so they do not use this format. That is expected (SD1 guide).

### 3.3 Validation rules (T-18)

Add to Member 2's request DTO (agree the changes with Member 2; they review your PR):

```java
public record PaymentRecordRequest(
    @NotBlank(message = "Tenant name is required")
    @Size(max = 100, message = "Tenant name must be at most 100 characters")
    String tenantName,

    @NotBlank(message = "Unit number is required")
    @Size(max = 20, message = "Unit number must be at most 20 characters")
    String unitNumber,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount can have at most 10 digits and 2 decimals")
    BigDecimal amount,

    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,

    @NotBlank(message = "Reference number is required")
    @Size(max = 50, message = "Reference number must be at most 50 characters")
    String referenceNumber) { }
```

Imports: `jakarta.validation.constraints.*` (never `javax`). The starter `spring-boot-starter-validation` must be in `pom.xml` (it is in Member 1's skeleton).

| Annotation | Checks |
|---|---|
| `@NotBlank` | Text is not null and not only spaces |
| `@NotNull` | Value is present |
| `@Size(max=)` | Length limit, matching the database column |
| `@Positive` | Number is greater than zero |
| `@Digits` | Digits before and after the decimal point (fits `DECIMAL(12,2)`) |

Three kinds of checking exist (Chapter 4.7 table):

| Kind | Where | Example in TENANT |
|---|---|---|
| Input validation | DTO annotations plus `@Valid` | Name not blank |
| Business validation | Service (Member 3) | Reference not already used |
| Database constraint | `@Column(unique=true)` | Last line of defense |

`@Valid` must also be on the controller parameter (Member 4 adds it). Without it these rules are skipped (4.9 Q9).

### 3.4 Postman collection (T-20)

Create a collection named `TENANT API` with a variable `baseUrl = http://localhost:8080`. One folder per check, one request per step. Save with **Export → Collection v2.1** to `postman/TENANT-API-Collection.json`.

Useful idea: a **Tests** script on the create request can remember the new ID for later requests:

```javascript
pm.test("status is 201", () => pm.response.to.have.status(201));
pm.collectionVariables.set("recordId", pm.response.json().id);
```

Then later URLs use `{{baseUrl}}/api/payment-records/{{recordId}}`. Authorization uses Postman's **Basic Auth** tab. CSRF header steps come from Member 5's proven method; do not guess them. Note: never put real passwords in the collection; the demo passwords are fine because they are demo-only.

### 3.5 The ten checks (T-21)

Run these **after** Members 3, 4 and 5 are merged, on a fresh app start. For each, record: request or input, expected, actual, pass/fail, and a screenshot or saved response in `docs/evidence/` (for example `check-01.png`). Summary goes in `docs/evidence/CHECK-RESULTS.md`.

| No. | What to do | Expected |
|---|---|---|
| 1 | POST a valid record as `staff` with valid CSRF | 201, body has a generated `id` |
| 2 | GET the list; GET the new id; GET `?unitNumber=<its unit>` | 200 each; the filter returns matching records only |
| 3 | PUT the record changing the amount but **keeping its reference**; then GET it | 200; GET shows the change; no 409 |
| 4 | DELETE as `admin` with valid CSRF; then GET the id | 204; then 404 |
| 5 | POST with empty `tenantName`, negative `amount`, missing date | 400 with a message for each bad field |
| 6 | GET `/api/payment-records/99999` | 404 with a clear message |
| 7 | POST using an existing reference; then PUT record B using record A's reference | 409 both times; GET both afterwards: data unchanged |
| 8 | GET without credentials; GET with `staff` and a wrong password | 401 both times |
| 9 | DELETE as `staff` **with a valid CSRF token**; then GET the record | 403; the record still exists |
| 10 | POST a record; in a **separate** request GET it; also note the Hibernate `insert` line in the console | 200 with the same data, proving it was stored through JPA |

Rules for you as tester:

- Do **not** mark a check "Pass" until you ran it. Use `Not run` until then.
- If a check fails, write "Fail", paste the real response, and open an issue for the owner. Re-run after the fix and record both runs.
- Check 9 needs a valid token so the 403 shows **role denial**, not a CSRF failure. Prove this by also showing that the same request as `admin` returns 204.
- Check 8 uses GET because a POST without a token can return 403 before login is checked.

### 3.6 Chapter 4 concepts you show

4.7 Bean Validation, `@Valid`, `@RestControllerAdvice`, 400/404/409. 4.2 status codes. 4.4 `Optional` and not-found. And the testing mindset throughout.

### 3.7 Common mistakes

| Mistake | Result |
|---|---|
| `javax.validation` imports | Compile errors under Boot 3 |
| Rules on the DTO but no `@Valid` in the controller | Rules silently ignored |
| Handler class not under `com.tenant.app` | Never found |
| Returning 500 for missing records | Handler for `RecordNotFoundException` missing |
| Marking checks "Pass" from memory | Violates SD1 honesty rule; can lose points |
| Check 9 without a CSRF token | 403 for the wrong reason |

## 4. Why your contribution matters

Your code carries the 400/404/409 behavior and part of the 20 security-validation-rule points. Your evidence is most of the 15 testing and documentation points.

## 5. How it connects

```
Bad DTO ──► @Valid fails ──┐
Missing id ──► RecordNotFoundException (M3) ──┼──► YOUR GlobalExceptionHandler ──► 400 / 404 / 409
Duplicate ref ──► DuplicateReferenceException (M3) ──┘
Security 401/403 happen earlier and are NOT handled here
```

## 6. Software and setup

Java 17, Maven, Git, GitHub Desktop, VS Code or IntelliJ, **Postman**, a screenshot tool (Windows: Win+Shift+S), Claude. You need the skeleton and data model merged first (exceptions have no other dependency). For the ten checks you need everything merged.

## 7. Files you own

- `src/main/java/com/tenant/app/exception/RecordNotFoundException.java`
- `src/main/java/com/tenant/app/exception/DuplicateReferenceException.java`
- `src/main/java/com/tenant/app/exception/GlobalExceptionHandler.java`
- `postman/TENANT-API-Collection.json`
- `docs/evidence/**` (screenshots and `CHECK-RESULTS.md`)
- `docs/API_TESTING_GUIDE.md` (with Member 5 for the CSRF steps)

## 8. Files you must not edit without coordination

`PaymentRecordRequest` (Member 2 owns it; you add annotations through a PR that Member 2 reviews). Anything in service, controller, config, security, `static`. If a check fails, open an issue for the owner; do not fix their files yourself.

## 9. Step-by-step: vibe coding with Claude

1. Ask Claude to explain validation vs business rules vs database constraints.
2. Ask it to inspect the project and list the current exceptions and DTOs.
3. Ask for the plan, phase by phase.
4. Build one phase and run it.
5. Paste **real** responses; never describe from memory.
6. Debug real errors.
7. Answer understanding questions.
8. Commit, push, PR.

## 10. Your master Claude prompt

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 6: Bean Validation on `PaymentRecordRequest`, `RecordNotFoundException`, `DuplicateReferenceException`, `GlobalExceptionHandler` (`@RestControllerAdvice`), the Postman collection, and executing and documenting the ten SD1 checks.
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-06.md` and inspect the project structure.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps; give a test I can actually run; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> Use `jakarta.validation`. Follow Chapter 4 Code 4.14 for error bodies. Do not edit other members' files except by a reviewed pull request. Do not add features outside the scope.
>
> You must never write, guess or fill in test results. Only record results I paste from real runs, and mark anything I have not run as "Not run". My goal is not only to finish the work but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Test (you run it) |
|---|---|---|
| 1 | T-19: the two exception classes and `GlobalExceptionHandler` | `mvn compile` succeeds; PR merged **early** so Member 3 can compile |
| 2 | T-18: validation annotations (PR reviewed by Member 2) | Needs the controller: POST an empty body, see 400 with field messages |
| 3 | Add the malformed-body handler | POST `"paymentDate":"abc"`: 400 with the helpful message |
| 4 | T-20: Postman collection with variables and one folder per check | Collection runs against the live app |
| 5 | Dry run of all ten checks (to find bugs early, not yet "evidence") | Issues opened for any failure |
| 6 | T-21: final run on a fresh start, after security is merged; screenshots and `CHECK-RESULTS.md` | Each row filled with the real result |
| 7 | Help write `docs/API_TESTING_GUIDE.md` CSRF steps with Member 5 | Another member follows it successfully |
| 8 | PR with evidence | Reviewer re-runs two checks |

## 12. How to run and test

Start with `mvn spring-boot:run`, wait for "Started TenantApplication", then use Postman. For the final evidence run, stop the app and start it fresh so the data is in a known state (sample records and demo accounts). Save a screenshot showing the request, the status code and the body.

## 13. Debugging with Claude

| Symptom | Likely cause |
|---|---|
| Invalid input returns 200 | `@Valid` missing in the controller |
| Invalid input returns 400 but empty body | Handler class not found or not `@RestControllerAdvice` |
| 404 case returns 500 | `RecordNotFoundException` handler missing, or the service throws another type |
| Duplicate returns 500 and a database error | Service check missing; the database constraint caught it instead |
| `package jakarta.validation... does not exist` | `spring-boot-starter-validation` missing in `pom.xml` |
| 403 on a write request in a check | CSRF token missing; see Member 5's table |
| Check 4 shows 404 straight away | Wrong id, or the record was already deleted by an earlier run |

## 14. Create your Git branch

`feature/member-06-validation-qa` already exists. **Current Branch** → select it → **Branch → Update from develop**.

## 15. Commit and push

Split your work: one commit for exceptions and handler (merge early), later commits for validation, Postman and evidence. **Commit to feature/member-06-validation-qa**, then **Push origin**. Screenshots are fine to commit (small PNGs).

## 16. Create a pull request

**Branch → Create pull request**, base `develop`. Because you must merge early, make **two** PRs: the exceptions first, the rest later. Fill the template with real results. Move T-18 to T-21 to Review.

## 17. Respond to code review

Fix, commit, push, reply to each comment. Never merge your own PR.

## 18. Defense questions you must answer

1. What does `@Valid` do and what happens if it is missing? (4.9 Q9)
2. Which exception does `@Valid` throw and when, relative to the controller method?
3. What does `@RestControllerAdvice` do? Why one class for all controllers?
4. Input validation vs business validation vs database constraint: give a TENANT example of each.
5. Why do 401 and 403 not use your error format?
6. Why 404 for a missing record and 409 for a duplicate? Why not 500?
7. Why does Check 9 need a valid CSRF token? What would a missing token show?
8. Why does Check 8 use GET?
9. Check 7: what do you verify afterwards and why?
10. Check 10: how does your evidence show the record was stored by JPA?
11. A check failed: what exactly did you do?
12. What did Claude suggest that you changed, and how did you test it?

## 19. Completion checklist

- [ ] Exceptions and handler merged early; handler returns 400, 404, 409 (I ran each)
- [ ] Validation annotations on every request field; `jakarta` imports
- [ ] Malformed body gives a helpful 400
- [ ] Postman collection exported to `postman/`
- [ ] Ten checks run on a fresh start; each has request, expected, actual, pass/fail, screenshot
- [ ] Failures (if any) have issues and re-run evidence
- [ ] `docs/API_TESTING_GUIDE.md` CSRF steps verified by another member
- [ ] No result written that I did not see
- [ ] PRs opened and reviews answered
- [ ] I can answer all twelve defense questions
