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
import db.SignalInfoUnitInterface;
import db.SystemSignalCustomInfoUnit;
import db.UserInfoUnit;
import db.BeecomDB.GetSnErrorEnum;
import db.BeecomDB.LoginErrorEnum;
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
		PUSH_SIGNAL_VALUE,
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
		BeecomDB beecomDb = BeecomDB.getInstance();
		
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
								BPPacketCONNACK.RET_CODE_USER_OR_PASSWORD_INVALID);
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
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
				beecomDb.getUserName2SessionMap().put(userName, bpSession);
				beecomDb.getUserId2SessionMap().put(userInfoUnit.getUserInfoHbn().getId(), bpSession);
				beecomDb.updateUserDevRel(userInfoUnit.getUserInfoHbn());

			} 
			if(devClntFlag) {
				BeecomDB.LoginErrorEnum loginErrorEnum = LoginErrorEnum.USER_OR_PASSWORD_INVALID;
				try {
					// devUniqId = Integer.valueOf(userName).intValue();
					DeviceInfoUnit deviceInfoUnit = new DeviceInfoUnit();
					devUniqId = BeecomDB.getInstance().getDeviceUniqId(userName, deviceInfoUnit);
					if(devUniqId > 0) {
						if(deviceInfoUnit.getDevInfoHbn().getPassword().equals(password)) {
							loginErrorEnum = LoginErrorEnum.LOGIN_OK;
						}
					}
					// loginErrorEnum = BeecomDB.getInstance().checkDevicePassword(devUniqId, password, deviceInfoUnit);
					if(loginErrorEnum != LoginErrorEnum.LOGIN_OK) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_USER_OR_PASSWORD_INVALID);
						session.write(packAck);
						session.closeOnFlush();
					}
					if (loginErrorEnum != BeecomDB.LoginErrorEnum.LOGIN_OK) {
						return;
					}
					bpSession = new BPDeviceSession(session, devUniqId, password, deviceInfoUnit.getDevInfoHbn().getAdminId(), deviceInfoUnit.getDevInfoHbn().getSnId());
					BeecomDB.getInstance().getDevUniqId2SessionMap().put(devUniqId, bpSession);
					session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
					if(deviceInfoUnit != null) {
						beecomDb.updateUserDevRel(deviceInfoUnit.getDevInfoHbn());
					}
					
				} catch (NumberFormatException e) {
					packAck.getVrbHead().setRetCode(
							BPPacketCONNACK.RET_CODE_USER_OR_PASSWORD_INVALID);
					session.write(packAck);
					session.closeOnFlush();
				}

			/*
				if(!BeecomDB.getInstance().getDevUniqId2SessionMap().containsKey(devUniqId)) {
					bpSession = new BPDeviceSession(session, devUniqId, password);
					BeecomDB.getInstance().getDevUniqId2SessionMap().put(devUniqId, bpSession);
				} else {
					bpSession = BeecomDB.getInstance().getDevUniqId2SessionMap().get(devUniqId);
				}
				*/

				

				
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
			boolean cusSigMapFlag = vrb.getCusSigMapFlag();
			boolean devIdFlag = vrb.getDevIdFlag();
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean pushDeviceIdFlag = vrb.getReqAllDeviceId();
			
			pldAck = packAck.getPld();
			vrbAck = packAck.getVrbHead();
			
			if(devIdFlag) {
				if(sysSigMapFlag || cusSigMapFlag || sysSigFlag || cusSigFlag || sysSigCusInfoFlag || pushDeviceIdFlag) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				String sn = pld.getDeviceSn();
				long devUniqIdTmp = BeecomDB.getInstance().getDeviceUniqId(sn, null);
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
				pushMessage(bpUserSession, ProductType.PUSH_DEVICE_ID_LIST, null);
				return;
			}
			long uniqDevId = pld.getUniqDevId();
			
			pldAck.setDevUniqId(uniqDevId);
			if(!BeecomDB.getInstance().checkGetDeviceSignalMapPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), uniqDevId)) {
				packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR);
				session.write(packAck);
				return;
			}
			
			if(sysSigMapFlag) {			
				pldAck.packSysSigMap(uniqDevId);
			}
			if(sysSigCusInfoFlag || cusSigMapFlag) {
				int langSupportMask = BeecomDB.getInstance().getDeviceLangSupportMask(uniqDevId) & vrb.getLangFlags();
				if(langSupportMask <= 0 || (langSupportMask & 0xFF) == 0) {
					packAck.getVrbHead().setRetCode(BPPacketGET.RET_CODE_INNER_ERR);
					session.write(packAck);
					return;
				}
				pldAck.setCustomSignalLangSupportMask(langSupportMask);
			}
			if(sysSigCusInfoFlag) {
				pldAck.packSysSigCusInfo(uniqDevId);
			}
			if(cusSigMapFlag) {
				pldAck.packCusSigMap(uniqDevId);
				
			}
			BPError bpError = new BPError();
			BPDeviceSession bpDeviceSession = (BPDeviceSession)beecomDb.getDevUniqId2SessionMap().get(devUniqId);
			
			boolean signalValuePackOk = true;
			if(sysSigFlag && signalValuePackOk) {
				List<Integer> sysSigLst = pld.getSysSig();
				signalValuePackOk = pldAck.packSysSignalValues(sysSigLst, bpDeviceSession, bpError);
			}
			if(cusSigFlag && signalValuePackOk) {
				List<Integer> cusSigLst = pld.getCusSig();
				signalValuePackOk = pldAck.packCusSignalValues(cusSigLst, bpDeviceSession, bpError);
			}

			
			if(!signalValuePackOk) {
				vrbAck.setRetCode(bpError.getErrorCode());
				session.write(packAck);
			} else if(bpError.getErrorCode() == BPError.BP_ERROR_STATISTICS_NONE_SIGNAL) {
				List<Integer> statisticsNoneSignalList = bpError.getStatisticsNoneSignalList();
				if(null == statisticsNoneSignalList || statisticsNoneSignalList.isEmpty()) {
					logger.error("Inner error: null == statisticsNoneSignalList || statisticsNoneSignalList.isEmpty()");
					vrbAck.setRetCode(BPPacketGET.RET_CODE_INNER_ERR);
					session.write(packAck);
				} else {
					bpDeviceSession.getSession().write(decodedPack);
					if(!bpDeviceSession.putRelayList(session, BPPackFactory.createBPPackAck(decodedPack), bpDeviceSession.getTimeout())) {
						packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_BUFFER_FILLED_ERR);
						session.write(packAck);
						return;
					}
				}
			} else {
				session.write(packAck);
			}
		} else if (BPPacketType.GETACK == packType) {
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
			bpDeviceSession.startRelay(vrb.getPackSeq());
			return;

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
			
			vrbAck = packAck.getVrbHead();
			pldAck = packAck.getPld();
			
			boolean sysSigFlag = vrb.getSysSigFlag();
			boolean cusSigFlag = vrb.getCusSigFlag();
			boolean sysSigAttrFlag = vrb.getSysSigAttrFlag();
			boolean cusSigAttrFlag = vrb.getCusSigAttrFlag();
			boolean sigValFlag = vrb.getSigValFlag();
			if((sysSigFlag || cusSigFlag) && (sysSigAttrFlag || cusSigAttrFlag)) {
				packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_INVALID_FLAGS_ERR);
				session.write(packAck);
				return;
			}
			
			if(sigValFlag) {
				BPDeviceSession bpDeviceSession = (BPDeviceSession)beecomDb.getDevUniqId2SessionMap().get(devUniqId);
				if(null == bpDeviceSession) {
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_OFF_LINE_ERR);
					session.write(packAck);
					return;
				}
				Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = bpDeviceSession.getSignalId2InfoUnitMap();
				if(null == signalId2InfoUnitMap) {
			    	vrbAck.setRetCode(BPPacketPOST.RET_CODE_OFF_LINE_ERR);
			    	session.write(packAck);
			    	return;
				}
				Map<Integer, Pair<Byte, Object> > sigValMap = pld.getSigValMap();
				Iterator<Map.Entry<Integer, Pair<Byte, Object>>> entriesSigVals = sigValMap.entrySet().iterator();
				SignalInfoUnitInterface signalInfoUnitInterface;
				Map.Entry<Integer, Pair<Byte, Object>> entry;
				/* check error */
				while (entriesSigVals.hasNext()) {  
				    entry = entriesSigVals.next();  
				    signalInfoUnitInterface = signalId2InfoUnitMap.get(entry.getKey());
				    if(null == signalInfoUnitInterface) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    
				    if(signalInfoUnitInterface.checkSignalValueUnformed(entry.getValue().getKey(), entry.getValue().getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_VAL_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				} 
				
				
				/*
				entriesSigVals = sigValMap.entrySet().iterator();
				while (entriesSigVals.hasNext()) {  
				    entry = entriesSigVals.next();  
				    signalInfoUnitInterface = signalId2InfoUnitMap.get(entry.getKey());
				    signalInfoUnitInterface.putSignalValue(entry);
				} 
				*/
				
				//byte[] relayData = decodedPack.getSignalValueRelay();
				//pushMessage(bpDeviceSession, ProductType.PUSH_SIGNAL_VALUE, relayData);
				
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
				logger.info("PUSHACK OK");
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
			boolean cusSigMapFlag = vrb.getCusSigMapFlag();
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
				bpDeviceSession.setSigMapCheckOK(false);
				if (!BeecomDB.getInstance().checkSignalMapChksum(uniqDevId, pld.getSigMapCheckSum())) {
					packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
				} else {
					if(BeecomDB.getInstance().getSignalInfoUnitInterfaceMap(bpDeviceSession)) {
						bpDeviceSession.setSigMapCheckOK(true);
					} else {
						packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
					}
					
				}
				session.write(packAck);
				return;
			} 
			
			if(sysSigMapFlag || sysSigCusInfoFlag || cusSigMapFlag) {
				BeecomDB.getInstance().clearDeviceSignalInfo(uniqDevId);
				bpDeviceSession.setSigMapCheckOK(false);
			}
			
			boolean newSigMapFlagOk = true;
			
			if(newSigMapFlagOk && sysSigMapFlag) {			
				List<Integer> systemSignalEnabledList = pld.getSystemSignalEnabledList();
				/* input the session of the signal map */
				if(!BeecomDB.getInstance().putSystemSignalEnabledMap(uniqDevId, systemSignalEnabledList)) {
					newSigMapFlagOk = false;
				}
			}
			
			if(newSigMapFlagOk && sysSigCusInfoFlag) {
				List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = pld.getSystemSignalCustomInfoUnitLst();
				// packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
				if(!BeecomDB.getInstance().putSystemCustomSignalInfoMap(uniqDevId, systemSignalCustomInfoUnit)) {
					newSigMapFlagOk = false;
				}
			}
			if(newSigMapFlagOk && cusSigMapFlag) {
				List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
				// packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
				if(!BeecomDB.getInstance().putCustomSignalMap(uniqDevId, customSignalInfoUnitList)) {
					newSigMapFlagOk = false;
				}
			}
			if(!newSigMapFlagOk) {
				packAck.getVrbHead().setRetCode(BPPacketREPORT.RET_CODE_SIGNAL_MAP_ERR);
				session.write(packAck);
				return;
			}
			if(newSigMapFlagOk && (sysSigMapFlag || cusSigMapFlag || sysSigCusInfoFlag)) {
				if(!BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapCheckSum())) {
					logger.error("Internal error: !BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapChecksum())");
				} else {
					List<Integer> systemSignalEnabledList = pld.getSystemSignalEnabledList();
					List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = pld.getSystemSignalCustomInfoUnitLst();
					List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
					bpDeviceSession.parseSignalInfoUnitInterfaceMap(systemSignalEnabledList, systemSignalCustomInfoUnit, customSignalInfoUnitList);
					bpDeviceSession.setSigMapCheckOK(true);

				}
			}
			
			if(sigValFlag) {
				Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = bpDeviceSession.getSignalId2InfoUnitMap();
				if(null == signalId2InfoUnitMap) {
			    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_MAP_UNCHECK);
			    	session.write(packAck);
			    	return;
				}
				Map<Integer, Pair<Byte, Object> > sigValMap = pld.getSigValMap();
				Iterator<Map.Entry<Integer, Pair<Byte, Object>>> entriesSigVals = sigValMap.entrySet().iterator();
				SignalInfoUnitInterface signalInfoUnitInterface;
				Map.Entry<Integer, Pair<Byte, Object>> entry;
				/* check error */
				while (entriesSigVals.hasNext()) {  
				    entry = entriesSigVals.next();  
				    signalInfoUnitInterface = signalId2InfoUnitMap.get(entry.getKey());
				    if(null == signalInfoUnitInterface) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				    
				    if(signalInfoUnitInterface.checkSignalValueUnformed(entry.getValue().getKey(), entry.getValue().getValue())) {
				    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_VAL_INVALID);
				    	pldAck.setUnsupportedSignalId(entry.getKey());
				    	session.write(packAck);
				    	return;
				    }
				}  
				
				/* put signal value */
				entriesSigVals = sigValMap.entrySet().iterator();
				while (entriesSigVals.hasNext()) {  
				    entry = entriesSigVals.next();  
				    signalInfoUnitInterface = signalId2InfoUnitMap.get(entry.getKey());
				    signalInfoUnitInterface.putSignalValue(entry);
				}  
				// bpDeviceSession.startNotify(decodedPack);
				// BPPacketREPORT report = (BPPacketREPORT)decodedPack;
				byte[] relayData = decodedPack.getSignalValueRelay();
				// TODO: strip(relayData), to avoid pushing no-notifying signals
				// bpDeviceSession.reportSignalValue2UserClient(relayData);
				pushMessage(bpDeviceSession, ProductType.PUSH_SIGNAL_VALUE, relayData);
			}
			
			session.write(packAck);

		} else if(BPPacketType.RPRTACK == packType) {
			/* NOT SUPPORTED */
		} else if (BPPacketType.DISCONN == packType) {
			bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			if(bpSession != null) {
				logger.info("Disconn, {}", bpSession.toString());
			}
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
	
	private void pushMessage(BPSession bpSession, ProductType productType, byte[] para) {
		Product product = null;
		try {
			switch (productType) {
			case PUSH_DEVICE_ID_LIST:
				product = new PushPacketDeviceIDProduct((BPUserSession) bpSession);
				break;
			case PUSH_SIGNAL_VALUE:
				product = new PushSignalValuesProduct((BPDeviceSession) bpSession, para);
			}
			if (product != null) {
				product.produce();
				BcServerMain.consumerTask.produce(product);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		BPSession bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
		if(null == bpSession) {
			return;
		}
		if(bpSession.ifUserSession()) {
			logger.info("user client {}: session closed", bpSession.getUserName());
			BeecomDB.getInstance().getUserName2SessionMap().remove(bpSession.getUserName());
		} else {
			logger.info("device client {}: session closed", bpSession.getUniqDevId());
			BeecomDB.getInstance().getDevUniqId2SessionMap().remove(bpSession.getUniqDevId());
		}
	}
	
	
}
