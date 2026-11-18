# MEMBER-01: Project Lead and Frontend Design

Branch: `feature/member-01-frontend`. Issues: T-01 to T-04.

Read `CLAUDE.md` first. Everything here is a **plan**. Nothing has been run yet, so every test below is for you to run and report honestly.

---

## Taglish: Step-by-step (simulan dito)

Ito ang mabilis na bersyon sa Taglish para sa **Member 1 (Leader at Frontend)**. Nasa ibaba ang buong paliwanag sa English (mga section 1 hanggang 19), at doon din nakasulat ang code na halimbawa. Hindi nagbago ang mga numero ng section.

Paalala: ang code sa guide na ito ay halimbawa pa lang at hindi pa napapatakbo. Ikaw ang magpapatakbo at magte-test, at ikaw ang magsasabi kung ano ang totoong nangyari.

### A. Setup

Tapos mo na ito (clone, branch, at pagpapatakbo ng skeleton). Sa IntelliJ, **File > Open** > `pom.xml` > **Open as Project**. Kung mag-update ng ibang code, **Branch > Update from develop** muna.

### B. Ang gagawin mo

1. Tapos mo na ang T-01 (GitHub setup) at T-02 (skeleton). Ang natitira sa iyo ay T-03 at T-04, at ang pag-review ng PRs ng iba.
2. **T-03:** Gumawa ng folder `src/main/resources/static`. Sa loob, gumawa ng `index.html` at ng folder na `css` na may `tenant.css`.
3. Ilagay sa `tenant.css` ang Palette 2 na kulay bilang CSS variables (nasa section 3.3 ng guide). Sa buong CSS, `var(--...)` lang ang gagamitin, walang direktang hex na kulay sa labas ng `:root`.
4. Gawin ang mga screen: sign in, overview, records, add/edit, at details. Sa unang bersyon, wala pang API.
5. Patakbuhin ang app at buksan ang `http://localhost:8080`. Kapag hindi nagbabago ang itsura, i-refresh gamit ang Ctrl+Shift+R.
6. I-resize ang browser sa mga 375px (phone) at 1280px (desktop). Subukan ding gamitin ang Tab key. Mag-screenshot para sa PR.
7. Isulat sa PR ang contrast numbers ng text at background (gumamit ng contrast checker).
8. **T-04:** Hintayin munang ma-merge sa `develop` ang controller ni Member 4 at ang security ni Member 5. Tapos gawin ang `js/api.js`, `auth.js`, `ui.js`, at `app.js` at ikonekta sa totoong API.
9. Tingnan sa browser DevTools > Application > Local Storage na **walang naka-save na password**.
10. Wording: "Record saved" at "Payment record" lang. Hindi "Payment received", at walang payment method.
11. Mag-review ng PR ng bawat member. Hindi mo irere-review ang sarili mong PR.

### C. Paano gamitin ang Claude

1. Sa Claude, i-upload ang `CLAUDE.md`, ang guide mo (`member-guides/MEMBER-01.md`), at ang SD1 Chapter 4 PDF (ipapadala ni Member 1 sa chat, wala ito sa GitHub). Idagdag ang files ng ibang member na kailangan mo: `pom.xml` at ang controller ni Member 4 (kapag nagkokonekta ka na sa API).
2. I-paste ang **master prompt** (section 10 ng guide na ito).
3. Isa-isang phase lang. Huwag magpa-generate ng lahat nang sabay.
4. Pagkatapos ng bawat phase, patakbuhin at i-test mo. I-paste kay Claude ang **totoong** output o error, hindi ang kuwento mo lang.
5. Sagutin ang mga tanong ni Claude. Kung hindi mo masagot, ipaturo ulit hanggang maintindihan mo.

### D. Kapag tapos na at gumagana

1. Sa GitHub Desktop, tingnan ang **Changes**. Dapat sarili mong files lang ang nandoon. Kung may ibang file, huminto at magtanong kay Member 1.
2. Isulat ang summary (hal. `Add RentPaymentRecord entity`) at i-click ang **Commit to feature/member-01-frontend**.
3. I-click ang **Push origin**.
4. **Branch > Create pull request**. Sa **base**, dapat `develop` (HINDI `main`). Sa compare, `feature/member-01-frontend`.
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

You keep six people moving in the same direction and you build the part users actually see: the web pages. You also create the empty Spring Boot project everyone else fills in, set up GitHub, review pull requests, and keep the README honest.

