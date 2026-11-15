# MEMBER-05: Security and Authentication

Branch: `feature/member-05-security`. Issues: T-14 to T-17.

Read `CLAUDE.md` first. All code below is a **plan or example**. Nothing has been compiled or run. The CSRF part in particular is **unverified**: you must test it and report what really happens.

---

## 1. Your role and mission

You protect the API. You decide who may enter (**authentication**) and what they may do (**authorization**): both demo roles may read, create and update; only ADMIN may delete. You also make the demo accounts, keep CSRF protection on, and find a documented way for clients to send a valid CSRF token.

## 2. Level 1: beginner explanation

**What am I building?** A security guard at the building entrance.

- **Authentication** is the guard checking your ID badge: "Who are you?" Wrong or no badge: 401.
- **Authorization** is the guard checking your badge level: "Are you allowed on this floor?" Staff badge on the manager's floor: 403.
- **CSRF** is a second pass the guard requires for any action that **changes** things. It stops another website from secretly making your browser do something. You must show the pass each time you add, change or delete.

**Why does it exist?** Without it, anyone who knows the URL can read or delete every record.

**What happens when someone uses it?** Every request passes the guard first. Only after the guard is satisfied does the controller run.

**If it did not exist?** Anyone could delete payment records. The SD1 guide requires roles and hashed passwords.

## 3. Level 2: technical explanation

### 3.1 Add the starter (T-15 start)

