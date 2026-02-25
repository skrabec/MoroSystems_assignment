package extensions.junit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import extensions.GuicePageModule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIExtensions implements BeforeEachCallback, AfterEachCallback {
    private static final Logger log = LoggerFactory.getLogger(UIExtensions.class);
    Injector injector;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        log.info("Starting test: {}", context.getDisplayName());
        injector = Guice.createInjector(new GuicePageModule());
        injector.injectMembers(context.getTestInstance().get());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        boolean passed = context.getExecutionException().isEmpty();
        log.info("Test {}: {}", context.getDisplayName(), passed ? "PASSED" : "FAILED");

        BrowserContext browserContext = injector.getProvider(BrowserContext.class).get();

        if (GuicePageModule.TRACING_ENABLED) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
            String tracePath = "target/" + testName + "_" + timestamp + "_trace.zip";
            browserContext.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
            log.info("Trace saved: {}", tracePath);
        }

        browserContext.close();
    }
}
