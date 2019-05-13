package unit_test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ BeecomDBUnitTest.class, HibernateUnitTest.class, CheckUserPasswordUnitTest.class })
public class AllTests {

}
