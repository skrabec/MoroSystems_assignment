# MoroSystems Automation Assignment

UI test automation for [morosystems.cz](https://www.morosystems.cz) built with Java, Playwright, JUnit 5, and Guice.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11+ | Language |
| Playwright | 1.49.0 | Browser automation |
| JUnit 5 | 5.11.0 | Test framework |
| Google Guice | 7.0.0 | Dependency injection |
| Allure | 2.33.0 | Reporting |
| Maven | 3.x | Build & dependency management |

## Project Structure

```
src/
├── main/java/
│   ├── extensions/
│   │   ├── GuicePageModule.java          # Guice DI module — browser setup, page object bindings
│   │   └── junit/
│   │       └── UIExtensions.java         # JUnit 5 extension — screenshots/traces/logs on failure
│   ├── model/
│   │   └── ScreenResolution.java         # Enum of tested viewport sizes
│   └── pages/
│       ├── SeznamPage.java               # Seznam.cz search page
│       ├── MoroSystemsPage.java          # MoroSystems homepage
│       └── KarieryPage.java              # Kariéra page with city filter
└── test/java/
    └── ui/
        ├── AllTestsSuite.java            # JUnit 5 suite — runs all test classes
        ├── MoroSystemsUITest.java        # Kariéra filter scenarios
        └── ResponsiveDesignTest.java     # Responsive design checks across resolutions
```

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

### Specific browser

```bash
mvn test -P firefox
mvn test -P edge
```

### Suite only

Runs all tests through `AllTestsSuite` instead of discovering classes individually:

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
- **Kariéra page** — `h1` visible, city filter visible, no horizontal overflow

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

## Allure Report

Generate and open the report after running tests:

```bash
mvn allure:serve
```

Or generate a static report in `target/site/allure-maven-plugin/`:

```bash
mvn allure:report
```