Member 1's skeleton has **no** security starter, on purpose. Tell Member 1, then add to `pom.xml`:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
```

The moment the starter exists, **every** endpoint is locked (Chapter 4.5) until your configuration is in place. Do not merge a half-finished version.

### 3.2 The user table (T-14)

```java
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;     // BCrypt hash, never the real password

    @Column(nullable = false, length = 20)
    private String role;             // "USER" or "ADMIN" (no ROLE_ prefix)

    protected AppUser() { }
    public AppUser(String username, String passwordHash, String role) { /* assign fields */ }
    // getters
}

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);     // derived query
}
```

This entity has **no relationship** to payment records. It is never put in an API response.

### 3.3 Loading users from the database (Chapter 4.5, Code 4.10)

```java
@Service
public class DbUserDetailsService implements UserDetailsService {
    private final AppUserRepository repo;
    public DbUserDetailsService(AppUserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())     // already hashed
                .roles(u.getRole())                // "ADMIN" becomes authority ROLE_ADMIN
                .build();
    }
}
```

Spring Security finds this bean and uses it by itself. `User` is `org.springframework.security.core.userdetails.User`, not your entity.

### 3.4 The security rules (Chapter 4.5, Code 4.9)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")   // specific first
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated())                                   // everything else
            .httpBasic(Customizer.withDefaults());
        // CSRF: left ENABLED (do not call csrf().disable()). Token delivery: see 3.6.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- **Rule order matters.** The first matching rule wins, so specific rules go first and `anyRequest()` goes last.
- The chapter's example also permits `/api/auth/**`. TENANT has no login endpoint, so that line is removed.
- **Added by us (the chapter is silent):** the static frontend files are made public, because the sign-in page must load **before** anyone can sign in. The API itself (`/api/**`) stays protected.
- `hasRole("ADMIN")` checks the authority `ROLE_ADMIN`. `.roles("ADMIN")` in 3.3 adds the prefix for you.
- `BCryptPasswordEncoder`: a salted, one-way hash. It slows down guessing but does not make weak passwords safe (4.9 Q7).

### 3.5 Demo accounts (T-16)

```java
@Component
public class DemoAccountSeeder implements CommandLineRunner {
    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public DemoAccountSeeder(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repo.findByUsername("staff").isEmpty())
            repo.save(new AppUser("staff", encoder.encode("staff123"), "USER"));
        if (repo.findByUsername("admin").isEmpty())
            repo.save(new AppUser("admin", encoder.encode("admin123"), "ADMIN"));
    }
}
```

The passwords are encoded **when the app starts**, so no hash and no real password sits in the database dump or in config. Credentials are demo-only (`staff/staff123`, `admin/admin123`) and appear in `README.md`. H2 is in memory, so the accounts are recreated at every start.

### 3.6 CSRF (T-17): the part the chapter does not explain

The chapter says: keep CSRF **enabled**; POST, PUT and DELETE need a valid token; an ADMIN role does **not** bypass it; a missing or invalid token can also give 403.

It does **not** say how a client gets the token. Two options, both **untested**. Time-box Option A to about one hour. If it is not working, switch to Option B.

**Option A (planned first): cookie token.** The server puts the token in a cookie named `XSRF-TOKEN` that JavaScript can read, and the client sends it back in the header `X-XSRF-TOKEN`. Typical shape from Spring's documentation (verify against the docs for your exact version):

```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
// plus a small OncePerRequestFilter, added after BasicAuthenticationFilter,
// that reads the CsrfToken request attribute and calls getToken() so the cookie is actually written.
```

Why the extra filter: Spring Security 6 loads the token lazily, so the cookie may never be written unless something asks for the token.

**Option B (fallback): token endpoint.** A tiny `GET /api/csrf` returns the token for the logged-in session, and the client sends it in the header name the endpoint reports. Session cookies must be kept by the client (Postman and browsers do this automatically; curl needs a cookie jar).

How to find out what is true: ask Claude to read the official Spring Security CSRF reference for the version in your `pom.xml` (the chapter links "Spring Security: CSRF"). Then **run it** and record what really happened.

**Facts to prove with your own test, then document:**

| Test | Expected | Record real result |
|---|---|---|
| POST with valid login and **no** token | 403 | |
| POST with valid login and valid token | 201 | |
| DELETE as `staff` with valid token | 403 (role) | |
| DELETE as `admin` with valid token | 204 | |
| DELETE as `admin` with **no** token | 403 (CSRF, not role) | |

Write the final working steps (for Postman and for the browser) in `docs/API_TESTING_GUIDE.md` together with Member 6. Do not write steps you have not run.

### 3.7 Order of failures (useful to know)

The CSRF check happens early in the filter chain. A POST or DELETE with **no token and no login** can return **403 (CSRF)** instead of 401. Check 8 therefore uses **GET**, which is not CSRF protected: no credentials or a wrong password gives **401**.

### 3.8 `GET /api/me` (optional, approved)

```java
public record MeResponse(String username, String role) { }

@RestController
public class MeController {
    @GetMapping("/api/me")
    public MeResponse me(Authentication auth) {
        String role = auth.getAuthorities().stream().findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", "")).orElse("NONE");
        return new MeResponse(auth.getName(), role);
    }
}
```

It returns only the username and role, never a hash. Member 1 uses it to hide Delete for USER. The server still enforces the rule. If this causes any trouble, drop it; it is not graded.

### 3.9 Chapter 4 concepts you show

4.5 authentication (401), authorization (403), `SecurityFilterChain`, `UserDetailsService`, BCrypt, HTTP Basic, CSRF. 4.4 `AppUser` entity and `findByUsername` derived query. 4.1 beans and constructor injection.

### 3.10 Common mistakes

| Mistake | Result |
|---|---|
| `csrf.disable()` "just to test" | Breaks the SD1 requirement and may be forgotten |
| Putting `anyRequest()` first | All later rules ignored |
| Storing plain passwords | Fails the hashing requirement |
| Hiding the Delete button and calling it done | Not security (4.5 COMMON MISTAKE) |
| Role stored as `ROLE_ADMIN` then `.roles(...)` | Becomes `ROLE_ROLE_ADMIN`; delete never allowed |
| Returning `AppUser` from any endpoint | Leaks the hash |
| Testing DELETE as USER **without** a CSRF token | 403 for the wrong reason |

## 4. Why your contribution matters

Security, validation and the business rule together are 20 points. Checks 8 and 9 depend on you, and the project's honesty about CSRF depends on your real test results.

## 5. How it connects

```
Request ► SECURITY FILTER CHAIN (you): login? CSRF token? role? ► Controller ► Service ► Repository ► H2
                  ▲ users loaded from app_user (you) via DbUserDetailsService
```

## 6. Software and setup

Java 17, Maven, Git, GitHub Desktop, VS Code or IntelliJ, Postman, Claude. You need the skeleton, data model, service and controller merged first, so you can see a working open API before you lock it. Then **Branch → Update from develop**.

## 7. Files you own

- `src/main/java/com/tenant/app/entity/AppUser.java`
- `src/main/java/com/tenant/app/repository/AppUserRepository.java`
- `src/main/java/com/tenant/app/config/SecurityConfig.java`
- `src/main/java/com/tenant/app/config/DbUserDetailsService.java`
- `src/main/java/com/tenant/app/config/DemoAccountSeeder.java`
- `src/main/java/com/tenant/app/config/MeController.java` (with `MeResponse`)
- A CSRF filter or endpoint class, in `config`, if needed
- `src/test/java/com/tenant/app/config/SecurityTest.java` (optional)

## 8. Files you must not edit without coordination

`pom.xml` (Member 1: tell them before adding the starter), controller, service, entity and repository for payment records, `static/**`. If the frontend needs a header or flow, tell Member 1.

## 9. Step-by-step: vibe coding with Claude

1. Ask Claude to explain authentication vs authorization vs CSRF with analogies.
2. Ask it to inspect the project and confirm the API works open first.
3. Ask for a plan, one piece at a time (below).
4. Run each piece. Paste the real status codes.
5. For CSRF, tell Claude exactly what you saw and ask it to check the Spring docs for your version.
6. Debug real errors only.
7. Answer understanding questions.
8. Commit, push, PR.

## 10. Your master Claude prompt

> You are my beginner-friendly programming mentor for the TENANT SD1 project.
>
> I am Member 5: Spring Security with HTTP Basic, database-backed users (`AppUser`, `AppUserRepository`, `DbUserDetailsService`), BCrypt demo accounts (`staff` as USER, `admin` as ADMIN), ADMIN-only DELETE, CSRF kept enabled, and the optional `GET /api/me`.
>
> Before coding, read `CLAUDE.md` and `member-guides/MEMBER-05.md` and inspect the project structure.
>
> Teach me what I am building in simple language first. Then explain the technical concept and how it fits into TENANT.
>
> Do not generate everything at once. Divide my work into small phases. For every phase: explain the purpose, the files involved, and the plan; generate only the code needed for this phase; explain the important parts; give exact steps; give a test I can actually run; wait for my real result; help me debug the real error; then ask me 2 or 3 questions to check my understanding.
>
> Follow Chapter 4 (Codes 4.9 and 4.10). Never disable CSRF. Never store plain-text passwords or expose hashes. Check the official Spring Security documentation for my Spring Boot version before suggesting any CSRF code, and tell me when something is unverified. Do not touch other members' files. Do not add features outside the scope.
>
> Never claim code works unless I show you the real response. My goal is not only to finish the code but to understand and defend it.

## 11. Implementation phases

| Phase | Task | Test (you run it) |
|---|---|---|
| 1 | `AppUser` and `AppUserRepository` | App starts; table `app_user` appears in the SQL log |
| 2 | Add the security starter (tell Member 1) | App starts; `GET /api/payment-records` now returns **401**, proving the lock works |
| 3 | `DbUserDetailsService`, `SecurityConfig` (rules and BCrypt bean), `DemoAccountSeeder` | `GET` with Basic auth `staff/staff123` returns 200; wrong password returns 401 |
| 4 | Role rule | Delete as `staff` returns 403 (needs the CSRF step to be sure why); as `admin` returns 204 |
| 5 | CSRF Option A (time-boxed), else Option B | Table in 3.6 filled with real results |
| 6 | `GET /api/me` | Returns `{username, role}` and no hash |
| 7 | Optional security tests with `spring-security-test` | `mvn test` output recorded |
| 8 | Hand the CSRF steps to Members 1 and 6; PR | Reviewer repeats your table |

Optional test idea (not yet run). It uses `spring-security-test` helpers for Basic auth and a CSRF token:

```java
mockMvc.perform(delete("/api/payment-records/1")
        .with(httpBasic("staff", "staff123"))
        .with(csrf()))
    .andExpect(status().isForbidden());     // role denied, token valid
```

## 12. How to run and test

`mvn spring-boot:run`. In Postman: Authorization tab → Basic Auth, enter username and password. For POST, PUT and DELETE, add the CSRF header using the method you proved works. Keep a screenshot of every row in the 3.6 table.

## 13. Debugging with Claude

| Symptom | Likely cause |
|---|---|
| Everything is 401 even with the right password | Seeder did not run, or username/password typo; check the `app_user` log |
| Console prints "Using generated security password" | Your `UserDetailsService` bean was not found (is it `@Service` and under `com.tenant.app`?) |
| 403 on every POST | CSRF token missing or wrong |
| `staff` can delete | Rule order wrong, or `.roles(...)` stored with `ROLE_` twice |
| `admin` delete gives 403 with a valid login | CSRF token problem, not role |
| `There is no PasswordEncoder mapped for the id "null"` | Password in the table is not a BCrypt hash |
| Browser shows its own login box | Browser got a 401 with `WWW-Authenticate`; talk to Member 1 about `X-Requested-With` |
| Cannot load the page at `/` | Static file paths not permitted |

## 14. Create your Git branch

`feature/member-05-security` already exists. **Current Branch** → select it → **Branch → Update from develop**.

## 15. Commit and push

Example: `Add AppUser entity and repository`. Keep CSRF commits separate so they are easy to review. **Commit to feature/member-05-security**, then **Push origin**.

## 16. Create a pull request

**Branch → Create pull request**, base `develop`. Paste the filled 3.6 table with **real** results. Move T-14 to T-17 to Review.

## 17. Respond to code review

Fix, commit, push, reply to each comment. Never merge your own PR.

## 18. Defense questions you must answer

1. Authentication vs authorization: what does each answer? Which status code for each? (4.5)
2. A USER calls DELETE with a valid CSRF token. What status and why? (4.9 Q6)
3. Why store a BCrypt hash? Does hashing make weak passwords safe? (4.9 Q7)
4. Where do the users come from? What does `DbUserDetailsService` return?
5. Why does rule order matter? Show the order in your config.
6. What does CSRF protect against? Why does the ADMIN role not bypass it?
7. Which CSRF method did you use and what happened when you tested the alternatives?
8. Why is HTTP Basic only acceptable on localhost?
9. Why is hiding the Delete button not enough?
10. Why can a request with no login and no token get 403 instead of 401?
11. Why are the static files public but `/api/**` protected?
12. What did Claude suggest that you changed, and how did you test it?

## 19. Completion checklist

- [ ] `AppUser` and repository; `app_user` table created (I ran it)
- [ ] Passwords stored as BCrypt hashes, created at startup; nothing plain in the repo
- [ ] Unauthenticated GET gives 401; wrong password gives 401 (I ran it)
- [ ] `staff` cannot DELETE (403 with a valid token); `admin` can (204)
- [ ] CSRF still enabled; working token method proven and documented with real results
- [ ] 3.6 table fully filled in with real results
- [ ] `/api/me` returns no hash (or dropped, with a note)
- [ ] Members 1 and 6 know the token steps
- [ ] PR opened and review answered
- [ ] I can answer all twelve defense questions
