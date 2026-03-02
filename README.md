# MoroSystems Automation Assignment

UI and API test automation for [morosystems.cz](https://www.morosystems.cz) built with Java, Playwright, REST Assured, JUnit 5, and Guice.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11+ | Language |
| Playwright | 1.49.0 | Browser automation |
| REST Assured | 5.4.0 | API test HTTP client |
| OpenAPI Generator | 7.2.0 | API model codegen from Swagger spec |
| JUnit 5 | 5.11.0 | Test framework |
| Google Guice | 7.0.0 | Dependency injection |
| Allure | 2.33.0 | Reporting |
| Maven | 3.x | Build & dependency management |

## Project Structure

```
src/
├── main/java/
│   ├── api/
│   │   └── TaskApiClient.java            # REST Assured client for Todo API
│   ├── extensions/
│   │   ├── GuicePageModule.java          # Guice DI module — browser setup, page object bindings
│   │   └── junit/
│   │       ├── UIExtensions.java         # JUnit 5 extension — screenshots/traces/logs on failure
│   │       └── ApiExtension.java         # JUnit 5 extension for API tests
│   ├── model/
│   │   └── ScreenResolution.java         # Enum of tested viewport sizes
│   └── pages/
│       ├── SeznamPage.java               # Seznam.cz search page
│       ├── MoroSystemsPage.java          # MoroSystems homepage
│       └── KarieryPage.java              # Kariera page with city filter
├── main/resources/
│   └── openapi/
│       └── openapi.json                  # OpenAPI spec — source for model codegen
└── test/java/
    ├── api/
    │   └── TaskApiTest.java              # REST API tests for Todo backend
    └── ui/
        ├── AllTestsSuite.java            # JUnit 5 suite — runs all test classes
        ├── MoroSystemsUITest.java        # Kariera filter scenarios
        └── ResponsiveDesignTest.java     # Responsive design checks across resolutions
```

Generated API models (`Task`, `CreateTask`, `UpdateTask`) are produced automatically at build time from `openapi.json` into `target/generated-sources/openapi/`.

## Prerequisites

- Java 11 or higher
- Maven 3.x
- Playwright browsers installed:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Running Tests

### All tests, default browser (Chromium)

```bash
mvn test
```

### API tests only

```bash
mvn test -Dtest=TaskApiTest
```

### API tests in parallel

```bash
mvn test -Dtest=TaskApiTest -P parallel
```

### Specific browser

```bash
mvn test -P firefox
mvn test -P edge
```

### Suite only

Runs all tests (UI + API) through `AllTestsSuite` instead of discovering classes individually:

```bash
mvn test -P suite
```

Can be combined with a browser profile:

```bash
mvn test -P suite,firefox
```

### All browsers in sequence

Runs Chromium → Firefox → Edge one after another. Surefire reports are written to separate subdirectories per browser:

```
target/surefire-reports/chromium/
target/surefire-reports/firefox/
target/surefire-reports/edge/
```

```bash
mvn test -P all-browsers
```

Can be combined with suite:

```bash
mvn test -P all-browsers,suite
```

### Parallel execution

Tests are isolated (each test gets its own browser instance), so they can safely run in parallel. Disabled by default to avoid resource spikes.

```bash
mvn test -P parallel
```

Thread count defaults to `4`. Override with:

```bash
mvn test -P parallel -Dparallel.parallelism=8
```

Composes with other profiles:

```bash
mvn test -P parallel,firefox
mvn test -P parallel,suite
mvn test -P parallel,all-browsers
```

### Headless mode

```bash
mvn test -Dheadless=true
```

### With tracing enabled

When `-Dtracing.enabled=true` is set, traces are also saved for **passing** tests. Failing tests always produce a trace regardless of this flag.

```bash
mvn test -Dtracing.enabled=true
```

## Test Scenarios

### TaskApiTest

Tests the [Todo API](https://todo-be-production-0bb9.up.railway.app/api-docs/). Runs in parallel (`@Execution(CONCURRENT)`).

| Test | Description |
|------|-------------|
| `getAllTasksReturnsList` | GET /tasks returns 200 with non-null list |
| `createTaskReturnsCorrectData` | POST /tasks returns task with correct id, text, defaults |
| `updateTaskTextReturnsUpdatedData` | POST /tasks/{id} updates text |
| `deleteTaskRemovesItFromList` | DELETE /tasks/{id} removes task from GET response |
| `completeTaskSetsCompletedFlagAndDate` | POST /tasks/{id}/complete sets completed=true and completedDate |
| `incompleteTaskClearsCompletedFlag` | POST /tasks/{id}/incomplete clears completed flag |
| `getCompletedTasksReturnsOnlyCompleted` | GET /tasks/completed returns only completed tasks |
| `createTaskWithEmptyTextReturns422` | POST /tasks with empty text returns 422 |
| `deleteNonExistentTaskReturns400` | DELETE /tasks/{id} with unknown id returns 404 |

### MoroSystemsUITest

| Test | Expected result |
|------|----------------|
| `filterKarieraPositionsByCity` | No positions shown for Bratislava — **passes** |
| `expectPositionsInBratislava` | Asserts positions exist in Bratislava — **intentionally fails** (negative scenario) |

### ResponsiveDesignTest

Parameterized across 5 viewport sizes:

| Resolution | Width | Height |
|------------|-------|--------|
| Mobile S | 375 | 667 |
| Mobile L | 425 | 896 |
| Tablet | 768 | 1024 |
| Desktop | 1440 | 900 |
| Desktop 4K | 2560 | 1440 |

Each resolution runs two checks:

- **Homepage** — `header` visible, `h1` visible, no horizontal overflow
- **Kariera page** — `h1` visible, city filter visible, no horizontal overflow

## Artifacts on Failure

When a test fails, the following are saved automatically:

| Artifact | Location |
|----------|----------|
| Screenshot | `target/screenshots/<test>_<timestamp>.png` |
| Playwright trace | `target/traces/<test>_<timestamp>_trace.zip` |
| Log file | `target/test-logs/<test>_<timestamp>.log` |

A log file is also written for every **passing** test to `target/test-logs/`.

Traces can be inspected with:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace target/traces/<file>.zip"
```

## Troubleshooting

### Visual regression tests fail with large pixel diffs

Baselines are stored in `src/test/resources/baselines/`. If the site has changed and diffs are expected, delete the outdated baseline files and re-run. The test will save a new baseline on first run, then compare on subsequent runs:

```bash
# Delete all baselines and regenerate
rm src/test/resources/baselines/*.png
mvn test -Dtest=VisualRegressionTest
```

Or delete a specific baseline:

```bash
rm src/test/resources/baselines/homepage_DESKTOP.png
```

### Visual regression tests fail with TimeoutError (NETWORKIDLE)

If `waitForLoadState(NETWORKIDLE)` times out, it means the site has continuous background network activity (analytics, ads) and the idle state is never reached. The test uses `LOAD` instead, which waits only for the DOM and page resources.

## Allure Report

Generate and open the report after running tests:

```bash
mvn allure:serve
```

Or generate a static report in `target/site/allure-maven-plugin/`:

```bash
mvn allure:report
```
