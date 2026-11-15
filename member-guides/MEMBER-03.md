# MEMBER-03: Service Layer and Business Rules

Branch: `feature/member-03-service`. Issues: T-08 to T-10.

Read `CLAUDE.md` first. All code below is a **plan or example**. Nothing has been compiled or run. You must run it and report real results.

---

## 1. Your role and mission

You write the **brain** of TENANT: the service that creates, finds, updates and deletes records, enforces the "unique reference number" rule, and converts between entities and DTOs. You also add a few invented sample records at startup.

## 2. Level 1: beginner explanation

**What am I building?** In an office, the front desk (controller) takes requests, and the filing clerk (repository) fetches folders. In between is the **supervisor**: "Is this reference already used? Does this record exist? Yes, then go ahead; no, then stop and say why."

**Why does it exist?** Rules need one home. If they lived in the controller, they would mix with web code and be hard to test or change.

**What happens when someone uses it?** The controller hands you a request. You check the rules, ask the repository to save or find, convert the result to a response, and give it back. If a rule fails, you throw an exception. Member 6's handler turns it into a 404 or 409.

**If it did not exist?** Duplicate references would be accepted or crash the database, and missing records would crash the app.

## 3. Level 2: technical explanation

### 3.1 The service (Chapter 4.6, Code 4.11)

```java
@Service
public class PaymentRecordService {

    private final PaymentRecordRepository repository;

    public PaymentRecordService(PaymentRecordRepository repository) {   // constructor injection
        this.repository = repository;
    }

    public PaymentRecordResponse create(PaymentRecordRequest req) {
        if (repository.existsByReferenceNumber(req.referenceNumber())) {
            throw new DuplicateReferenceException(req.referenceNumber());        // business rule
        }
        RentPaymentRecord saved = repository.save(new RentPaymentRecord(
                req.tenantName(), req.unitNumber(), req.amount(),
                req.paymentDate(), req.referenceNumber()));
        return toResponse(saved);
    }

    public List<PaymentRecordResponse> list(String unitNumber) {
        List<RentPaymentRecord> records = (unitNumber == null || unitNumber.isBlank())
                ? repository.findAll()
                : repository.findByUnitNumber(unitNumber);
        return records.stream().map(this::toResponse).toList();
    }

    public PaymentRecordResponse getById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    @Transactional
    public PaymentRecordResponse update(Long id, PaymentRecordRequest req) {
        RentPaymentRecord record = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        if (repository.existsByReferenceNumberAndIdNot(req.referenceNumber(), id)) {
            throw new DuplicateReferenceException(req.referenceNumber());
        }
        record.setTenantName(req.tenantName());
        record.setUnitNumber(req.unitNumber());
        record.setAmount(req.amount());
        record.setPaymentDate(req.paymentDate());
        record.setReferenceNumber(req.referenceNumber());
        return toResponse(repository.save(record));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RecordNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private PaymentRecordResponse toResponse(RentPaymentRecord r) {   // entity to DTO
        return new PaymentRecordResponse(r.getId(), r.getTenantName(), r.getUnitNumber(),
                r.getAmount(), r.getPaymentDate(), r.getReferenceNumber());
    }
}
```

Imports: `org.springframework.stereotype.Service`, `org.springframework.transaction.annotation.Transactional`, and your own packages.

Line by line, the important parts:

| Part | Why |
|---|---|
| `@Service` | Spring finds this class and creates one managed object (a bean) from it. |
| Constructor with the repository | **Constructor injection** (4.1). Spring supplies the repository. The field is `final`. |
| `existsByReferenceNumber` on create | Business rule: reference must be unique. |
| `existsByReferenceNumberAndIdNot(ref, id)` on update | "Is another record using this reference?" Saving a record with its **own** reference passes. |
| Not-found check **before** the duplicate check on update | A missing ID should say 404, not 409. |
| `Optional.orElseThrow` | `findById` can be empty. Never call `.get()` without checking (4.4). |
| `toResponse` | Entity becomes DTO here, so controllers never see entities (4.3, 4.6). |
| `@Transactional` on update | Groups the read and write into one unit of work (Chapter 4.6 lists transactions as a service job). |
| No `ResponseEntity`, no `HttpStatus` | The service must not know about HTTP (4.6). The controller chooses status codes. |

