package ui;

import com.google.inject.Inject;
import extensions.junit.UIExtensions;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.SeznamPage;
import pages.KarieryPage;
import pages.MoroSystemsPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(UIExtensions.class)
@Feature("MoroSystems website")
public class MoroSystemsUITest {

    @Inject
    private MoroSystemsPage moroSystemsPage;

    @Inject
    private KarieryPage karieryPage;

    @Inject
    private SeznamPage seznamPage;

    @Test
    @Story("Filter kariéra positions by city")
    @Description("Navigate to MoroSystems website, go to Kariéra page and filter positions by city")
    void filterKarieraPositionsByCity() {
        seznamPage.open();
        seznamPage.search("MoroSystems.cz");

        moroSystemsPage.open();
        moroSystemsPage.openKariera();

        karieryPage.filterByCity("Bratislava");

        assertTrue(karieryPage.areResultsEmpty(),
            "No positions should be displayed for Bratislava");
    }

    @Test
    @Story("Filter kariéra positions by city")
    @Description("Navigate to MoroSystems website, go to Kariéra page and expect positions in Bratislava - should fail as no positions exist")
    void expectPositionsInBratislava() {
        seznamPage.open();
        seznamPage.search("MoroSystems.cz");

        moroSystemsPage.open();
        moroSystemsPage.openKariera();

        karieryPage.filterByCity("Bratislava");

        assertFalse(karieryPage.areResultsEmpty(),
            "Some positions should be displayed for Bratislava");
    }
}
