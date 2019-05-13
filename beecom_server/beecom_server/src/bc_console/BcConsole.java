package bc_console;

/**
 * @author Ansersion
 *
 */
import java.io.*;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import db.BeecomDB;
import other.Util;
import server_chain.ServerChain;
import server_chain.ServerNode;

public class BcConsole {
	private static final Logger logger = LoggerFactory.getLogger(BcConsole.class); 
	private static final int BC_CONSOLE_PORT = 8090;
	private static final int BC_CONSOLE_BUF_SIZE = 1024;
	private static final int BC_CONSOLE_IDLE_TIME = 300; // 300s
	
	private static Map<String, ServerNode> serverChildrenMap = new HashMap<>();
	private static ServerNode serverFather = ServerChain.SERVER_NODE_DEFAULT;
	private static Lock serverLock = new ReentrantLock();
	public static int maxDeviceClientPayload = 30000; // 30000 device client payload for 4G memory
	
	public static String updateServerChildren(String file) {
    	/* load the configuration file if any:
    	 * 1->192.168.1.2;
    	 * 3->hk.bcserver.site;
    	 * */
		String ret = "OK";
		serverLock.lock();
		try(FileInputStream fis = new FileInputStream(file)) {
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			try (BufferedReader sysSigIn = new BufferedReader(isr)) {
				String s;
				String pattern = "(.+)->(.+);";
				Pattern r = Pattern.compile(pattern);
				serverChildrenMap.clear();

				while ((s = sysSigIn.readLine()) != null) {
					Matcher m = r.matcher(s);
					if (!m.find()) {
						continue;
					}
					if(m.group(2).trim().isEmpty()) {
						continue;
					}
					ServerNode serverNode = new ServerNode(ServerChain.parseInt(m.group(1).trim()), m.group(2).trim());
					serverChildrenMap.put(m.group(2).trim(), serverNode);
					
					Util.logger(logger, Util.INFO, m.group(1) + "->" + m.group(2));
				}
			}
		} catch (Exception e) {
			Util.logger(logger, Util.DEBUG, e);
			ret = e.getMessage();
		} finally {
			serverLock.unlock();
		}
		
		return ret;
	}
	
	public static String updateServerFather(ServerNode serverNode) {
		String ret = "OK";
		serverLock.lock();
		try {
			if(serverFather.getType() == serverNode.getType() && 0 == serverFather.getAddress().compareTo(serverNode.getAddress())  ) {
				ret = "Same server father";
			} else {
				serverFather = serverNode;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.DEBUG, e);
			ret = e.getMessage();
		} finally {
			serverLock.unlock();
		}
		
		return ret;
	}
	
	public static String updateDeviceClientPayload(int payload) {
		maxDeviceClientPayload = payload;
		return "* DevicePayload: " + maxDeviceClientPayload + "\r\n";
	}
	
	public static String print() {
		String ret = "";
		serverLock.lock();
		try {
			ret += "* OpenRegister: " + BPPacket.isOpenRegister() + "\r\n";
			ret += "* Father: "  + serverFather.getType() + "->" + serverFather.getAddress() + "\r\n";
			for (Map.Entry<String, ServerNode> entry : serverChildrenMap.entrySet()) { 
				ret += "* Child: "  + entry.getValue().getType() + "->" + entry.getValue().getAddress() + "\r\n";
			}
			ret += "* DevicePayload: " + maxDeviceClientPayload + "; current: " + BeecomDB.getInstance().getDevicePaylaod() + "\r\n";
		} catch (Exception e) {
			Util.logger(logger, Util.DEBUG, e);
			ret = e.getMessage();
		} finally {
			serverLock.unlock();
		}
		
		return ret;
	}
	
	public static String adopt(ServerNode serverNode) {
		String ret = "OK";
		serverLock.lock();
		try {
			if(serverChildrenMap.containsKey(serverNode.getAddress())) {
				ret = "Already adopted";
			} else {
				serverChildrenMap.put(serverNode.getAddress(), serverNode);
			}
		} catch (Exception e) {
			Util.logger(logger, Util.DEBUG, e);
			ret = e.getMessage();
		} finally {
			serverLock.unlock();
		}
		
		return ret;
	}

	/**
	 * @param args
	 */
	public void start() {

		NioSocketAcceptor bcAcceptor = new NioSocketAcceptor();

		bcAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
		bcAcceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new BcConsoleCodecFactory()));
		bcAcceptor.setHandler(new BcConsoleHandler());

		bcAcceptor.setReuseAddress(true);

		bcAcceptor.getSessionConfig().setReadBufferSize(BC_CONSOLE_BUF_SIZE);
		bcAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				BC_CONSOLE_IDLE_TIME);
		
		try {
			logger.info("start bc console");
			bcAcceptor.bind(new InetSocketAddress("127.0.0.1", BC_CONSOLE_PORT));
		} catch (IOException e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}
	
}