The update rule, in plain words: *"Reject only if someone else has this reference."* A test that updates a record without changing its reference **must succeed**. This is explicitly required by the SD1 guide.

### 3.2 Exceptions you throw

`RecordNotFoundException` and `DuplicateReferenceException` are written by **Member 6** and merged before your service. Do not write them yourself. If you need them early, ask Member 6. Each should carry a clear message, for example "Payment record with id 5 not found".

### 3.3 Sample data (T-10)

```java
@Component
public class SampleDataSeeder implements CommandLineRunner {

    private final PaymentRecordRepository repository;

    public SampleDataSeeder(PaymentRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;      // do not add twice
        repository.save(new RentPaymentRecord("Demo Tenant A", "1A",
                new BigDecimal("7500.00"), LocalDate.of(2026, 10, 1), "DEMO-0001"));
        // 4 to 7 more, invented, clearly demo data
    }
}
```

- A `CommandLineRunner` runs once after the app starts.
- Data is invented ("Demo Tenant A"). No real names or numbers.
- H2 is in memory, so these records come back on every start. Say so in the README.
- Put it in the `service` package as agreed in `PROJECT_ARCHITECTURE.md`.

### 3.4 Chapter 4 concepts you show

4.1 constructor injection and beans. 4.3 entity/DTO mapping. 4.4 `Optional` handling. 4.6 layers: rule in the service. 4.7 business validation (service layer) as opposed to input validation (DTO).

### 3.5 Common mistakes

| Mistake | Result |
|---|---|
| Using `existsByReferenceNumber` on update | A record cannot be saved with its own reference. **Fails an SD1 requirement.** |
| `.get()` on the `Optional` | Crash instead of a 404 |
| Returning the entity | Leaks internals; breaks the DTO rule |
| `ResponseEntity` in the service | Layer violation |
| `new PaymentRecordService(...)` somewhere | Not a managed bean; nothing is injected |
| Putting `if` rules in the controller | Wrong layer |

## 4. Why your contribution matters

Rubric: 20 points cover security, validation **and the business rule**. This is the rule. It is also checks 3, 4, 6 and 7.

## 5. How it connects

```
Controller (M4) → YOUR SERVICE → Repository (M2) → H2
                     ↘ throws exceptions → GlobalExceptionHandler (M6) → 404 / 409
```

## 6. Software and setup

Same toolset as other members. You need these merged into `develop` first: skeleton (M1), entity/DTOs/repository (M2), exceptions (M6). Then **Branch → Update from develop**.

## 7. Files you own

- `src/main/java/com/tenant/app/service/PaymentRecordService.java`
- `src/main/java/com/tenant/app/service/SampleDataSeeder.java`
- `src/test/java/com/tenant/app/service/PaymentRecordServiceTest.java` (recommended)

## 8. Files you must not edit without coordination

Entity, DTOs, repository (M2). Exceptions (M6). Controller (M4). If you need a new repository method or entity setter, ask Member 2 by comment or issue.

## 9. Step-by-step: vibe coding with Claude

1. Ask Claude to explain "service layer and business rules" to a beginner.
2. Ask it to inspect the project and list what exists from M2 and M6.
3. Ask for the plan: methods, exceptions, how to test.
4. Do one method at a time (phases below).
5. Run each one. Paste real output.
6. Debug real errors with Claude.
7. Answer its questions, especially about the update rule.
8. Commit, push, PR.