You must still write real code (the frontend and the skeleton). Coordinating alone does not count as a contribution.

## 2. Level 1: beginner explanation

**What am I building?** Think of a restaurant. The kitchen (backend) cooks the food. The dining room, menu and waiter (frontend) are what the customer sees and touches. You build the dining room: the screens where staff sign in, see payment records, add a record, fix a record, and (if admin) delete one.

**Why does it exist?** The API alone only speaks JSON. Staff need buttons and forms.

**Where does it fit?** Your pages sit on top of Member 4's API. When someone clicks "Save", your JavaScript sends a request to the API and shows the answer.

**What happens when someone uses it?** They sign in, see a list of records, click "Add record", fill a form, click Save. Your page sends the data, then shows "Record saved" or the error message.

**If your part did not exist?** The project still works in Postman, and the SD1 guide says the interface is optional. But the app would be hard to demonstrate, and the project is less useful.

**Your other job.** The skeleton: one empty Spring Boot project that starts. Without it nobody can begin.

## 3. Level 2: technical explanation

### 3.1 The skeleton (T-02)

Spring Boot projects are generated at **start.spring.io** (Spring Initializr). Chapter 4 (4.1) says to tick the starters you need.

| Setting | Value |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | latest stable 3.x (not a snapshot) |
| Group | `com.tenant` |
| Artifact | `app` |
| Name | `Tenant` (this makes the main class `TenantApplication`) |
| Package name | `com.tenant.app` |
| Packaging | Jar |
| Java | 17 |
| Dependencies | Spring Web, Spring Data JPA, Validation, H2 Database |

**Do not tick Spring Security yet.** The security starter locks every endpoint the moment it exists (Chapter 4.5). If it is in the skeleton, Members 2, 3 and 4 cannot test. Member 5 adds it later and tells you.

`application.properties` (Chapter 4, Code 4.8 style):

```properties
spring.application.name=tenant
spring.datasource.url=jdbc:h2:mem:tenantdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- `jdbc:h2:mem:tenantdb`: an in-memory database named `tenantdb`. Data is lost when the app stops.
- `ddl-auto=update`: Hibernate creates the tables from the entities.
- `show-sql=true`: prints SQL in the console so you can see what JPA does.
- No H2 console (decision in `PROJECT_ARCHITECTURE.md`).

The main class (Chapter 4, Code 4.1):

```java
@SpringBootApplication
public class TenantApplication {
    public static void main(String[] args) {
        SpringApplication.run(TenantApplication.class, args);
    }
}
```

`@SpringBootApplication` = configuration + auto-configuration + component scanning. Component scanning only finds classes in `com.tenant.app` and below. Never put classes outside it.

### 3.2 Static frontend

Spring Boot serves files in `src/main/resources/static/` automatically. `index.html` there appears at `http://localhost:8080/`. No build tool, no second server.

Files you own: `index.html`, `css/tenant.css`, `js/api.js`, `js/auth.js`, `js/ui.js`, `js/app.js`.

### 3.3 Palette 2 as CSS variables

```css
:root {
  --accent:#9E2C44;  --ground:#E2DBCC;  --hero:#0F748D;  --card:#F6F2E9;
  --ink:#231B1D;     --muted:#5E5456;   --line:#D3CBB9;  --field:#BDB5A2;
  --ok-bg:#CFE3E8;   --ok-ink:#0A5568;  --err-bg:#F0D3DA; --err-ink:#7A1F33;
}
body { background:var(--ground); color:var(--ink); font-family:'Inter',system-ui,sans-serif; }
```

Every color in the project uses `var(--name)`. Hardcoded colors elsewhere are a review failure. Contrast: after styling, check each text and background pair against a contrast checker (WCAG AA is 4.5:1 for normal text) and write down the real numbers. Light text on `--accent` and `--hero` must be checked too.

### 3.4 Talking to the API

