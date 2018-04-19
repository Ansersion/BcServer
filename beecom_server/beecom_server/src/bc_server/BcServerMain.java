package bc_server;

/**
 * @author Ansersion
 *
 */

import java.io.IOException;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.InetSocketAddress;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import sys_sig_table.BPSysEnmLangResTable;
import sys_sig_table.BPSysSigLangResTable;
import sys_sig_table.BPSysSigTable;

public class BcServerMain {

	private static final int BC_SERVER_PORT = 8025;
	private static final int BC_SOCK_BUFF_SIZE = 2048;
	private static final int IDLE_READ_PROC_TIME = 3600;

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		BPSysSigLangResTable sig_lang_res_tab = BPSysSigLangResTable.getSysSigLangResTable();
		try {
			sig_lang_res_tab.loadTab();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BPSysEnmLangResTable enm_lang_res_tab = BPSysEnmLangResTable.getSysEnmLangResTable();
		try {
			enm_lang_res_tab.loadTab();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BPSysSigTable sys_sig_tab = BPSysSigTable.getSysSigTableInstance();
		try {
			sys_sig_tab.loadTab();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NioSocketAcceptor BcAcceptor = new NioSocketAcceptor();

		BcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		BcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcCodecFactory()));
		BcAcceptor.setHandler(new BcServerHandler());

		BcAcceptor.setReuseAddress(true);

		BcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
		BcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDLE_READ_PROC_TIME);
		try {
			BcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
