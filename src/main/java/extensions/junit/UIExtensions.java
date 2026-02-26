package extensions.junit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import extensions.GuicePageModule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIExtensions implements BeforeEachCallback, AfterEachCallback {
    private static final Logger log = LoggerFactory.getLogger(UIExtensions.class);
    private static final ExtensionContext.Namespace NAMESPACE =
        ExtensionContext.Namespace.create(UIExtensions.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        log.info("Starting test: {}", context.getDisplayName());
        Injector injector = Guice.createInjector(new GuicePageModule());
        context.getStore(NAMESPACE).put("injector", injector);
        injector.injectMembers(context.getTestInstance().get());

        injector.getProvider(BrowserContext.class).get()
            .tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Injector injector = context.getStore(NAMESPACE).get("injector", Injector.class);

        boolean passed = context.getExecutionException().isEmpty();
        log.info("Test {}: {}", context.getDisplayName(), passed ? "PASSED" : "FAILED");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
        BrowserContext browserContext = injector.getProvider(BrowserContext.class).get();

        if (!passed) {
            // Screenshot on failure
            try {
                java.nio.file.Path dir = Paths.get("target/screenshots");
                Files.createDirectories(dir);
                String path = dir + "/" + testName + "_" + timestamp + ".png";
                injector.getProvider(Page.class).get()
                    .screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
                log.info("Screenshot saved: {}", path);
            } catch (Exception e) {
                log.warn("Failed to take screenshot: {}", e.getMessage());
            }

            // Trace on failure
            try {
                java.nio.file.Path dir = Paths.get("target/traces");
                Files.createDirectories(dir);
                String path = dir + "/" + testName + "_" + timestamp + "_trace.zip";
                browserContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(path)));
                log.info("Trace saved: {}", path);
            } catch (Exception e) {
                log.warn("Failed to save trace: {}", e.getMessage());
            }
        } else if (GuicePageModule.TRACING_ENABLED) {
            // Trace on pass when explicitly requested via flag
            try {
                java.nio.file.Path dir = Paths.get("target/traces");
                Files.createDirectories(dir);
                String path = dir + "/" + testName + "_" + timestamp + "_trace.zip";
                browserContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(path)));
                log.info("Trace saved: {}", path);
            } catch (Exception e) {
                log.warn("Failed to save trace: {}", e.getMessage());
            }
        } else {
            browserContext.tracing().stop(new Tracing.StopOptions());
        }

        // Log file after every test
        try {
            java.nio.file.Path dir = Paths.get("target/test-logs");
            Files.createDirectories(dir);
            String path = dir + "/" + testName + "_" + timestamp + ".log";
            StringBuilder content = new StringBuilder();
            content.append("Test:      ").append(context.getDisplayName()).append("\n");
            content.append("Status:    ").append(passed ? "PASSED" : "FAILED").append("\n");
            content.append("Timestamp: ").append(timestamp).append("\n");
            context.getExecutionException().ifPresent(ex ->
                content.append("Failure:   ").append(ex.getMessage()).append("\n"));
            Files.write(Paths.get(path), content.toString().getBytes());
            log.info("Log saved: {}", path);
        } catch (Exception e) {
            log.warn("Failed to save test log: {}", e.getMessage());
        }

        browserContext.close();
    }
}