## 10. Your master Claude prompt

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 3: `PaymentRecordService` (create, list with optional unit filter, get by id, update, delete), the unique `referenceNumber` rule on create and update, entity-to-DTO mapping, and the startup sample-data seeder.
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-03.md` and inspect the project structure, including Member 2's and Member 6's classes.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps; give a test I can actually run; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> Use constructor injection. No `ResponseEntity` or `HttpStatus` in the service. Handle every `Optional`. On update, a record must be allowed to keep its own reference number. Do not touch other members' files. Do not add dependencies or features outside the scope.
>
> Never claim code works unless I show you the real output. My goal is not only to finish the code but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Test (you run it) |
|---|---|---|
| 1 | Class skeleton with constructor injection | `mvn compile` succeeds |
| 2 | `create` with duplicate check, plus `toResponse` | Unit test: second create with the same reference throws `DuplicateReferenceException` |
| 3 | `getById` and `list` with filter | Unit test: unknown id throws `RecordNotFoundException`; filter returns only that unit |
| 4 | `update` with the rule | Unit test: updating with its **own** reference succeeds; using **another** record's reference throws |
| 5 | `delete` | Unit test: missing id throws; existing id removes it |
| 6 | `SampleDataSeeder` | App starts; the Hibernate log shows inserts; (after M4) `GET /api/payment-records` shows them |
| 7 | PR | Reviewer can run the tests |

Test idea for the key rule (not yet run). Mockito comes with `spring-boot-starter-test`:

```java
@ExtendWith(MockitoExtension.class)
class PaymentRecordServiceTest {
    @Mock PaymentRecordRepository repo;
    @InjectMocks PaymentRecordService service;

    @Test
    void updateKeepingOwnReferenceIsAllowed() {
        RentPaymentRecord existing = new RentPaymentRecord("Demo", "1A",
                new BigDecimal("100.00"), LocalDate.of(2026, 11, 1), "REF-1");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsByReferenceNumberAndIdNot("REF-1", 1L)).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentRecordResponse r = service.update(1L, new PaymentRecordRequest(
                "Demo", "1A", new BigDecimal("200.00"), LocalDate.of(2026, 11, 2), "REF-1"));

        assertEquals(new BigDecimal("200.00"), r.amount());
    }
}
```

## 12. How to run and test

`mvn test` for the unit tests, `mvn spring-boot:run` for the app. Read the output. A test that does not run is not a passing test. Record the real result (for example "5 tests, 0 failures") in your PR.

## 13. Debugging with Claude

| Symptom | Likely cause |
|---|---|
| `cannot find symbol: RecordNotFoundException` | Member 6's PR is not merged or not pulled |
| `NullPointerException` on `repository` | You used `new` instead of letting Spring inject |
| Test: "Unnecessary stubbings" | A `when(...)` your code never calls; check the logic |
| Update with the same reference returns 409 | You used `existsByReferenceNumber` instead of the `...AndIdNot` version |
| App fails at startup in the seeder | Duplicate reference in sample data, or null field |

## 14. Create your Git branch

`feature/member-03-service` is already published. Select it under **Current Branch**, then **Branch → Update from develop**.

## 15. Commit and push

Example summaries: `Add create with duplicate reference check`, `Allow update to keep own reference`. **Commit to feature/member-03-service**, then **Push origin**.

## 16. Create a pull request

**Branch → Create pull request**, base `develop`. Fill the template with your real test results. Move T-08 to T-10 to Review.

## 17. Respond to code review

Fix, commit, push, reply to every comment. Never merge your own PR.

## 18. Defense questions you must answer

1. Why is the unique rule in the service, not the controller or repository? (4.6, 4.9 Q8)
2. Explain constructor injection. Why not `new`? (4.9 Q10)
3. Why does `update` use `existsByReferenceNumberAndIdNot`?
4. What happens if two records are updated to the same reference? Which layer catches it, and what does the client see?
5. Why must the service not use `ResponseEntity` or `HttpStatus`?
6. What does `Optional.orElseThrow` do? What goes wrong with `.get()`? (4.4)
7. Why map entity to DTO inside the service?
8. What does `@Transactional` do on `update`, in simple words?
9. What does `CommandLineRunner` do, and why check `count() > 0`?
10. Trace `POST` from the controller to the database. Which of your lines run?
11. What did Claude suggest that you changed, and how did you test it?

## 19. Completion checklist

- [ ] Constructor injection; no `new` for managed classes
- [ ] Create rejects duplicate reference; update rejects only **another** record's reference
- [ ] Update with its own reference succeeds (tested, I ran it)
- [ ] Missing id throws `RecordNotFoundException` (get, update, delete)
- [ ] No HTTP classes in the service; no entities leave it
- [ ] Seeder adds invented sample data once; app starts
- [ ] `mvn test` output recorded in the PR
- [ ] PR opened and review answered
- [ ] I can answer all eleven defense questions
