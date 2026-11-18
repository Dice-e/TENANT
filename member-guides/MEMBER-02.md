# MEMBER-02: Entity, DTOs and Repository

Branch: `feature/member-02-data-model`. Issues: T-05 to T-07.

Read `CLAUDE.md` first. All code below is a **plan or example**. None of it has been compiled or run. You must run it and report real results.

---

## Taglish: Step-by-step (simulan dito)

Ito ang mabilis na bersyon sa Taglish para sa **Member 2 (Database: entity, DTO, repository)**. Nasa ibaba ang buong paliwanag sa English (mga section 1 hanggang 19), at doon din nakasulat ang code na halimbawa. Hindi nagbago ang mga numero ng section.

Paalala: ang code sa guide na ito ay halimbawa pa lang at hindi pa napapatakbo. Ikaw ang magpapatakbo at magte-test, at ikaw ang magsasabi kung ano ang totoong nangyari.

### A. Unang setup (isang beses lang)

1. Buksan ang email mo at tanggapin ang invite ng GitHub para sa repo na `Dice-e/TENANT`.
2. Sa **GitHub Desktop**: **File > Clone repository** > piliin ang `Dice-e/TENANT` > **Clone**.
3. I-click ang **Current Branch** at piliin ang branch mo: `feature/member-02-data-model`. Kung wala sa listahan, i-click muna ang **Fetch origin**.
4. **Branch > Update from develop**. Importante ito para makuha mo ang skeleton ng project at ang gawa ng iba.
5. Buksan ang folder sa **IntelliJ**: **File > Open** > piliin ang `pom.xml` > **Open as Project**. Hintayin matapos ang loading sa baba. (Kung VS Code: **File > Open Folder**.)
6. Siguraduhing Java 17 pataas ang gamit: **File > Project Structure > Project > SDK**. Kung wala, **Add SDK > Download JDK > 21**.
7. Subukan kung tumatakbo: buksan ang `TenantApplication.java` > i-click ang green ▶ > **Run**. Dapat makita mo ang `Started TenantApplication`. I-stop gamit ang red ■.

### B. Ang gagawin mo

1. Sa `src/main/java/com/tenant/app`, gumawa ng tatlong package (folder): `entity`, `dto`, `repository`. (IntelliJ: right-click > New > Package.)
2. **T-05:** Gawin ang `RentPaymentRecord` sa `entity`. Ipa-explain kay Claude ang halimbawa sa section 3.1 ng guide, linya por linya, bago mo ito gamitin.
3. Patakbuhin ang app. Sa console dapat may `create table rent_payment_record`. Kung wala, i-paste kay Claude ang totoong error.
4. **T-06:** Gawin ang `PaymentRecordRequest` at `PaymentRecordResponse` bilang `record` sa `dto`. Walang `id` sa request.
5. **T-07:** Gawin ang `PaymentRecordRepository` sa `repository` na may `findByUnitNumber`, `existsByReferenceNumber`, at `existsByReferenceNumberAndIdNot`.
6. Patakbuhin ulit ang app. Dapat makita pa rin ang `Started TenantApplication`. Kapag may typo sa pangalan ng method, hindi mag-start ang app.
7. (Optional) Gumawa ng repository test (nasa section 11 ng guide). IntelliJ: right-click sa test class > Run. Isulat ang totoong resulta.
8. Sabihan sina Member 3 at Member 6 kung anong fields at setters ang ginawa mo.
9. Mauna kang mag-PR at ma-merge sa `develop`, dahil kailangan ito ng iba.

### C. Paano gamitin ang Claude

1. Sa Claude, i-upload ang `CLAUDE.md`, ang guide mo (`member-guides/MEMBER-02.md`), at ang SD1 Chapter 4 PDF (ipapadala ni Member 1 sa chat, wala ito sa GitHub). Idagdag ang files ng ibang member na kailangan mo: wala, ikaw ang mauuna.
2. I-paste ang **master prompt** (section 10 ng guide na ito).
3. Isa-isang phase lang. Huwag magpa-generate ng lahat nang sabay.
4. Pagkatapos ng bawat phase, patakbuhin at i-test mo. I-paste kay Claude ang **totoong** output o error, hindi ang kuwento mo lang.
5. Sagutin ang mga tanong ni Claude. Kung hindi mo masagot, ipaturo ulit hanggang maintindihan mo.

