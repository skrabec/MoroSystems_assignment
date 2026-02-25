package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class MoroSystemsPage {
    private final Page page;

    private static final String URL = "https://www.morosystems.cz";
    private static final String KARIERA_LINK = "a[href*='kariera']";

    public MoroSystemsPage(Page page) {
        this.page = page;
    }

    @Step("Open MoroSystems website")
    public void open() {
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
        page.locator(KARIERA_LINK).first().click();
        page.waitForURL("**/kariera/**");
    }
}
