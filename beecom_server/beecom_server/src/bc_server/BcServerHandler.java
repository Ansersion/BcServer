package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import db.ClientIDDB;
import db.DBDevInfoRec;
import db.DBSysSigRec;

public class BcServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcServerHandler.class);
	

	Map<Integer, BPSession> cliId2SsnMap = new HashMap<>();
	Map<Long, BPSession> devUniqId2SsnMap = new HashMap<>();
	long devUniqId = 0;
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";

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
			Map<Integer, Short> sig_map_new = sigData.
			*/

			sigId = 0xE001;
			sigVal = 826;
			sigMap.put(sigId, sigVal);
			packTst.getPld().setSigData(sigData);

			session.write(packTst);
			return;
		}
		BPPacket decodedPack = (BPPacket) message;

		BPPacketType packType = decodedPack.getPackTypeFxHead();
		if (BPPacketType.CONNECT == packType) {
			int clientIdOld = decodedPack.getClientId();
			String userName = new String(decodedPack.getUserNamePld());
			int level = decodedPack.getVrbHead().getLevel();
			byte[] password = decodedPack.getPasswordPld();
			boolean userClntFlag = decodedPack.getUsrClntFlag();
			boolean devClntFlag = decodedPack.getDevClntFlag();

			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			if (level > BPPacket.BP_LEVEL) {
				logger.warn("Unsupported level: {} > {}", level, BPPacket.BP_LEVEL);
				packAck.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_LEVEL_ERR);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}
			if (!(userClntFlag ^ devClntFlag)) {
				logger.info("Invalid client flag:{}, {}", userClntFlag, devClntFlag);
				packAck.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_CLNT_UNKNOWN);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}
			/* check user/pwd valid */
			if (userClntFlag) {
				if (!BeecomDB.chkUserName(userName)) {
					logger.warn("Invalid user name:{}", userName);
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
				if (!BeecomDB.chkUserPwd(userName, password)) {
					logger.info("{}: Incorrect password '{}'", userName, password);
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_PWD_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
			} else {
				try {
					devUniqId = Integer.parseInt(userName);
					if (!BeecomDB.chkDevUniqId(devUniqId)) {
						logger.warn("Invalid device unique id:{}", userName);
						packAck.getVrbHead().setRetCode(
								BPPacketCONNACK.RET_CODE_USER_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
				} catch (NumberFormatException e) {
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
				if (!BeecomDB.chkDevPwd(devUniqId, password)) {
					logger.info("{}: Incorrect password '{}'", userName, password);
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_PWD_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
			}

			/* check client_id valid */
			int clientId;
			if (0 == clientIdOld) {
				clientId = ClientIDDB.distributeID(clientIdOld);
			} else if (cliId2SsnMap.containsKey(clientIdOld)) {
				// TODL: check if the id expired
				clientId = clientIdOld;
			} else {
				// maybe the clientIdOld is expired
				clientId = clientIdOld;
				packAck.setClntIdExpired(true);
			}
			if (packAck.isClntIdExpired()) {
				logger.warn("Err: Expired client id({})", clientId);
				packAck.getVrbHead().setRetCode(
						BPPacketCONNACK.RET_CODE_CLNT_ID_INVALID);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}
			if (clientId != clientIdOld) {
				packAck.setNewClntIdFlg();
			}
			packAck.getPld().setClientIdLen();
			packAck.getPld().setClientId(clientId);
			/* update login flags */
			BPSession newBPSession = new BPSession(userName.getBytes(),
					password, clientId, userClntFlag, devClntFlag, devUniqId);
			cliId2SsnMap.put(clientId, newBPSession);
			if (devClntFlag) {
				devUniqId2SsnMap.put(devUniqId, newBPSession);
			}
			logger.info("Alive time={}", decodedPack.getVrbHead().getAliveTime());
			session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
					decodedPack.getVrbHead().getAliveTime());
			session.setAttribute(SESS_ATTR_ID, newBPSession);

			session.write(packAck);

		} else if (BPPacketType.GET == packType) {
		} else if (BPPacketType.GETACK == packType) {
			int retCode = decodedPack.getVrbHead().getRetCode();
			if (retCode != 0) {
				logger.warn("Error(GETACK): get return code={}", retCode);
				return;
			}
			DevSigData devSigData = decodedPack.getPld().getSigData();
			devSigData.dump();

		} else if (BPPacketType.POST == packType) {
		} else if (BPPacketType.POSTACK == packType) {
			byte flags = decodedPack.getVrbHead().getFlags();
			int clientId = decodedPack.getVrbHead().getClientId();
			int seqId = decodedPack.getVrbHead().getPackSeq();
			int retCode = decodedPack.getVrbHead().getRetCode();
			if (retCode != 0) {
				logger.info("Error(GETACK): get return code = {}", retCode);
				return;
			}
			logger.info("POSTACK: flags={}, cid={}, sid={}, rcode={}", flags, clientId
					, seqId, retCode);

		} else if (BPPacketType.PING == packType) {
			byte flags = decodedPack.getVrbHead().getFlags();
			int clntId = decodedPack.getVrbHead().getClientId();
			int seqId = decodedPack.getVrbHead().getPackSeq();
			logger.info("PING: flags={}, cid={}, sid={}", flags, clntId, seqId);

			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);

			BPSession bpSessPara = (BPSession) session
					.getAttribute(SESS_ATTR_ID);
			if (clntId != bpSessPara.getClientId()) {
				packAck.getVrbHead().setRetCode(
						BPPacketPINGACK.RET_CODE_CLNT_ID_INVALID);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}

			packAck.getVrbHead().setClientId(clntId);
			packAck.getVrbHead().setPackSeq(seqId);
			packAck.getVrbHead().setRetCode(BPPacketPINGACK.RET_CODE_OK);

			session.write(packAck);
		} else if (BPPacketType.PUSHACK == packType) {
		} else if (BPPacketType.REPORT == packType) {

			int clientId = decodedPack.getClientId();
			int retCode = BPPacketRPRTACK.RET_CODE_OK;
			int seqId = decodedPack.getPackSeq();
			BPSession bpSess = (BPSession) session.getAttribute(SESS_ATTR_ID);
			BeecomDB db = BeecomDB.getInstance();
			DBDevInfoRec devRec = db.getDevInfoRec(Integer
					.parseInt(new String(bpSess.getUserName())));
			
			if (devRec.getDevUniqId() == 0) {
				logger.debug("TODO: dev_rec.getDevUniqId() == 0");
				return;
			}
			
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			
			VariableHeader vrb = decodedPack.getVrbHead();
			
			do {
				if(bpSess.getClientId() != clientId) {
					logger.error("Err: client id err");
					retCode = BPPacketRPRTACK.RET_CODE_CLNT_ID_INVALID;
					break;
				}
				if (vrb.getSigFlag()) {
					if (vrb.getSysSigMapFlag() || vrb.getDevNameFlag()) {
						retCode = BPPacketRPRTACK.RET_CODE_FLAGS_INVALID;
						break;
					}
					logger.debug("REPORT signal values");
					decodedPack.getPld().getSigData().dump();
					if(!bpSess.setSysSig(decodedPack.getPld().getSigData())) {
						retCode = bpSess.getError().getErrId();
						if(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID == retCode) {
							packAck.getPld().setError(bpSess.getError());
						}
						break;
					}

				} else {

					if (decodedPack.getVrbHead().getDevNameFlag()) {
						bpSess.setDevName(decodedPack.getPld().getDevName());
						// System.out.println("DevName: " +
						devRec.setDevName(bpSess.getDevName());
					}
					if (decodedPack.getVrbHead().getSysSigMapFlag()) {
						bpSess.setSysSigMap(decodedPack.getPld()
								.getMapDist2SysSigMap());
						bpSess.initSysSigValDefault();

						DBSysSigRec sysSigRec;
						if (devRec.getSysSigTabId() == 0) {
							devRec.setSysSigTabId(devRec.getDevUniqId());
							sysSigRec = new DBSysSigRec();
							sysSigRec.setSysSigTabId(devRec.getDevUniqId());

							Map<Integer, Byte[]> sysSigMap = decodedPack
									.getPld().getMapDist2SysSigMap();
							sysSigRec.setSysSigEnableLst(sysSigMap);
							sysSigRec.insertRec(db.getConn());
						} else {
							logger.debug("TODO: get sys_sig_info from database");
							List<DBSysSigRec> lst = db.getSysSigRecLst();
							for (int i = 0; i < lst.size(); i++) {
								if (lst.get(i).getSysSigTabId() == devRec
										.getSysSigTabId()) {
									lst.get(i).dumpRec();
								}
							}
						}

					}
				}
			} while (false);
			
			logger.debug("Start dump(REPORT)");
			bpSess.dumpSysSig();
			
			packAck.getVrbHead().setPackSeq(seqId);
			packAck.getVrbHead().setRetCode(retCode);

			devRec.updateRec(db.getConn());

			session.write(packAck);
		} else if (BPPacketType.DISCONN == packType) {
			int clientId = decodedPack.getClientId();
			logger.info("Disconn {}", clientId);
			cliId2SsnMap.remove(clientId);
			session.closeOnFlush();
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
