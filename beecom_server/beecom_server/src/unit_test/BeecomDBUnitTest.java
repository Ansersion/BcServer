/*
 * Author: Ansersion
 * Date: 2018.05.27
 * Note: Use test bc_server_db database
 */

package unit_test;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import sys_sig_table.BPSysEnmLangResTable;
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		
		BPSysEnmLangResTable enumLangResTab = BPSysEnmLangResTable.getSysEnmLangResTable();
		try {
			enumLangResTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		
		BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
		try {
			sysSigTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		long n = beecomDB.getDeviceUniqId("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2", null);
		assertEquals(n,2);
		
		List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = new ArrayList<SystemSignalCustomInfoUnit>();
		systemSignalCustomInfoUnit = beecomDB.getSystemSignalCustomInfoUnitLst(3L, systemSignalCustomInfoUnit);
		assertEquals(systemSignalCustomInfoUnit.size(), 1);
		assertEquals(systemSignalCustomInfoUnit.get(0).getSysSigId(), 0xE001);
		
		List<SystemSignalInfoUnit> systemSignalInfoUnitLst = new ArrayList<SystemSignalInfoUnit>();
		systemSignalInfoUnitLst = beecomDB.getSystemSignalUnitLst(3L, systemSignalInfoUnitLst);
		assertEquals(systemSignalInfoUnitLst.size(), 2);
		assertEquals(systemSignalInfoUnitLst.get(0).getSysSigId(), 0xE001);
		assertEquals(systemSignalInfoUnitLst.get(1).getSysSigId(), 0xE002);
		
		List<CustomSignalInfoUnit> customSignalInfoUnitLst = new ArrayList<CustomSignalInfoUnit>();
		customSignalInfoUnitLst = beecomDB.getCustomSignalUnitLst(3L, customSignalInfoUnitLst, 0x80);
		assertEquals(customSignalInfoUnitLst.size(), 2);
		assertEquals(customSignalInfoUnitLst.get(0).getCusSigId(), 1);
		assertEquals(customSignalInfoUnitLst.get(1).getCusSigId(), 2);
	}

}
