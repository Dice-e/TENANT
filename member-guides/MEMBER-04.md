# MEMBER-04: REST Controllers and API

Branch: `feature/member-04-api`. Issues: T-11 to T-13.

Read `CLAUDE.md` first. All code below is a **plan or example**. Nothing has been compiled or run. You must run it and report real results.

---

## Taglish: Step-by-step (simulan dito)

Ito ang mabilis na bersyon sa Taglish para sa **Member 4 (API: controllers)**. Nasa ibaba ang buong paliwanag sa English (mga section 1 hanggang 19), at doon din nakasulat ang code na halimbawa. Hindi nagbago ang mga numero ng section.

Paalala: ang code sa guide na ito ay halimbawa pa lang at hindi pa napapatakbo. Ikaw ang magpapatakbo at magte-test, at ikaw ang magsasabi kung ano ang totoong nangyari.

### A. Unang setup (isang beses lang)

1. Buksan ang email mo at tanggapin ang invite ng GitHub para sa repo na `Dice-e/TENANT`.
2. Sa **GitHub Desktop**: **File > Clone repository** > piliin ang `Dice-e/TENANT` > **Clone**.
3. I-click ang **Current Branch** at piliin ang branch mo: `feature/member-04-api`. Kung wala sa listahan, i-click muna ang **Fetch origin**.
4. **Branch > Update from develop**. Importante ito para makuha mo ang skeleton ng project at ang gawa ng iba.
5. Buksan ang folder sa **IntelliJ**: **File > Open** > piliin ang `pom.xml` > **Open as Project**. Hintayin matapos ang loading sa baba. (Kung VS Code: **File > Open Folder**.)
6. Siguraduhing Java 17 pataas ang gamit: **File > Project Structure > Project > SDK**. Kung wala, **Add SDK > Download JDK > 21**.
7. Subukan kung tumatakbo: buksan ang `TenantApplication.java` > i-click ang green ▶ > **Run**. Dapat makita mo ang `Started TenantApplication`. I-stop gamit ang red ■.

### B. Ang gagawin mo

1. **Bago magsimula:** hintayin na naka-merge sa `develop` ang service ni Member 3. Habang naghihintay, i-install ang **Postman** at ipa-explain kay Claude ang REST at status codes.
2. **Branch > Update from develop** ulit bago magsimula.
3. Gumawa ng package na `controller`, tapos ang `PaymentRecordController` (halimbawa sa section 3.1 ng guide).
4. **T-11:** GET list (may optional na `unitNumber`) at GET by id. Test sa Postman: `http://localhost:8080/api/payment-records`.
5. **T-12:** POST (201), PUT (200), at DELETE (204) na may `@Valid @RequestBody`.
6. I-test ang bawat isa sa Postman. Mag-screenshot ng status code at body. Gamitin ang sample JSON sa section 11 ng guide.
7. **T-13:** Isulat ang `docs/API_ENDPOINTS.md`. **Totoong response lang** ang ilalagay, hindi gawa-gawa.
8. Paalala: kapag na-merge na ang security ni Member 5, kakailanganin na ng login at CSRF token para sa POST, PUT, at DELETE. Sundin ang `docs/API_TESTING_GUIDE.md`.
9. Walang business rules sa controller, at hindi dapat tumawag sa repository. Service lang ang tinatawag.

### C. Paano gamitin ang Claude

1. Sa Claude, i-upload ang `CLAUDE.md`, ang guide mo (`member-guides/MEMBER-04.md`), at ang SD1 Chapter 4 PDF (ipapadala ni Member 1 sa chat, wala ito sa GitHub). Idagdag ang files ng ibang member na kailangan mo: `PaymentRecordService` at ang DTOs.
2. I-paste ang **master prompt** (section 10 ng guide na ito).
3. Isa-isang phase lang. Huwag magpa-generate ng lahat nang sabay.
4. Pagkatapos ng bawat phase, patakbuhin at i-test mo. I-paste kay Claude ang **totoong** output o error, hindi ang kuwento mo lang.
5. Sagutin ang mga tanong ni Claude. Kung hindi mo masagot, ipaturo ulit hanggang maintindihan mo.

### D. Kapag tapos na at gumagana

