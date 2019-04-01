package unit_test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.BeecomDB;
import db.UserInfoUnit;
import other.BeecomEncryption;

public class checkUserPasswordUnitTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheckUserPasswordStringStringUserInfoUnit() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		String userName = "Ansersion2";
		String password = BeecomEncryption.getInstance().getSHA256StrJava("ansersion2");
		UserInfoUnit userInfoUnit = new UserInfoUnit();
		BeecomDB.LoginErrorEnum loginErrorEnum = beecomDB.checkUserPassword(userName, password, userInfoUnit);
		assertEquals(BeecomDB.LoginErrorEnum.LOGIN_OK, loginErrorEnum);
		assertNotEquals(null, userInfoUnit.getUserInfoHbn());
	}

}
