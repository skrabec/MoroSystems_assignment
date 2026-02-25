package extensions.junit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import extensions.GuicePageModule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIExtensions implements BeforeEachCallback, AfterEachCallback {
    Injector injector;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        injector = Guice.createInjector(new GuicePageModule());
        injector.injectMembers(context.getTestInstance().get());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (GuicePageModule.TRACING_ENABLED) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
            injector.getProvider(BrowserContext.class).get().tracing()
                .stop(new Tracing.StopOptions()
                    .setPath(Paths.get("target/" + testName + "_" + timestamp + "_trace.zip")));
        }
    }
}