### D. Kapag tapos na at gumagana

1. Sa GitHub Desktop, tingnan ang **Changes**. Dapat sarili mong files lang ang nandoon. Kung may ibang file, huminto at magtanong kay Member 1.
2. Isulat ang summary (hal. `Add RentPaymentRecord entity`) at i-click ang **Commit to feature/member-02-data-model**.
3. I-click ang **Push origin**.
4. **Branch > Create pull request**. Sa **base**, dapat `develop` (HINDI `main`). Sa compare, `feature/member-02-data-model`.
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

You build the **data foundation**: the Java class that matches the database table, the two small classes that carry data in and out of the API, and the repository that saves and finds records. Everyone else builds on your work, so merge it early.

## 2. Level 1: beginner explanation

**What am I building?** Imagine a paper form with five boxes (tenant name, unit, amount, date, reference) and a filing cabinet to store filled forms.

- The **entity** is the form that goes into the cabinet (one row in the database).
- The **DTOs** are the forms handed across the counter: one for what the customer fills in (request), one for what we hand back (response).
- The **repository** is the clerk who files and finds forms. You only describe the jobs; Spring does the work.

**Why does it exist?** Without it there is nowhere to keep records.

**Why two kinds of form?** The customer must not choose the ID or see internal fields. The cabinet form can change without changing what customers see.

**What happens when someone uses it?** A request arrives as a DTO, the service turns it into an entity, the repository saves it as a row, and a response DTO goes back.

**If it did not exist?** The app would forget everything and the other layers would have nothing to work with.

## 3. Level 2: technical explanation

### 3.1 The entity (Chapter 4.4, Code 4.6)

```java
@Entity
@Table(name = "rent_payment_record")
public class RentPaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tenantName;

    @Column(nullable = false, length = 20)
    private String unitNumber;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false, unique = true, length = 50)
    private String referenceNumber;

    protected RentPaymentRecord() { }   // JPA needs a no-argument constructor

    public RentPaymentRecord(String tenantName, String unitNumber, BigDecimal amount,
                             LocalDate paymentDate, String referenceNumber) {
        this.tenantName = tenantName;
        this.unitNumber = unitNumber;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.referenceNumber = referenceNumber;
    }

    // getters for every field, and setters for every field EXCEPT id
}
```

Line by line:

| Part | Meaning |
|---|---|
| `@Entity` | This class is a table row. Needs `jakarta.persistence.Entity`. |
| `@Table(name=...)` | The table name. Matches `PROJECT_ARCHITECTURE.md`. |
| `@Id` | The primary key. |
| `@GeneratedValue(IDENTITY)` | The database creates the ID. This is why the create request has no ID. |
| `@Column(nullable=false)` | The database refuses an empty value. |
| `unique = true` | The database refuses a duplicate reference. This is the **last line of defense**; the friendly 409 comes from Member 3's service check. |
| `BigDecimal` | Exact decimal math. `double` can round money wrongly. |
| `protected` constructor | JPA creates objects through it. |

Why setters? Member 3's update changes an existing record. **Tell Member 3** that setters exist for all fields but `id`. The ID must never change.

Imports: always `jakarta.persistence.*`. If Claude writes `javax.persistence`, it is the old style and will not work with Spring Boot 3.

### 3.2 The DTOs (Chapter 4.3, Code 4.4)

```java
public record PaymentRecordRequest(
        String tenantName,
        String unitNumber,
        BigDecimal amount,
        LocalDate paymentDate,
        String referenceNumber) { }

public record PaymentRecordResponse(
        Long id,
        String tenantName,
        String unitNumber,
        BigDecimal amount,
        LocalDate paymentDate,
        String referenceNumber) { }
```

- A **record** is a short way to write a data class. Java makes the constructor, getters like `tenantName()`, `equals`, `hashCode` and `toString`.
- The request has **no `id`**: the client must not choose it.
- The response has `id` so the client can use `/api/payment-records/{id}`.
- Neither contains anything about passwords or users.

