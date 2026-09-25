# Sprint 11 Demo 1 – Automate Build and Tests Using GitHub Actions

This demo uses the existing **Event Service** to implement a simple Continuous Integration (CI) workflow using **GitHub Actions**.

The demo is divided into two parts:

- **Part 1:** Create and verify a successful CI workflow.
- **Part 2:** Introduce a failing test, inspect the CI failure, fix the test, and verify the workflow succeeds again.

---

## Problem statement

### Part 1 — Successful CI Workflow

Create a GitHub Actions workflow for the existing Event Service. Configure it to run on a push to `main`, set up Java 25, execute the Maven unit tests, and verify that the workflow completes successfully with all tests passing.

### Part 2 — Diagnose a Failed CI Workflow

Deliberately change one expected value in a unit test and push the change. Verify that the workflow fails, inspect the GitHub Actions logs to identify the failed test and expected/actual values, correct the test, and push again to verify that the workflow returns to green.

---

## Project Used

```text
Event Service
     ↓
 PostgreSQL
```

The project contains:

- Spring Boot Event Service
- PostgreSQL configuration
- Dockerfile
- Docker Compose file
- Service-layer unit tests

> PostgreSQL is part of the application setup, but it is not required for the unit tests in this demo because the repository is mocked using Mockito.

---

## Test Scope

For this demo, unit tests are written only for the **service layer**.

```text
EventController
      ↓
EventServiceImpl   ← Unit tests
      ↓
EventRepository    ← Mocked using Mockito
```

Tests used:

| Test | Purpose |
|---|---|
| `shouldCreateEvent()` | Verifies event creation. |
| `shouldReturnEventById()` | Verifies that an existing event can be retrieved. |
| `shouldReturnAllEvents()` | Verifies that all events are returned. |
| `shouldThrowExceptionWhenEventNotFound()` | Verifies behaviour when an event does not exist. |

---

# Part 1 — Successful CI Workflow

## 1. Verify the Tests Locally

From the project root, run:

```bash
mvn clean test
```

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0
BUILD SUCCESS
```

Complete this step before configuring GitHub Actions.

---

## 2. Create a GitHub Repository

Sign in to GitHub and create a new repository.

Example repository name:

```text
eventhub-sprint11-ci-demo
```

Do not add another README, `.gitignore`, or licence if these files already exist in the local project.

---

## 3. Create a Fine-Grained Personal Access Token

GitHub does not accept the normal GitHub account password for Git operations over HTTPS.

Use a **Personal Access Token (PAT)** as the password.

1. Open **Profile picture → Settings**.
2. Open **Developer settings**.
3. Open **Personal access tokens → Fine-grained tokens**.
4. Create a new token.
5. Select the repository created for this demo.

### Configure Repository Permissions

After selecting the repository for the token:

1. Scroll to the **Repository permissions** section.
2. Click **Add permissions**.
3. Select **Contents**.
4. Set **Contents** to **Read and write**.
5. Click **Add permissions** again.
6. Select **Workflows**.
7. Set **Workflows** to **Read and write**.
8. Keep **Metadata** as **Read-only**.
9. Click **Generate token** or **Update**.

Final permissions should be:

```text
Contents   → Read and write
Workflows  → Read and write
Metadata   → Read-only
```

> The token is used only for authentication. Do not add it to source code, `application.properties`, `ci.yml`, or commit it to Git.

---

## 4. Use the Token with Git

When Git asks:

```text
Username for 'https://github.com':
```

enter your GitHub username.

When Git asks:

```text
Password for 'https://<username>@github.com':
```

paste the **Personal Access Token**, not your GitHub account password.

---

## 5. Push the Existing Event Service to GitHub

From the project root:

```bash
git add .
git commit -m "Add Event Service project"
git branch -M main
git remote add origin <repository-url>
git push -u origin main
```

Verify that the Event Service source code is visible in the GitHub repository.

> If the remote is already configured, do not run `git remote add origin` again.

---

## 6. Create the GitHub Actions Workflow

At the project root, create:

```text
.github/
└── workflows/
    └── ci.yml
```

The `.github` folder should be at the same level as `pom.xml`.

Example project structure:

```text
event-service/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 7. Add the CI Workflow

Add the following to `.github/workflows/ci.yml`:

```yaml
# Name of the GitHub Actions workflow
name: Event Service CI

# Run the workflow when code is pushed or a pull request targets main
on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

# Define the CI job that will build and test the application
jobs:
  build-and-test:

    # Run the job on a GitHub-hosted Ubuntu machine
    runs-on: ubuntu-latest

    steps:
      # Download the repository code into the runner
      - name: Checkout code
        uses: actions/checkout@v7

      # Install Java 25 and enable Maven dependency caching
      - name: Set up Java 25
        uses: actions/setup-java@v6
        with:
          distribution: 'temurin'
          java-version: '25'
          cache: 'maven'

      # Build the project and run all Maven tests
      - name: Build and run tests
        run: mvn clean test
```

### Important Points

- The workflow runs automatically when code is pushed to `main`.
- GitHub uses a hosted runner and sets up Java 25.
- `mvn clean test` builds the project and runs all unit tests.

---

## 8. Commit and Push the Workflow

Run:

