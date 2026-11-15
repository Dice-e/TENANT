# CONTRIBUTION_TRACKER.md

This file records **evidence** of each member's work. Fill it in from GitHub (issues, PRs, reviews) and from real test runs. Do not write anything here that you cannot point to.

Status words: `Not started`, `In progress`, `Review`, `Done (verified)`. Use "Done (verified)" only when the PR is merged and the test was actually run.

Last reviewed: (not reviewed yet)

## Tracker

Names are placeholders until the team supplies them.

| Member | Name | Responsibilities | Issues | Status | PRs | Verified files | Tests completed | Docs completed | Defense readiness | Blockers |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | (TBD) | Lead, frontend, integration, docs, reviews | T-01 to T-04 | Not started | none | none | none | none | Not checked | none |
| 2 | (TBD) | Entity, DTOs, repository, filter | T-05 to T-07 | Not started | none | none | none | none | Not checked | none |
| 3 | (TBD) | Service, unique rule, sample data | T-08 to T-10 | Not started | none | none | none | none | Not checked | none |
| 4 | (TBD) | Controllers, endpoint docs | T-11 to T-13 | Not started | none | none | none | none | Not checked | none |
| 5 | (TBD) | Security, accounts, CSRF, `/api/me` | T-14 to T-17 | Not started | none | none | none | none | Not checked | none |
| 6 | (TBD) | Validation, errors, Postman, ten checks | T-18 to T-21 | Not started | none | none | none | none | Not checked | none |

Defense readiness values: `Not checked`, `Needs teaching`, `Can explain with help`, `Ready`.

## What counts as evidence

| Counts | Does not count alone |
|---|---|
| Merged PRs with real test steps | Number of commits |
| Passing checks with saved responses | Messages in the group chat |
| Written docs and guides | Being "present" in meetings |
| Helpful reviews with comments | Code the member cannot explain |
| Debugging notes (problem, cause, fix) | Code generated but never run |

## Review procedure (every 2 to 3 days; weekly if the schedule stretches)

Member 1 leads a 15 minute check-in with all six:

1. Open the board. Each member shows their cards and PR links.
2. Update the table above from what is on GitHub, not from memory.
3. Ask each member one question about their own code (use the questions in their guide). Record the readiness value.
4. Write blockers and who will help.
5. Check that each member has at least one PR or review since the last check. If someone has none, agree on a small next task for them.
6. Set the **Last reviewed** date.

## Check-in log

| Date | Who attended | Notes (real outcomes only) |
|---|---|---|
| | | |

## Ten SD1 checks (filled by Member 6 after running them)

| No. | Check | Status | Evidence file |
|---|---|---|---|
| 1 | Create valid record | Not run | |
| 2 | List, get by ID, filter | Not run | |
| 3 | Update, same reference stays valid | Not run | |
| 4 | Delete as ADMIN, then 404 | Not run | |
| 5 | Invalid input, 400 | Not run | |
| 6 | Nonexistent ID, 404 | Not run | |
| 7 | Duplicate on create and update, 409 | Not run | |
| 8 | No credentials and wrong password, 401 | Not run | |
| 9 | DELETE as USER, 403, record remains | Not run | |
| 10 | Create then fetch separately (JPA stored it) | Not run | |

## Final contribution statement

Written at the end for the write-up. One short paragraph per member describing what they built, based on the table and PRs above. (Not written yet.)
