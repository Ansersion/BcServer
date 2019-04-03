package bc_console;

/**
 * @author Ansersion
 *
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcConsoleHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcConsoleHandler.class);
	
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";
	public static final String SESS_ATTR_BP_SESSION = "SESS_ATTR_BP_SESSION";
	
	enum ProductType {
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
		String response = bcConsoleCommand.doCommand() + "\r\n";
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
