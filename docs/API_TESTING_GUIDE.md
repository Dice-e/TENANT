# API_TESTING_GUIDE.md: Testing TENANT and running the ten checks

Owner: Member 6, with Member 5 for the CSRF steps.

**Status: template.** Nothing here has been run. Every Result cell is blank on purpose. Section 4 (CSRF) must **not** be filled in until Member 5 has proven a working method.

## 1. Setup

- Start the app (see `SETUP_GUIDE.md`) and wait for `Started TenantApplication`.
- Base URL: `http://localhost:8080`
- For the final evidence run, stop and start the app fresh so the data is in a known state.
- Tool: **Postman** (recommended). curl is an alternative.

| Account | Password | Role |
|---|---|---|
| `staff` | `staff123` | USER |
| `admin` | `admin123` | ADMIN |

## 2. Sending requests

**Postman:** create a request, choose the method and URL. Under **Authorization** choose **Basic Auth** and enter the username and password. For POST and PUT, open **Body → raw → JSON** and paste the JSON. Postman sets `Content-Type` for you when you choose JSON.

**curl** (use quotes carefully; Windows PowerShell and Command Prompt quote JSON differently, so Postman is easier):

```
curl -i -u staff:staff123 http://localhost:8080/api/payment-records
```

`-i` shows the status line, `-u` sends Basic credentials.

Sample body for create or update:

```json
{
  "tenantName": "Demo Tenant X",
  "unitNumber": "4C",
  "amount": 7500.00,
  "paymentDate": "2026-11-01",
  "referenceNumber": "DEMO-0100"
}
```

## 3. What the status codes mean

| Code | Meaning | Where it comes from |
|---|---|---|
| 200 | OK | Controller |
| 201 | Created | Controller (POST) |
| 204 | No Content (deleted) | Controller (DELETE) |
| 400 | Invalid input | `@Valid` plus the exception handler |
| 401 | Not logged in or wrong password | Security filter (before the controller) |
| 403 | Logged in but not allowed, or missing CSRF token | Security filter |
| 404 | Record not found | Service exception plus the handler |
| 409 | Duplicate reference number | Service exception plus the handler |

401 and 403 come from Spring Security **before** the controller, so their bodies do not use the handler's error format.

## 4. CSRF tokens for POST, PUT and DELETE

**TO BE COMPLETED BY MEMBER 5 AND MEMBER 6 AFTER A REAL TEST. Do not guess these steps.**

Why it exists: CSRF protection is kept **enabled** (Chapter 4.5). Every write request needs a valid token, and the ADMIN role does not skip it.

Fill in only the method that was proven to work:

| Item | Result |
|---|---|
| Method chosen (cookie token or token endpoint) | |
| Why this one | |
| Steps for Postman (numbered, exact) | |
| Steps for the browser frontend | |
| Steps for curl (if any) | |
| Date tested and by whom | |
| Result of the five-row table in `MEMBER-05.md` section 3.6 | |

## 5. The ten mandatory checks

For each check record: the request or input, the expected result, the actual result, pass or fail, and a screenshot or saved response in `docs/evidence/`. Use **Not run** until it really ran. The same table is summarized in `docs/evidence/CHECK-RESULTS.md`.

| No. | Do this | Expected |
|---|---|---|
| 1 | POST a valid record as `staff` with a valid CSRF token | 201 and a generated `id` |
| 2 | GET the list; GET the new id; GET `?unitNumber=` with that record's unit | 200 each; filter returns only matching records |
| 3 | PUT the record, change the amount, keep the same reference; then GET it | 200; GET shows the change; no 409 |
| 4 | DELETE as `admin` with a valid CSRF token; then GET that id | 204; then 404 |
| 5 | POST with an empty `tenantName`, a negative `amount`, a missing `paymentDate` | 400 with a message per bad field |
| 6 | GET `/api/payment-records/99999` | 404 with a clear message |
| 7 | POST a duplicate reference; PUT record B using record A's reference; then GET both | 409 both times; the existing data is unchanged |
| 8 | GET with no credentials; GET with `staff` and a wrong password | 401 both times |
| 9 | DELETE as `staff` with a **valid** CSRF token; then GET the record | 403; the record still exists |
| 10 | POST a record; GET it in a separate request; note the console `insert` line | 200 with the same data, showing JPA stored it |

Notes:

- Check 8 uses GET because a POST or DELETE with no token can return 403 before the login is even checked.
- Check 9 must use a valid token so the 403 means "wrong role" and not "missing token". Show that the same request as `admin` returns 204.
- If a check fails, write **Fail**, paste the real response, open an issue for the owner, and re-run after the fix. Keep both runs.

## 6. Result record (copy for each check)

```
Check no.:
Date and tester:
Request / input:
Expected result:
Actual result:
Pass / Fail:
Evidence file (screenshot or saved response):
Notes:
```

## 7. Summary table (fill from real runs only)

| No. | Status (Not run / Pass / Fail) | Evidence file |
|---|---|---|
| 1 | Not run | |
| 2 | Not run | |
| 3 | Not run | |
| 4 | Not run | |
| 5 | Not run | |
| 6 | Not run | |
| 7 | Not run | |
| 8 | Not run | |
| 9 | Not run | |
| 10 | Not run | |

## 8. Postman collection

Saved at `postman/TENANT-API-Collection.json` by Member 6. Import it in Postman with **Import**. It uses a variable `baseUrl` and saves the new record id in `recordId`. It must contain only demo credentials.

## 9. Frontend checks (not part of the ten, but needed)

Member 1 records real results for: sign in, list, filter, add, edit, details, delete (ADMIN), delete (USER shows a clear message), a validation error, and the layout at about 375px and 1280px wide. Screenshots go in `docs/evidence/`.
