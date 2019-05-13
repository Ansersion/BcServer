package bp_packet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.RelayData;
import db.SignalInfoUnitInterface;
import db.SnInfoHbn;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import other.Util;
import sys_sig_table.BPSysSigTable;

public class BPDeviceSession extends BPSession {
	private static final Logger logger = LoggerFactory.getLogger(BPDeviceSession.class); 
	private static final int MAX_RELAY_LIST_SIZE = 10;
	private Long uniqDeviceId;
	private int maxReportSignalMapNumber;
	private String password;
	private Map<Integer, List<Object> > signalValueMap;
	private Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap;
	private long snId;
	
	private List<RelayAckData> relayAckDataList;
	private Lock relayAckDataListLock = new ReentrantLock();
	
	public BPDeviceSession(IoSession session) {
		super(session);
		uniqDeviceId = 0L;
		password = "";
		signalValueMap = new HashMap<>();
		signalId2InfoUnitMap = null;
		snId = 0L;
		relayAckDataList = new ArrayList<>();
	}
	
	public BPDeviceSession(IoSession session, Long uniqDeviceId, String password, Long adminId, long snId, int maxReportSigTabNum) {
		super(session);
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
		relayAckDataList = new ArrayList<>();
		this.signalValueMap = new HashMap<>();
		signalId2InfoUnitMap = null;
		this.snId = snId;
		this.maxReportSignalMapNumber = maxReportSigTabNum;
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
					getSession().write(packAck);
					relayData.getIoSession().write(packAck);
					relayData.setTimeoutRelayed(true);
					removeRelayList(packAck.getVrbHead().getPackSeq());
				} catch (Exception e) {
					Util.logger(logger, Util.ERROR, e);
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
	
	@Override
	public long getUniqDevId() {
		return uniqDeviceId;
	}

	public Map<Integer, SignalInfoUnitInterface> getSignalId2InfoUnitMap() {
		return signalId2InfoUnitMap;
	}

	public void setSignalId2InfoUnitMap(Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap) {
		this.signalId2InfoUnitMap = signalId2InfoUnitMap;
	}
	
	public Map<Integer, SignalInfoUnitInterface> parseSignalInfoUnitInterfaceMap(List<Integer> systemSignalEnabledList, List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitList, List<CustomSignalInfoUnit> customSignalInfoUnitList) {
		Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMapTmp = new HashMap<>();
		
		try {
			Iterator<Integer> itInteger = systemSignalEnabledList.iterator();
			Integer signalId;
			while (itInteger.hasNext()) {
				signalId = itInteger.next() + BPPacket.SYS_SIG_START_ID;
				SystemSignalInfoUnit tmp = BPSysSigTable.getSysSigTableInstance().createNewSystemSignalInfoUnit(signalId - BPPacket.SYS_SIG_START_ID);
				if(null == tmp) {
					logger.error("Inner Error: null == BPSysSigTable.getSysSigTableInstance().createNewSystemSignalInfoUnit({})", signalId);
					continue;
				}
				signalId2InfoUnitMapTmp.put(signalId, tmp);
			}
			Iterator<SystemSignalCustomInfoUnit> itSystemSignalCustomInfoUnit = systemSignalCustomInfoUnitList
					.iterator();
			SystemSignalCustomInfoUnit systemSignalCustomInfoUnit;
			int customFlags;
			while (itSystemSignalCustomInfoUnit.hasNext()) {
				systemSignalCustomInfoUnit = itSystemSignalCustomInfoUnit.next();
				signalId = systemSignalCustomInfoUnit.getSysSigId();
				if(!signalId2InfoUnitMapTmp.containsKey(signalId)) {
					logger.error("Inner Error: system custom signal map info error({})", systemSignalCustomInfoUnit.getSysSigId());
					continue;
				}
				SystemSignalInfoUnit systemSignalInfoUnit = (SystemSignalInfoUnit)signalId2InfoUnitMapTmp.get(signalId);
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
		
		return signalId2InfoUnitMapTmp;
	}

	@Override
	public long getSnId() {
		return snId;
	}

	public void setSnId(long snId) {
		this.snId = snId;
	}
	
	public boolean putRelayList(BPUserSession userSession, int packSeq) {
		boolean ret = false;
		if(null == userSession) {
			return ret;
		}
		relayAckDataListLock.lock();
        try {
        	long timestamp = System.currentTimeMillis();
        	Iterator<RelayAckData> it = relayAckDataList.iterator();
        	RelayAckData tmp;
        	while(it.hasNext()){
        		tmp = it.next();
        	    if(tmp.getTimestamp() > timestamp || timestamp > tmp.getTimestamp() + userSession.getTimeout() * 1000){
        	        it.remove();
        	    } else {
        	    	/* later item is new and not need to remove anymore */
        	    	break;
        	    }
        	}
        	relayAckDataList.add(new RelayAckData(userSession, packSeq));
        	ret = true;
        } catch (Exception e) {
        	logger.error("Inner error: Exception in relayAckDataList");
        }finally {
        	relayAckDataListLock.unlock();
        }
        
        return ret;
	}
	
	public BPUserSession getRelayUserSession(int packSeq) {
		BPUserSession ret = null;
		relayAckDataListLock.lock();
        try {
        	long timestamp = System.currentTimeMillis();
        	Iterator<RelayAckData> it = relayAckDataList.iterator();
        	RelayAckData tmp;
        	while(it.hasNext()){
        		tmp = it.next();
        	    if(tmp.getPackSeq() == packSeq) {
            	    if(tmp.getTimestamp() > timestamp || timestamp > tmp.getTimestamp() + tmp.getUserSession().getTimeout() * 1000){
            	        it.remove();
            	        continue;
            	    }
            	    ret = tmp.getUserSession();
        	    	break;
        	    }
        	}
        } catch (Exception e) {
        	Util.logger(logger, Util.ERROR, e);
        }finally {
        	relayAckDataListLock.unlock();
        }
        
        return ret;
	}

	@Override
	public void updateLoginTime() {
		BeecomDB beecomDb = BeecomDB.getInstance();
		SnInfoHbn snInfoHbn = beecomDb.getSnInfoHbn(getSnId());
		long currentTimestamp = System.currentTimeMillis();
		if(null == snInfoHbn) {
			logger.error("Inner error: snId({}) not found", getSnId());
			return;
		}
		if(snInfoHbn.getExpiredDate().getTime() > currentTimestamp) {
			/* no need to update: the expired time is not reached*/
			/* update the timestamp in order to make it smooth to exist time calculation*/
			setLoginTimestamp(currentTimestamp);
			return;
		}
		if(snInfoHbn.getExistTime() == 0) {
			/* no need to update: the exist time is none already */
			return;
		}
		
		int loginPeriod = (int) ((currentTimestamp - getLoginTimestamp()) / 1000);
		if(loginPeriod > 0) {
			if(snInfoHbn.getExistTime() < loginPeriod) {
				snInfoHbn.setExistTime(0);
			} else {
				snInfoHbn.setExistTime(snInfoHbn.getExistTime() - loginPeriod);
			}
			beecomDb.updateHbn(snInfoHbn);
			setLoginTimestamp(currentTimestamp);
			logger.info("LOGIN PERIOD: {}", loginPeriod);
		}
		
	}

	public int getMaxReportSignalMapNumber() {
		return maxReportSignalMapNumber;
	}

	public void setMaxReportSignalMapNumber(int maxReportSignalMapNumber) {
		this.maxReportSignalMapNumber = maxReportSignalMapNumber;
	}
	
	
	
}
