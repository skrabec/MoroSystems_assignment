# MoroSystems Automation Assignment

UI test automation for [morosystems.cz](https://www.morosystems.cz) built with Java, Playwright, JUnit 5, and Guice.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11+ | Language |
| Playwright | 1.49.0 | Browser automation |
| JUnit 5 | 5.11.0 | Test framework |
| Google Guice | 7.0.0 | Dependency injection |
| Allure | 2.17.0 | Reporting |
| Maven | 3.x | Build & dependency management |

## Project Structure

```
src/
├── main/java/
│   ├── extensions/
│   │   ├── GuicePageModule.java      # Guice DI module — browser setup, page object bindings
│   │   └── junit/
│   │       └── UIExtensions.java     # JUnit 5 extension — injects page objects before each test
│   └── pages/
│       ├── GooglePage.java           # Google search page
│       ├── MoroSystemsPage.java      # MoroSystems homepage
│       └── KarieryPage.java          # Kariéra page with city filter
└── test/java/
    └── ui/
        └── MoroSystemsUITest.java    # UI test scenario
```

## Prerequisites

- Java 11 or higher
- Maven 3.x
- Playwright browsers installed:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## Running Tests

### Default (Chromium)

```bash
mvn test
```

### Specific browser

```bash
mvn test -P chromium
mvn test -P firefox
mvn test -P edge
```

### Parallel execution across browsers

```bash
mvn test -P chromium & mvn test -P firefox & mvn test -P edge
```

### With tracing enabled

Traces are saved to `target/<testName>_<timestamp>_trace.zip` and can be opened with the [Playwright Trace Viewer](https://playwright.dev/docs/trace-viewer).

```bash
mvn test -Dtracing.enabled=true
```

## Test Scenario

1. Navigate to [morosystems.cz](https://www.morosystems.cz)
2. Accept cookie consent
3. Navigate to the **Kariéra** page
4. Filter positions by city — **Bratislava**
5. Validate that no positions are displayed for that city

## Allure Report

Generate and open the report after running tests:

```bash
mvn allure:serve
```

Or generate a static report in `target/site/allure-maven-plugin/`:

```bash
mvn allure:report
```
