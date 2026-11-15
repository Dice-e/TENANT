# DEFENSE_GUIDE.md: Preparing the final demonstration

The demonstration is about **10 to 15 minutes per group**. Every member must explain a contribution and trace a request through the code. A slide deck is optional; the live app and the write-up are enough.

## 1. The most important skill: trace one request

Everyone must be able to say this without notes, using their own files:

```
HTTP request → Security filter → Controller → Service → Repository → Database
                                   ↑ DTO in                         ↓ entity row
HTTP response ← JSON (DTO out) ←───────────────────────────────────┘
```

Example script for `POST /api/payment-records`:

1. The client sends JSON, login, and a CSRF token.
2. The security filter checks the login, the CSRF token and the role. Bad login is 401. Bad token or wrong role is 403.
3. Spring finds `PaymentRecordController.create`. `@Valid` checks the request DTO. A failure is 400.
4. The controller calls `PaymentRecordService.create`.
5. The service checks the unique reference. A duplicate throws an exception that becomes 409.
6. The service builds an entity and calls `repository.save`.
7. Hibernate writes a row to H2 and the ID is generated.
8. The service converts the entity to a response DTO. The controller returns 201.

Each member should know which of these steps are theirs and be able to point to the lines of code.

## 2. Suggested running order (about 12 minutes)

| Time | What | Who (suggested) |
|---|---|---|
| 0:00 | Problem, fictional organization, and the "records only, no real payments" limitation | Member 1 |
| 1:00 | Architecture diagram and layers | Member 1 |
| 2:00 | Entity, DTOs and repository: show the table and the filter query | Member 2 |
| 3:00 | Service: the unique rule on create and update, including keeping the own reference | Member 3 |
| 4:30 | Live: list, get, create, update, filter, delete in the UI and in Postman | Member 4 |
| 6:30 | Live: wrong password (401), staff delete (403 with valid token), admin delete (204), CSRF explained | Member 5 |
| 8:30 | Live: invalid input (400), missing id (404), duplicate (409); ten-check evidence | Member 6 |
| 10:30 | Frontend on desktop and phone width | Member 1 |
| 11:30 | Limitations and AI-use note | Member 6 or Member 1 |
| 12:00 | Questions: any member may be asked about any part | All |

Adjust to what the professor asks. Practice with a timer.

## 3. Demo script (rehearse until it is smooth)

Start from a **fresh app start** so the data is known.

1. Sign in as `staff`. List records.
2. Filter by one unit.
3. Add a record. Show "Record saved".
4. Edit it without changing the reference: works.
5. Try to add a record with the same reference: 409 message.
6. Submit an empty name: 400 field message.
7. Request a record that does not exist: 404 message.
8. As `staff`, try to delete: 403. As `admin`, delete: works.
9. Show a request with a wrong password: 401.
10. Show the database proof: create then fetch separately; point out the SQL `insert` in the console.

## 4. The ten Chapter 4 review questions (know these)

Answers come from Chapter 4.

| No. | Question | Answer |
|---|---|---|
| 1 | Which annotation combines `@SpringBootConfiguration`, `@EnableAutoConfiguration`, `@ComponentScan`? | `@SpringBootApplication` |
| 2 | Which annotation reads `course` from `?course=CpE`? Which reads `id` from `/students/5`? | `@RequestParam`; `@PathVariable` |
| 3 | Status for a successful POST that creates something? | 201 Created |
| 4 | Two reasons to use DTOs instead of exposing an entity | For example: never expose sensitive fields; the client must not set server-controlled fields like the id; the API stays stable when the table changes; create and show need different shapes |
| 5 | Repository method to find students by course | `List<Student> findByCourse(String course);` (for TENANT: `findByUnitNumber`) |
| 6 | Authenticated user lacks ADMIN for DELETE: which status? | 403 (other checks passing) |
| 7 | Why BCrypt, and does it make weak passwords safe? | It is a salted, one-way hash, so the real password is not stored and guessing is slowed. Weak passwords can still be guessed. |
| 8 | Which layer checks for a duplicate? What chooses the HTTP status? | Service layer; the controller (and the exception handler for exceptions) |
| 9 | What if `@Valid` is omitted? | The DTO constraints are not automatically checked by that controller method |
| 10 | Why constructor injection instead of `new`? | Spring supplies managed beans; `new` objects are not managed so nothing is injected. Constructor injection makes dependencies final, visible and easy to supply in tests. |

## 5. Questions every member may be asked

- What does your code do and why is it needed?
- Which Chapter 4 concept does it show?
- How does data travel through the layers? Where does an entity become a DTO?
- Why did validation or security accept or reject this request? What does that status code mean?
- What did Claude suggest that you changed or corrected? How did you test it?
- What happens if I change this one small thing in your code? (You may be asked to explain the effect of a small change.)

Each member's own guide has a list of role-specific questions in section 18.

## 6. Member explanation template (about 1 minute each)

> "I am Member __. I built __. It is needed because __. The request comes in as __ and leaves as __. It shows Chapter 4.__ because __. Claude helped me with __; I changed __; I tested it by __ and saw __. A limitation is __."

## 7. Say these limitations clearly

- TENANT keeps payment **records** only. It does not process payments.
- H2 is in memory, so data is lost when the app stops. Sample data and demo accounts are recreated at start.
- HTTP Basic is used on localhost only; Base64 is encoding, not encryption, so HTTPS would be needed on a network.
- Credentials are kept in browser memory only, so a refresh requires signing in again.
- No registration, no pagination, no reports (out of scope by design).
- Add anything else the team really found during testing.

## 8. If something goes wrong during the demo

- Stay calm and say what you see. Read the real status code.
- If the app crashes, restart it (you rehearsed a fresh start). Data resets, which is expected.
- If a screen fails, switch to Postman for the same operation.
- Never claim something works if it just failed. Explain the cause if you can.

## 9. Rehearsal checklist

- [ ] The app starts from a clean clone using only the README
- [ ] Full demo script run at least twice, timed
- [ ] Each member explained their part aloud without reading
- [ ] Each member traced a request through all layers
- [ ] Each member answered their guide's defense questions
- [ ] Each member answered the ten Chapter 4 questions
- [ ] Postman collection imports and runs
- [ ] Evidence files are ready to show
- [ ] Laptop charged, browser zoom at 100%, notifications off
- [ ] Rehearsal dates written in `CONTRIBUTION_TRACKER.md` (real dates only)
