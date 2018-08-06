package bc_server;

/**
 * @author Ansersion
 *
 */
import java.io.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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
import db.CustomSignalInfoUnit;
import db.CustomSignalNameLangInfoHbn;
import db.DevInfoHbn;
import db.SignalInfoHbn;
import db.SnInfoHbn;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoHbn;
import db.SystemSignalInfoUnit;
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
	public static ConsumerTask consumerTask;

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
		
		consumerTask = new ConsumerTask();
		consumerTask.start();

		NioSocketAcceptor bcAcceptor = new NioSocketAcceptor();

		bcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		bcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcCodecFactory()));
		bcAcceptor.setHandler(new BcServerHandler());

		bcAcceptor.setReuseAddress(true);

		bcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
		bcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDLE_READ_PROC_TIME);
		
		try {
			bcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
	}
	
}
