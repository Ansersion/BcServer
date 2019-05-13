package unit_test;

import static org.junit.Assert.*;

import org.junit.Test;

import db.BeecomDB;


public class CheckGetSNPermissionUnitTest {

	@Test
	public void testCheckGetSNPermission() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		String sn = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2";
		String sn3 = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX3";
		long userAdmin = 2;
		long userNoPermission = 3;
		BeecomDB.GetSnErrorEnum getSnErrorEnum = beecomDB.checkGetSNPermission(userAdmin, sn);
		assertEquals(BeecomDB.GetSnErrorEnum.GET_SN_OK, getSnErrorEnum);
		getSnErrorEnum = beecomDB.checkGetSNPermission(userNoPermission, sn);
		assertEquals(BeecomDB.GetSnErrorEnum.GET_SN_PERMISSION_DENY, getSnErrorEnum);
		getSnErrorEnum = beecomDB.checkGetSNPermission(3, sn3);
		assertEquals(BeecomDB.GetSnErrorEnum.GET_SN_OK, getSnErrorEnum);
	}

}
