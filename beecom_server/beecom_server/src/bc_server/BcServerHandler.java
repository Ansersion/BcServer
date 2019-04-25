package bc_server;

/**
 * @author Ansersion
 *
 */

import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
import bp_packet.BPPacketGETACK;
import bp_packet.BPPacketPINGACK;
import bp_packet.BPPacketPOST;
import bp_packet.BPPacketPUSH;
import bp_packet.BPPacketRPRTACK;
import bp_packet.BPSession;
import bp_packet.BPUserSession;
import bp_packet.Payload;
import bp_packet.VariableHeader;
import db.BPLanguageId;
import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.DevInfoHbn;
import db.DeviceInfoUnit;
import db.ServerChainHbn;
import db.SignalInfoUnitInterface;
import db.SnInfoHbn;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import db.UserDevRelInfoInterface;
import db.UserInfoUnit;
import db.BeecomDB.LoginErrorEnum;
import other.BPError;
import other.CrcChecksum;
import other.Util;
import sys_sig_table.BPSysSigTable;
import sys_sig_table.SysSigInfo;

public class BcServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(BcServerHandler.class);

	Map<Integer, BPSession> cliId2SsnMap = new HashMap<>();
	Map<Long, BPSession> devUniqId2SsnMap = new HashMap<>();
	
	static final String SESS_ATTR_ID = "SESS_ATTR_ID";
	public static final String SESS_ATTR_BP_SESSION = "SESS_ATTR_BP_SESSION";
	
	enum ProductType {
		PUSH_DEVICE_ID_LIST,
		PUSH_SIGNAL_VALUE,
		POST_SIGNAL_VALUE,
		POSTACK_SIGNAL_VALUE,
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
		BPPacket decodedPack = (BPPacket) message;
		BPPacketType packType = decodedPack.getPackTypeFxHead();
		
		if (BPPacketType.CONNECT == packType) {
			onConnect(session, decodedPack);
		} else if (BPPacketType.GET == packType) {
			onGet(session, decodedPack);
		} else if (BPPacketType.GETACK == packType) {
			onGetAck(session, decodedPack);
		} else if (BPPacketType.POST == packType) {
			onPost(session, decodedPack);
		} else if (BPPacketType.POSTACK == packType) {
			onPostAck(session, decodedPack);
		} else if (BPPacketType.PING == packType) {
			onPing(session, decodedPack);
		} else if(BPPacketType.PINGACK == packType) {
			/* NOT SUPPORTED */
		} else if(BPPacketType.PUSH == packType) {
			/* NOT SUPPORTED */
		} else if (BPPacketType.PUSHACK == packType) {
			onPushAck(session, decodedPack);
		} else if (BPPacketType.REPORT == packType) {
			onReport(session, decodedPack);
		} else if(BPPacketType.RPRTACK == packType) {
			/* NOT SUPPORTED */
		} else if (BPPacketType.DISCONN == packType) {
			onDisconn(session, decodedPack);
		} else {
			logger.error("Error: messageRecevied: Not supported packet type={}", packType);
		}
	}

	// 会话空闲
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Over Alive time: ");
		if(!session.containsAttribute(SESS_ATTR_BP_SESSION)) {
			sb.append("no bp session");
		} else {
			BPSession bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
			sb.append(bpSession.isSessionReady());
		}
		logger.info(sb.toString());
		session.closeOnFlush();

	}
	
	private void pushMessage(BPSession bpSession, ProductType productType, byte[] para, int packSeq) {
		Product product = null;
		try {
			switch (productType) {
			case PUSH_DEVICE_ID_LIST:
				product = new PushPacketDeviceIDProduct((BPUserSession) bpSession);
				break;
			case PUSH_SIGNAL_VALUE:
				product = new PushSignalValuesProduct((BPDeviceSession) bpSession, para);
				break;
			case POST_SIGNAL_VALUE:
				product = new PostSignalValuesProduct((BPDeviceSession) bpSession, para, packSeq);
				break;
			case POSTACK_SIGNAL_VALUE:
				product = new PostackSignalValuesProduct((BPUserSession) bpSession, para, packSeq);
				break;
			}
			if (product != null) {
				product.produce();
				BcServerMain.consumerTask.produce(product);
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		BPSession bpSession = (BPSession)session.getAttribute(SESS_ATTR_BP_SESSION);
		if(null == bpSession) {
			return;
		}
		/* TODO: not need to remove the sessions every time */
		if(bpSession.ifUserSession()) {
			bpSession.setSessionReady(false);
			logger.info("user client {}: session closed", bpSession.getUserName());
			BeecomDB.getInstance().getUserName2SessionMap().remove(bpSession.getUserName());
		} else {
			logger.info("device client {}: session closed", bpSession.getUniqDevId());
			BeecomDB.getInstance().removeBPDeviceSession(bpSession.getUniqDevId());
			if(!bpSession.isSessionReady()) {
				return;
			}
			bpSession.setSessionReady(false);
			/* notify the users that the device is disconnected */
			BPDeviceSession bpDeviceSession = (BPDeviceSession)bpSession;
			try {
				BPPacket bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
				bpPacket.getVrbHead().setSigValFlag(true);
				bpPacket.getFxHead().setCrcType(CrcChecksum.CRC32);
				Payload pld = bpPacket.getPld();
				pld.setDevUniqId(bpSession.getUniqDevId());
				Map<Integer, Map.Entry<Byte, Object>> sigValMap = new HashMap<>();
				Payload.MyEntry<Byte, Object> communicateStateEntry = new Payload.MyEntry<Byte, Object>((byte)BPPacket.VAL_TYPE_ENUM, Integer.valueOf(1));
				sigValMap.put(0xE001, communicateStateEntry);
				pld.setSigValMap(sigValMap);
				
				BeecomDB beecomDb = BeecomDB.getInstance();
				List<UserDevRelInfoInterface> userDevRelInfoInterfaceList = beecomDb.getSn2UserDevRelInfoList(bpDeviceSession.getSnId());
				if(null == userDevRelInfoInterfaceList) {
					return;
				}
				int size = userDevRelInfoInterfaceList.size();
				long userId;
				BPUserSession userSession;
				IoSession ioSession;
				Map<Long, BPSession> userId2SessionMap = beecomDb.getUserId2SessionMap();
				UserDevRelInfoInterface userDevRelInfoInterface;
				for(int i = 0; i < size; i++) {
					userDevRelInfoInterface = userDevRelInfoInterfaceList.get(i);
					if(0 == (userDevRelInfoInterface.getAuth() & BPPacket.USER_AUTH_READ)) {
						/* no read auth */
						continue;
					}
					userId = userDevRelInfoInterface.getUserId();
					userSession = (BPUserSession)userId2SessionMap.get(userId);
					if(null == userSession) {
						/* not logined yet */
						continue;
					}
					ioSession = userSession.getSession();
					if(null == ioSession || !ioSession.isConnected()) {
						/* session is not active */
						continue;
					}
					ioSession.write(bpPacket);
				}
			} catch(Exception e) {
				Util.logger(logger, Util.ERROR, e);
			}
		}
		
	}
	
	private void onConnect(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		Payload pld;
		long devUniqId = 0;
		BPSession bpSession = null;
		BeecomDB beecomDb = BeecomDB.getInstance();

		try {
			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			int level = vrb.getLevel();
			boolean userClntFlag = vrb.getUserClntFlag();
			boolean devClntFlag = vrb.getDevClntFlag();

			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			packAck.getFxHead().setFlags(decodedPack.getFxHead().getFlags());
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
			String userName = pld.getUserName();
			String password = pld.getPassword();
			if (userClntFlag) {
				UserInfoUnit userInfoUnit = new UserInfoUnit();
				BeecomDB.LoginErrorEnum loginErrorEnum = beecomDb.checkUserPassword(userName, password,
						userInfoUnit);

				switch (loginErrorEnum) {
				case USER_INVALID:
					logger.warn("Invalid user name:{}", userName);
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_USER_OR_PASSWORD_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					break;
				case PASSWORD_INVALID:
					logger.info("{}: Incorrect password '{}'", userName, password);
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_PWD_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					break;
				case LOGIN_OK:
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_OK);
					break;
				}
				
				if(BeecomDB.LoginErrorEnum.LOGIN_OK != loginErrorEnum) {
					return;
				}

				bpSession = new BPUserSession(session, userInfoUnit);
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
				bpSession.setEncryptionType(decodedPack.getFxHead());
				bpSession.setCrcType(decodedPack.getFxHead());
				bpSession.setSessionReady(true);
				beecomDb.getUserName2SessionMap().put(userName, bpSession);
				beecomDb.getUserId2SessionMap().put(userInfoUnit.getUserInfoHbn().getId(), bpSession);
				beecomDb.updateUserDevRel(userInfoUnit.getUserInfoHbn());
				packAck.getPld().setServerChainHbn(null);

			} else if (devClntFlag) {

				BeecomDB.LoginErrorEnum loginErrorEnum;
				DeviceInfoUnit deviceInfoUnit = new DeviceInfoUnit();
				loginErrorEnum = beecomDb.checkSnPassword(userName, password, deviceInfoUnit);
				switch(loginErrorEnum) {
				case USER_INVALID:
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_USER_INVALID);
					break;
				  case PASSWORD_INVALID:
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_PWD_INVALID);
					break;
				case LOGIN_OK:
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_OK);
					break;
				}
				
				if(pld.getAdminName() != null) {
					long userId = beecomDb.getUserIdByUserName(pld.getAdminName());
					if(userId < 0) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_ADMIN_NAME_INVALID);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
					
					deviceInfoUnit.getDevInfoHbn().setAdminId(userId);
					if(!beecomDb.updateHbn(deviceInfoUnit.getSnInfoHbn())) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_SERVER_UNAVAILABLE);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
					// TODO: push to the user online with the new device

				}
				
				if(LoginErrorEnum.USER_INVALID == loginErrorEnum && vrb.getRegisterFlag() && BPPacket.isOpenRegister()) {
					SnInfoHbn snInfoHbn = new SnInfoHbn();
					snInfoHbn.setSn(userName);
					snInfoHbn.setDevelopUserId(0L);
					if(!beecomDb.putNewSnInfo(snInfoHbn)) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_REGISTER_FAILED);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
					DevInfoHbn devInfoHbn = new DevInfoHbn();
					devInfoHbn.setSnId(snInfoHbn.getId());
					devInfoHbn.setPassword(password);
					devInfoHbn.setAdminId(0L);
					if(!beecomDb.putNewDevInfo(devInfoHbn)) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_REGISTER_FAILED);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
					ServerChainHbn serverChainHbn = new ServerChainHbn();
					serverChainHbn.setClientId(devInfoHbn.getId());
					if(!beecomDb.putNewServerChain(serverChainHbn)) {
						packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_REGISTER_FAILED);
						session.write(packAck);
						session.closeOnFlush();
						return;
					}
					deviceInfoUnit.setDevInfoHbn(devInfoHbn);
					deviceInfoUnit.setSnInfoHbn(snInfoHbn);
				} else if (loginErrorEnum != LoginErrorEnum.LOGIN_OK) {
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
				devUniqId = deviceInfoUnit.getDevInfoHbn().getId();
				if(BeecomDB.getInstance().isDeviceOnline(devUniqId)) {
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_DEVICE_ONLINE);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}

				ServerChainHbn serverChainHbn = BeecomDB.getInstance().getServerChain(devUniqId);
				if (null == serverChainHbn) {
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_SERVER_CHAIN_INVALID);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
				packAck.getPld().setServerChainHbn(serverChainHbn);
				bpSession = new BPDeviceSession(session, devUniqId, password,
						deviceInfoUnit.getDevInfoHbn().getAdminId(), deviceInfoUnit.getDevInfoHbn().getSnId());
				bpSession.setEncryptionType(decodedPack.getFxHead());
				bpSession.setCrcType(decodedPack.getFxHead());
				bpSession.setSessionReady(false);
				if(0 != BeecomDB.getInstance().putDevUnitId2SessioinMap(devUniqId, bpSession)) {
					packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_SERVER_LOADING_FULL);
					session.write(packAck);
					session.closeOnFlush();
					return;
				}
				session.setAttribute(SESS_ATTR_BP_SESSION, bpSession);
				beecomDb.updateUserDevRel(deviceInfoUnit.getDevInfoHbn());
			} else {
				logger.error("Inner error: client type unknown");
				packAck.getVrbHead().setRetCode(BPPacketCONNACK.RET_CODE_SERVER_ERR);
				session.write(packAck);
				session.closeOnFlush();
				return;
			}

			int aliveTime = vrb.getAliveTime();
			if(aliveTime < BcServerMain.IDLE_TIME_MIN || aliveTime > BcServerMain.IDLE_TIME_MAX) {
				aliveTime = userClntFlag ? BcServerMain.IDLE_TIME_DEFAULT_USER_CLIENT : BcServerMain.IDLE_TIME_DEFAULT_DEVICE_CLIENT;
			}
			bpSession.setAliveTime(aliveTime);
			short timeout = vrb.getTimeout();
			byte performanceClass = vrb.getPerformanceClass();
			bpSession.setTimeout(timeout);
			bpSession.setPerformanceClass(performanceClass);
			session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, aliveTime);
			session.write(packAck);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}

	private void onGet(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		VariableHeader vrbAck;
		Payload pld;
		Payload pldAck;
		long devUniqId = 0;
		BeecomDB beecomDb = BeecomDB.getInstance();

		try {
			BPUserSession bpUserSession = null;
			bpUserSession = (BPUserSession)getBPSession(session);
			if(null == bpUserSession) {
				/* not connected */
				session.closeOnFlush();
				return;
			}
			
			BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
			if (null == packAck) {
				logger.error("Invalid packAck");
				return;
			}
			
			packAck.getFxHead().setCrcType(bpUserSession.getCrcType());
			packAck.getFxHead().setEncryptType(bpUserSession.getEncryptionType());

			vrb = decodedPack.getVrbHead();
			pld = decodedPack.getPld();
			boolean sysSigMapFlag = vrb.getSysSigMapFlag();
			boolean cusSigMapFlag = vrb.getCusSigMapFlag();
			boolean devIdFlag = vrb.getDevIdFlag();
			boolean sigFlag = vrb.getSigValFlag();
			boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
			boolean pushDeviceIdFlag = vrb.getReqAllDeviceId();

			pldAck = packAck.getPld();
			vrbAck = packAck.getVrbHead();

			if (devIdFlag) {
				if (sysSigMapFlag || cusSigMapFlag || sigFlag || sysSigCusInfoFlag
						|| pushDeviceIdFlag) {
					packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				String sn = pld.getSN();
				long devUniqIdTmp = BeecomDB.getInstance().getDeviceUniqId(sn, null);
				BeecomDB.GetSnErrorEnum getSnErrorEnum = BeecomDB.getInstance()
						.checkGetSNPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), sn);
				if (getSnErrorEnum != BeecomDB.GetSnErrorEnum.GET_SN_OK) {
					packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_GET_SN_PERMISSION_DENY_ERR);
					session.write(packAck);
					return;
				}
				packAck.getVrbHead().setDevIdFlag(true);
				packAck.getPld().setDevUniqId(devUniqIdTmp);
				session.write(packAck);
				return;
			}
			if (pushDeviceIdFlag) {
				if (sysSigMapFlag || cusSigMapFlag || sigFlag || sysSigCusInfoFlag) {
					packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_VRB_HEADER_FLAG_ERR);
					session.write(packAck);
					return;
				}
				packAck.getVrbHead().setReqAllDeviceIdFlag(true);
				session.write(packAck);
				pushMessage(bpUserSession, ProductType.PUSH_DEVICE_ID_LIST, null, 0);
				return;
			}
			long uniqDevId = pld.getUniqDevId();

			pldAck.setDevUniqId(uniqDevId);
			if (!BeecomDB.getInstance().checkGetDeviceSignalMapPermission(
					bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), uniqDevId)) {
				packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR);
				session.write(packAck);
				return;
			}

			if (sysSigMapFlag) {
				pldAck.packSysSigMap(uniqDevId);
			}
			if (sysSigCusInfoFlag || cusSigMapFlag) {
				int langSupportMask = BeecomDB.getInstance().getDeviceLangSupportMask(uniqDevId) & vrb.getLangFlags();
				if (langSupportMask <= 0 || (langSupportMask & 0xFF) == 0) {
					packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_SERVER_ERROR);
					session.write(packAck);
					return;
				}
				pldAck.setCustomSignalLangSupportMask(langSupportMask);
			}
			if (sysSigCusInfoFlag) {
				pldAck.packSysSigCusInfo(uniqDevId);
			}
			if (cusSigMapFlag) {
				pldAck.packCusSigMap(uniqDevId);
			}
			BPError bpError = new BPError();
			BPDeviceSession bpDeviceSession = beecomDb.getBPDeviceSession(devUniqId);

			if (sigFlag) {
				bpDeviceSession = beecomDb.getBPDeviceSession(uniqDevId);
				if(null == bpDeviceSession) {
					packAck.getVrbHead().setRetCode(BPPacketGETACK.RET_CODE_OFF_LINE_ERR);
					session.write(packAck);
					return;
				}
				Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = bpDeviceSession.getSignalId2InfoUnitMap();
				if(null == signalId2InfoUnitMap) {
			    	vrbAck.setRetCode(BPPacketPOST.RET_CODE_OFF_LINE_ERR);
			    	session.write(packAck);
			    	return;
				}
				List<Integer> sigList = pld.getSignalLst();
				pldAck.initSigValMap();
				if(null == sigList || sigList.isEmpty()) {
			    	session.write(packAck);
			    	return;
				}
				Iterator<Integer> it = sigList.iterator();
				SignalInfoUnitInterface signalInfoUnitInterface;
				/* check error */
				Integer signalId;
				byte flags;
				Object value;
				while (it.hasNext()) {  
					signalId = it.next(); 
				    if(!signalId2InfoUnitMap.containsKey(signalId)) {
				    	vrbAck.setRetCode(BPPacketGETACK.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
				    	pldAck.setUnsupportedSignalId(signalId);
				    	session.write(packAck);
				    	return;
				    }
				    signalInfoUnitInterface = signalId2InfoUnitMap.get(signalId);
			    	value = signalInfoUnitInterface.getSignalValue(); 
			    	if(null == value) {
			    		logger.error("Inner error: null == value");
				    	vrbAck.setRetCode(BPPacketGETACK.RET_CODE_SERVER_ERROR);
				    	pldAck.setUnsupportedSignalId(signalId);
				    	session.write(packAck);
				    	return;
			    	}
				    if(signalId < BPPacket.SYS_SIG_START_ID) {
				    	if(null == signalInfoUnitInterface.getSignalInterface()) {
				    		logger.error("Inner error: null == signalInfoUnitInterface.getSignalInterface())");
					    	vrbAck.setRetCode(BPPacketGETACK.RET_CODE_SERVER_ERROR);
					    	pldAck.setUnsupportedSignalId(signalId);
					    	session.write(packAck);
					    	return;
				    	}
				    	flags = (byte)signalInfoUnitInterface.getSignalInterface().getValType();

				    } else {
						BPSysSigTable bpSysSigTable = BPSysSigTable.getSysSigTableInstance();
						int systemSignalIdOffset = signalId - BPPacket.SYS_SIG_START_ID;
						SysSigInfo sysSigInfo = bpSysSigTable.getSysSigInfo(systemSignalIdOffset);
						if(null == sysSigInfo) {
				    		logger.error("Inner error: null == sysSigInfo");
					    	vrbAck.setRetCode(BPPacketGETACK.RET_CODE_SERVER_ERROR);
					    	pldAck.setUnsupportedSignalId(signalId);
					    	session.write(packAck);
					    	return;
						}
						flags = sysSigInfo.getValType();
				    }

				    pldAck.putSigValMap(signalId, flags, value);
				} 
			}

			if (bpError.getErrorCode() == BPError.BP_ERROR_STATISTICS_NONE_SIGNAL) {
				List<Integer> statisticsNoneSignalList = bpError.getStatisticsNoneSignalList();
				if (null == statisticsNoneSignalList || statisticsNoneSignalList.isEmpty()) {
					logger.error("Inner error: null == statisticsNoneSignalList || statisticsNoneSignalList.isEmpty()");
					vrbAck.setRetCode(BPPacketGETACK.RET_CODE_SERVER_ERROR);
					session.write(packAck);
				} else {
					bpDeviceSession.getSession().write(decodedPack);
					if (!bpDeviceSession.putRelayList(session, BPPackFactory.createBPPackAck(decodedPack),
							bpDeviceSession.getTimeout())) {
						packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_BUFFER_FILLED_ERR);
						session.write(packAck);
						return;
					}
				}
			} else {
				session.write(packAck);
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}
	
	private void onGetAck(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		
		BPDeviceSession bpDeviceSession = null;
		try {
			bpDeviceSession = (BPDeviceSession)session.getAttribute(SESS_ATTR_BP_SESSION);
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		if(null == bpDeviceSession) {
			return;
		}
		
		vrb = decodedPack.getVrbHead();
		bpDeviceSession.startRelay(vrb.getPackSeq());
	}
	
	private void onPost(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		VariableHeader vrbAck;
		Payload pld;
		Payload pldAck;
		long devUniqId = 0;
		BeecomDB beecomDb = BeecomDB.getInstance();
		

		BPUserSession bpUserSession = null;
		bpUserSession = (BPUserSession)getBPSession(session);
		if(null == bpUserSession) {
			/* not connected */
			session.closeOnFlush();
			return;
		}
		
		BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
		if(null == packAck) {
			logger.error("Invalid packAck");
			return;
		}
		
		packAck.getFxHead().setCrcType(bpUserSession.getCrcType());
		packAck.getFxHead().setEncryptType(bpUserSession.getEncryptionType());
		
		vrb = decodedPack.getVrbHead();
		pld = decodedPack.getPld();
		devUniqId = pld.getUniqDevId();
		
		vrbAck = packAck.getVrbHead();
		pldAck = packAck.getPld();
		
		boolean sigAttrFlag = vrb.getSigAttrFlag();
		boolean sigValFlag = vrb.getSigValFlag();
		if(sigAttrFlag && sigValFlag) {
			packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_INVALID_FLAGS_ERR);
			session.write(packAck);
			return;
		}
		
		if(sigValFlag) {
			BPDeviceSession bpDeviceSession = beecomDb.getBPDeviceSession(devUniqId);
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
			Map<Integer, Map.Entry<Byte, Object> > sigValMap = pld.getSigValMap();
			Iterator<Map.Entry<Integer, Map.Entry<Byte, Object>>> entriesSigVals = sigValMap.entrySet().iterator();
			SignalInfoUnitInterface signalInfoUnitInterface;
			Map.Entry<Integer, Map.Entry<Byte, Object>> entry;
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
			
			byte[] relayData = decodedPack.getSignalValueRelay();
			pushMessage(bpDeviceSession, ProductType.POST_SIGNAL_VALUE, relayData, vrb.getPackSeq());
			/* cache for POSTACK to find the user */
			bpDeviceSession.putRelayList(bpUserSession, vrb.getPackSeq());
			return;
		}
		
		if(sigAttrFlag) {
			BPDeviceSession bpDeviceSesssion = null;
			bpDeviceSesssion = BeecomDB.getInstance().getBPDeviceSession(devUniqId);

			if(null == bpDeviceSesssion) {
				packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_INVALID_DEVICE_ID_ERR);
				session.write(packAck);
				return;
			}
			
			if(!BeecomDB.getInstance().checkGetDeviceSignalMapPermission(bpUserSession.getUserInfoUnit().getUserInfoHbn().getId(), devUniqId)) {
				packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR);
				session.write(packAck);
				return;
			}
			
			// TODO: check bpDeviceSession.getSession() active and assemble the packAck
			

			if(vrb.getSigAttrFlag()) {
				/* forward the packet to the device
				 * and put a callback when get the response */
			}
		}
		
		session.write(packAck);
		

	}
	
	private void onPostAck(IoSession session, BPPacket decodedPack) {
		BPDeviceSession bpDeviceSession = null;
		VariableHeader vrb;
		
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
		// pld = decodedPack.getPld();
		
		boolean sigAttrFlag = vrb.getSigAttrFlag();
		
		if(sigAttrFlag) {
			decodedPack.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_PEER_INNER_ERR);
			bpDeviceSession.updateRelayList(vrb.getPackSeq(), decodedPack);	
		}
		
		byte[] relayData = decodedPack.getSignalValueRelay();
		BPUserSession userSession = bpDeviceSession.getRelayUserSession(vrb.getPackSeq());
		pushMessage(userSession, ProductType.POSTACK_SIGNAL_VALUE, relayData, vrb.getPackSeq());

	}
	
	private void onReport(IoSession session, BPPacket decodedPack) {
		/* NOTE:
		 * get essential variables */
		BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
		if(null == packAck) {
			logger.error("Invalid packAck");
			return;
		}
		
		BPSession bpSessionTmp = getBPSession(session);
		if(null == bpSessionTmp || !(bpSessionTmp instanceof BPDeviceSession)) {
			/* NOTE: 
			 * not connected */
			session.closeOnFlush();
			return;
		}
		
		BeecomDB beecomDb = BeecomDB.getInstance();
		VariableHeader vrb = decodedPack.getVrbHead();
		VariableHeader vrbAck = packAck.getVrbHead();
		Payload pld = decodedPack.getPld();
		Payload pldAck = packAck.getPld();
		BPDeviceSession bpDeviceSession = (BPDeviceSession)bpSessionTmp;
		
		boolean sysSigMapFlag = vrb.getSysSigMapFlag();
		boolean cusSigMapFlag = vrb.getCusSigMapFlag();
		boolean sigValFlag = vrb.getSigValFlag();
		boolean sysSigCusInfoFlag = vrb.getSysCusFlag();
		long uniqDevId = bpDeviceSession.getUniqDevId();
		
		/* NOTE:
		 * set packAck fixed head info
		 */
		packAck.getFxHead().setCrcType(bpDeviceSession.getCrcType());
		packAck.getFxHead().setEncryptType(bpDeviceSession.getEncryptionType());
		
		/* NOTE:
		 * handle the packet message
		 */
		if(vrb.getSigMapChecksumFlag()) {
			/* NOTE:
			 * Check the signal map checksum. 
			 * If error occurred, then the device needs to report its new signal map
			 */
			if (!beecomDb.checkSignalMapChksum(uniqDevId, pld.getSigMapCheckSum())) {
				vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIGNAL_MAP_CHECKSUM_ERR);
			} else {
				if(beecomDb.getSignalInfoUnitInterfaceMap(bpDeviceSession)) {
					bpDeviceSession.setSessionReady(true);
					List<SystemSignalInfoUnit> systemSignalInfoUnitTmp = new ArrayList<>();
					systemSignalInfoUnitTmp = beecomDb.getSystemSignalUnitLst(uniqDevId, systemSignalInfoUnitTmp);
					List<CustomSignalInfoUnit> customSignalInfoUnitTmp = new ArrayList<>();
					customSignalInfoUnitTmp = beecomDb.getCustomSignalUnitLst(uniqDevId, customSignalInfoUnitTmp, BPLanguageId.STANDARD_ALL_LANG_MASK);
					Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = new HashMap<>();
					int size;
					SignalInfoUnitInterface signalInfoUnitInterfaceTmp;
					if(null != systemSignalInfoUnitTmp) {
						size = systemSignalInfoUnitTmp.size();
						for(int i = 0; i < size; i++) {
							signalInfoUnitInterfaceTmp = systemSignalInfoUnitTmp.get(i);
							signalId2InfoUnitMap.put(signalInfoUnitInterfaceTmp.getSignalId(), signalInfoUnitInterfaceTmp);
						}
					}
					if(null != customSignalInfoUnitTmp) {
						size = customSignalInfoUnitTmp.size();
						for(int i = 0; i < size; i++) {
							signalInfoUnitInterfaceTmp = customSignalInfoUnitTmp.get(i);
							signalId2InfoUnitMap.put(signalInfoUnitInterfaceTmp.getSignalId(), signalInfoUnitInterfaceTmp);
						}
					}
					bpDeviceSession.setSignalId2InfoUnitMap(signalId2InfoUnitMap);
					bpDeviceSession.setSessionReady(true);
				} else {
					vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIGNAL_MAP_DAMAGED_ERR);
				}
			}
			session.write(packAck);
			return;
		} 
		
		if(sysSigMapFlag || sysSigCusInfoFlag || cusSigMapFlag) {
			/* TODO: not support save single signal map now */
			BeecomDB.getInstance().clearDeviceSignalInfo(uniqDevId);
		}
		
		List<Integer> systemSignalEnabledList = pld.getSystemSignalEnabledList();
		List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit = pld.getSystemSignalCustomInfoUnitLst();
		List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
		boolean newSigMapFlagOk = true;
		
		if(newSigMapFlagOk && sysSigMapFlag) {			
			/* NOTE:
			 * check and save system signal map
			 */
			/* input the session of the signal map */
			if(!BeecomDB.getInstance().putSystemSignalEnabledMap(uniqDevId, systemSignalEnabledList)) {
				newSigMapFlagOk = false;
			}
		}
		
		if(newSigMapFlagOk && sysSigCusInfoFlag) {
			/* NOTE:
			 * check and save system custom signal map
			 */
			if(!BeecomDB.getInstance().putSystemCustomSignalInfoMap(uniqDevId, systemSignalCustomInfoUnit)) {
				newSigMapFlagOk = false;
			}
		}
		if(newSigMapFlagOk && cusSigMapFlag) {
			/* NOTE:
			 * check and save custom signal map
			 */
			if(!BeecomDB.getInstance().putCustomSignalMap(uniqDevId, customSignalInfoUnitList)) {
				newSigMapFlagOk = false;
			}
		}
		if(!newSigMapFlagOk) {
			/* NOTE:
			 * return error when checking signal map wrong
			 */
			packAck.getVrbHead().setRetCode(BPPacketRPRTACK.RET_CODE_SIGNAL_MAP_ERR);
			session.write(packAck);
			return;
		}
		if(sysSigMapFlag || cusSigMapFlag || sysSigCusInfoFlag) {
			/* NOTE: 
			 * save the signal map checksum
			 * */
			if(!BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapCheckSum())) {
				logger.error("Internal error: !BeecomDB.getInstance().putSignalMapChksum(uniqDevId, pld.getSigMapChecksum())");
				packAck.getVrbHead().setRetCode(BPPacketRPRTACK.RET_CODE_SERVER_ERROR);
				session.write(packAck);
				return;
			} 
			/* NOTE:
			 * set the signal id and its info map 
			 * */
			Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = bpDeviceSession.parseSignalInfoUnitInterfaceMap(systemSignalEnabledList, systemSignalCustomInfoUnit, customSignalInfoUnitList);
			bpDeviceSession.setSessionReady(true);
			bpDeviceSession.setSignalId2InfoUnitMap(signalId2InfoUnitMap);
			return;
		}
		
		if(sigValFlag) {
			Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = bpDeviceSession.getSignalId2InfoUnitMap();
			if(null == signalId2InfoUnitMap) {
		    	vrbAck.setRetCode(BPPacketRPRTACK.RET_CODE_SIG_MAP_UNCHECK);
		    	session.write(packAck);
		    	return;
			}
			Map<Integer, Map.Entry<Byte, Object> > sigValMap = pld.getSigValMap();
			Iterator<Map.Entry<Integer, Map.Entry<Byte, Object>>> entriesSigVals = sigValMap.entrySet().iterator();
			SignalInfoUnitInterface signalInfoUnitInterface;
			Map.Entry<Integer, Map.Entry<Byte, Object>> entry;
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
			// TODO: [NEED](no-notifying only working locally):strip(relayData), to avoid pushing no-notifying signals
			// bpDeviceSession.reportSignalValue2UserClient(relayData);
			pushMessage(bpDeviceSession, ProductType.PUSH_SIGNAL_VALUE, relayData, 0);
		}
		
		session.write(packAck);
	}
	
	private void onPing(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		// Payload pld, pldAck;
		// long devUniqId = 0;
		BPSession bpSession;
		// BPPacketType packType = decodedPack.getPackTypeFxHead();
		// BeecomDB beecomDb = BeecomDB.getInstance();
		
		vrb = decodedPack.getVrbHead();
		byte flags = vrb.getFlags();
		int seqId = vrb.getPackSeq();
		// boolean userOnLine = vrb.getUserOnLine();
		logger.info("PING: flags={}, sid={}", flags, seqId);
		
		bpSession = getBPSession(session);
		if(null == bpSession) {
			/* not connected */
			session.closeOnFlush();
			return;
		}

		BPPacket packAck = BPPackFactory.createBPPackAck(decodedPack);
		
		packAck.getFxHead().setCrcType(bpSession.getCrcType());
		packAck.getFxHead().setEncryptType(bpSession.getEncryptionType());

		packAck.getVrbHead().setPackSeq(seqId);
		packAck.getVrbHead().setRetCode(BPPacketPINGACK.RET_CODE_OK);

		session.write(packAck);
	}
	
	private void onPushAck(IoSession session, BPPacket decodedPack) {
		VariableHeader vrb;
		
		vrb = decodedPack.getVrbHead();
		int retCode = vrb.getRetCode();
		// int packSeq = vrb.getPackSeq();
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
	}
	
	private void onDisconn(IoSession session, BPPacket decodedPack) {
		BPSession bpSession;
		bpSession = getBPSession(session);
		if(null == bpSession) {
			/* not connected */
			session.closeOnFlush();
			return;
		}
		if(bpSession != null) {
			logger.info("Disconn, {}", bpSession);
		}
		session.closeOnFlush();
	}
	
	private BPSession getBPSession(IoSession session) {
		BPSession ret = null;
		try {
			ret = (BPSession) session
					.getAttribute(SESS_ATTR_BP_SESSION);
			if(null != ret && ret.isSessionReady()) {
				ret.setLatestPingTimestamp(System.currentTimeMillis());
			}
			
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		return ret;
	}
}