Validation annotations (`@NotBlank` and so on) are added by **Member 6** through a pull request into your branch. Leave your records plain at first. Agree the final annotations with Member 6 so you both understand them.

The conversion between entity and DTO (the "mapper") lives in **Member 3's service** (Chapter 4.6). You explain it; you do not write it.

### 3.3 The repository (Chapter 4.4, Code 4.7)

```java
public interface PaymentRecordRepository extends JpaRepository<RentPaymentRecord, Long> {

    List<RentPaymentRecord> findByUnitNumber(String unitNumber);   // the required derived filter

    boolean existsByReferenceNumber(String referenceNumber);       // used on create

    boolean existsByReferenceNumberAndIdNot(String referenceNumber, Long id); // used on update
}
```

- `JpaRepository<RentPaymentRecord, Long>`: handles this entity; the ID type is `Long`. You get `save`, `findById`, `findAll`, `deleteById`, `existsById`, `count` free.
- **Derived query:** Spring reads the method name and writes the SQL. `findByUnitNumber` becomes `SELECT ... WHERE unit_number = ?`. The name after `findBy` must match the entity field exactly (`unitNumber`).
- `existsByReferenceNumberAndIdNot(ref, id)`: "is there a record with this reference whose ID is **not** this one?" This lets Member 3 allow a record to keep its own reference when updated.
- `findById` returns an `Optional`. Member 3 must handle the empty case (404).

### 3.4 Which Chapter 4 concepts you show

4.3 DTOs and records. 4.4 entity, `@Id`, `JpaRepository`, derived query. Also 4.1 beans: Spring creates your repository automatically at startup.

### 3.5 Common mistakes

| Mistake | Result |
|---|---|
| `javax.persistence` imports | Compile errors under Boot 3 |
| No no-arg constructor | Startup error |
| Field name typo in the derived method (`findByUnit`) | App fails to start with "No property" |
| Returning the entity from a controller | Breaks the DTO rule (Member 4 and 3 must not do it) |
| Using `double` for amount | Rounding errors |
| Classes outside `com.tenant.app` | Spring does not find them |

## 4. Why your contribution matters

Rubric: Chapter 4 implementation (25 points) depends on correct entity, DTO and repository. Check 10 (JPA stored it) depends on your mapping.

## 5. How it connects

```
Controller (M4) ⇄ DTOs (you) ⇄ Service (M3) ⇄ Entity + Repository (you) ⇄ H2
```

## 6. Software and setup

Same as Member 1: Java 17, Maven, Git, GitHub Desktop, VS Code or IntelliJ, Postman, Claude. You need Member 1's skeleton merged into `develop` first. Then in GitHub Desktop: switch to your branch and **Branch → Update from develop**.

## 7. Files you own

- `src/main/java/com/tenant/app/entity/RentPaymentRecord.java`
- `src/main/java/com/tenant/app/dto/PaymentRecordRequest.java`
- `src/main/java/com/tenant/app/dto/PaymentRecordResponse.java`
- `src/main/java/com/tenant/app/repository/PaymentRecordRepository.java`
- `src/test/java/com/tenant/app/repository/PaymentRecordRepositoryTest.java` (optional)

## 8. Files you must not edit without coordination

`AppUser` and `AppUserRepository` (Member 5). Anything in `service`, `controller`, `config`, `exception`, `static`. For validation annotations on your DTOs, accept Member 6's PR.

## 9. Step-by-step: vibe coding with Claude

1. Ask Claude to explain "entity, DTO, repository" in beginner language.
2. Ask it to inspect the project and confirm the skeleton exists.
3. Ask for the plan: files, order, how to test.
4. Do one phase at a time (below).
5. Run it. Paste the real console output.
6. Fix real errors with Claude.
7. Answer its questions. If stuck, ask for a simpler explanation.
8. Commit, push, PR.

