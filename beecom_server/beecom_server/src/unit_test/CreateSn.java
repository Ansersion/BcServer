/**
 * 
 */
package unit_test;

import static org.junit.Assert.*;

import org.junit.Test;

import db.BeecomDB;
import other.Util;

/**
 * @author Ansersion
 *
 */
public class CreateSn {

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		long time = System.currentTimeMillis();
		for(int i = 0; i < 3000; i++) {
			assertTrue(beecomDB.putNewDevelopmentSnAndDevInfo("BEECOM_DEVELOP" + (time++), Util.generatePassword(16), 0));
		}
	}

}
