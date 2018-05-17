package bc_server;

/**
 * @author Ansersion
 *
 */
import java.io.*;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalEnumInfoHbn;
import db.CustomSignalEnumLangInfoHbn;
import db.CustomSignalInfoHbn;
import db.CustomSignalNameLangInfoHbn;
import db.DevInfoHbn;
import db.SignalInfoHbn;
import db.SnInfoHbn;
import db.SystemSignalInfoHbn;
import db.SystemSignalStringInfoHbn;
import db.UserDevRelInfoHbn;
import db.UserInfoHbn;
import sys_sig_table.BPSysEnmLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BcServerMain {

	private static final Logger logger = LoggerFactory.getLogger(BcServerMain.class); 

	private static final int BC_SERVER_PORT = 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_READ_PROC_TIME = 3600;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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

		NioSocketAcceptor bcAcceptor = new NioSocketAcceptor();

		bcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		bcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcCodecFactory()));
		bcAcceptor.setHandler(new BcServerHandler());

		bcAcceptor.setReuseAddress(true);

		bcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
		bcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDLE_READ_PROC_TIME);
		
		/*
		BeecomDB beecomDB = BeecomDB.getInstance();
		long n = beecomDB.getDeviceUniqId("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX2");
		System.out.println("n: " + n);
		*/
		BeecomDB beecomDB = BeecomDB.getInstance();
		List<Integer> sysSigMap = beecomDB.getSysSigMapLst(3L);
		for(int i = 0; i < sysSigMap.size(); i++) {
			System.out.println("sysSig: " + sysSigMap.get(i));
		}
		
		/*
		SessionFactory sessionFactory = buildSessionFactory(); 
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			   tx = session.beginTransaction();
			   // do some work
			   UserInfoHbn userInfoHbn = session.load(UserInfoHbn.class, 1L);
			   logger.info(userInfoHbn.toString());
			   DevInfoHbn devInfoHbn = session.load(DevInfoHbn.class, 1L);
			   logger.info(devInfoHbn.toString());
			   SnInfoHbn snInfoHbn = session.load(SnInfoHbn.class, 1L);
			   logger.info(snInfoHbn.toString());
			   UserDevRelInfoHbn userDevRelInfoHbn = session.load(UserDevRelInfoHbn.class, 1L);
			   logger.info(userDevRelInfoHbn.toString());
			   SignalInfoHbn signalInfoHbn = session.load(SignalInfoHbn.class, 1L);
			   logger.info(signalInfoHbn.toString());
			   CustomSignalInfoHbn customSignalInfoHbn = session.load(CustomSignalInfoHbn.class, 1L);
			   logger.info(customSignalInfoHbn.toString());
			   SystemSignalInfoHbn systemSignalInfoHbn = session.load(SystemSignalInfoHbn.class, 1L);
			   logger.info(systemSignalInfoHbn.toString());
			   CustomSignalEnumInfoHbn customSignalEnumInfoHbn = session.load(CustomSignalEnumInfoHbn.class, 1L);
			   logger.info(customSignalEnumInfoHbn.toString());
			   CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = session.load(CustomSignalNameLangInfoHbn.class, 1L);
			   logger.info(customSignalNameLangInfoHbn.toString());
			   CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = session.load(CustomSignalEnumLangInfoHbn.class, 1L);
			   logger.info(customSignalEnumLangInfoHbn.toString());
			   SystemSignalStringInfoHbn systemSignalStringInfoHbn = session.load(SystemSignalStringInfoHbn.class, 1L);
			   logger.info(systemSignalStringInfoHbn.toString());
			   // tx.commit();
			}
			catch (Exception e) {
			   if (tx!=null) {
				   tx.rollback();
			   }
	            StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw, true));
	            String str = sw.toString();
	            logger.error(str);
			}finally {
			   session.close();
			}
		
		try {
			bcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
		*/
	}
	
}
