/**
 * 
 */
package unit_test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.BeecomDB;
import db.DevInfoHbn;
import db.UserInfoUnit;
import other.BeecomEncryption;
import other.Util;

/**
 * @author isdt
 *
 */
public class CreateSn {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		long time = System.currentTimeMillis();
		for(int i = 0; i < 3000; i++) {
			assertTrue(beecomDB.putNewDevelopmentSnAndDevInfo("BEECOM_DEVELOP" + (time++), Util.generatePassword(16), 0));
		}
	}

}
