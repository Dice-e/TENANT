# PROJECT_SCOPE.md: What TENANT is and is not

This is the short proposal the SD1 guide asks for, kept in the repository so the whole team shares one scope.

## Title and team

- **Title:** TENANT, Tenant Payment Management System
- **Course:** Software Development 1, final project
- **Members:** Member 1 (lead), Members 2 to 6. Real names are not filled in yet, so the lead adds them here later.
- **Six-member note:** the guide suggests groups of 3 to 5 and says to follow the instructor's assignment. Member 1 confirms with the professor that six is allowed. Until then, assume it is.

## Organization and users

- **Organization (fictional):** Sampaguita Apartments, a small rental property with a handful of units. The name is invented and can be changed.
- **Users:**
  - **Staff (role USER):** front-desk or office staff who record and correct payment entries.
  - **Admin (role ADMIN):** the property manager, who can do everything staff can, plus delete records.

## Problem

Rent payment details are kept in scattered notebooks, chat messages and spreadsheets. It is hard to find who paid for which unit, and the same reference can be written twice.

## Intended benefit

One place to add, view, search by unit, correct, and (for admins) remove rent payment records, with one rule that stops duplicate reference numbers.

## Important limitation: records only

TENANT does **not** process payments. The SD1 guide lists payments as not required, so TENANT only keeps a **record** that a staff member typed in.

- It never moves money.
- It never connects to GCash, Maya, a bank or a card gateway.
- It never collects card or e-wallet details.
- It never says a payment was received or successful. The UI says "Record saved".
- The record-details screen is receipt-*style* only. It carries the note "Not an official receipt. Demo record only."

## Main record

**RentPaymentRecord** (table `rent_payment_record`):

| Field | Type | Meaning |
|---|---|---|
| id | Long, generated | Database-created identifier |
| tenantName | text, required | Tenant's name |
| unitNumber | text, required | Unit, for example `3B` |
| amount | decimal, must be positive | Amount that was recorded |
| paymentDate | date, required | Date written on the record |
| referenceNumber | text, required, **unique** | The unique business value |

**AppUser** (table `app_user`): id, username, passwordHash, role. It exists only for login, is seeded with two demo accounts, and is never returned by the API.

## Business rule and filter

- **Rule:** `referenceNumber` must be unique. It is checked in the service on create and update. A record keeping its own reference on update is valid.
- **Filter:** by `unitNumber`, using the derived repository query `findByUnitNumber`.

## Roles

| Action | USER | ADMIN |
|---|---|---|
| List, view, filter | Yes | Yes |
| Create | Yes | Yes |
| Update | Yes | Yes |
| Delete | No (403) | Yes |

No registration or account-management screen.

## Included

- REST API with five operations and the unit filter.
- H2 database, DTOs (with records), layered code.
- HTTP Basic login, BCrypt, USER and ADMIN, CSRF enabled.
- Bean Validation and a global exception handler.
- A plain HTML/CSS/JS interface in Palette 2, working on desktop and mobile.
- Ten executed checks, a Postman collection, a README, a 4 to 6 page write-up, and an AI-use note.

## Excluded

- Any real or simulated payment processing and any payment-method selection.
- Dashboards, reports, charts, pagination, audit trails, approval workflows.
- Registration, password reset, account management, JWT, custom production login.
- Deployment, backup and restore, performance tests, hardware integration.
- Relationships between business tables, a second business entity, rent/water/electricity breakdowns, "due" or "overdue" tracking.
- A database migration tool. No H2 console.

## Demo data

- Five to eight invented sample records, clearly labeled as demo data.
- Two demo accounts, `staff` (USER) and `admin` (ADMIN), with demo-only passwords. The passwords are set at startup by the account seeding code (Phase 5) and listed in `README.md`.
- H2 is in-memory, so **all data is lost when the app stops**. Sample records and accounts are recreated on each start by the seeding code.

## Schedule (team target: about 10 days)

The guide allows 3 to 4 weeks. You asked to finish as soon as possible, so this compressed plan keeps the same scope. It is a team target, not a course rule.

| Days | Focus | Done when |
|---|---|---|
| 1 | Docs, GitHub setup, Spring Boot skeleton starts | App starts; repo shared with all six |
| 2 to 3 | Entity, DTOs, repository, service, controller (basic create/read) | Create and fetch work against H2 |
| 4 to 5 | Full CRUD, unit filter, unique rule, validation, error handler | Checks 1 to 7 pass |
| 5 to 6 | Security: users, BCrypt, roles, CSRF | Checks 8 and 9 pass |
| 6 to 8 | Frontend on the real API (starts once backend and security work) | Desktop and mobile verified |
| 8 to 9 | Run all ten checks, fix defects, write-up, AI-use note | Evidence complete |
| 10 | Rehearse the demo; each member traces a request | Ready to submit |

## Scope control rule

If an idea is not listed under **Included**, it is out. Ask Member 1 before starting it.