```javascript
const Api = {
  credentials: null,   // "username:password", kept in memory only. Refresh = sign in again.

  async request(method, path, body) {
    const headers = { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' };
    if (this.credentials) headers['Authorization'] = 'Basic ' + btoa(this.credentials);
    if (body) headers['Content-Type'] = 'application/json';
    // CSRF: header added for POST, PUT, DELETE once Member 5 confirms the method (Phase 5)
    const res = await fetch('/api/payment-records' + path, {
      method, headers, body: body ? JSON.stringify(body) : undefined
    });
    if (res.status === 204) return null;
    const text = await res.text();               // 404 and 409 bodies are plain text, 400 is JSON
    let data = text;
    try { data = text ? JSON.parse(text) : null; } catch (e) { /* keep the plain text */ }
    if (!res.ok) throw { status: res.status, body: data };
    return data;
  }
};
```

- `btoa` Base64-encodes `username:password`. Chapter 4 warns Base64 is encoding, **not** encryption. That is acceptable only because we run on localhost.
- Credentials are never written to `localStorage`, files or the page source.
- `X-Requested-With`: *unverified idea.* It may stop the browser from popping up its own login box on a 401. Test it in Phase 6 and report the real result.
- Error shapes from the API: 400 is a map `{field: message}`, 404 and 409 are plain text, 401 and 403 come from the security filter and may have an empty body. Your code must handle all of them.

### 3.5 Screens to build

| Screen | Content |
|---|---|
| Sign in | Username, password, button. Shows 401 errors in plain words. |
| Overview | Count of records and the most recent record, **computed from the API**, nothing invented. |
| Records | Table (desktop) or cards (mobile), unit filter box, "Add record" button |
| Add / Edit | The five fields, field-level error messages, Save and Cancel |
| Record details | Receipt-style card with the note "Not an official receipt. Demo record only." Edit and (ADMIN) Delete buttons |

Required states: loading, empty ("No records yet"), error, success ("Record saved"), validation messages per field, delete confirmation.

Wording: "Record payment", "Record saved", "Payment record". Never "Pay", "Payment received", or a payment-method choice. The prototype's Pay and Receipt screens are the ones to **change**, not copy.

Responsive: desktop has a sidebar and a table. Mobile has a bottom tab bar and cards. Touch targets at least 44px. Test at about 375px and 1280px wide.

### 3.6 Hiding Delete

If Member 5 provides `GET /api/me`, hide Delete for USER. The server still enforces it. If a USER somehow clicks it, show the 403 message nicely.

## 4. Why your contribution matters

It carries two rubric areas: documentation and demonstration, and individual understanding. It also holds the project together. A broken skeleton or unreviewed PR blocks everyone.

## 5. How it connects to the system

```
Browser ──fetch──► Security filter ► Controller (M4) ► Service (M3) ► Repository (M2) ► H2
   ▲                                                                                 │
   └──────────────────────────── JSON or error ◄─────────────────────────────────────┘
```

## 6. Software and setup

Java 17 JDK, Maven (or the generated Maven wrapper `mvnw`), Git, GitHub Desktop, VS Code with the "Extension Pack for Java" (or IntelliJ Community), a modern browser, Postman, and Claude.

Check Java: open a terminal and run `java -version`. It must say 17 or higher.

## 7. Files you own

`pom.xml`, `src/main/java/com/tenant/app/TenantApplication.java`, `src/main/resources/application.properties`, `src/main/resources/static/**`, `README.md`, `CLAUDE.md`, and the other root docs.

## 8. Files you must not edit without coordination

Everything under `entity/`, `dto/`, `repository/`, `service/`, `controller/`, `config/`, `exception/`, and `postman/`. Review them in PRs instead.

`pom.xml` is yours, but Member 5 will need to add `spring-boot-starter-security`. Agree on it together.

## 9. Step-by-step: vibe coding with Claude

1. **Understand.** Paste your member guide into Claude and ask: "Explain my job in beginner language."
2. **Inspect.** Ask Claude to read the project folder and list what exists.
3. **Plan.** Ask which files change, why, and how you will test it.
4. **Build one phase.** Ask for code for that phase only.
5. **Run it.** Do the exact steps. Copy the *real* output or screenshot.
6. **Debug.** Give Claude the real error text. Do not describe it from memory.
7. **Understand.** Answer Claude's questions. If you cannot, ask it to teach again.
8. **GitHub.** Commit, push, open a PR.

## 10. Your master Claude prompt

