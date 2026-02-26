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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.MoroSystemsPage;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.comparison.PointsMarkupPolicy;
import visual.VisualBaseline;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(UIExtensions.class)
@Feature("MoroSystems visual regression")
public class VisualRegressionTest {
    private static final Logger log = LoggerFactory.getLogger(VisualRegressionTest.class);

    // Number of pixels allowed to differ (accounts for minor rendering differences)
    private static final int ALLOWED_DIFF_PIXELS = 500;

    @Inject
    private Page page;

    @Inject
    private MoroSystemsPage moroSystemsPage;

    @ParameterizedTest(name = "{0}")
    @EnumSource(ScreenResolution.class)
    @Story("Homepage visual regression")
    @Description("Compare MoroSystems homepage screenshot against baseline for each resolution")
    void homepageMatchesBaseline(ScreenResolution resolution) throws Exception {
        page.setViewportSize(resolution.width, resolution.height);
        moroSystemsPage.open();
        compareWithBaseline("homepage_" + resolution.name(), page);
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(ScreenResolution.class)
    @Story("Kariéra page visual regression")
    @Description("Compare MoroSystems Kariéra page screenshot against baseline for each resolution")
    void karieraMatchesBaseline(ScreenResolution resolution) throws Exception {
        page.setViewportSize(resolution.width, resolution.height);
        moroSystemsPage.openKariera();
        compareWithBaseline("kariera_" + resolution.name(), page);
    }

    @Step("Compare screenshot with baseline \"{name}\"")
    private void compareWithBaseline(String name, Page page) throws Exception {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        BufferedImage actual = VisualBaseline.fromBytes(page.screenshot());

        BufferedImage baseline = VisualBaseline.load(name);

        if (baseline == null) {
            VisualBaseline.save(actual, name);
            log.info("No baseline found for '{}' — saved current screenshot as baseline. " +
                "Re-run the test to perform comparison.", name);
            return;
        }

        ImageDiff diff = new ImageDiffer()
            .withDiffMarkupPolicy(new PointsMarkupPolicy().withDiffColor(Color.RED))
            .makeDiff(new Screenshot(baseline), new Screenshot(actual));

        int diffSize = diff.getDiffSize();
        boolean hasSignificantDiff = diffSize > ALLOWED_DIFF_PIXELS;

        if (hasSignificantDiff) {
            VisualBaseline.saveDiff(diff.getMarkedImage(), name);
            log.warn("Visual diff detected for '{}': {} pixels differ (allowed: {})",
                name, diffSize, ALLOWED_DIFF_PIXELS);
        }

        assertFalse(hasSignificantDiff,
            String.format("Visual diff detected for '%s': %d pixels differ (allowed: %d). " +
                "Diff image saved to target/visual-diffs/",
                name, diffSize, ALLOWED_DIFF_PIXELS));
    }
}
