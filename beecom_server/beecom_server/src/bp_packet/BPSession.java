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
import other.BPValue;
import other.CrcChecksum;
import other.Util;
import sys_sig_table.BPSysSigTable;

/**
 * @author Ansersion
 *
 */

class SigIdNonExistException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1342721670055052282L;
	SigIdNonExistException(String msg) {
		super(msg);
	}
	SigIdNonExistException() {
		super();
	}
}
public abstract class BPSession {
	
	private static final Logger logger = LoggerFactory.getLogger(BPSession.class);
	
	IoSession session;
	int seqIdUsrClnt = 0;
	Map<Integer, Byte[]> mapDist2SysSigMap = null;
	Map<Integer, Object> sysSigMap;
	Map<Integer, Object> systemSignalValueMap;
	Map<Integer, Map.Entry<Byte, Object>> customSignalValueMap;
	
	private int procLevel;
	private int aliveTime;
	private short timeout;
	private boolean debugMode;
	private byte performanceClass;
	private long latestPingTimestamp;
	private boolean sessionReady;
	
	private Map<Integer, RelayData> seqId2TimerRelayMap;
	private Lock relayMaplock = new ReentrantLock();
	
	private EncryptType.EnType encryptionType;
	private CrcChecksum crcType;
	
	public BPSession(IoSession session) {
		this.session = session;
		this.procLevel = 0;
		this.aliveTime = 3600;
		this.timeout = 120;
		this.seqId2TimerRelayMap = new HashMap<>();
		this.relayMaplock = new ReentrantLock();    
		this.sessionReady = false;
	}
	
	public BPSession(IoSession session, String userName, String password) {
		this(session);
	}
	
	public BPSession(IoSession session, Long uniqDeviceId, String password) {
		this(session);    
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

	public BPSession(byte[] userName, byte[] password, int clientId, boolean userLogin, boolean devLogin, long uniqDevId) {
		seqIdUsrClnt = 0;
		sysSigMap = new HashMap<>();
	}
	
	public long getUniqDevId() {
		return 0;
	}
	
	public void setUniqDevId(long uniqDevId) {
	}
	
	public Map<Integer, Object> getSysSigMap() {
		return sysSigMap;
	}
	
	public IoSession getSession() {
		return session;
	}
	
	public void dumpSysSig() {
		Iterator<Map.Entry<Integer, Object>> entries = sysSigMap.entrySet().iterator(); 
		while (entries.hasNext()) {    
		    Map.Entry<Integer, Object> entry = entries.next();
		    Integer key = entry.getKey();  
		    Object value = entry.getValue();  
		    logger.info("{}=>{}", key, value); 
		}  
	}
	public void setSysSigMap(Map<Integer, Byte[]> sysSigMap) {
		if(null == mapDist2SysSigMap) {
			mapDist2SysSigMap = sysSigMap;
		} else {
			Iterator<Map.Entry<Integer, Byte[]>> entries = sysSigMap.entrySet().iterator(); 
			while (entries.hasNext()) {    
			    Map.Entry<Integer, Byte[]> entry = entries.next();
			    Integer key = entry.getKey();  
			    Byte[] value = entry.getValue();  
			    mapDist2SysSigMap.put(key, value);  
			  
			}  
		}
	}
	
	public void initSysSigValDefault() {
		sysSigMap.clear();
		Iterator<Map.Entry<Integer, Byte[]>> entries = mapDist2SysSigMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<Integer, Byte[]> entry = entries.next();
			Integer key = entry.getKey();
			Byte[] value = entry.getValue();
			int distSigStartId = BPPacket.SYS_SIG_START_ID + key * BPPacket.SYS_SIG_DIST_STEP;
			BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
			for(int i = 0; i < value.length; i++) {
				if(((1 << 0) & value[i]) == (1 << 0)) {
					sysSigMap.put(distSigStartId + i * 8 + 7, sysSigTab.getSysSigInfo(i * 8 + 7).getValDef());
				}
				if(((1 << 1) & value[i]) == (1 << 1)) {
					sysSigMap.put(distSigStartId + i * 8 + 6, sysSigTab.getSysSigInfo(i * 8 + 6).getValDef());
				}
				if(((1 << 2) & value[i]) == (1 << 2)) {
					sysSigMap.put(distSigStartId + i * 8 + 5, sysSigTab.getSysSigInfo(i * 8 + 5).getValDef());
				}
				if(((1 << 3) & value[i]) == (1 << 3)) {
					sysSigMap.put(distSigStartId + i * 8 + 4, sysSigTab.getSysSigInfo(i * 8 + 4).getValDef());
				}
				if(((1 << 4) & value[i]) == (1 << 4)) {
					sysSigMap.put(distSigStartId + i * 8 + 3, sysSigTab.getSysSigInfo(i * 8 + 3).getValDef());
				}
				if(((1 << 5) & value[i]) == (1 << 5)) {
					sysSigMap.put(distSigStartId + i * 8 + 2, sysSigTab.getSysSigInfo(i * 8 + 2).getValDef());
				}
				if(((1 << 6) & value[i]) == (1 << 6)) {
					sysSigMap.put(distSigStartId + i * 8 + 1, sysSigTab.getSysSigInfo(i * 8 + 1).getValDef());
				}
				if(((1 << 7) & value[i]) == (1 << 7)) {
					sysSigMap.put(distSigStartId + i * 8 + 0, sysSigTab.getSysSigInfo(i * 8 + 0).getValDef());
				}
			}
		}
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

	public Map<Integer, Object> getSystemSignalValueMap() {
		return systemSignalValueMap;
	}

	public void setSystemSignalValueMap(Map<Integer, Object> systemSignalValueMap) {
		this.systemSignalValueMap = systemSignalValueMap;
	}

	public Map<Integer, Map.Entry<Byte, Object>> getCustomSignalValueMap() {
		return customSignalValueMap;
	}

	public void setCustomSignalValueMap(Map<Integer, Map.Entry<Byte, Object>> customSignalValueMap) {
		this.customSignalValueMap = customSignalValueMap;
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
}
