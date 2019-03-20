package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.CustomSignalInfoUnit;
import db.RelayData;
import db.SignalInfoUnitInterface;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import other.Util;

public class BPDeviceSession extends BPSession {
	private static final Logger logger = LoggerFactory.getLogger(BPDeviceSession.class); 
	private static final int MAX_RELAY_LIST_SIZE = 10;
	private Long uniqDeviceId;
	private String password;
	private Long adminId;
	private Map<Integer, List<Object> > signalValueMap;
	private Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap;
	private boolean sigMapCheckOK;
	private long snId;
	
	public BPDeviceSession(IoSession session) {
		super(session);
		uniqDeviceId = 0L;
		password = "";
		signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
		sigMapCheckOK = false;
		signalId2InfoUnitMap = null;
		snId = 0L;
	}
	
	public BPDeviceSession(IoSession session, Long uniqDeviceId, String password, Long adminId, long snId) {
		super(session);
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
		this.adminId = adminId;
		this.signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
		sigMapCheckOK = false;
		signalId2InfoUnitMap = null;
		this.snId = snId;
	}
	
	public boolean putRelayList(IoSession iosession, BPPacket bppacket, int timeout) {
		
		if(MAX_RELAY_LIST_SIZE <= getRelayListSize()) {
			return false;
		}
		
        TimerTask timeoutTask = new TimerTask() {  
            @Override  
            public void run() {  
            	try {
					RelayData relayData = getRelayData(this);
					BPPacket packAck = (BPPacket) relayData.getRelayData();
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_TIMEOUT_ERR);
					session.write(packAck);
					relayData.getIoSession().write(packAck);
					relayData.setTimeoutRelayed(true);
					removeRelayList(packAck.getVrbHead().getPackSeq());
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
				}
            }  
        };
        
		RelayData relayData = new RelayData(new Timer(), timeoutTask, iosession, System.currentTimeMillis(), bppacket);
		
		return super.putRelayList(bppacket.getVrbHead().getPackSeq(), timeout * 1000, relayData);
	}
	
	@Override
	public Map<Integer, List<Object> > getSignalValueMap() {
		return signalValueMap;
	}
	
	@Override
	public boolean ifUserSession() {
		return false;
	}

	@Override
	public void setUniqDeviceId(Long uniqDeviceId) {
		this.uniqDeviceId = uniqDeviceId;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return uniqDeviceId.toString();
	}
	
	public long getUniqDevId() {
		return uniqDeviceId;
	}

	public Map<Integer, SignalInfoUnitInterface> getSignalId2InfoUnitMap() {
		return signalId2InfoUnitMap;
	}

	public void setSignalId2InfoUnitMap(Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap) {
		this.signalId2InfoUnitMap = signalId2InfoUnitMap;
	}

	public boolean isSigMapCheckOK() {
		return sigMapCheckOK;
	}

	public void setSigMapCheckOK(boolean sigMapCheckOK) {
		this.sigMapCheckOK = sigMapCheckOK;
	}
	
	public void parseSignalInfoUnitInterfaceMap(List<Integer> systemSignalEnabledList, List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitList, List<CustomSignalInfoUnit> customSignalInfoUnitList) {
		Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = new HashMap<>();
		
		try {
			Iterator<Integer> itInteger = systemSignalEnabledList.iterator();
			Integer signalId;
			while (itInteger.hasNext()) {
				signalId = itInteger.next() + BPPacket.SYS_SIG_START_ID;
				signalId2InfoUnitMap.put(signalId, new SystemSignalInfoUnit(signalId));
			}
			Iterator<SystemSignalCustomInfoUnit> itSystemSignalCustomInfoUnit = systemSignalCustomInfoUnitList
					.iterator();
			SystemSignalCustomInfoUnit systemSignalCustomInfoUnit;
			int customFlags;
			while (itSystemSignalCustomInfoUnit.hasNext()) {
				systemSignalCustomInfoUnit = itSystemSignalCustomInfoUnit.next();
				SystemSignalInfoUnit systemSignalInfoUnit = (SystemSignalInfoUnit)signalId2InfoUnitMap.get(systemSignalCustomInfoUnit.getSysSigId());
				customFlags = systemSignalCustomInfoUnit.getCustomFlags();
				systemSignalInfoUnit.setCustomFlags(customFlags);
				systemSignalInfoUnit.setSystemSignalInterface(systemSignalCustomInfoUnit.getSignalInterface());
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_STATISTICS) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
					/* in SignalInterface */
					/* do nothing */
					/* TODO: reconstruct SystemSignalCustomInfoUnit to have enum language */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_GROUP_LANG) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ACCURACY) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
					/* in SignalInterface */
					/* do nothing */
				}

				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM) != 0) {
					/* no use now */
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_CLASS) != 0) {
					systemSignalInfoUnit.setAlarmClass(systemSignalCustomInfoUnit.getAlarmClass());
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_BEF) != 0) {
					systemSignalInfoUnit.setAlarmDelayBef(systemSignalCustomInfoUnit.getDelayBeforeAlarm());
				}
				if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_AFT) != 0) {
					systemSignalInfoUnit.setAlarmDelayAft(systemSignalCustomInfoUnit.getDelayAfterAlarm());
				}

			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		
		setSignalId2InfoUnitMap(signalId2InfoUnitMap);
	}
	
	public void reportSignalValue2UserClient(byte[] reportData) {
		/* TODO: push the signal value to the device
		 * and put a callback when get the response if notifying flag set */
	}

	public long getSnId() {
		return snId;
	}

	public void setSnId(long snId) {
		this.snId = snId;
	}
	
	
}