1. Sa GitHub Desktop, tingnan ang **Changes**. Dapat sarili mong files lang ang nandoon. Kung may ibang file, huminto at magtanong kay Member 1.
2. Isulat ang summary (hal. `Add RentPaymentRecord entity`) at i-click ang **Commit to feature/member-04-api**.
3. I-click ang **Push origin**.
4. **Branch > Create pull request**. Sa **base**, dapat `develop` (HINDI `main`). Sa compare, `feature/member-04-api`.
5. Punan ang PR template sa `GITHUB_WORKFLOW.md`, kasama ang **totoong** test result (console o Postman screenshot).
6. Ilipat ang issue mo sa **Review** sa board at magpa-review sa ibang member.
7. **Huwag i-merge ang sarili mong PR.** Ayusin ang comments ng reviewer, mag-commit, at mag-push ulit.
8. Pagkatapos ma-merge: **Fetch origin**, tapos **Branch > Update from develop** bago ang susunod na task.

### E. Kapag may problema

1. I-copy ang **buong** error text (o mag-screenshot), hindi ang buod lang.
2. I-paste kay Claude at sabihin kung anong file at anong ginawa mo bago lumabas.
3. Kung hindi pa rin malutas, i-send sa group chat kay Member 1.

---

## 1. Your role and mission

You build the **front door** of the backend: the controller that receives web requests, passes them to Member 3's service, and sends back the right status code and JSON. You also write the endpoint documentation.

## 2. Level 1: beginner explanation

**What am I building?** The receptionist at a front desk. A visitor (the browser or Postman) walks up and says "show me record 5" or "add this record". The receptionist does **not** decide anything. They pass the request to the supervisor (service), then tell the visitor the answer with a clear signal: "here it is" (200), "created" (201), "done" (204), "no such record" (404).

**Why does it exist?** Without a controller, no URL leads anywhere. The service has no way to be reached from outside.

**What happens when someone uses it?** A request such as `GET /api/payment-records/5` arrives. Spring finds your method, fills in `id = 5`, calls the service, and turns the returned object into JSON.

**If it did not exist?** TENANT would have no API, and the frontend and Postman checks would have nothing to call.

## 3. Level 2: technical explanation

### 3.1 The controller (Chapter 4.2 and 4.6, Code 4.12)

```java
@RestController
@RequestMapping("/api/payment-records")
public class PaymentRecordController {

    private final PaymentRecordService service;

    public PaymentRecordController(PaymentRecordService service) {   // constructor injection
        this.service = service;
    }

    @GetMapping
    public List<PaymentRecordResponse> list(@RequestParam(required = false) String unitNumber) {
        return service.list(unitNumber);
    }

    @GetMapping("/{id}")
    public PaymentRecordResponse getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<PaymentRecordResponse> create(@Valid @RequestBody PaymentRecordRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));   // 201
    }

    @PutMapping("/{id}")
    public PaymentRecordResponse update(@PathVariable Long id,
                                        @Valid @RequestBody PaymentRecordRequest req) {
        return service.update(id, req);                                               // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();                                    // 204
    }
}
```

Imports: `org.springframework.web.bind.annotation.*`, `org.springframework.http.*`, `jakarta.validation.Valid`, and your own packages.

Line by line:

| Part | Meaning |
|---|---|
| `@RestController` | A class whose methods answer HTTP requests and return data (JSON), not web pages. |
| `@RequestMapping("/api/payment-records")` | Shared URL start for every method. The URL is a **noun**; the method is the **verb** (Chapter 4.2 REMEMBER box). |
| Constructor with the service | Constructor injection. The controller talks **only to the service**, never to the repository. |
| `@RequestParam(required=false)` | Reads `?unitNumber=3B`. Optional. No value means "list everything". |
| `@PathVariable Long id` | Reads the `5` in `/api/payment-records/5`. |
| `@RequestBody` | Turns the JSON body into a `PaymentRecordRequest`. |
| `@Valid` | Tells Spring to check the DTO's validation rules **before** your method runs. Without it, the rules are not checked (4.9 Q9). Member 6 adds the rules; you add `@Valid` now. |
| `ResponseEntity` | Lets you choose the status code with the body. |
| Default return | A method that just returns an object answers **200 OK**. |

