package ui;

import api.TaskApiTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("MoroSystems Test Suite")
@SelectClasses({
    MoroSystemsUITest.class,
    ResponsiveDesignTest.class,
    TaskApiTest.class
})
public class AllTestsSuite {
}
