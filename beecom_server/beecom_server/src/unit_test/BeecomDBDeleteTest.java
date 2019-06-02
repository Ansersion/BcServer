package unit_test;


import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		
		/*
		BPSysEnmLangResTable enumLangResTab = BPSysEnmLangResTable.getSysEnmLangResTable();
		try {
			enumLangResTab.loadTab();
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		*/
		
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

	@Test
	public void test() {
		BeecomDB beecomDB = BeecomDB.getInstance();
		beecomDB.clearDeviceSignalInfo(3L);
	}


}
