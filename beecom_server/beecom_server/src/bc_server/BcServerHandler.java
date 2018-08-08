package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import bp_packet.BPParseException;
import bp_packet.BPPacketCONNACK;
import bp_packet.BPPacketGET;
import bp_packet.BPPacketPINGACK;
import bp_packet.BPPacketPOST;
import bp_packet.BPPacketPUSH;
import bp_packet.BPPacketREPORT;
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
import db.CustomSignalInfoUnit;
import db.DBDevInfoRec;
import db.DBSysSigRec;
import db.DeviceInfoUnit;
import db.SystemSignalCustomInfoUnit;
import db.UserInfoUnit;
import db.BeecomDB.GetSnErrorEnum;
import javafx.util.Pair;
import other.BPError;

public class BcServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcServerHandler.class);

	Map<Integer, BPSession> cliId2SsnMap = new HashMap<>();
	Map<Long, BPSession> devUniqId2SsnMap = new HashMap<>();
	
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";
	public static final String SESS_ATTR_BP_SESSION = "SESS_ATTR_BP_SESSION";
	
	static enum ProductType {
		PUSH_DEVICE_ID_LIST,
	}
	
	static class PushPacketDeviceIDProduct extends Product {
		private BPUserSession bpUserSession;
		private BPPacket bpPacket;
		
		public PushPacketDeviceIDProduct(BPUserSession bpSession) {
			super();
			this.bpUserSession = bpSession;
			bpPacket = null;
		}
		
		@Override
		public boolean consume() {
			boolean ret = false; 
			try {
				logger.info("PushPacketDeviceIDProduct consumed");
				IoSession session = bpUserSession.getSession();
				session.write(bpPacket);
				ret = true;
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			return ret;
		}

		@Override
		public boolean product() {
			boolean ret = false;
			if(null == bpUserSession) {
				return ret;
			}
			try {
				logger.info("PushPacketDeviceIDProduct producting");
				// IoSession session = bpUserSession.getSession();
				bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
				bpPacket.getVrbHead().setReqAllDeviceIdFlag(true);
				String userName = bpUserSession.getUserName();
				Payload pld = bpPacket.getPld();
				BeecomDB beecomDb = BeecomDB.getInstance();
				List<Long> deviceIDList = beecomDb.getDeviceIDList(bpUserSession.getUserName());
				pld.setDeviceIdList(deviceIDList);
				// session.write(bpPacket);
				ret = true;
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			return ret;
		}
		
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
				bpSession = new BPUserSession(session, userInfoUnit);
				BeecomDB.getInstance().getUserName2SessionMap().put(userName, bpSession);
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
				
			} 
			if(devClntFlag) {
				BeecomDB.LoginErrorEnum loginErrorEnum;
				try {
					// devUniqId = Integer.valueOf(userName).intValue();
					devUniqId = BeecomDB.getInstance().getDeviceUniqId(userName);
					DeviceInfoUnit deviceInfoUnit = new DeviceInfoUnit();
					loginErrorEnum = BeecomDB.getInstance().checkDevicePassword(devUniqId, password, deviceInfoUnit);
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
						if(null == deviceInfoUnit.getDevInfoHbn()) {
							throw new Exception("Inner error: null == deviceInfoUnit.getDeviceInfoHbn()");
						}
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
				if(!BeecomDB.getInstance().getDevUniqId2SessionMap().containsKey(devUniqId)) {
					bpSession = new BPDeviceSession(session, devUniqId, password);
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
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			if(null == packAck) {
				logger.error("Invalid packAck");
				return;
			}
			BPUserSession bpUserSession = null;
			try {
				bpUserSession = (BPUserSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			if(null == bpUserSession) {
				return;
			}
			
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			boolean sysSigMapFlag = vrb.getSysSigMapFlag();
			boolean cusSigMapFlag = vrb.getCusSigFlag();
			boolean devIdFlag = vrb.getDevIdFlag();
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean pushDeviceIdFlag = vrb.getReqAllDeviceId();
			
			if(devIdFlag) {
				if(sysSigMapFlag || cusSigMapFlag || sysSigFlag || cusSigFlag || sysSigCusInfoFlag || pushDeviceIdFlag) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				String sn = pld.getDeviceSn();
				long devUniqIdTmp = BeecomDB.getInstance().getDeviceUniqId(sn);
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
			if(pushDeviceIdFlag) {
				if(sysSigMapFlag || cusSigMapFlag || sysSigFlag || cusSigFlag || sysSigCusInfoFlag) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				packAck.getVrbHead().setReqAllDeviceIdFlag(true);
				session.write(packAck);
				pushMessage(bpUserSession, ProductType.PUSH_DEVICE_ID_LIST);
				return;
			}
			long uniqDevId = pld.getUniqDevId();
			pldAck = packAck.getPld();
			if(!BeecomDB.getInstance().checkGetDeviceSignalMapPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), uniqDevId)) {
				packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR);
				session.write(packAck);
				return;
			}
			
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
			if(null == packAck) {
				logger.error("Invalid packAck");
				return;
			}
			BPUserSession bpUserSession = null;
			try {
				bpUserSession = (BPUserSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			if(null == bpUserSession) {
				return;
			}
			
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			long uniqDevId = pld.getUniqDevId();
			
			
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigAttrFlag = vrb.getSysSigAttrFlag();
			boolean cusSigAttrFlag = vrb.getCusSigAttrFlag();
			if((sysSigFlag || cusSigFlag) && (sysSigAttrFlag || cusSigAttrFlag)) {
				packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_INVALID_FLAGS_ERR);
				session.write(packAck);
				return;
			}
			
			// TODO: 
			// 1. check if attribute packet / control packet
			if(sysSigFlag || cusSigFlag) {
				if(!BeecomDB.getInstance().getDevUniqId2SessionMap().containsKey(devUniqId)) {
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_OFF_LINE_ERR);
					session.write(packAck);
					return;
				}
				BPDeviceSession bpDeviceSession = (BPDeviceSession)BeecomDB.getInstance().getDevUniqId2SessionMap().get(devUniqId);
				// 2. relay the packet when it is a control packet
  
				bpDeviceSession.getSession().write(decodedPack);
				if(!bpDeviceSession.putRelayList(session, BPPackFactory.createBPPackAck(decodedPack), bpDeviceSession.getTimeout())) {
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_BUFFER_FILLED_ERR);
					session.write(packAck);
					return;
				}
			}
			
			if(sysSigAttrFlag || cusSigAttrFlag) {
				BPDeviceSession bpDeviceSesssion = null;
				bpDeviceSesssion = (BPDeviceSession)BeecomDB.getInstance().getDevUniqId2SessionMap().get(devUniqId);

				if(null == bpDeviceSesssion) {
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_INVALID_DEVICE_ID_ERR);
					session.write(packAck);
					return;
				}
				
				if(!BeecomDB.getInstance().checkGetDeviceSignalMapPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), uniqDevId)) {
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR);
					session.write(packAck);
					return;
				}
				
				// TODO: check bpDeviceSession.getSession() active and assemble the packAck
				
				if(vrb.getSysSigAttrFlag()) {
					/* Not completed yes */
					Map<Integer, SignalAttrInfo> sysSigAttrMap = pld.getSysSigAttrMap();
					BeecomDB.getInstance().modifySysSigAttrMap(uniqDevId, sysSigAttrMap);
					
				}
				if(vrb.getCusSigAttrFlag()) {
					/* Not completed yes */
					Map<Integer, SignalAttrInfo> sysCusAttrMap = pld.getCusSigAttrMap();
					/* change the custom signal attributes */
				}
				if(vrb.getSysSigFlag() || vrb.getCusSigFlag()) {
					/* forward the packet to the device
					 * and put a callback when get the response */
				}
			}
			
			session.write(packAck);
			

		} else if (BPPacketType.POSTACK == packType) {
			BPDeviceSession bpDeviceSession = null;
			try {
				bpDeviceSession = (BPDeviceSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			if(null == bpDeviceSession) {
				return;
			}
			
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			
			boolean sysSigAttrFlag = vrb.getSysSigAttrFlag();
			boolean cusSigAttrFlag = vrb.getCusSigAttrFlag();
			
			if(sysSigAttrFlag || cusSigAttrFlag) {
				decodedPack.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_PEER_INNER_ERR);
				bpDeviceSession.updateRelayList(vrb.getPackSeq(), decodedPack);	
			}
			
			bpDeviceSession.startRelay(vrb.getPackSeq());
			return;

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
				// TODO: change the ProductType 
				// pushMessage(bpSession, ProductType.PUSH_DEVICE_ID_LIST);
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
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			if(null == packAck) {
				logger.error("Invalid packAck");
				return;
			}
			BPDeviceSession bpDeviceSession = null;
			try {
				bpDeviceSession = (BPDeviceSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
			if(null == bpDeviceSession) {
				return;
			}
			
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			boolean sysSigMapFlag = vrb.getSysSigMapFlag();
			boolean cusSigMapFlag = vrb.getCusSigFlag();
			boolean sigValFlag = vrb.getSigValFlag();
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean sigMapChecksumFlagOnly = vrb.getSigMapChecksumFlag();
			boolean gotNewSigMapChecksum = false;
			
			
			long uniqDevId = bpDeviceSession.getUniqDevId();
			pldAck = packAck.getPld();
			vrbAck = packAck.getVrbHead();
			
			if(sigMapChecksumFlagOnly) {
				if (!BeecomDB.getInstance().checkSignalMapChksum(uniqDevId, pld.getSigMapChecksum())) {
					packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
				}
			} 
			
			if(sysSigMapFlag || cusSigMapFlag || sysSigCusInfoFlag) {
				if(!BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapChecksum())) {
					logger.error("Internal error: !BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapChecksum())");
				}
			}
			
			if(sysSigMapFlag) {			
				List<Integer> systemSignalEnabledList = pld.getSystemSignalEnabledList();
				BeecomDB.getInstance().putSystemSignalEnabledMap(uniqDevId, systemSignalEnabledList);
			}
			if(cusSigMapFlag) {
				List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
				BeecomDB.getInstance().putCustomSignalMap(uniqDevId, customSignalInfoUnitList);
			}
			if(sigValFlag) {
				Map<Integer, Object> systemSignalValuesSessoin = bpDeviceSession.getSystemSignalValueMap();
				Map<Integer, Object> systemSignalValues = pld.getSysSigValMap();
				  
				Iterator<Map.Entry<Integer, Object>> entriesSysSigVals = systemSignalValues.entrySet().iterator();  
				while (entriesSysSigVals.hasNext()) {  
				    Map.Entry<Integer, Object> entry = entriesSysSigVals.next();  
				    if(!systemSignalValuesSessoin.containsKey(entry.getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    
				    if(BeecomDB.getInstance().checkSystemSignalValueUnformed(bpDeviceSession.getUniqDevId(), entry.getKey(), entry.getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_VAL_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    systemSignalValuesSessoin.put(entry.getKey(), entry.getValue());
				}  
				
				Map<Integer, Pair<Byte, Object>> customSignalValuesSessoin = bpDeviceSession.getCustomSignalValueMap();
				Map<Integer, Pair<Byte, Object>> customSignalValues = pld.getCusSigValMap();
				  
				Iterator<Map.Entry<Integer, Pair<Byte, Object>>> entriesCusSigVals = customSignalValues.entrySet().iterator();  
				while (entriesCusSigVals.hasNext()) {  
				    Map.Entry<Integer, Pair<Byte, Object>> entry = entriesCusSigVals.next();  
				    if(!customSignalValuesSessoin.containsKey(entry.getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    if(BeecomDB.getInstance().checkCustomSignalValueUnformed(bpDeviceSession.getUniqDevId(), entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_VAL_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    customSignalValuesSessoin.put(entry.getKey(), entry.getValue());
				  
				}  
			}

			if(sysSigCusInfoFlag) {
				List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = pld.getSystemSignalCustomInfoUnitLst();
				BeecomDB.getInstance().putSystemCustomSignalInfoMap(uniqDevId, systemSignalCustomInfoUnit);
			}
			
			session.write(packAck);
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
	
	private void pushMessage(BPSession bpSession, ProductType productType) {
		Product product = null;
		switch(productType) {
			case PUSH_DEVICE_ID_LIST:
				product = new PushPacketDeviceIDProduct((BPUserSession)bpSession);
				break;
		}
		if(product != null) {
			product.product();
			BcServerMain.consumerTask.produce(product);
		}
		
	}
}
