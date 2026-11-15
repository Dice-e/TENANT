# GITHUB_WORKFLOW.md: How we work together on GitHub

GitHub is the official record of who did what. This guide assumes you have never used a branch or a pull request. Member 1 (lead) sets up the repository once (Part A). Everyone else starts at Part B.

## Words you need first

| Word | Plain meaning |
|---|---|
| Repository (repo) | The project folder, stored on GitHub and copied to your laptop |
| Clone | Copy the repo from GitHub to your laptop (once) |
| Commit | A saved snapshot of your changes, with a message |
| Branch | Your own copy of the work line, so you do not break others |
| Push | Send your commits from your laptop to GitHub |
| Pull request (PR) | A request: "please review my branch and merge it" |
| Merge | Combine a branch into another branch |
| Issue | A task card: what to do and how we know it is done |

## The workflow in one line

`Backlog → Assigned → In Progress → Review → Done`

| Column | Meaning | Who moves it |
|---|---|---|
| Backlog | Task exists, nobody has taken it | Member 1 |
| Assigned | Has an owner, not started | Member 1 |
| In Progress | Owner is working on the branch | Owner |
| Review | PR open, waiting for a reviewer | Owner |
| Done | PR merged **and** the test was actually run | Reviewer or Member 1 |

## Branches

| Branch | Purpose | Who uses it |
|---|---|---|
| `main` | Stable, demo-ready version | Only Member 1 merges into it |
| `develop` | Where all finished work comes together | Everyone merges PRs here |
| `feature/member-01-frontend` | Member 1's work | Member 1 |
| `feature/member-02-data-model` | Member 2's work | Member 2 |
| `feature/member-03-service` | Member 3's work | Member 3 |
| `feature/member-04-api` | Member 4's work | Member 4 |
| `feature/member-05-security` | Member 5's work | Member 5 |
| `feature/member-06-validation-qa` | Member 6's work | Member 6 |

Rules: never commit directly to `main` or `develop`. Each person works only on their own feature branch. PRs go **into `develop`**.

**Why this simple setup:** one stable branch, one shared branch, one branch each. More branches would only confuse beginners.

## Part A: Member 1 sets up the repository (Phase 2)

1. On github.com, click **New repository**. Name it `TENANT`. Choose Public or Private. Do not add a README (we already have one).
2. Open GitHub Desktop. **File → Add local repository**, choose the `TENANT` folder. If it says it is not a Git repository, click **create a repository** there.
3. Check the `.gitignore` file is present (see below). Make sure `_journal/` is listed so it is not uploaded.
4. In GitHub Desktop, write the summary `Initial project documents` and click **Commit to main**.
5. Click **Publish repository** and select your TENANT repo on GitHub.
6. Create `develop`: **Branch → New branch**, name `develop`, based on `main`, then **Publish branch**.
7. Create each feature branch: **Branch → New branch**, based on `develop`, then **Publish branch**. Do this six times with the names in the table above.
8. Invite the others: on github.com open the repo, **Settings → Collaborators → Add people**, enter each member's GitHub username. Each member must accept the email invite.
9. Protect the branches (when available): **Settings → Branches** (or **Rules**), add a rule for `main` and `develop`: require a pull request before merging, with 1 approval. On a private repo with a free plan this feature may be unavailable. If so, follow the rule above by agreement and note it in the tracker.
10. Make the board: **Projects → New project → Board**. Set the Status options to Backlog, Assigned, In Progress, Review, Done.
11. Create the issues from the list below. Assign each one and put it in the right column.
12. Review the first PR together as a team so everyone sees how it works.

## Part B: Every member, one time

1. Create a free GitHub account and send your username to Member 1.
2. Accept the invitation email.
3. Install **GitHub Desktop** and sign in.
4. **File → Clone repository**, pick `TENANT`, choose a folder, click **Clone**.
5. Click **Current Branch** at the top, find your own feature branch, and click it. If it is not in the list, click **Fetch origin** first.

## Part C: Your daily routine (every task)

