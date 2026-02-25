package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class GooglePage {
    private final Page page;

    private static final String URL = "https://www.google.com";
    private static final String SEARCH_INPUT = "textarea[name='q']";
    private static final String SEARCH_RESULTS = "#search";

    public GooglePage(Page page) {
        this.page = page;
    }

    @Step("Open Google")
    public void open() {
        page.navigate(URL);
        acceptCookiesIfPresent();
    }

    private void acceptCookiesIfPresent() {
        try {
            page.locator("button:has-text('Přijmout vše'), button:has-text('Accept all')")
                .click(new Locator.ClickOptions().setTimeout(5000));
        } catch (Exception e) {
            // No cookie banner present, continue
        }
    }

    @Step("Search for \"{query}\"")
    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.press(SEARCH_INPUT, "Enter");
        page.waitForSelector(SEARCH_RESULTS);
    }

    @Step("Verify search results page is displayed")
    public boolean isResultsPageDisplayed() {
        return page.isVisible(SEARCH_RESULTS);
    }

    @Step("Verify MoroSystems link is present in search results")
    public boolean isMoroSystemsLinkPresent() {
        return page.isVisible("a[href*='morosystems.cz']");
    }

    @Step("Click on MoroSystems link")
    public void clickMoroSystemsLink() {
        page.locator("a[href*='morosystems.cz']:not([href*='google'])")
            .first()
            .click();
    }
}
