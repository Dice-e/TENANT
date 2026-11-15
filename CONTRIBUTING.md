# CONTRIBUTING.md: Rules for contributing to TENANT

Read `CLAUDE.md` (project rules) and your member guide first. Git steps are in `GITHUB_WORKFLOW.md`.

## Ground rules

1. Work only on your own files (ownership table in `CLAUDE.md`). Need a change in someone else's file? Open an issue or comment on their PR. Do not edit it yourself.
2. Work on your own feature branch and open a PR into `develop`.
3. One task per PR. Small PRs are easier to review.
4. Run and test before you commit. A PR says how you tested and what really happened.
5. You must be able to explain every line you submit, including lines Claude wrote. If you cannot, ask Claude to teach it first.
6. No new dependencies and no extra features without Member 1's approval.
7. No passwords, hashes or personal data in code, logs or commit messages. Demo data only.
8. Be kind in reviews. Comment on the code, not the person.

## Commit messages

Short, in present tense, saying what changed.

| Good | Not good |
|---|---|
| `Add RentPaymentRecord entity` | `update` |
| `Reject duplicate reference on create` | `fix stuff` |
| `Add unit filter to controller` | `asdf` |

## Using Claude (summary)

Use the prompt in your member guide. Claude works in small phases and you actually run each step. Never paste results you did not see. Keep a short note of what Claude helped with and what you checked or changed. That note feeds `docs/AI_USE_DISCLOSURE.md`.

## Code standards

- Java 17, Spring Boot 3.x, `jakarta.*` imports only.
- Constructor injection, no `@Autowired` on fields.
- Controllers thin, rules in services, no entities returned from controllers.
- Meaningful names, short methods, comments only where they explain intent.
- Frontend: plain HTML, CSS and JS; Palette 2 tokens only (no hardcoded colors outside `:root`).

## Reviewer checklist

Tick each point before approving.

**General**
- [ ] The PR is one task and only touches the author's files.
- [ ] The author ran it and wrote real test steps and results.
- [ ] The author can explain the code (ask one question).
- [ ] No passwords, hashes, or secrets.

**Backend**
- [ ] Right layer: HTTP in the controller, rules in the service, data access in the repository.
- [ ] Constructor injection used.
- [ ] DTOs at the API edge, entities inside. No entity returned.
- [ ] `jakarta.*` imports, not `javax.*`.
- [ ] Status codes match the table in `CLAUDE.md`.
- [ ] `Optional` from `findById` is handled (404, not `.get()`).
- [ ] Wording says "record", never "payment received".

**Frontend**
- [ ] Palette 2 variables used; text is readable.
- [ ] Loading, empty, error and success states exist.
- [ ] Works at phone width and desktop width.
- [ ] Delete asks for confirmation. Credentials are not stored in `localStorage`.

## Responding to review

1. Read each comment. Ask if it is unclear.
2. Fix it, commit, push. The PR updates by itself.
3. Reply to each comment saying what you did.
4. Ask the reviewer to look again. Do not merge your own PR.

## Reporting a problem

Open an issue with: what you did, what you expected, what actually happened (copy the real error), and your branch. Never write "it works" or "it fails" without the evidence.
