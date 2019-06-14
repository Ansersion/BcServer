/*
 * Author: Ansersion
 * Date: 2018.05.27
 * Note: Use test bc_server_db database
 */

package unit_test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import other.Util;
import sys_sig_table.BPSysLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BeecomDBUnitTest {
	
	private static final Logger logger = LoggerFactory.getLogger(BeecomDBUnitTest.class);

	@Before
	public void setUp() throws Exception {
		BPSysSigLangResTable sigLangResTab = BPSysSigLangResTable.getSysSigLangResTable();
		try {
			sigLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		BPSysLangResTable.enumLangResTab = new BPSysLangResTable("config/sys_enum_language_resource.csv");
		try {
			BPSysLangResTable.enumLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		BPSysLangResTable.unitLangResTab = new BPSysLangResTable("config/sys_unit_language_resource.csv");
		try {
			BPSysLangResTable.unitLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		BPSysLangResTable.groupLangResTab = new BPSysLangResTable("config/sys_group_language_resource.csv");
		try {
			BPSysLangResTable.groupLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
		if(!sysSigTab.loadTab()) {
			logger.error("!sysSigTab.loadTab()");
			System.exit(0);
		}
	}

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		long n = beecomDB.getDeviceUniqId("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2", null);
		assertEquals(n,2);
		
		List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = new ArrayList<>();
		systemSignalCustomInfoUnit = beecomDB.getSystemSignalCustomInfoUnitLst(3L, systemSignalCustomInfoUnit);
		assertEquals(systemSignalCustomInfoUnit.size(), 1);
		assertEquals(systemSignalCustomInfoUnit.get(0).getSysSigId(), 0xE001);
		
		List<SystemSignalInfoUnit> systemSignalInfoUnitLst = new ArrayList<>();
		systemSignalInfoUnitLst = beecomDB.getSystemSignalUnitLst(3L, systemSignalInfoUnitLst);
		assertEquals(systemSignalInfoUnitLst.size(), 2);
		assertEquals(systemSignalInfoUnitLst.get(0).getSignalId(), 0xE001);
		assertEquals(systemSignalInfoUnitLst.get(1).getSignalId(), 0xE002);
		
		List<CustomSignalInfoUnit> customSignalInfoUnitLst = new ArrayList<>();
		customSignalInfoUnitLst = beecomDB.getCustomSignalUnitLst(3L, customSignalInfoUnitLst, 0x80);
		assertEquals(customSignalInfoUnitLst.size(), 2);
		assertEquals(customSignalInfoUnitLst.get(0).getSignalId(), 1);
		assertEquals(customSignalInfoUnitLst.get(1).getSignalId(), 2);
	}

}
