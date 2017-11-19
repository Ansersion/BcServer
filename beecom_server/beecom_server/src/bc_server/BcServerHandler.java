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
	int dev_uniq_id = 0; // TODO: read from file

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
			int client_id_old = decoded_pack.getClientId();
			String user_name = new String(decoded_pack.getUserNamePld());
			byte[] password = decoded_pack.getPasswordPld();
			boolean user_clnt_flag = decoded_pack.getUsrClntFlag();
			boolean dev_clnt_flag = decoded_pack.getDevClntFlag();

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);
			if(false == (user_clnt_flag ^ dev_clnt_flag)) {
				System.out.println("Invalid client flag:" + user_clnt_flag + "," + dev_clnt_flag);
				// TODO: set error code
				return;
			}
			/* check user/pwd valid */
			// if (!User_DB.ChkUserName(user_name)) {
			if(!BeecomDB.ChkUserName(user_name)) {
				System.out.println("Invalid user name:" + user_name);
				// TODO: set error code
				return;
			}
			// if (!User_DB.ChkUserPwd(user_name, password)) {
			if(!BeecomDB.ChkUserPwd(user_name, password)) {
				System.out.println(user_name + ": Incorrect password '"
						+ password + "'");
				// TODO: set error code
				return;
			}

			/* check client_id valid */
			int client_id;
			if (0 == client_id_old) {
				client_id = ClientID_DB.distributeID(client_id_old);
			} else if (CliId2SsnMap.containsKey(client_id_old)) {
				// TODL: check if the id expired
				client_id = client_id_old;
			} else {
				// maybe the client_id_old is expired
				client_id = client_id_old;
				pack_ack.setClntIdExpired(true);
			}
			// TODO: if client_id == 0 -> error
			if (pack_ack.isClntIdExpired()) {
				// TODO: use macro instead of constant number
				pack_ack.getVrbHead().setRetCode(0x08);
			}
			if (client_id != client_id_old) {
				pack_ack.setNewClntIdFlg(true);
			}
			pack_ack.getPld().setClientIdLen();
			pack_ack.getPld().setClientId(client_id);
			/* update login flags */
			BPSession newBPSession = new BPSession(user_name.getBytes(),
					password, client_id, user_clnt_flag, dev_clnt_flag);
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
			int client_id = decoded_pack.getClientId();
			String user_name = new String(decoded_pack.getUserNamePld());
			byte[] password = decoded_pack.getPasswordPld();
			boolean user_login_flag = decoded_pack.getUsrClntFlag();
			boolean dev_login_flag = decoded_pack.getDevClntFlag();

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);

			/*
			 * CliId2SsnMap.containsKey(client_id) // TODO: if client_id == 0 ->
			 * error if(pack_ack.isClntIdExpired()) { // TODO: use macro instead
			 * of constant number pack_ack.getVrbHead().setRetCode(0x08); }
			 * if(client_id != client_id_old) { pack_ack.setNewClntIdFlg(true);
			 * } pack_ack.getPld().setClientIdLen();
			 * pack_ack.getPld().setClientId(client_id);
			 */
			/* update login flags */
			/*
			 * BPSession newBPSession = new BPSession(user_name.getBytes(),
			 * password, client_id, user_login_flag, dev_login_flag);
			 * CliId2SsnMap.put(client_id, newBPSession);
			 * 
			 * session.write(pack_ack);
			 */

		} else if (BPPacketType.PUSHACK == pack_type) {
			/* check if server get it */
			/*
			 * if(so) { reply it(for GET/POST) }
			 */
		} else if (BPPacketType.REPORT == pack_type) {
			int client_id = decoded_pack.getClientId();
			BPSession bp_sess;
			if (CliId2SsnMap.containsKey(client_id)) {
				bp_sess = CliId2SsnMap.get(client_id);
			} else {
				throw new Exception("Error: client ID not existed(REPORT)");
			}

			int seq_id = decoded_pack.getPackSeq();
			if (decoded_pack.getVrbHead().getDevNameFlag()) {
				bp_sess.setDevName(decoded_pack.getPld().getDevName());
			}
			if(decoded_pack.getVrbHead().getSysSigFlag()) {
				bp_sess.setSysSigMap(decoded_pack.getPld().getMapDist2SysSigMap());
			}
			if(decoded_pack.getVrbHead().getSigFlag()) {
				// set signal values;
			}

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);

			pack_ack.getVrbHead().setPackSeq(seq_id);
			pack_ack.getVrbHead().setRetCode(0x00);
			
			session.write(pack_ack);
		} else if (BPPacketType.DISCONN == pack_type) {
			int client_id = decoded_pack.getClientId();
			System.out.println("Disconn " + client_id);
			CliId2SsnMap.remove(client_id);
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
