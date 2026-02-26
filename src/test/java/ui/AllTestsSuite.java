package ui;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("MoroSystems UI Test Suite")
@SelectClasses({
    MoroSystemsUITest.class,
    ResponsiveDesignTest.class
})
public class AllTestsSuite {
}