```bash
git add .github/workflows/ci.yml
git commit -m "Add GitHub Actions CI workflow"
git push
```

The push to `main` should automatically trigger the workflow.

---

## 9. Verify the Successful GitHub Actions Run

In the GitHub repository:

1. Open the **Actions** tab.
2. Click the workflow run that was created after the push.
3. On the workflow run page, click the **`build-and-test`** job.
4. Verify that the following steps completed successfully:

```text
Checkout code          ✓
Set up Java 25         ✓
Build and run tests    ✓
```

5. Click **Build and run tests** to view the Maven output.
6. Confirm that all tests passed and the build completed successfully.

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0
BUILD SUCCESS
```

A green workflow confirms that the Event Service was built and tested successfully by GitHub Actions.

---

## Part 1 Expected Flow

```text
Code pushed to main
        ↓
GitHub Actions starts
        ↓
Checkout code
        ↓
Set up Java 25
        ↓
Maven build and tests run
        ↓
All tests pass
        ↓
Workflow turns green
```

---

# Part 2 — Diagnose a Failed CI Workflow

## 10. Introduce a Failing Test

To understand how CI detects problems, deliberately change one expected value in `EventServiceImplTest`.

Change:

```java
assertEquals(
        "Spring Boot Workshop",
        result.getName()
);
```

to:

```java
assertEquals(
        "Docker Workshop",
        result.getName()
);
```

The actual value returned by the service is still:

```text
Spring Boot Workshop
```

---

## 11. Commit and Push the Failing Test

Run:

```bash
git add .
git commit -m "Introduce failing test for CI verification"
git push
```

Because the workflow is configured to run on every push to `main`, GitHub Actions starts automatically.

---

## 12. Verify the Failed Workflow

In GitHub:

1. Open the **Actions** tab.
2. Click the latest workflow run.
3. Click the **`build-and-test`** job.
4. Click **Build and run tests**.

The workflow should now fail.

You should see an assertion failure similar to:

```text
expected: <Docker Workshop>
but was: <Spring Boot Workshop>
```

The logs help identify:

- Which test failed
- Expected value
- Actual value
- Error details

A red workflow indicates that one or more CI checks failed.

---

## 13. Fix the Test

Correct the assertion back to:

```java
assertEquals(
        "Spring Boot Workshop",
        result.getName()
);
```

---

## 14. Commit and Push the Fix

Run:

```bash
git add .
git commit -m "Fix failing unit test"
git push
```

GitHub Actions starts automatically again.

---

## 15. Verify the Workflow Returns to Green

In the GitHub repository:

1. Open the **Actions** tab.
2. Click the latest workflow run.
3. Click the **`build-and-test`** job.
4. Verify:

```text
Checkout code          ✓
Set up Java 25         ✓
Build and run tests    ✓
```

5. Open **Build and run tests** and confirm that all tests passed.

The workflow should now turn green again.

---

## Part 2 Expected Flow

```text
Working test
     ↓
Change expected value
     ↓
Push code
     ↓
GitHub Actions runs
     ↓
Test fails
     ↓
Workflow turns red
     ↓
Inspect logs
     ↓
Fix the test
     ↓
Push again
     ↓
Workflow turns green
```

---

## Demo Completion Criteria

The demo is complete when:

- The Event Service is available in the GitHub repository.
- `.github/workflows/ci.yml` is present.
- A push to `main` triggers GitHub Actions automatically.
- Java 25 is configured on the runner.
- Maven executes the unit tests.
- All tests pass in Part 1.
- A deliberately incorrect assertion causes the workflow to fail in Part 2.
- The GitHub Actions logs are used to identify the failure.
- The corrected test causes the workflow to return to green.

---

## Troubleshooting

### Push Is Rejected When Adding `ci.yml`

You may see an error similar to:

```text
refusing to allow a Personal Access Token to create or update workflow
`.github/workflows/ci.yml`
```

This means the token does not have permission to modify GitHub Actions workflow files.

For a fine-grained PAT, verify:

```text
Contents   → Read and write
Workflows  → Read and write
Metadata   → Read-only
```

Update the token permissions and run:

```bash
git push
```

> You do not need to create another commit if the commit was already created successfully.

---

### Git Asks for a Password

Do not enter the normal GitHub account password.

Use:

```text
Username → GitHub username
Password → Personal Access Token
```

---

### Git Still Uses an Old Token

If Git continues using a previous token, remove the saved `github.com` credential from the credential manager configured on the machine.

Then run:

```bash
git push
```

Enter the GitHub username and the updated Personal Access Token when prompted.

---

### Workflow Does Not Start

Check:

- `ci.yml` is inside `.github/workflows/`.
- The file has been committed and pushed.
- The push was made to the `main` branch.
- The workflow contains:

```yaml
on:
  push:
    branches: [ "main" ]
```

---

### Workflow Fails During Maven Tests

Open:

```text
GitHub Repository
→ Actions
→ Latest workflow run
→ build-and-test
→ Build and run tests
```

Read the Maven output to identify the failed test and the expected/actual values.

---

## Key Learning

```text
Push code
   ↓
GitHub Actions
   ↓
Build + Tests
   ↓
Green / Red Result
```

GitHub Actions automatically runs the same build and test checks whenever code is pushed and provides immediate feedback when a test fails.