Copy this into Claude.

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 1: project lead, Spring Boot skeleton, GitHub setup, and the Palette 2 frontend (plain HTML, CSS and JavaScript served from Spring Boot's static folder).
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-01.md` and inspect the project structure.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps to implement it; give a test I can actually run; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> TENANT is a record keeper, not a payment system: never use wording like "payment received" or add payment methods. Use Palette 2 CSS variables only. Keep credentials in JavaScript memory only. Do not edit other members' files. Do not add dependencies or features outside the scope.
>
> Never claim code works unless I show you it ran. My goal is not only to finish the code but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Done when (you must run it) |
|---|---|---|
| 1 | T-01: GitHub repo, branches, board, invites (see `GITHUB_WORKFLOW.md` Part A) | All six can clone and see their branch |
| 2 | T-02: generate skeleton, add `application.properties` | `mvn spring-boot:run` starts with no errors; `localhost:8080` loads (a 404 page is fine before `index.html` exists) |
| 3 | T-03a: `index.html` shell and `tenant.css` tokens | Page loads, colors correct, Inter font loads |
| 4 | T-03b: responsive layout: sidebar plus bottom tab bar | Looks right at 375px and 1280px |
| 5 | T-04a: `api.js`, sign-in, records list with loading and empty states | List shows real API data (after M2 to M4 merge) |
| 6 | T-04b: add, edit, details screens with field errors | Saving creates and updates a real record |
| 7 | T-04c: delete with confirmation, role-aware button, error display | ADMIN deletes; USER sees a clear 403 message |
| 8 | Integration: README, contrast numbers, phone and desktop screenshots | Everything in the checklist |

## 12. How to run and test

```
mvn spring-boot:run
```

(or `./mvnw spring-boot:run`). Open `http://localhost:8080`. Stop with Ctrl+C. Test the frontend: add, edit, filter, error cases, resize the browser, try the keyboard (Tab and Enter work on every control). Check that nothing is stored in the browser: DevTools → Application → Local Storage should be empty.

## 13. Debugging with Claude

Give Claude: the exact error text, the file name, and what you just changed.

| Symptom | First thing to check |
|---|---|
| App will not start | Java version, then the red lines at the top of the console |
| `Port 8080 was already in use` | Another run is still open; stop it |
| Blank page | Browser DevTools Console tab for red errors |
| CSS not applied | File path in `<link>`, and a hard refresh (Ctrl+Shift+R) |
| Fetch fails | DevTools Network tab: status code and response |
| 401 popup in browser | The `X-Requested-With` idea above; tell Member 5 |
| 403 on save | CSRF token not sent; see `docs/API_TESTING_GUIDE.md` |

## 14. Create your Git branch

The branch `feature/member-01-frontend` is created in Part A of `GITHUB_WORKFLOW.md`. In GitHub Desktop click **Current Branch** and choose it.

## 15. Commit and push

Changes appear on the left. Write a summary such as `Add Palette 2 CSS tokens`, click **Commit to feature/member-01-frontend**, then **Push origin**.

## 16. Create a pull request

**Branch → Create pull request**. Base: `develop`. Fill the template from `GITHUB_WORKFLOW.md`. Move your issue to Review. Another member reviews your PR (never merge your own).

## 17. Respond to code review

Read every comment. Fix, commit, push, reply. Ask the reviewer to look again.

## 18. Defense questions you must answer

1. Which annotation starts a Spring Boot app, and what three things does it combine? (4.1)
2. What does a starter do? Name two we use.
3. Why does `index.html` appear at `localhost:8080` without any controller?
4. How does your JavaScript send credentials, and why is Base64 not security? Why only localhost?
5. Why do we keep credentials in memory and not `localStorage`?
6. Trace a "Save record" click through browser, controller, service, repository and database.
7. Why is hiding the Delete button not real security? (4.5)
8. Why does the UI say "Record saved" and never "Payment received"?
9. How do the CSS variables keep colors consistent? Change one: what happens?
10. What did Claude suggest that you changed or corrected, and how did you test it?

## 19. Completion checklist

- [ ] Skeleton starts; `localhost:8080` loads (I ran it)
- [ ] Repo, branches, board, six collaborators set up
- [ ] 21 starter issues created and assigned
- [ ] Palette 2 variables only; contrast numbers recorded
- [ ] Sign in, list, filter, add, edit, details, delete all work on real API data
- [ ] Loading, empty, error, success, field-error states exist
- [ ] Verified at about 375px and 1280px, with screenshots
- [ ] No payment wording; no stored credentials
- [ ] README and tracker updated from real evidence
- [ ] I reviewed at least one PR from each other member
- [ ] I can answer all ten defense questions