What the controller must **not** do: contain `if` rules (service's job), call the repository, or return an entity. 404 and 409 are produced by exceptions from the service and turned into responses by Member 6's `GlobalExceptionHandler`. You do not write `try/catch` for them.

### 3.2 Status codes you are responsible for

| Request | Success | Other results (not written by you) |
|---|---|---|
| `GET /api/payment-records` | 200 | 401 from security |
| `GET /api/payment-records?unitNumber=3B` | 200, only unit `3B` | |
| `GET /api/payment-records/{id}` | 200 | 404 from the handler |
| `POST /api/payment-records` | **201** | 400 invalid, 409 duplicate, 403 CSRF |
| `PUT /api/payment-records/{id}` | 200 | 400, 404, 409 |
| `DELETE /api/payment-records/{id}` | **204** (no body) | 404, 403 not ADMIN |

### 3.3 Dates and money in JSON

Spring Boot writes a `LocalDate` as `"2026-11-01"` and a `BigDecimal` as a number like `7500.00`. Send dates in this form. A bad date such as `"abc"` makes the request unreadable. Member 6's handler turns that into a clear 400.

### 3.4 Endpoint documentation (T-13)

You write `docs/API_ENDPOINTS.md`. For each endpoint: method and path, who may call it, request example, response example, status codes, and what the status code means. Use **real** responses you actually received, copied from Postman, not invented ones.

### 3.5 Chapter 4 concepts you show

4.1 constructor injection. 4.2 REST controller, mapping annotations, `@PathVariable`, `@RequestParam`, `@RequestBody`, status codes. 4.3 DTOs at the boundary. 4.6 thin controller. 4.7 `@Valid`.

### 3.6 Common mistakes

| Mistake | Result |
|---|---|
| URLs like `/getRecord` | Breaks the REST rule |
| Returning the entity | Leaks internals |
| Calling the repository | Layer violation |
| Forgetting `@RequestBody` | Fields arrive empty |
| Forgetting `@Valid` | Validation silently skipped |
| Delete returns 200 with body | Should be 204 |
| Package outside `com.tenant.app` | Controller never found; every URL gives 404 |

## 4. Why your contribution matters

Working API and appropriate scope is 25 points, the largest single criterion. Checks 1 to 4 and 6 run through your code. Everyone's work is only visible through your endpoints.

## 5. How it connects

```
Browser / Postman ► Security (M5) ► YOUR CONTROLLER ► Service (M3) ► Repository (M2) ► H2
                                         ▲ @Valid uses rules from M6 ▲ errors turned to 400/404/409 by M6
```

## 6. Software and setup

Java 17, Maven, Git, GitHub Desktop, VS Code or IntelliJ, **Postman** (free), Claude. You need these merged into `develop`: skeleton, entity/DTOs/repository, exceptions, service. Then **Branch → Update from develop**.

## 7. Files you own

- `src/main/java/com/tenant/app/controller/PaymentRecordController.java`
- `docs/API_ENDPOINTS.md`
- `src/test/java/com/tenant/app/controller/PaymentRecordControllerTest.java` (optional)

## 8. Files you must not edit without coordination

Service (M3), DTOs and repository (M2), exceptions (M6), security config (M5), static frontend (M1), `postman/` (M6).

## 9. Step-by-step: vibe coding with Claude

1. Ask Claude to explain controllers, status codes and DTO boundary in beginner language.
2. Ask it to inspect the project: does the service exist? what methods?
3. Ask for the plan: one endpoint at a time, how to test each.
4. Build the phases below.
5. Test each endpoint in Postman. Paste the **real** status code and body.
6. Debug real errors.
7. Answer understanding questions.
8. Commit, push, PR.

## 10. Your master Claude prompt

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 4: `PaymentRecordController` (GET list with optional `unitNumber` filter, GET by id, POST, PUT, DELETE) and the endpoint documentation in `docs/API_ENDPOINTS.md`.
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-04.md` and inspect the project structure, especially Member 3's service and Member 2's DTOs.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps; give a test I can actually run in Postman; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> Keep the controller thin: constructor injection, call only the service, use DTOs, use `@Valid`, return 200, 201 and 204 as specified. No business rules, no repository calls, no entities in responses. Do not touch other members' files. Do not add dependencies or features outside the scope.
>
> Never claim code works unless I show you the real response. My goal is not only to finish the code but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Test in Postman (you run it) |
|---|---|---|
| 1 | Controller class, constructor injection, `GET` list | `GET http://localhost:8080/api/payment-records` returns 200 and a JSON array (sample data from Member 3's seeder) |
| 2 | Add the `unitNumber` filter | `...?unitNumber=1A` returns only unit 1A; `?unitNumber=9Z` returns `[]` |
| 3 | `GET /{id}` | An existing id gives 200. Unknown id gives 404 (needs the handler from Member 6) |
| 4 | `POST` with 201 | Body below returns 201 and an `id`. Fetch it with GET |
| 5 | `PUT` | Change the amount; 200; a later GET shows it |
| 6 | `DELETE` with 204 | 204 and an empty body; GET afterward gives 404 |
| 7 | `docs/API_ENDPOINTS.md` from real responses | Every example was actually received |
| 8 | PR | Reviewer repeats your Postman requests |

Sample POST body (Content-Type `application/json`):

```json
{
  "tenantName": "Demo Tenant X",
  "unitNumber": "4C",
  "amount": 7500.00,
  "paymentDate": "2026-11-01",
  "referenceNumber": "DEMO-0100"
}
```

Before Member 5's security merges, the API is open and POST works with no login or token. **After** security merges, you need Basic auth and a CSRF token for POST, PUT and DELETE. Follow `docs/API_TESTING_GUIDE.md` then. Postman is easier than curl for beginners (curl quoting differs on Windows).

Optional controller test: a `@WebMvcTest` with a mocked service. The mock annotation name depends on the Spring Boot version (`@MockBean` or the newer `@MockitoBean`), so ask Claude to check your `pom.xml` version. After Member 5 merges security, controller tests may need extra setup (a test user and CSRF); coordinate with Member 5 or skip these tests and rely on the Postman checks.

## 12. How to run and test

Start the app: `mvn spring-boot:run`. Use Postman against `http://localhost:8080`. Watch the console for errors. Keep each real response (screenshot) for your PR and your endpoint documentation.

## 13. Debugging with Claude

| Symptom | Likely cause |
|---|---|
| Every URL returns 404 | Controller outside `com.tenant.app`, or missing `@RestController` |
| `405 Method Not Allowed` | Wrong HTTP verb for that path |
| `415 Unsupported Media Type` | Missing `Content-Type: application/json` header |
| `400` with a body about JSON parse | Bad JSON, or date not `YYYY-MM-DD` |
| Fields are all `null` | Missing `@RequestBody` |
| Create gives 200 not 201 | Forgot `ResponseEntity` with `HttpStatus.CREATED` |
| `NoSuchBeanDefinitionException` for the service | Service class missing `@Service`, or not merged yet |
| Later, 401 or 403 appears | Security merged: add auth and CSRF header |

## 14. Create your Git branch

`feature/member-04-api` already exists. **Current Branch** → select it → **Branch → Update from develop**.

## 15. Commit and push

Example: `Add GET list with unit filter`. **Commit to feature/member-04-api**, then **Push origin**.

## 16. Create a pull request

**Branch → Create pull request**, base `develop`, fill the template with the real Postman results. Move T-11 to T-13 to Review.

## 17. Respond to code review

Fix, commit, push, reply to each comment. Never merge your own PR.

## 18. Defense questions you must answer

1. What does `@RestController` do? What does `@RequestMapping` on the class do?
2. Which annotation reads `unitNumber` from `?unitNumber=3B`? Which reads `id` from `/5`? (4.9 Q2)
3. What status code does a successful POST return? Successful DELETE? (4.9 Q3)
4. Why does the URL say `payment-records` and not `getPaymentRecords`?
5. Why does the controller not check for duplicate references?
6. Who chooses the status code: controller or service? Who turns an exception into 404? (4.9 Q8)
7. What happens to the DTO's rules if `@Valid` is missing? (4.9 Q9)
8. Why does the controller return `PaymentRecordResponse` and not the entity?
9. Trace `POST /api/payment-records` from the controller down to the database and back.
10. What did Claude suggest that you changed, and how did you test it?

## 19. Completion checklist

- [ ] Five operations plus the unit filter, all run in Postman (I have screenshots)
- [ ] 200, 201 and 204 returned as specified
- [ ] Constructor injection; only the service is called
- [ ] `@Valid @RequestBody` on POST and PUT
- [ ] No entities returned; no business `if` rules
- [ ] `docs/API_ENDPOINTS.md` uses real responses
- [ ] PR opened with real evidence; review answered
- [ ] I can answer all ten defense questions
