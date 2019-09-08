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
import sys_sig_table.BPSysLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BcServerMain {

	private static final Logger logger = LoggerFactory.getLogger(BcServerMain.class); 
	
	public static final String BC_SERVER_VERSION = "1.0.0.1"; 
			
	private static final int BC_SERVER_PORT = 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_BOTH_PROC_TIME = 30;
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
		Util.logger(logger, Util.INFO, "BcServer Version: " + BC_SERVER_VERSION);
		
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
				IDLE_BOTH_PROC_TIME);
		
		try {
			bcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}
	
}
