# Sprint 11 Demo 1 – Automate Build and Tests Using GitHub Actions

This demo uses the existing **Event Service** to implement a simple Continuous Integration (CI) workflow using **GitHub Actions**.

The workflow automatically builds the Event Service and runs its unit tests whenever code is pushed to the `main` branch.

---

## Problem statement 

Create a GitHub Actions CI workflow for the existing Event Service.

Configure the workflow to run automatically when code is pushed to the main branch.

Set up Java 25 and run the Maven build and unit tests.

Verify that all tests pass and the GitHub Actions workflow completes successfully.

---

## Demo Flow

```text
Developer pushes code to GitHub
        ↓
GitHub Actions starts
        ↓
Java 25 is set up
        ↓
Maven build and tests run
        ↓
Pass / Fail result is shown
```

---

## Project Used

```text
Event Service
     ↓
 PostgreSQL
```


## Test Scope

For this demo, unit tests are written only for the **service layer**.

```text
EventController
      ↓
EventServiceImpl   ← Unit tests
      ↓
EventRepository    ← Mocked using Mockito
```

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

## 3. Configure Git Authentication for HTTPS

GitHub does not accept the normal GitHub account password for Git operations over HTTPS.

Use a **Personal Access Token (PAT)** as the password.

### Create a Fine-Grained Personal Access Token

1. Click **Profile picture → Settings**.
2. Click **Developer settings**.
3. Click **Personal access tokens → Fine-grained tokens**.
4. Create a new token.
5. Select the repository created for this demo -**eventhub-sprint11-ci-demo**

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
> The token is shown only for authentication. Do not add it to source code, `application.properties`, `ci.yml`, or commit it to Git.
Copy the token and save it in a secure location. You will not be able to see it again.
### Use the Token with Git

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

## 4. Push the Existing Event Service to GitHub

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

## 5. Create the GitHub Actions Workflow

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

## 6. Add the CI Workflow

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

### Important Parts of the Workflow

- `on` starts the workflow automatically when code is pushed to `main`.
- `runs-on: ubuntu-latest` provides a GitHub-hosted machine on which the CI job runs.
- `mvn clean test` builds the project and runs all unit tests.

---

## 7. Commit and Push the Workflow

Run:

```bash
git add .github/workflows/ci.yml
git commit -m "Add GitHub Actions CI workflow"
git push
```

The push to `main` should automatically trigger the workflow.

---

## 8. Verify the GitHub Actions Run

### Verify the GitHub Actions Run

1. Open the **Actions** tab in the GitHub repository.
2. Click the workflow run that was created after the push.
3. On the workflow run page, click the **`build-and-test`** job.
4. Verify that the following steps completed successfully:

```text
Checkout code          ✓
Set up Java 25         ✓
Build and run tests    ✓
```

A green workflow confirms that the Event Service was built and tested successfully by GitHub Actions.

---

## Demo Completion Criteria

The demo is complete when:

- The Event Service is available in the GitHub repository.
- `.github/workflows/ci.yml` is present.
- A push to `main` triggers GitHub Actions automatically.
- Java 25 is configured on the runner.
- Maven executes the unit tests.
- All four tests pass.
- The workflow is shown as successful in the GitHub **Actions** tab.

---

## Troubleshooting

### Push is rejected when adding `ci.yml`

You may see an error similar to:

```text
refusing to allow a Personal Access Token to create or update workflow
`.github/workflows/ci.yml`
```

This means the token does not have permission to modify GitHub Actions workflow files.

For a **fine-grained PAT**, verify:

```text
Contents   → Read and write
Workflows  → Read and write
Metadata   → Read-only
```

Update the token permissions and run again:

```bash
git push
```

You do not need to create another commit if the commit was already created successfully.

---

### Git asks for a password

Do not enter the normal GitHub account password.

Use:

```text
Username → GitHub username
Password → Personal Access Token
```

---

### Git still uses an old token

If Git continues using a previous token, remove the saved `github.com` credential from the credential manager configured on the machine, then run:

```bash
git push
```

Enter the GitHub username and the updated Personal Access Token when prompted.

---

### Workflow does not start

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

### Workflow fails during Maven tests

Open:

```text
GitHub Repository
→ Actions
→ Event Service CI
→ build-and-test
→ Build and run tests
```

Read the Maven output to identify the failed test or build error.

---

## Key Learning

```text
Push code
   ↓
GitHub Actions
   ↓
Build + Tests
   ↓
Pass / Fail
```

GitHub Actions allows the same build and test checks to run automatically whenever code is pushed.

---

## Official References

- GitHub Actions – Building and testing Java with Maven:
  https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven

- GitHub Actions – `setup-java`:
  https://github.com/actions/setup-java

- GitHub – Personal Access Tokens:
  https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens