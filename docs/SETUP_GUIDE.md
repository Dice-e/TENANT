# SETUP_GUIDE.md: Get TENANT running on your laptop

Status: **written before the code exists.** The install steps are standard, but the "run the app" steps have not been run yet. When Member 1 finishes the skeleton (T-02), Member 1 must run them, fix anything wrong here, and fill in section 8.

## 1. What you need

| Tool | Why | Free? |
|---|---|---|
| Java 17 JDK or newer | Runs the app | Yes (for example Eclipse Temurin from adoptium.net) |
| Git | Version control | Yes (git-scm.com) |
| GitHub Desktop | Easy Git for beginners | Yes (desktop.github.com) |
| VS Code + "Extension Pack for Java" | Editor | Yes (code.visualstudio.com). IntelliJ IDEA Community also works |
| Postman | Test the API | Yes (free plan, postman.com) |
| A browser (Chrome, Edge or Firefox) | Use the web interface | Yes |
| Maven | Builds the project | Optional: the project includes the Maven wrapper (`mvnw`), so you do not have to install Maven |

An ordinary student laptop is enough. Run only **one** copy of the app at a time.

## 2. Install (Windows)

1. Install the **Java 17 JDK** (choose "JDK", not "JRE"). In the installer, enable the options that set `JAVA_HOME` and add Java to `PATH` if offered.
2. Install **Git**. Accept the defaults.
3. Install **GitHub Desktop** and sign in with your GitHub account.
4. Install **VS Code**, then install the "Extension Pack for Java" from the Extensions tab.
5. Install **Postman**.
6. **Close and reopen** any terminal windows so they see the new tools.

Mac: use the same tools. Linux: use your package manager.

## 3. Check your tools

Open a terminal (Windows: PowerShell or Command Prompt) and run:

```
java -version
git --version
```

`java -version` must show **17** or higher. If it shows 8 or 11, or says "not recognized", fix this first. Ask Claude with the **exact** output you see.

## 4. Get the project

1. Accept the GitHub invitation from Member 1.
2. GitHub Desktop → **File → Clone repository** → choose `TENANT` → pick a folder without spaces if possible (for example `C:\projects\TENANT`) → **Clone**.
3. Click **Current Branch** and select **your own** feature branch (see `GITHUB_WORKFLOW.md`).
4. **Branch → Update from develop** to get the latest shared code.
5. VS Code → **File → Open Folder** → choose the `TENANT` folder.

## 5. Run the app

In the VS Code terminal, inside the `TENANT` folder:

- Windows: `mvnw.cmd spring-boot:run`
- Mac or Linux: `./mvnw spring-boot:run`
- If you installed Maven yourself: `mvn spring-boot:run`

The first run downloads libraries and can take a few minutes. You are done when the console shows a line like `Started TenantApplication`. Leave the terminal open. **Stop the app** with `Ctrl+C`.

## 6. Open it

- Web interface: `http://localhost:8080`
- API example: `http://localhost:8080/api/payment-records` (needs login after Member 5's security is merged)

## 7. Demo accounts and data

| Username | Password | Role |
|---|---|---|
| `staff` | `staff123` | USER |
| `admin` | `admin123` | ADMIN |

(Available after Member 5's seeder is merged.) These are demo-only. Sample records are created by Member 3's seeder at each start.

**All data is lost when you stop the app** because H2 is in memory. To reset everything, stop and start the app again.

## 8. Versions in use (Member 1 fills this in after generating `pom.xml`)

| Item | Version |
|---|---|
| Java | (fill in) |
| Spring Boot | (fill in) |
| Maven wrapper | (fill in) |
| Date confirmed running | (fill in) |

## 9. Common problems

| Problem | What to do |
|---|---|
| `java` is not recognized | Reopen the terminal; check the install and `PATH`; ask Claude with the exact message |
| `java -version` shows an old version | Install JDK 17 and make sure it comes first on `PATH` / `JAVA_HOME` |
| `Port 8080 was already in use` | Another run is open. Stop it (`Ctrl+C`) or close the other terminal |
| `mvnw` "permission denied" (Mac/Linux) | Run `chmod +x mvnw` once |
| Download errors or very slow | Check the internet, then retry; do not interrupt the first download |
| Red errors in VS Code but Maven works | Wait for the Java extension to finish importing; reload the window |
| Build fails with `javax` errors | Code uses old `javax.*` imports; change to `jakarta.*` |
| Browser shows an unexpected login box | Browser Basic prompt; tell Member 1 and Member 5 |
| GitHub Desktop shows a conflict | Stop and ask Member 1; do not guess |

## 10. Using Claude for setup

Copy the **exact** error text and tell Claude your operating system and what command you ran. Never summarize an error from memory.
