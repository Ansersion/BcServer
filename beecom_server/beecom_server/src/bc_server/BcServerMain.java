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

		// TODO Auto-generated method stub
		// socket鎺ユ敹鍣�
		NioSocketAcceptor BcAcceptor = new NioSocketAcceptor();

		// 娣诲姞鏃ュ織璁板綍
		BcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		// 娣诲姞缂栫爜瑙ｇ爜鍣�
		BcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcCodecFactory()));
		// BcAcceptor.getFilterChain().addLast("mycoder", new
		// ProtocolCodecFilter(new ByteArrayCodecFactory()));
		// 娣诲姞澶勭悊鍣�(鐢ㄤ簬鎺ユ敹鏁版嵁鍚庡鐞嗗鐞嗘暟鎹�昏緫)
		BcAcceptor.setHandler(new BcServerHandler());

		// 绔彛澶嶇敤
		BcAcceptor.setReuseAddress(true);

		// 璁剧疆璇诲彇鏁版嵁缂撳瓨鍗曚綅byte
		BcAcceptor.getSessionConfig().setReadBufferSize(BC_SOCK_BUFF_SIZE);
		// 璁剧疆澶氶暱鏃堕棿鍚庢帴鏀跺櫒寮�濮嬬┖闂�
		BcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDLE_READ_PROC_TIME);
		try {
			// 缁戝畾鏌愪釜绔彛锛屼綔涓烘暟鎹叆鍙�
			BcAcceptor.bind(new InetSocketAddress(BC_SERVER_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
