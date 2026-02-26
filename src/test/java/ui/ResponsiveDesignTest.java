package ui;

import com.google.inject.Inject;
import com.microsoft.playwright.Page;
import extensions.junit.UIExtensions;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import model.ScreenResolution;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pages.KarieryPage;
import pages.MoroSystemsPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(UIExtensions.class)
@Feature("MoroSystems website responsive design")
public class ResponsiveDesignTest {

    @Inject
    private Page page;

    @Inject
    private MoroSystemsPage moroSystemsPage;

    @Inject
    private KarieryPage karieryPage;

    @ParameterizedTest(name = "{0}")
    @EnumSource(ScreenResolution.class)
    @Story("Homepage responsive design")
    @Description("Validate MoroSystems homepage displays correctly on different screen resolutions")
    void homepageDisplaysCorrectly(ScreenResolution resolution) {
        page.setViewportSize(resolution.width, resolution.height);
        moroSystemsPage.open();

        assertAll(
            () -> assertTrue(page.isVisible("header"),
                "Header should be visible at " + resolution.label),
            () -> assertTrue(page.isVisible("h1"),
                "Main heading should be visible at " + resolution.label),
            () -> assertNoHorizontalOverflow(resolution)
        );
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(ScreenResolution.class)
    @Story("Kariéra page responsive design")
    @Description("Validate MoroSystems Kariéra page displays correctly on different screen resolutions")
    void karieraPageDisplaysCorrectly(ScreenResolution resolution) {
        page.setViewportSize(resolution.width, resolution.height);
        moroSystemsPage.openKariera();

        assertAll(
            () -> assertTrue(page.isVisible("h1"),
                "Page heading should be visible at " + resolution.label),
            () -> assertTrue(page.isVisible(".inp-custom-select__select-wrap"),
                "City filter should be visible at " + resolution.label),
            () -> assertNoHorizontalOverflow(resolution)
        );
    }

    @Step("Verify no horizontal overflow at {resolution}")
    private void assertNoHorizontalOverflow(ScreenResolution resolution) {
        boolean noOverflow = (Boolean) page.evaluate(
            "document.documentElement.scrollWidth <= window.innerWidth");
        assertTrue(noOverflow,
            "Page should not have horizontal overflow at " + resolution.label);
    }
}
