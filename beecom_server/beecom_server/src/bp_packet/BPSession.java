/**
 * 
 */
package bp_packet;

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

import db.RelayData;
import other.CrcChecksum;
import other.Util;

/**
 * @author Ansersion
 *
 */

public abstract class BPSession {
	private static final Logger logger = LoggerFactory.getLogger(BPSession.class);
	public static final int LOGIN_TIME_UPDATE_TRIGGER_LIMIT = 30 * 60 * 1000; // 30 minites 
	
	private IoSession session;
	
	private int procLevel;
	private int aliveTime;
	private short timeout;
	private boolean debugMode;
	private byte performanceClass;
	private long latestPingTimestamp;
	private boolean sessionReady;
	private long loginTimestamp;
	
	private Map<Integer, RelayData> seqId2TimerRelayMap;
	private Lock relayMaplock = new ReentrantLock();
	
	private EncryptType.EnType encryptionType;
	private CrcChecksum crcType;
	
	private int langMask;
	
	public BPSession(IoSession session) {
		this.session = session;
		this.procLevel = 0;
		this.aliveTime = 3600;
		this.timeout = 120;
		this.seqId2TimerRelayMap = new HashMap<>();
		this.relayMaplock = new ReentrantLock();    
		this.sessionReady = false;
	}
	
	public abstract boolean ifUserSession(); 
	
	public Map<Integer, List<Object> > getSignalValueMap() {
		return null;
	}
	
	public String getUserName() {
		return null;
	}

	public void setUserName(String userName) {
	}

	public String getEmail() {
		return null;
	}

	public void setEmail(String email) {
	}

	public String getPhone() {
		return null;
	}

	public void setPhone(String phone) {
	}

	public String getPassword() {
		return null;
	}

	public void setPassword(String password) {
	}
	
	public void setUniqDeviceId(Long uniqDeviceId) {
	}

	public int getProcLevel() {
		return procLevel;
	}

	public void setProcLevel(int procLevel) {
		this.procLevel = procLevel;
	}

	public int getAliveTime() {
		return aliveTime;
	}

	public void setAliveTime(int aliveTime) {
		this.aliveTime = aliveTime;
	}

	public short getTimeout() {
		return timeout;
	}

	public void setTimeout(short timeout) {
		this.timeout = timeout;
	}

	public String getSn() {
		return null;
	}

	public void setSn(String sn) {
	}
	
	public long getUniqDevId() {
		return 0;
	}
	
	public void setUniqDevId(long uniqDevId) {
	}
	
	public IoSession getSession() {
		return session;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public byte getPerformanceClass() {
		return performanceClass;
	}

	public void setPerformanceClass(byte performanceClass) {
		this.performanceClass = performanceClass;
	}

	public int getRelayListSize() {
		int ret = 0;
		relayMaplock.lock();
        try {
        	ret = seqId2TimerRelayMap.size();
        } catch (Exception e) {
        	logger.error("Inner error: Exception in getRelayListSize");
            ret = 0;
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}
	
	public boolean putRelayList(int seqId, int delayMs, RelayData relayData) {
		boolean ret = false;
		relayMaplock.lock();
        try {
        	if(!seqId2TimerRelayMap.containsKey(seqId)) {
        		ret = true;
        		Timer timer = new Timer();
        		timer.schedule(relayData.getTimerTask(), delayMs);
        		seqId2TimerRelayMap.put(seqId, relayData);
        	} else {
        		logger.error("Inner error: seqId2TimerRelayMap.containsKey(seqId)");
        	}
        	
        } catch (Exception e) {
        	logger.error("Inner error: Exception in putRelayList");
        	ret = false;
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}
	
	public RelayData getRelayData(TimerTask timerTask) {
		RelayData ret = null;
		relayMaplock.lock();
        try {
			Iterator<Map.Entry<Integer, RelayData>> it = seqId2TimerRelayMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, RelayData> entry = it.next();
				if(entry.getValue().getTimerTask() == timerTask) {
					ret = entry.getValue();
				}
			}
        } catch (Exception e) {
        	logger.error("Inner error: Exception in getRelayData");
        	ret = null;
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}
	
	/* used for reply to user client */
	public boolean startRelay(int seqId) {
		boolean ret = false;
		relayMaplock.lock();
        try {
        	if(!seqId2TimerRelayMap.containsKey(seqId)) {
        		RelayData relayData = seqId2TimerRelayMap.get(seqId);
        		relayData.getTimer().cancel();
        		if(relayData.isTimeoutRelayed()) {
        			return ret;
        		}
        		relayData.getIoSession().write(relayData.getRelayData());
        		removeRelayList(seqId);
        		ret = true;
        		
        	} else {
        		logger.error("Inner error: seqId2TimerRelayMap.containsKey(seqId)");
        	}
        	
        } catch (Exception e) {
        	Util.logger(logger, Util.ERROR, e);
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}
	
	public boolean updateRelayList(int seqId, Object relayData) {
		boolean ret = false;
		relayMaplock.lock();
        try {
        	if(!seqId2TimerRelayMap.containsKey(seqId)) {
        		seqId2TimerRelayMap.get(seqId).setRelayData(relayData);
        	} else {
        		logger.error("Inner error: seqId2TimerRelayMap.containsKey(seqId)");
        	}
        	
        } catch (Exception e) {
        	logger.error("Inner error: Exception in putRelayList");
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}
	
	public boolean removeRelayList(int seqId) {
		boolean ret = false;
		relayMaplock.lock();
        try {
        	seqId2TimerRelayMap.remove(seqId);
        	ret = true;
        } catch (Exception e) {
        	logger.error("Inner error: Exception in removeRelayList");
        }finally {
        	relayMaplock.unlock();
        }
        
        return ret;
	}

	public long getLatestPingTimestamp() {
		return latestPingTimestamp;
	}

	public void setLatestPingTimestamp(long latestPingTimestamp) {
		this.latestPingTimestamp = latestPingTimestamp;
	}

	public EncryptType.EnType getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(EncryptType.EnType encryptionType) {
		this.encryptionType = encryptionType;
	}

	public CrcChecksum getCrcType() {
		return crcType;
	}

	public void setCrcType(CrcChecksum crcType) {
		this.crcType = crcType;
	}
	
	public void setEncryptionType(FixedHeader fxHead) {
		this.encryptionType = fxHead.getEncryptType();
	}

	public void setCrcType(FixedHeader fxHead) {
		this.crcType = fxHead.getCrcChk();
	}
	
	public boolean isSessionReady() {
		return sessionReady;
	}
	public void setSessionReady(boolean sessionReady) {
		this.sessionReady = sessionReady;
	}

	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}
	
	public long getSnId() {
		return 0;
	}
	
	public abstract void updateLoginTime();
	

	public int getLangMask() {
		return langMask;
	}

	public void setLangMask(int langMask) {
		this.langMask = langMask;
	}
}
