package bc_server;

/**
 * @author Ansersion
 *
 */
import java.io.*;

import java.net.InetSocketAddress;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_console.BcConsole;
import db.BeecomDB;
import other.Util;
import sys_sig_table.BPSysEnmLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BcServerMain {

	private static final Logger logger = LoggerFactory.getLogger(BcServerMain.class); 

	private static final int BC_SERVER_PORT = 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_READ_PROC_TIME = 60;
	public static final int IDLE_TIME_MIN = 30;
	public static final int IDLE_TIME_DEFAULT_USER_CLIENT = 600;
	public static final int IDLE_TIME_DEFAULT_DEVICE_CLIENT = 60;
	public static final int IDLE_TIME_MAX = 0xFFFF;
	public static ConsumerTask consumerTask;
	public static BcConsole bcConsole;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BPSysSigLangResTable sigLangResTab = BPSysSigLangResTable.getSysSigLangResTable();
		try {
			sigLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		BPSysEnmLangResTable enumLangResTab = BPSysEnmLangResTable.getSysEnmLangResTable();
		try {
			enumLangResTab.loadTab();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
		if(!sysSigTab.loadTab()) {
			logger.error("!sysSigTab.loadTab()");
			System.exit(0);
		}
		
		consumerTask = new ConsumerTask();
		consumerTask.start();
		
		bcConsole = new BcConsole();
		bcConsole.start();
		
		/* initialize database */
		BeecomDB.getInstance();

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
			Util.logger(logger, Util.ERROR, e);
		}
	}
	
}
