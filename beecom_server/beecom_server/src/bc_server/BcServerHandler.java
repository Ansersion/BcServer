package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.Map;
import java.util.HashMap;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class BcServerHandler extends IoHandlerAdapter {

	Map<Integer, BPSession> CliId2SsnMap = new HashMap<Integer, BPSession>();



	// 捕获异常
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	// 消息接收
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		BPPacket decoded_pack = (BPPacket) message;

		BPPacketType pack_type = decoded_pack.getPackTypeFxHead();
		if (BPPacketType.CONNECT == pack_type) {
			int client_id = decoded_pack.getClientId();
			String user_name = new String(decoded_pack.getUserNamePld());
			byte[] password = decoded_pack.getPasswordPld();
			boolean user_login_flag = decoded_pack.getUsrLoginFlagVrbHead();
			boolean dev_login_flag = decoded_pack.getDevLoginFlagVrbHead();

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);
			/* check user/pwd valid */
			if (!User_DB.ChkUserName(user_name)) {
				System.out.println("Invalid user name:" + user_name);
				return;
			}
			if (!User_DB.ChkUserPwd(user_name, password)) {
				System.out.println(user_name + ": Incorrect password '"
						+ password + "'");
				return;
			}

			/* check client_id valid */
			ClientID_DB cli_ID_DB = ClientID_DB.getInstance();
			client_id = ClientID_DB.distributeID(client_id);
			/* update login flags */
			BPSession newBPSession = new BPSession(user_name.getBytes(),
					password, client_id, user_login_flag, dev_login_flag);
			CliId2SsnMap.put(client_id, newBPSession);

			session.write(pack_ack);

		} else if (BPPacketType.GET == pack_type) {
			int client_id = decoded_pack.getClientId();
			boolean chinese_flag = decoded_pack.getChineseFlag();
			boolean english_flag = decoded_pack.getEnglishFlag();
			int pack_seq = decoded_pack.getPackSeq();
			/* check if there is device update report */
			/*
			 * if (so) { push command to get the latest data
			 * 
			 * } return current data; }
			 */

		} else if (BPPacketType.POST == pack_type) {

		} else if (BPPacketType.PING == pack_type) {
			/* update alive-time of the client ID with this socket */
		} else if (BPPacketType.PUSHACK == pack_type) {
			/* check if server get it */
			/*
			 * if(so) { reply it(for GET/POST) }
			 */
		} else if (BPPacketType.REPORT == pack_type) {
			/* update the device ID map */
		} else if (BPPacketType.DISCONN == pack_type) {
			int client_id = decoded_pack.getClientId();
			/* check if client ID is valid */
			/*
			 * if(so) { update the client ID database } close the socket
			 */
		} else {
			throw new Exception(
					"Error: messageRecevied: Not supported packet type");
		}

	}

	// 会话空闲
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		System.out.println("IDLE" + session.getIdleCount(status));
	}
}
