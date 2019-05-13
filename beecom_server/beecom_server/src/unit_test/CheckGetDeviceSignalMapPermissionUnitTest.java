package unit_test;

import static org.junit.Assert.*;

import org.junit.Test;

import db.BeecomDB;

public class CheckGetDeviceSignalMapPermissionUnitTest {

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();

		long deviceId = 1;
		long deviceIdNoOtherUser = 3;
		long userAdminId = 1;
		long userOtherId = 2;
		
		assertEquals(true, beecomDB.checkGetDeviceSignalMapPermission(userAdminId, deviceId));
		assertEquals(true, beecomDB.checkGetDeviceSignalMapPermission(userOtherId, deviceId));
		assertEquals(false, beecomDB.checkGetDeviceSignalMapPermission(userOtherId, deviceIdNoOtherUser));
	}

}
