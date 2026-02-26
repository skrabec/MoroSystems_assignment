package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class SeznamPage {
    private final Page page;

    private static final String URL = "https://www.seznam.cz";
    private static final String SEARCH_INPUT = "input[name='q']:visible";
    private static final String SEARCH_RESULTS_URL = "**/search.seznam.cz/**";

    public SeznamPage(Page page) {
        this.page = page;
    }

    @Step("Open Seznam")
    public void open() {
        page.navigate(URL);
        acceptCookiesIfPresent();
    }

    private void acceptCookiesIfPresent() {
        try {
            page.locator("button:has-text('Souhlasit'), button:has-text('Přijmout vše')")
                .first()
                .click(new Locator.ClickOptions().setTimeout(5000));
        } catch (Exception e) {
            // No cookie banner present, continue
        }
    }

    @Step("Search for \"{query}\"")
    public void search(String query) {
        page.fill(SEARCH_INPUT, query);
        page.press(SEARCH_INPUT, "Enter");
        page.waitForURL(SEARCH_RESULTS_URL);
    }

    @Step("Verify search results page is displayed")
    public boolean isResultsPageDisplayed() {
        return page.url().contains("search.seznam.cz");
    }

    @Step("Verify MoroSystems link is present in search results")
    public boolean isMoroSystemsLinkPresent() {
        return page.isVisible("a[href*='morosystems.cz']");
    }

    @Step("Click on MoroSystems link")
    public void clickMoroSystemsLink() {
        page.locator("a[href*='morosystems.cz']")
            .first()
            .click();
    }
}
