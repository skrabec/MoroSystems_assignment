package extensions;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class GuicePageModule extends AbstractModule {
    private Page page;
    private BrowserContext context;

    public GuicePageModule() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));

        BrowserContext context = browser.newContext();

        context
            .tracing()
            .start(new Tracing.StartOptions()
                .setScreenshots(false)
                .setSnapshots(false));

        this.page = context.newPage();
        this.context = context;
    }

    @Singleton
    @Provides
    public BrowserContext getContext() {
        return context;
    }
}
