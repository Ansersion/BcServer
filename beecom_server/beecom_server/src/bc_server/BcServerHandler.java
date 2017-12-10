package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class BcServerHandler extends IoHandlerAdapter {

	Map<Integer, BPSession> CliId2SsnMap = new HashMap<Integer, BPSession>();
	Map<Long, BPSession> DevUniqId2SsnMap = new HashMap<Long, BPSession>();
	int dev_uniq_id = 0; // TODO: read from file
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";

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
		if(message instanceof String) {
			byte first_byte = (byte)(BPPacketType.GET.getType());
			first_byte = (byte)(first_byte << 4);
			BPPacket pack_tst = BPPackFactory.createBPPack(first_byte);
			FixedHeader fxHead = pack_tst.getFxHead();
			fxHead.setBPType(first_byte);
			fxHead.setFlags(first_byte);
			// pack_tst;
			BPSession sess = (BPSession)session.getAttribute(SESS_ATTR_ID);
			pack_tst.getVrbHead().setClientId(sess.ClientId);
			pack_tst.getVrbHead().setPackSeq(0);
			Map<Integer, List<Integer>> sig_map = pack_tst.getPld().getMapDev2SigLst();
			List<Integer> sig_lst = new ArrayList<Integer>();
			sig_lst.add(0xE000);
			sig_lst.add(0xE001);
			sig_map.put(sess.ClientId, sig_lst);
			session.write(pack_tst);
			return;
		}
		BPPacket decoded_pack = (BPPacket) message;

		BPPacketType pack_type = decoded_pack.getPackTypeFxHead();
		if (BPPacketType.CONNECT == pack_type) {
			int client_id_old = decoded_pack.getClientId();
			String user_name = new String(decoded_pack.getUserNamePld());
			byte[] password = decoded_pack.getPasswordPld();
			boolean user_clnt_flag = decoded_pack.getUsrClntFlag();
			boolean dev_clnt_flag = decoded_pack.getDevClntFlag();
			long dev_uniq_id = 0;
			String dev_name = new String("");
			int id = 0;

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);
			if (false == (user_clnt_flag ^ dev_clnt_flag)) {
				System.out.println("Invalid client flag:" + user_clnt_flag
						+ "," + dev_clnt_flag);
				// TODO: set error code
				return;
			}
			/* check user/pwd valid */
			if (user_clnt_flag) {
				if (!BeecomDB.ChkUserName(user_name)) {
					System.out.println("Invalid user name:" + user_name);
					// TODO: set error code
					return;
				}
				if (!BeecomDB.ChkUserPwd(user_name, password)) {
					System.out.println(user_name + ": Incorrect password '"
							+ password + "'");
					// TODO: set error code
					return;
				}
			} else {
				dev_uniq_id = Integer.parseInt(user_name);
				if (!BeecomDB.ChkDevUniqId(dev_uniq_id)) {
					System.out.println("Invalid device unique id:" + user_name);
					// TODO: set error code
					return;
				}
				if (!BeecomDB.ChkDevPwd(dev_uniq_id, password)) {
					System.out.println(user_name + ": Incorrect password '"
							+ password + "'");
					// TODO: set error code
					return;
				}
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
			if (dev_clnt_flag) {
				DevUniqId2SsnMap.put(dev_uniq_id, newBPSession);
				id = (int)dev_uniq_id;
			}
			session.setAttribute(SESS_ATTR_ID, newBPSession);

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
		} else if(BPPacketType.GETACK == pack_type) {
			byte flags = decoded_pack.getVrbHead().getFlags();
			int client_id = decoded_pack.getVrbHead().getClientId();
			int seq_id = decoded_pack.getVrbHead().getPackSeq();
			int ret_code = decoded_pack.getVrbHead().getRetCode();
			if(ret_code != 0) {
				System.out.println("Error(GETACK): get return code = " + ret_code);
				return;
			}
			BPSession bp_sess = (BPSession)session.getAttribute(SESS_ATTR_ID);
			DevSigData dev_sig_data = decoded_pack.getPld().getSigData();
			dev_sig_data.dump();

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
			BPSession bp_sess = (BPSession)session.getAttribute(SESS_ATTR_ID);
			
			/*
			if (CliId2SsnMap.containsKey(client_id)) {
				bp_sess = CliId2SsnMap.get(client_id);
			} else {
				throw new Exception("Error: client ID not existed(REPORT)");
			}
			*/
			
			BeecomDB db = BeecomDB.getInstance();
			
			/*
			System.out.println("test: before handle report");
			db.dumpDevInfo();
			*/
			
			int seq_id = decoded_pack.getPackSeq();
			
			DB_DevInfoRec dev_rec = db.getDevInfoRec(Integer.parseInt(new String(bp_sess.UserName)));
			if(dev_rec.getDevUniqId() == 0) {
				// TODO: handle the error;
				System.out.println("TODO: dev_rec.getDevUniqId() == 0");
				return;
			}
			
			if (decoded_pack.getVrbHead().getDevNameFlag()) {
				// bp_sess.setDevName(decoded_pack.getPld().getDevName());
				bp_sess.setDevName(decoded_pack.getPld().getDevName());
				// System.out.println("DevName: " + decoded_pack.getPld().getDevName());
				dev_rec.setDevName(bp_sess.getDevName());
			}
			if (decoded_pack.getVrbHead().getSysSigMapFlag()) {
				bp_sess.setSysSigMap(decoded_pack.getPld()
						.getMapDist2SysSigMap());
				DB_SysSigRec sys_sig_rec;
				if(dev_rec.getSysSigTabId() == 0) {
					dev_rec.setSysSigTabId(dev_rec.getDevUniqId());
					sys_sig_rec = new DB_SysSigRec();
					sys_sig_rec.setSysSigTabId(dev_rec.getDevUniqId());
					
					Map<Integer, Byte[]> sys_sig_map = decoded_pack.getPld().getMapDist2SysSigMap();
					sys_sig_rec.setSysSigEnableLst(sys_sig_map);
					sys_sig_rec.insertRec(db.getConn());
				} else {
					System.out.println("TODO: get sys_sig_info from database");
					List<DB_SysSigRec> lst = db.getSysSigRecLst();
					for(int i = 0; i < lst.size(); i++) {
						if(lst.get(i).getSysSigTabId() == dev_rec.getSysSigTabId()) {
							lst.get(i).dumpRec();
						}
					}
				}
				
			}
			if (decoded_pack.getVrbHead().getSigFlag()) {
				// TODO: set signal values;
			}
			BPPacket pack_ack = BPPackFactory.createBPPackAck(decoded_pack);
			
			/*
			System.out.println("Start dev_info_dump");
			dev_rec.dumpRec();
			*/

			pack_ack.getVrbHead().setPackSeq(seq_id);
			pack_ack.getVrbHead().setRetCode(0x00);
			
			dev_rec.updateRec(db.getConn());
			
			/*
			System.out.println("test: after handle report");
			db.dumpDevInfo();
			*/

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
