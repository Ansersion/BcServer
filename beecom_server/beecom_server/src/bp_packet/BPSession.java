/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
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

import javafx.util.Pair;
import other.BPValue;
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
	
	private static final int MIN_ALIVE_TIME = 5;
	
	// private int clientId = 0;
	// long uniqDevId = 0;
	// private byte[] userName = null;
	// byte[] password = null;
	// boolean isUserLogin = false;
	// boolean isDevLogin = false;
	// private int seqIdDevClnt = 0;
	IoSession session;
	int seqIdUsrClnt = 0;
	// String devName;
	Map<Integer, Byte[]> mapDist2SysSigMap = null;
	Map<Integer, Object> sysSigMap;
	Map<Integer, Object> systemSignalValueMap;
	Map<Integer, Pair<Byte, Object>> customSignalValueMap;
	// private BPError error;
	
	private int procLevel;
	private int aliveTime;
	private short timeout;
	private boolean debugMode;
	private byte performanceClass;
	
	private Map<Integer, Timer> seqId2TimerRelayMap;
	private Lock relayMaplock = new ReentrantLock();    
	
	public BPSession(IoSession session) {
		this.session = session;
		this.procLevel = 0;
		this.aliveTime = 3600;
		this.timeout = 120;
		this.seqId2TimerRelayMap = new HashMap<>();
		this.relayMaplock = new ReentrantLock();    
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

	public Long getUniqDeviceId() {
		return 0L;
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
		if(aliveTime < MIN_ALIVE_TIME) {
			aliveTime = MIN_ALIVE_TIME;
		}
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
		// isDevLogin = devLogin;
		// isUserLogin = userLogin;
		// seqIdDevClnt = 0;
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
	
	// public String getDevName() {
	//	return devName;
	// }

	
	
	public boolean setSysSig(DevSigData devSigData) {
		// error = new BPError();
		boolean ret = true;
		Integer key = null;
		Short value = null;
		Object tmp = null;
		
		try {
			Map<Integer, Short> sigVal = devSigData.get2ByteDataMap();
			if (null != sigVal) {
				Iterator<Map.Entry<Integer, Short>> entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Integer, Short> entry = entries.next();
					key = entry.getKey();
					value = entry.getValue();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					// tmp.setVal(value);
				}
			}
		} catch(SigIdNonExistException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			ret = false;
		}

		try {
			Map<Integer, Integer> sigVal = devSigData.get4ByteDataMap();
			if (null != sigVal) {
				Iterator<Map.Entry<Integer, Integer>> entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Integer, Integer> entry = entries.next();
					key = entry.getKey();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					// tmp.setVal(entry.getValue());
				}
			}
		} catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			ret = false;
		}

		try {
			Map<Integer, Byte[]> sigVal = devSigData.getxByteDataMap();
			if (null != sigVal) {
				Iterator<Map.Entry<Integer, Byte[]>> entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Integer, Byte[]> entry = entries.next();
					key = entry.getKey();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
				}
			}
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			ret = false;
		}
		return ret;
	}
	
	public IoSession getSession() {
		return session;
	}

	public boolean setSysSig(Integer sigId, BPValue val) {
		boolean ret = false;
		if(sysSigMap.containsKey(sigId)) {
			sysSigMap.put(sigId, val);
			ret = true;
		}
		return ret;
	}
	
	public BPValue getSysSigVal(Integer sigId) {
		BPValue val = null;
		if(sysSigMap.containsKey(sigId)) {
			Object tmp = sysSigMap.get(sigId);
			// val = new BPValue(tmp.getType());
			// val.setValStr(tmp.getValStr());
		}
		return val;
	}
	
	// public void setDevName(String devName) {
	//	this.devName = devName;
	// }
	
	public void dumpSysSig() {
		Iterator<Map.Entry<Integer, Object>> entries = sysSigMap.entrySet().iterator(); 
		while (entries.hasNext()) {    
		    Map.Entry<Integer, Object> entry = entries.next();
		    Integer key = entry.getKey();  
		    Object value = entry.getValue();  
		    logger.info("{}=>{}", key, value.toString()); 
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
					sysSigMap.put(distSigStartId + i * 8 + 7, sysSigTab.getSysSigInfoLst().get(i * 8 + 7).getValDef());
				}
				if(((1 << 1) & value[i]) == (1 << 1)) {
					sysSigMap.put(distSigStartId + i * 8 + 6, sysSigTab.getSysSigInfoLst().get(i * 8 + 6).getValDef());
				}
				if(((1 << 2) & value[i]) == (1 << 2)) {
					sysSigMap.put(distSigStartId + i * 8 + 5, sysSigTab.getSysSigInfoLst().get(i * 8 + 5).getValDef());
				}
				if(((1 << 3) & value[i]) == (1 << 3)) {
					sysSigMap.put(distSigStartId + i * 8 + 4, sysSigTab.getSysSigInfoLst().get(i * 8 + 4).getValDef());
				}
				if(((1 << 4) & value[i]) == (1 << 4)) {
					sysSigMap.put(distSigStartId + i * 8 + 3, sysSigTab.getSysSigInfoLst().get(i * 8 + 3).getValDef());
				}
				if(((1 << 5) & value[i]) == (1 << 5)) {
					sysSigMap.put(distSigStartId + i * 8 + 2, sysSigTab.getSysSigInfoLst().get(i * 8 + 2).getValDef());
				}
				if(((1 << 6) & value[i]) == (1 << 6)) {
					sysSigMap.put(distSigStartId + i * 8 + 1, sysSigTab.getSysSigInfoLst().get(i * 8 + 1).getValDef());
				}
				if(((1 << 7) & value[i]) == (1 << 7)) {
					sysSigMap.put(distSigStartId + i * 8 + 0, sysSigTab.getSysSigInfoLst().get(i * 8 + 0).getValDef());
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

	public Map<Integer, Pair<Byte, Object>> getCustomSignalValueMap() {
		return customSignalValueMap;
	}

	public void setCustomSignalValueMap(Map<Integer, Pair<Byte, Object>> customSignalValueMap) {
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
	
	public boolean putRelayList(int seqId, int delayMs, TimerTask timeTask) {
		boolean ret = false;
		relayMaplock.lock();
        try {
        	if(!seqId2TimerRelayMap.containsKey(seqId)) {
        		ret = true;
        		Timer timer = new Timer();
        		timer.schedule(timeTask, delayMs);
        		seqId2TimerRelayMap.put(seqId, timer);
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
	
	
	
}
