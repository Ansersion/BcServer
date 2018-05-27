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

import bp_packet.BPDeviceSession;
import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPParseException;
import bp_packet.BPPacketCONNACK;
import bp_packet.BPPacketGET;
import bp_packet.BPPacketPINGACK;
import bp_packet.BPPacketPUSH;
import bp_packet.BPPacketRPRTACK;
import bp_packet.BPSession;
import bp_packet.BPUserSession;
import bp_packet.DevSigData;
import bp_packet.FixedHeader;
import bp_packet.Payload;
import bp_packet.SignalAttrInfo;
import bp_packet.VariableHeader;
import db.BeecomDB;
import db.ClientIDDB;
import db.DBDevInfoRec;
import db.DBSysSigRec;
import db.UserInfoUnit;
import db.BeecomDB.GetSnErrorEnum;
import other.BPError;

public class BcServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcServerHandler.class);
	

	Map<Integer, BPSession> cliId2SsnMap = new HashMap<>();
	Map<Long, BPSession> devUniqId2SsnMap = new HashMap<>();
	
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";
	public static final String SESS_ATTR_BP_SESSION = "SESS_ATTR_BP_SESSION";

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
		
		String s;
		BPPacket decodedPack = (BPPacket) message;
		VariableHeader vrb, vrbAck;
		Payload pld, pldAck;
		long devUniqId = 0;
		BPSession bpSession = null;

		BPPacketType packType = decodedPack.getPackTypeFxHead();
		if (BPPacketType.CONNECT == packType) {
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			int level = vrb.getLevel();
			boolean userClntFlag = decodedPack.getUsrClntFlag();
			boolean devClntFlag = decodedPack.getDevClntFlag();

			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			if (level > BPPacket.BP_LEVEL) {
				logger.warn("Unsupported level: {} > {}", level, BPPacket.BP_LEVEL);
				packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_LEVEL_ERR);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}
			if (!(userClntFlag ^ devClntFlag)) {
				logger.info("Invalid client flag:{}, {}", userClntFlag, devClntFlag);
				packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_CLNT_UNKNOWN);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}
			String userName = decodedPack.getUserNamePld();
			String password = decodedPack.getPasswordPld();
			/* check user/pwd valid */
			if (userClntFlag) {
				UserInfoUnit userInfoUnit = new UserInfoUnit();
				BeecomDB.LoginErrorEnum loginErrorEnum = BeecomDB.getInstance().checkUserPassword(userName, password, userInfoUnit);
				
				switch(loginErrorEnum) {
					case USER_INVALID:
						logger.warn("Invalid user name:{}", userName);
						packAck.getVrbHead().setRetCode(
								BPPacketCONNACK.RET_CODE_USER_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						break;
					case PASSWORD_INVALID:
						logger.info("{}: Incorrect password '{}'", userName, password);
						packAck.getVrbHead().setRetCode(
								BPPacketCONNACK.RET_CODE_PWD_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						break;
					default:
						if(null == userInfoUnit.getUserInfoHbn()) {
							session.write(packAck);
							session.closeOnFlush();
							throw new Exception("Error: Database Error");
						}
						break;
				}
				
				if (loginErrorEnum != BeecomDB.LoginErrorEnum.LOGIN_OK) {
					return;
				}
				bpSession = new BPUserSession(userInfoUnit);
				BeecomDB.getInstance().getUserName2SessionMap().put(userName, bpSession);
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
				
			} 
			if(devClntFlag) {
				BeecomDB.LoginErrorEnum loginErrorEnum;
				try {
					devUniqId = Integer.valueOf(userName).intValue();
					loginErrorEnum = BeecomDB.checkDeviceUniqId(devUniqId);
					switch (loginErrorEnum) {
					case USER_INVALID:
						logger.warn("Invalid user name:{}", userName);
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_USER_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						break;
					case PASSWORD_INVALID:
						logger.info("{}: Incorrect password '{}'", userName, password);
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_PWD_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						break;
					default:
						/* LOGIN_OK */
						break;
					}

				} catch (NumberFormatException e) {
					loginErrorEnum = BeecomDB.LoginErrorEnum.USER_INVALID;
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_INVALID);
					session.write(packAck);
					session.closeOnFlush();
				}
				if (loginErrorEnum != BeecomDB.LoginErrorEnum.LOGIN_OK) {
					return;
				}
				// bpSession = new BPDeviceSession(devUniqId, password);
				if(!BeecomDB.getInstance().getDevUniqId2SessionMap().containsKey(devUniqId)) {
					bpSession = new BPDeviceSession(devUniqId, password);
					BeecomDB.getInstance().getDevUniqId2SessionMap().put(devUniqId, bpSession);
				} else {
					bpSession = BeecomDB.getInstance().getDevUniqId2SessionMap().get(devUniqId);
				}
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
			}

			int aliveTime = vrb.getAliveTime();
			logger.debug("Alive time={}", aliveTime);
			bpSession.setAliveTime(aliveTime);
			short timeout = vrb.getTimeout();
			boolean isDebugMode = vrb.getDebugMode();
			byte performanceClass = vrb.getPerformanceClass();
			bpSession.setTimeout(timeout);
			bpSession.setDebugMode(isDebugMode);
			bpSession.setPerformanceClass(performanceClass);
			session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
					decodedPack.getVrbHead().getAliveTime());
			session.write(packAck);

		} else if (BPPacketType.GET == packType) {
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			boolean sysSigMapFlag = vrb.getSysSigMapFlag();
			boolean cusSigMapFlag = vrb.getCusSigFlag();
			boolean devIdFlag = vrb.getDevIdFlag();
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean infoLeft = vrb.getInfoLeftFlag();
			
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			
			if(devIdFlag) {
				if(sysSigMapFlag || cusSigMapFlag || sysSigFlag || cusSigFlag || sysSigCusInfoFlag || infoLeft) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				String sn = pld.getDeviceSn();
				long devUniqIdTmp = BeecomDB.getInstance().getDeviceUniqId(sn);
				BPUserSession bpUserSession = (BPUserSession)session.getAttribute(SESS_ATTR_BP_SESSION);
				BeecomDB.GetSnErrorEnum getSnErrorEnum = BeecomDB.getInstance().checkGetSNPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), sn);
				if(getSnErrorEnum != BeecomDB.GetSnErrorEnum.GET_SN_OK) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_GET_SN_PERMISSION_DENY_ERR);
					session.write(packAck);
					return;
				}
				packAck.getVrbHead().setDevIdFlag(true);
				packAck.getPld().setDevUniqId(devUniqIdTmp);
				session.write(packAck);
				return;
			} 
			long uniqDevId = pld.getUniqDevId();
			pldAck = packAck.getPld();
			// TODO: check if the user has permission to access this device
			if(sysSigMapFlag) {			
				pldAck.packSysSigMap(uniqDevId);
			}
			if(cusSigMapFlag) {
				pldAck.packCusSigMap(uniqDevId);
			}
			if(sysSigFlag) {
				bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
				List<Integer> sysSigLst = pld.getSysSig();
				BPError bpError = new BPError();
				pldAck.packSysSignalValues(sysSigLst, bpSession, bpError);
			}
			if(cusSigFlag) {
				bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
				List<Integer> cusSigLst = pld.getCusSig();
				BPError bpError = new BPError();
				pldAck.packCusSignalValues(cusSigLst, bpSession, bpError);
			}
			if(sysSigCusInfoFlag) {
				pldAck.packSysSigCusInfo(uniqDevId);
			}
			
			session.write(packAck);
		} else if (BPPacketType.GETACK == packType) {
			/* NOT SUPPORTED */

		} else if (BPPacketType.POST == packType) {
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			long uniqDevId = pld.getUniqDevId();
			if(vrb.getSysSigAttrFlag()) {
				Map<Integer, SignalAttrInfo> sysSigAttrMap = pld.getSysSigAttrMap();
				/* change the system signal attributes */
			}
			if(vrb.getCusSigAttrFlag()) {
				Map<Integer, SignalAttrInfo> sysCusAttrMap = pld.getCusSigAttrMap();
				/* change the custom signal attributes */
			}
			if(vrb.getSysSigFlag() || vrb.getCusSigFlag()) {
				/* forward the packet to the device
				 * and put a callback when get the response */
			}
			
		} else if (BPPacketType.POSTACK == packType) {
			/* POSTACK forward */
			/*
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
			*/

		} else if (BPPacketType.PING == packType) {
			vrb = decodedPack.getVrbHead();
			byte flags = vrb.getFlags();
			int seqId = vrb.getPackSeq();
			boolean userOnLine = vrb.getUserOnLine();
			logger.info("PING: flags={}, cid={}, sid={}", flags, seqId);

			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);

			bpSession = (BPSession) session
					.getAttribute(SESS_ATTR_BP_SESSION);

			packAck.getVrbHead().setPackSeq(seqId);
			packAck.getVrbHead().setRetCode(BPPacketPINGACK.RET_CODE_OK);

			session.write(packAck);
			
			if(userOnLine) {
				pushMessage(bpSession);
			}
		} else if(BPPacketType.PINGACK == packType) {
			/* NOT SUPPORTED */
		} else if(BPPacketType.PUSH == packType) {
			/* NOT SUPPORTED */
		} else if (BPPacketType.PUSHACK == packType) {
			vrb = decodedPack.getVrbHead();
			int retCode = vrb.getRetCode();
			int packSeq = vrb.getPackSeq();
			/*
			if(!checkPackSeq()) {
				return;
			}
			*/
			switch(retCode) {
			case BPPacketPUSH.RET_CODE_OK:
				break;
			case BPPacketPUSH.RET_CODE_UNSUPPORTED_SIGNAL_ID:
				/* handle unsupported signal ID*/
				break;
			default:
				break;
			}
			
		} else if (BPPacketType.REPORT == packType) {
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			boolean sysSigMapFlag = vrb.getSysSigMapFlag();
			boolean cusSigMapFlag = vrb.getCusSigFlag();
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean sigMapChecksumFlag = vrb.getSigMapChecksumFlag();
			
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			
			long uniqDevId = pld.getUniqDevId();
			pldAck = packAck.getPld();
			// TODO: check if the user has permission to access this device
			
			if(sigMapChecksumFlag) {
				long sigMapChecksum = pld.getSigMapChecksum();
				/* check the sigMapChecksum */
			} 
			if(sysSigMapFlag) {			
				/* save the system signal map */
			}
			if(cusSigMapFlag) {
				/* save the custom signal map */
			}
			if(sysSigFlag) {
				/* save the system signal values */
			}
			if(cusSigFlag) {
				/* save the custom signal values */
			}
			if(sysSigCusInfoFlag) {
				/* save the system signal customized info */
			}
			
			session.write(packAck);
			/*
			
			int retCode = BPPacketRPRTACK.RET_CODE_OK;
			int seqId = decodedPack.getPackSeq();
			BPSession bpSess = (BPSession) session.getAttribute(SESS_ATTR_ID);
			BeecomDB db = BeecomDB.getInstance();
			DBDevInfoRec devRec = db.getDevInfoRec(Integer
					.parseInt(new String(bpSess.getUserName())));
			
			if (devRec.getDevUniqId() == 0) {
				s = "TODO: dev_rec.getDevUniqId() == 0";
				logger.debug(s);
				return;
			}
			
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			
			vrb = decodedPack.getVrbHead();
			
			do {
				if (vrb.getSigFlag()) {
					if (vrb.getSysSigMapFlag() || vrb.getDevNameFlag()) {
						retCode = BPPacketRPRTACK.RET_CODE_FLAGS_INVALID;
						break;
					}
					s = "REPORT signal values";
					logger.debug(s);
					decodedPack.getPld().getSigData().dump();
					if(!bpSess.setSysSig(decodedPack.getPld().getSigData())) {
						// retCode = bpSess.getError().getErrId();
						// if(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID == retCode) {
						//	packAck.getPld().setError(bpSess.getError());
						// }
						break;
					}

				} else {

					if (decodedPack.getVrbHead().getDevNameFlag()) {
						// bpSess.setDevName(decodedPack.getPld().getDevName());
						// System.out.println("DevName: " +
						// devRec.setDevName(bpSess.getDevName());
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
							s = "TODO: get sys_sig_info from database";
							logger.debug(s);
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
			
			s = "Start dump(REPORT)";
			logger.debug(s);
			bpSess.dumpSysSig();
			
			packAck.getVrbHead().setPackSeq(seqId);
			packAck.getVrbHead().setRetCode(retCode);

			devRec.updateRec(db.getConn());

			session.write(packAck);
			*/
		} else if(BPPacketType.RPRTACK == packType) {
			/* NOT SUPPORTED */
		} else if (BPPacketType.DISCONN == packType) {
			bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			logger.info("Disconn, {}", bpSession.toString());
			session.closeOnFlush();
		} else {
			logger.info("Error: messageRecevied: Not supported packet type");
			return;
		}

	}

	// 会话空闲
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		String s = "Over Alive time";
		logger.info(s);
		session.closeOnFlush();

	}
	
	private void pushMessage(BPSession bpSession) {
		// TODO: 
	}
}
