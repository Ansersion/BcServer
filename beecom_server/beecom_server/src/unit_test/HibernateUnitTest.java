package unit_test;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalEnumInfoHbn;
import db.CustomSignalEnumLangInfoHbn;
import db.CustomSignalInfoHbn;
import db.DevInfoHbn;
import db.SignalInfoHbn;
import db.SnInfoHbn;
import db.SystemSignalInfoHbn;
import db.SystemSignalStringInfoHbn;
import db.UserDevRelInfoHbn;
import db.UserInfoHbn;

public class HibernateUnitTest {

	private static final Logger logger = LoggerFactory.getLogger(HibernateUnitTest.class);

	@Test
	public void test() {
		boolean testResultAllOK = true;
		SessionFactory sessionFactory = BeecomDB.buildSessionFactory();
		try (Session session = sessionFactory.openSession()) {
			UserInfoHbn userInfoHbn = session.load(UserInfoHbn.class, 1L);
			assertNotEquals(null, userInfoHbn);
			DevInfoHbn devInfoHbn = session.load(DevInfoHbn.class, 1L);
			assertNotEquals(null, devInfoHbn);
			SnInfoHbn snInfoHbn = session.load(SnInfoHbn.class, 1L);
			assertNotEquals(null, snInfoHbn);
			UserDevRelInfoHbn userDevRelInfoHbn = session.load(UserDevRelInfoHbn.class, 1L);
			assertNotEquals(null, userDevRelInfoHbn);
			SignalInfoHbn signalInfoHbn = session.load(SignalInfoHbn.class, 1L);
			assertNotEquals(null, signalInfoHbn);
			CustomSignalInfoHbn customSignalInfoHbn = session.load(CustomSignalInfoHbn.class, 1L);
			assertNotEquals(null, customSignalInfoHbn);
			SystemSignalInfoHbn systemSignalInfoHbn = session.load(SystemSignalInfoHbn.class, 1L);
			assertNotEquals(null, systemSignalInfoHbn);
			CustomSignalEnumInfoHbn customSignalEnumInfoHbn = session.load(CustomSignalEnumInfoHbn.class, 1L);
			assertNotEquals(null, customSignalEnumInfoHbn);
			CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = session.load(CustomSignalEnumLangInfoHbn.class,1L);
			assertNotEquals(null, customSignalEnumLangInfoHbn);
			SystemSignalStringInfoHbn systemSignalStringInfoHbn = session.load(SystemSignalStringInfoHbn.class, 1L);
			assertNotEquals(null, systemSignalStringInfoHbn);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			testResultAllOK = false;
		} 
		assertEquals(testResultAllOK, true);
	}

}
