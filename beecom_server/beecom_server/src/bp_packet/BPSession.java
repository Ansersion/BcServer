/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	// private int clientId = 0;
	// long uniqDevId = 0;
	// private byte[] userName = null;
	// byte[] password = null;
	// boolean isUserLogin = false;
	// boolean isDevLogin = false;
	// private int seqIdDevClnt = 0;
	int seqIdUsrClnt = 0;
	// String devName;
	Map<Integer, Byte[]> mapDist2SysSigMap = null;
	Map<Integer, BPValue> sysSigMap;
	// private BPError error;
	
	

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
		return null;
	}

	public void setUniqDeviceId(Long uniqDeviceId) {
	}

	public String getSn() {
		return null;
	}

	public void setSn(String sn) {
	}
	
	public BPSession() {
		
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
	
	public Map<Integer, BPValue> getSysSigMap() {
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
		BPValue tmp = null;
		
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
					tmp.setVal(value);
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
					tmp.setVal(entry.getValue());
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
			BPValue tmp = sysSigMap.get(sigId);
			val = new BPValue(tmp.getType());
			val.setValStr(tmp.getValStr());
		}
		return val;
	}
	
	// public void setDevName(String devName) {
	//	this.devName = devName;
	// }
	
	public void dumpSysSig() {
		Iterator<Map.Entry<Integer, BPValue>> entries = sysSigMap.entrySet().iterator(); 
		while (entries.hasNext()) {    
		    Map.Entry<Integer, BPValue> entry = entries.next();
		    Integer key = entry.getKey();  
		    BPValue value = entry.getValue();  
		    logger.info("{}=>{}", key, value.getValStr()); 
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
	
}
