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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPPacketCONNACK;
import bp_packet.BPPacketPINGACK;
import bp_packet.BPPacketRPRTACK;
import bp_packet.BPSession;
import bp_packet.DevSigData;
import bp_packet.FixedHeader;
import bp_packet.VariableHeader;
import db.BeecomDB;
import db.ClientID_DB;
import db.DB_DevInfoRec;
import db.DB_SysSigRec;
import other.BPValue;

public class BcServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcServerHandler.class);
	

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
		if (message instanceof String) {
			byte firstByte = (byte) (BPPacketType.POST.getType());
			firstByte = (byte) (firstByte << 4);
			BPPacket packTst = BPPackFactory.createBPPack(firstByte);
			FixedHeader fxHead = packTst.getFxHead();
			fxHead.setBPType(firstByte);
			fxHead.setFlags(firstByte);
			// pack_tst_POST
			BPSession sess = (BPSession) session.getAttribute(SESS_ATTR_ID);
			packTst.getVrbHead().setClientId(sess.getClientId());
			packTst.getVrbHead().setPackSeq(sess.getSeqIdDevClnt());
			sess.setSeqIdDevClnt(sess.getSeqIdDevClnt()+1);
			DevSigData sigData = new DevSigData();
			Map<Integer, Short> sigMap = sigData.get2ByteDataMap();
			Integer sigId = 0xE000;
			Short sigVal = 825;
			sigMap.put(sigId, sigVal);
			
			/*
			Map<Integer, Short> sig_map_new = sig_data.
			*/

			sigId = 0xE001;
			sigVal = 826;
			sigMap.put(sigId, sigVal);
			packTst.getPld().setSigData(sigData);

			session.write(packTst);
			return;
		}
		BPPacket decodedPack = (BPPacket) message;

		BPPacketType pack_type = decodedPack.getPackTypeFxHead();
		if (BPPacketType.CONNECT == pack_type) {
			int client_id_old = decodedPack.getClientId();
			String user_name = new String(decodedPack.getUserNamePld());
			int level = decodedPack.getVrbHead().getLevel();
			byte[] password = decodedPack.getPasswordPld();
			boolean user_clnt_flag = decodedPack.getUsrClntFlag();
			boolean dev_clnt_flag = decodedPack.getDevClntFlag();
			long dev_uniq_id = 0;
			// int id = 0;

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decodedPack);
			if (level > BPPacket.BP_LEVEL) {
				logger.warn("Unsupported level: {} > {}", level, BPPacket.BP_LEVEL);
				pack_ack.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_LEVEL_ERR);
				session.write(pack_ack);
				session.closeOnFlush();
				return;
			}
			if (false == (user_clnt_flag ^ dev_clnt_flag)) {
				logger.info("Invalid client flag:{}, {}", user_clnt_flag, dev_clnt_flag);
				pack_ack.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_CLNT_UNKNOWN);
				session.write(pack_ack);
				session.closeOnFlush();
				return;
			}
			/* check user/pwd valid */
			if (user_clnt_flag) {
				if (!BeecomDB.ChkUserName(user_name)) {
					logger.warn("Invalid user name:{}" + user_name);
					pack_ack.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_INVALID);
					session.write(pack_ack);
					session.closeOnFlush();
					return;
				}
				if (!BeecomDB.ChkUserPwd(user_name, password)) {
					logger.info("{}: Incorrect password '{}'", user_name, password);
					pack_ack.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_PWD_INVALID);
					session.write(pack_ack);
					session.closeOnFlush();
					return;
				}
			} else {
				try {
					dev_uniq_id = Integer.parseInt(user_name);
					if (!BeecomDB.ChkDevUniqId(dev_uniq_id)) {
						logger.warn("Invalid device unique id:", user_name);
						pack_ack.getVrbHead().setRetCode(
								BPPacketCONNACK.RET_CODE_USER_INVALID);
						session.write(pack_ack);
						session.closeOnFlush();
						return;
					}
				} catch (NumberFormatException e) {
					pack_ack.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_INVALID);
					session.write(pack_ack);
					session.closeOnFlush();
					return;
				}
				if (!BeecomDB.ChkDevPwd(dev_uniq_id, password)) {
					logger.info("{}: Incorrect password '{}'", user_name, password);
					pack_ack.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_PWD_INVALID);
					session.write(pack_ack);
					session.closeOnFlush();
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
				logger.warn("Err: Expired client id({})", client_id);
				pack_ack.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_CLNT_ID_INVALID);
				session.write(pack_ack);
				session.closeOnFlush();
				return;
			}
			if (client_id != client_id_old) {
				pack_ack.setNewClntIdFlg();
			}
			pack_ack.getPld().setClientIdLen();
			pack_ack.getPld().setClientId(client_id);
			/* update login flags */
			BPSession newBPSession = new BPSession(user_name.getBytes(),
					password, client_id, user_clnt_flag, dev_clnt_flag, dev_uniq_id);
			CliId2SsnMap.put(client_id, newBPSession);
			if (dev_clnt_flag) {
				DevUniqId2SsnMap.put(dev_uniq_id, newBPSession);
			}
			logger.info("Alive time={}", decodedPack.getVrbHead().getAliveTime());
			session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
					decodedPack.getVrbHead().getAliveTime());
			session.setAttribute(SESS_ATTR_ID, newBPSession);

			session.write(pack_ack);

		} else if (BPPacketType.GET == pack_type) {
			int client_id = decodedPack.getClientId();
			boolean chinese_flag = decodedPack.getChineseFlag();
			boolean english_flag = decodedPack.getEnglishFlag();
			int pack_seq = decodedPack.getPackSeq();
			/* check if there is device update report */
			/*
			 * if (so) { push command to get the latest data
			 * 
			 * } return current data; }
			 */
		} else if (BPPacketType.GETACK == pack_type) {
			byte flags = decodedPack.getVrbHead().getFlags();
			int client_id = decodedPack.getVrbHead().getClientId();
			int seq_id = decodedPack.getVrbHead().getPackSeq();
			int ret_code = decodedPack.getVrbHead().getRetCode();
			if (ret_code != 0) {
				logger.warn("Error(GETACK): get return code={}", ret_code);
				return;
			}
			BPSession bp_sess = (BPSession) session.getAttribute(SESS_ATTR_ID);
			DevSigData dev_sig_data = decodedPack.getPld().getSigData();
			dev_sig_data.dump();

		} else if (BPPacketType.POST == pack_type) {
		} else if (BPPacketType.POSTACK == pack_type) {
			byte flags = decodedPack.getVrbHead().getFlags();
			int client_id = decodedPack.getVrbHead().getClientId();
			int seq_id = decodedPack.getVrbHead().getPackSeq();
			int ret_code = decodedPack.getVrbHead().getRetCode();
			if (ret_code != 0) {
				logger.info("Error(GETACK): get return code = {}", ret_code);
				return;
			}
			logger.info("POSTACK: flags={}, cid={}, sid={}, rcode={}", flags, client_id
					, seq_id, ret_code);

		} else if (BPPacketType.PING == pack_type) {
			byte flags = decodedPack.getVrbHead().getFlags();
			int clntId = decodedPack.getVrbHead().getClientId();
			int seq_id = decodedPack.getVrbHead().getPackSeq();
			int ret_code = BPPacketRPRTACK.RET_CODE_OK;
			logger.info("PING: flags={}, cid={}, sid={}", flags, clntId, seq_id);

			BPPacket pack_ack = BPPackFactory.createBPPackAck(decodedPack);

			BPSession bp_sess_para = (BPSession) session
					.getAttribute(SESS_ATTR_ID);
			if (clntId != bp_sess_para.getClientId()) {
				pack_ack.getVrbHead().setRetCode(
						BPPacketPINGACK.RET_CODE_CLNT_ID_INVALID);
				session.write(pack_ack);
				session.closeOnFlush();
				return;
			}

			pack_ack.getVrbHead().setClientId(clntId);
			pack_ack.getVrbHead().setPackSeq(seq_id);
			pack_ack.getVrbHead().setRetCode(BPPacketPINGACK.RET_CODE_OK);

			session.write(pack_ack);
		} else if (BPPacketType.PUSHACK == pack_type) {
			/* check if server get it */
			/*
			 * if(so) { reply it(for GET/POST) }
			 */
		} else if (BPPacketType.REPORT == pack_type) {

			int client_id = decodedPack.getClientId();
			int ret_code = BPPacketRPRTACK.RET_CODE_OK;
			int seq_id = decodedPack.getPackSeq();
			BPSession bp_sess = (BPSession) session.getAttribute(SESS_ATTR_ID);
			BeecomDB db = BeecomDB.getInstance();
			DB_DevInfoRec dev_rec = db.getDevInfoRec(Integer
					.parseInt(new String(bp_sess.userName)));
			
			if (dev_rec.getDevUniqId() == 0) {
				logger.debug("TODO: dev_rec.getDevUniqId() == 0");
				return;
			}
			
			BPPacket pack_ack = BPPackFactory.createBPPackAck(decodedPack);
			
			VariableHeader vrb = decodedPack.getVrbHead();
			
			do {
				if(bp_sess.getClientId() != client_id) {
					logger.error("Err: client id err");
					ret_code = BPPacketRPRTACK.RET_CODE_CLNT_ID_INVALID;
					break;
				}
				if (vrb.getSigFlag()) {
					if (vrb.getSysSigMapFlag() || vrb.getDevNameFlag()) {
						ret_code = BPPacketRPRTACK.RET_CODE_FLAGS_INVALID;
						break;
					}
					logger.debug("REPORT signal values");
					decodedPack.getPld().getSigData().dump();
					if(!bp_sess.setSysSig(decodedPack.getPld().getSigData())) {
						ret_code = bp_sess.getError().getErrId();
						if(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID == ret_code) {
							pack_ack.getPld().Error = bp_sess.getError();
						}
						break;
					}

				} else {

					if (decodedPack.getVrbHead().getDevNameFlag()) {
						bp_sess.setDevName(decodedPack.getPld().getDevName());
						// System.out.println("DevName: " +
						dev_rec.setDevName(bp_sess.getDevName());
					}
					if (decodedPack.getVrbHead().getSysSigMapFlag()) {
						bp_sess.setSysSigMap(decodedPack.getPld()
								.getMapDist2SysSigMap());
						bp_sess.initSysSigValDefault();

						DB_SysSigRec sys_sig_rec;
						if (dev_rec.getSysSigTabId() == 0) {
							dev_rec.setSysSigTabId(dev_rec.getDevUniqId());
							sys_sig_rec = new DB_SysSigRec();
							sys_sig_rec.setSysSigTabId(dev_rec.getDevUniqId());

							Map<Integer, Byte[]> sys_sig_map = decodedPack
									.getPld().getMapDist2SysSigMap();
							sys_sig_rec.setSysSigEnableLst(sys_sig_map);
							sys_sig_rec.insertRec(db.getConn());
						} else {
							logger.debug("TODO: get sys_sig_info from database");
							List<DB_SysSigRec> lst = db.getSysSigRecLst();
							for (int i = 0; i < lst.size(); i++) {
								if (lst.get(i).getSysSigTabId() == dev_rec
										.getSysSigTabId()) {
									lst.get(i).dumpRec();
								}
							}
						}

					}
				}
			} while (false);
			
			logger.debug("Start dump(REPORT)");
			bp_sess.dumpSysSig();
			
			pack_ack.getVrbHead().setPackSeq(seq_id);
			pack_ack.getVrbHead().setRetCode(ret_code);

			dev_rec.updateRec(db.getConn());

			session.write(pack_ack);
		} else if (BPPacketType.DISCONN == pack_type) {
			int client_id = decodedPack.getClientId();
			logger.info("Disconn " + client_id);
			CliId2SsnMap.remove(client_id);
			session.closeOnFlush();
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
		logger.info("Over Alive time");
		session.closeOnFlush();

	}
}
