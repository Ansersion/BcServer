package unit_test;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import other.Util;
import sys_sig_table.BPSysLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BeecomDBDeleteTest {

	private static final Logger logger = LoggerFactory.getLogger(BeecomDBDeleteTest.class);

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
		beecomDB.clearDeviceSignalInfo(3L);
	}


}
