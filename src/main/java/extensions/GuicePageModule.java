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
import com.microsoft.playwright.options.ViewportSize;
import pages.GooglePage;
import pages.KarieryPage;
import pages.MoroSystemsPage;

public class GuicePageModule extends AbstractModule {
    private Page page;
    private BrowserContext context;
    public static final boolean TRACING_ENABLED = Boolean.parseBoolean(System.getProperty("tracing.enabled", "false"));

    public GuicePageModule() {
        Playwright playwright = Playwright.create();
        String browserName = System.getProperty("browser", "chromium");
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(500);

        Browser browser;
        switch (browserName.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(options);
                break;
            case "edge":
                browser = playwright.chromium().launch(options.setChannel("msedge"));
                break;
            default:
                browser = playwright.chromium().launch(options);
                break;
        }

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
            .setLocale("cs-CZ")
            .setViewportSize(new ViewportSize(1920, 1080)));

        if (TRACING_ENABLED) {
            context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        }

        this.page = context.newPage();
        this.context = context;
    }

    @Singleton
    @Provides
    public Page getPage() {
        return page;
    }

    @Singleton
    @Provides
    public BrowserContext getContext() {
        return context;
    }

    @Singleton
    @Provides
    public GooglePage getGooglePage() {
        return new GooglePage(page);
    }

    @Singleton
    @Provides
    public MoroSystemsPage getMoroSystemsPage() {
        return new MoroSystemsPage(page);
    }

    @Singleton
    @Provides
    public KarieryPage getKarieryPage() {
        return new KarieryPage(page);
    }

}
