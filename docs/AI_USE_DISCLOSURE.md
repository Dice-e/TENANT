# AI_USE_DISCLOSURE.md

The SD1 guide allows AI help. AI use does not reduce the grade. What matters is that we can **explain** what we submit and that we **really tested** it. Full chat histories are not required. This note is submitted with the write-up.

**Status: partly template.** Sections marked "fill in" must be completed by each member with true information at the end. If you did not use AI for something, say so. Do not write that something was reviewed or tested unless it really was.

## 1. Tool used

- **Claude** (by Anthropic), used as a coding mentor, code generator, debugger and documentation assistant.
- Other AI tools used: (fill in, or write "none").

## 2. What Claude helped with (team level)

| Area | How Claude helped | Confirmed by |
|---|---|---|
| Project planning and scope check against the SD1 guide | Read the guide, Chapter 4 and the design prototype; proposed scope, architecture, roles and schedule | Member 1 (fill in date) |
| Documentation drafts | Wrote first drafts of README, `CLAUDE.md`, scope, architecture, GitHub workflow, tracker, six member guides, setup, testing, defense and checklist documents | Member 1 (fill in what was changed) |
| Backend code | (fill in per member, below) | |
| Frontend code | (fill in) | |
| Test ideas and Postman collection | (fill in) | |
| Debugging | (fill in) | |

Honest note: the planning and documentation drafts were produced by Claude and have **not** been run or verified as code. Anything that says "unverified" in those documents (for example the CSRF token method) was only confirmed if the later sections of this file say so.

## 3. How we reviewed AI output

Every member follows the same process:

1. Read the generated code and ask Claude to explain every unclear line.
2. Check it against Chapter 4 and `CLAUDE.md` (layers, DTOs, constructor injection, `jakarta` imports).
3. Compile and **run** it.
4. Test it with the steps in the member guide (Postman, `mvn test`, or the browser) and record the **real** result.
5. Have another member review the pull request.
6. Explain the code aloud to the team before submitting.

## 4. How we tested it

(Fill in at the end from real evidence.) Examples: manual checks in Postman recorded in `docs/evidence/CHECK-RESULTS.md`; unit tests run with `mvn test` (give the real counts); frontend checks at about 375px and 1280px. Do not list a test unless it was actually run.

## 5. Per-member AI-use notes (each member fills in their own row)

| Member | Parts Claude assisted | What I changed or corrected in Claude's suggestions | How I tested it | I can explain it (yes/no) |
|---|---|---|---|---|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |
| 4 | | | | |
| 5 | | | | |
| 6 | | | | |

Examples of useful "changed or corrected" notes (only if they really happened): "Claude used `javax.persistence`; I changed it to `jakarta.persistence`", "Claude used `existsByReferenceNumber` on update; I changed it so a record may keep its own reference", "A test failed with a 403; the CSRF token was missing, so I added the header".

## 6. Rules we follow

- We do not submit code we cannot explain.
- We do not present generated results as tests we ran.
- We do not store real passwords, personal data or API keys in prompts, code or files. Demo data only.
- We note when something was only generated and not yet run.
- If AI was not used for a part, we say so.

## 7. Team statement (fill in at the end)

(Short paragraph, written by the team, describing in their own words how AI was used and what they learned.)
