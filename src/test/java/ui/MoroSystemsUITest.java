package ui;

import com.google.inject.Inject;
import extensions.junit.UIExtensions;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.KarieryPage;
import pages.MoroSystemsPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(UIExtensions.class)
@Feature("MoroSystems website")
public class MoroSystemsUITest {

    @Inject
    private MoroSystemsPage moroSystemsPage;

    @Inject
    private KarieryPage karieryPage;

    @Test
    @Story("Filter kariéra positions by city")
    @Description("Navigate to MoroSystems website, go to Kariéra page and filter positions by city")
    void filterKarieraPositionsByCity() {
        moroSystemsPage.open();
        moroSystemsPage.goToKariera();

        karieryPage.filterByCity("Bratislava");

        assertTrue(karieryPage.areResultsEmpty(),
            "No positions should be displayed for Bratislava");
    }
}
