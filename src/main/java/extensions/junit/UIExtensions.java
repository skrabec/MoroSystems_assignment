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
    String timestamp = generateTimestamp();

    private static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        injector = Guice.createInjector(new GuicePageModule());
        injector.injectMembers(context.getTestInstance().get());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        injector.getProvider(BrowserContext.class).get().tracing()
            .stop(new Tracing.StopOptions().setPath(Paths.get(timestamp + "_trace.zip")));
    }
}
