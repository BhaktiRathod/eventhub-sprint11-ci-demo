# Event Service – CI Demo

This project is used in Sprint 11 to learn how to automate application build and testing using GitHub Actions.

The project contains a simple Event Service with PostgreSQL persistence and service-layer unit tests.


## Project Components

```text
Event Service
     ↓
 PostgreSQL
```

The Event Service contains:

* REST APIs for managing events
* Service and repository layers
* PostgreSQL persistence
* Dockerfile
* Docker Compose configuration
* Service-layer unit tests

## Test Scope

For this demo, unit tests are written for the `EventServiceImpl` service layer.

The repository dependency is mocked using Mockito, so PostgreSQL is not required when running the unit tests.

Tests covered:

| Test                                      | Purpose                                              |
| ----------------------------------------- | ---------------------------------------------------- |
| `shouldCreateEvent()`                     | Verifies that an event can be created.               |
| `shouldReturnEventById()`                 | Verifies that an existing event can be retrieved.    |
| `shouldReturnAllEvents()`                 | Verifies that all events are returned.               |
| `shouldThrowExceptionWhenEventNotFound()` | Verifies the behaviour when an event does not exist. |

## Run the Tests

From the `event-service` project directory, run:

```bash
mvn clean test
```

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0

BUILD SUCCESS
```

## Run the Application

The application and PostgreSQL can be started using Docker Compose:

```bash
docker compose up --build
```

Use the PostgreSQL configuration defined in `application.properties` and `docker-compose.yml`.

## Demo Starting Point

Before adding CI automation, verify that:

* The Event Service builds successfully.
* All four unit tests pass locally.
* The Dockerfile is available.
* The Docker Compose configuration is available.
* The project is pushed to GitHub.

GitHub Actions will be added during the demo to automate the build and test process.
