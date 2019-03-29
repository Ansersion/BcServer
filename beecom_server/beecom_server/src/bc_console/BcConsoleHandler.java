package bc_console;

/**
 * @author Ansersion
 *
 */

import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPDeviceSession;
import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPPacketCONNACK;
import bp_packet.BPPacketGET;
import bp_packet.BPPacketPINGACK;
import bp_packet.BPPacketPOST;
import bp_packet.BPPacketPUSH;
import bp_packet.BPPacketREPORT;
import bp_packet.BPPacketRPRTACK;
import bp_packet.BPSession;
import bp_packet.BPUserSession;
import bp_packet.Payload;
import bp_packet.SignalAttrInfo;
import bp_packet.VariableHeader;
import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.DeviceInfoUnit;
import db.ServerChainHbn;
import db.SignalInfoUnitInterface;
import db.SystemSignalCustomInfoUnit;
import db.UserInfoUnit;
import db.BeecomDB.LoginErrorEnum;
import other.BPError;
import other.Util;

public class BcConsoleHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcConsoleHandler.class);

	Map<Integer, BPSession> cliId2SsnMap = new HashMap<>();
	Map<Long, BPSession> devUniqId2SsnMap = new HashMap<>();
	
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";
	public static final String SESS_ATTR_BP_SESSION = "SESS_ATTR_BP_SESSION";
	
	static enum ProductType {
		PUSH_DEVICE_ID_LIST,
		PUSH_SIGNAL_VALUE,
		POST_SIGNAL_VALUE,
	}
	
	// 捕获异常
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        logger.error(str);
	}

	// 消息接收
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		BcConsoleCommand bcConsoleCommand = (BcConsoleCommand)message;
		String response = bcConsoleCommand.doCommand();
		session.write(response);
	}

	// 会话空闲
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		String s = "Over Alive time";
		logger.info(s);
		session.closeOnFlush();

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}
	
}