## 10. Your master Claude prompt

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 2: the `RentPaymentRecord` entity, the request and response DTO records, and `PaymentRecordRepository` with the `findByUnitNumber` derived query.
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-02.md` and inspect the project structure.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps; give a test I can actually run; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> Use `jakarta.*` imports, `BigDecimal` for money, no `id` in the request DTO, and records for DTOs. Do not touch other members' files. Do not add dependencies or features outside the scope.
>
> Never claim code compiles or works unless I show you the real output. My goal is not only to finish the code but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Test (you run it) |
|---|---|---|
| 1 | Create `RentPaymentRecord` with fields, constructors, getters and setters | `mvn compile` finishes with BUILD SUCCESS |
| 2 | Start the app | In the console you see Hibernate create table `rent_payment_record` (needs `show-sql=true`) |
| 3 | Create the two DTO records | `mvn compile` succeeds |
| 4 | Create `PaymentRecordRepository` with the three methods | App still starts (a wrong method name would stop it) |
| 5 | Optional: `@DataJpaTest` that saves a record and calls `findByUnitNumber` | `mvn test` shows the test passing |
| 6 | Tell Member 3 and Member 6 what you built; open PR | Reviewer can compile your branch |

Optional test idea (not yet run):

```java
@DataJpaTest
class PaymentRecordRepositoryTest {
    @Autowired PaymentRecordRepository repo;

    @Test
    void findsByUnit() {
        repo.save(new RentPaymentRecord("Demo Tenant", "3B", new BigDecimal("1000.00"),
                LocalDate.of(2026, 11, 1), "REF-T1"));
        assertEquals(1, repo.findByUnitNumber("3B").size());
        assertEquals(0, repo.findByUnitNumber("9Z").size());
    }
}
```

(Using `@Autowired` in a test is fine. The no-field-injection rule is for application classes.)

## 12. How to run and test

`mvn compile`, then `mvn spring-boot:run`, then `mvn test`. Read the console: you want "Started TenantApplication" and no red errors. Without a controller you cannot call the data from a browser yet; the repository test (or Member 3's seeder) proves it works.

## 13. Debugging with Claude

Paste the **first** error line and the lines under it, not just the last one.

| Error text | Likely cause |
|---|---|
| `package javax.persistence does not exist` | Wrong imports; use `jakarta` |
| `No default constructor for entity` | Missing `protected` no-arg constructor |
| `Could not create query ... No property 'xxx' found` | Method name does not match a field |
| `Not a managed type` | Entity is outside `com.tenant.app` |
| `cannot find symbol` | Missing import, or a typo |

## 14. Create your Git branch

Branch `feature/member-02-data-model` is already published. **Current Branch** → select it. Then **Branch → Update from develop**.

## 15. Commit and push

One commit per file or small idea. Summary example: `Add RentPaymentRecord entity`. **Commit to feature/member-02-data-model**, then **Push origin**.

## 16. Create a pull request

**Branch → Create pull request**, base `develop`, fill the template in `GITHUB_WORKFLOW.md`, and paste your real test result. Move T-05 to T-07 to Review.

## 17. Respond to code review

Read, fix, commit, push, reply to each comment. Do not merge your own PR.

## 18. Defense questions you must answer

1. What does `@Entity` do? What does `@Id` do? What does `@GeneratedValue` do?
2. Why does JPA need a no-argument constructor?
3. Why is the ID not in `PaymentRecordRequest`?
4. What is a Java record? What does Java generate for you?
5. Why separate DTOs from the entity? Give two reasons. (4.9 Q4)
6. Write a method that finds all records for a unit. Why does the name matter? (4.9 Q5)
7. What does `existsByReferenceNumberAndIdNot` mean in plain words? Why is it needed for update?
8. What does `findById` return and what must the caller do?
9. What is the difference between `unique = true` and the service's duplicate check?
10. What did Claude suggest that you changed, and how did you test it?

## 19. Completion checklist

- [ ] Entity compiles; table appears in the Hibernate log (I ran it)
- [ ] `jakarta.*` imports only; `BigDecimal` for amount
- [ ] Two DTO records, no `id` in request, no secrets anywhere
- [ ] Repository has the three methods; app starts
- [ ] Optional repository test passes (I ran `mvn test`)
- [ ] Members 3 and 6 told about setters and DTO fields
- [ ] PR opened with real test evidence; review answered
- [ ] I can answer all ten defense questions