1. **Fetch and pull.** Click **Fetch origin**, then **Pull origin** if it shows. Do this before you start.
2. **Update from develop.** **Branch → Update from develop** so you have the latest shared work.
3. **Work** on your files only. Save often. Run and test (see your member guide).
4. **Look at your changes** in the left panel. Only your own files should appear. If you see someone else's file, stop and ask.
5. **Commit.** Type a summary like `Add RentPaymentRecord entity` and click **Commit to feature/member-0X-...**. Make small commits, one idea each.
6. **Push.** Click **Push origin**.
7. **Open a PR.** Click **Create Pull Request** (or **Branch → Create pull request**). Set base to `develop` and compare to your branch. Fill the template below.
8. **Move your issue** to Review on the board and ask a reviewer in the group chat.
9. **Respond to review.** Fix what is asked, commit, push again. The PR updates by itself. Reply to each comment. Do not delete comments.
10. After the merge, click **Fetch origin**, switch to your branch, and **Update from develop** before the next task.

## Pull request template (copy into every PR)

```
## What this PR does
(1 to 2 sentences)

## Issue
Closes #__

## Files changed
(list them)

## How I tested it
(exact steps you ran and the real result you saw)

## Chapter 4 concept this shows
(for example: 4.4 JPA repository)

## AI help used
(what Claude helped with, and what I checked or changed)

## I can explain this code: yes / no
```

## Issue template

```
Title: [Member X] short task name
Assigned to: Member X
Description: what and why
Expected output: what exists when done
Files: list
Acceptance criteria: checklist of things that must be true
Test steps: exact steps and expected result
PR link: (added when opened)
Review result: (approved / changes requested, by whom)
```

## Starter issues (Member 1 creates these in Phase 2)

| ID | Owner | Title |
|---|---|---|
| T-01 | 1 | Create repo, branches, board, .gitignore |
| T-02 | 1 | Spring Boot skeleton starts (pom.xml, main class, application.properties) |
| T-03 | 1 | Frontend shell, Palette 2 tokens, responsive layout |
| T-04 | 1 | Connect frontend to API (list, create, edit, delete, errors) |
| T-05 | 2 | RentPaymentRecord entity |
| T-06 | 2 | Request and response DTO records |
| T-07 | 2 | PaymentRecordRepository with findByUnitNumber |
| T-08 | 3 | PaymentRecordService CRUD and entity-DTO mapping |
| T-09 | 3 | Unique reference rule on create and update |
| T-10 | 3 | Sample record seeding |
| T-11 | 4 | Controller: GET list with filter, GET by id |
| T-12 | 4 | Controller: POST, PUT, DELETE with status codes |
| T-13 | 4 | Endpoint documentation with examples |
| T-14 | 5 | AppUser entity and repository |
| T-15 | 5 | SecurityConfig, HTTP Basic, roles, DbUserDetailsService |
| T-16 | 5 | Demo account seeding with BCrypt |
| T-17 | 5 | CSRF token method tested and documented, /api/me |
| T-18 | 6 | Validation annotations (PR into Member 2's branch) |
| T-19 | 6 | Exceptions and GlobalExceptionHandler |
| T-20 | 6 | Postman collection |
| T-21 | 6 | Run and document the ten checks |

## Merge order (avoids conflicts)

Merge into `develop` in this order, because later work needs earlier work:

1. Member 1: skeleton (T-02). It has **no** security starter yet, so the API is open for local testing.
2. Member 2: entity, DTOs, repository
3. Member 6: exceptions and `GlobalExceptionHandler` (T-19). The service throws these, so they must exist first.
4. Member 3: service
5. Member 4: controller
6. Member 5: security. Adds `spring-boot-starter-security` to `pom.xml` after telling Member 1.
7. Member 6: validation annotations, Postman, checks
8. Member 1: frontend, then `develop` into `main` when all ten checks pass

If your task needs something not merged yet, say so in the group chat. Do not copy another member's file into your branch.

## Code review (short)

The reviewer opens the PR, reads **Files changed**, and checks the list in `CONTRIBUTING.md`. Then choose **Approve** or **Request changes** and write what to fix. A PR is not merged until the author can explain the code and the test result is real.

## Conflicts

A conflict means two people edited the same file. This should be rare because each person owns different files. If GitHub Desktop shows a conflict, **stop**, do not guess, and ask Member 1.

## .gitignore (should contain)

```
target/
.idea/
.vscode/
*.iml
*.log
.DS_Store
_journal/
```

## What counts as contribution

Commit count is **not** the measure. Evidence is code, tests, documentation, completed issues, reviews, debugging, and being able to explain it. Track it in `CONTRIBUTION_TRACKER.md`.
