package pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KarieryPage {
    private static final Logger log = LoggerFactory.getLogger(KarieryPage.class);
    private final Page page;

    private static final String CITY_DROPDOWN = ".inp-custom-select__select-wrap";
    private static final String CITY_OPTION = ".inp-item__text:has-text('%s')";
    private static final String POSITION_ITEMS = ".c-positions a[href]:not([href*='#']):not([href*='mailto'])";

    public KarieryPage(Page page) {
        this.page = page;
    }

    @Step("Filter positions by city \"{city}\"")
    public void filterByCity(String city) {
        log.info("Filtering positions by city: {}", city);
        page.locator(CITY_DROPDOWN).click();
        page.locator(String.format(CITY_OPTION, city)).click();
        page.waitForTimeout(1000);
    }

    @Step("Get visible position texts")
    public List<String> getVisiblePositionTexts() {
        return page.locator(POSITION_ITEMS).allTextContents();
    }

    @Step("Verify no positions are displayed")
    public boolean areResultsEmpty() {
        int count = page.locator(POSITION_ITEMS + ":visible").count();
        log.info("Visible positions count after filter: {}", count);
        return count == 0;
    }
}
