package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoroSystemsPage {
    private static final Logger log = LoggerFactory.getLogger(MoroSystemsPage.class);
    private final Page page;

    private static final String URL = "https://www.morosystems.cz";
    private static final String KARIERA_LINK = "a[href*='kariera']";

    public MoroSystemsPage(Page page) {
        this.page = page;
    }

    @Step("Open MoroSystems website")
    public void open() {
        log.info("Navigating to {}", URL);
        page.navigate(URL);
        acceptCookiesIfPresent();
    }

    private void acceptCookiesIfPresent() {
        try {
            page.locator("#cookiescript_accept")
                .click(new Locator.ClickOptions().setTimeout(5000));
        } catch (Exception e) {
            // No cookie banner present, continue
        }
    }

    @Step("Navigate to Kariéra page")
    public void goToKariera() {
        log.info("Clicking Kariéra link");
        page.locator(KARIERA_LINK).first().click();
        page.waitForURL("**/kariera/**");
        log.info("Navigated to: {}", page.url());
    }
}
