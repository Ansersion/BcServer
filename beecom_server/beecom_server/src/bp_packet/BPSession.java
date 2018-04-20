/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.BPError;
import other.BPValue;
import sys_sig_table.BPSysSigTable;

/**
 * @author Ansersion
 *
 */

class SigIdNonExistException extends Exception {
	SigIdNonExistException(String msg) {
		super(msg);
	}
	SigIdNonExistException() {
		super();
	}
}
public class BPSession {
	
	private static final Logger logger = LoggerFactory.getLogger(BPSession.class);
	
	private int clientId = 0;
	long uniqDevId = 0;
	private byte[] userName = null;
	byte[] password = null;
	boolean isUserLogin = false;
	boolean isDevLogin = false;
	private int seqIdDevClnt = 0;
	int seqIdUsrClnt = 0;
	String devName;
	Map<Integer, Byte[]> mapDist2SysSigMap = null;
	Map<Integer, BPValue> sysSigMap;
	private BPError error;
	
	
	
	public byte[] getUserName() {
		return userName;
	}

	public void setUserName(byte[] userName) {
		this.userName = userName;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getSeqIdDevClnt() {
		return seqIdDevClnt;
	}

	public void setSeqIdDevClnt(int seqIdDevClnt) {
		this.seqIdDevClnt = seqIdDevClnt;
	}

	public BPError getError() {
		return error;
	}

	public void setError(BPError error) {
		this.error = error;
	}

	public BPSession(byte[] userName, byte[] password, int clientId, boolean userLogin, boolean devLogin, long uniqDevId) {
		isDevLogin = devLogin;
		isUserLogin = userLogin;
		this.clientId = clientId;
		this.uniqDevId = uniqDevId;
		
		this.userName = new byte[userName.length];
		for(int i = 0; i < userName.length; i++) {
			this.userName[i] = userName[i];
		}
		
		this.password = new byte[password.length];
		for(int i = 0; i < password.length; i++) {
			this.password[i] = password[i];
		}
		seqIdDevClnt = 0;
		seqIdUsrClnt = 0;
		sysSigMap = new HashMap<>();
		
		error = new BPError();
	}
	
	public long getUniqDevId() {
		return uniqDevId;
	}
	
	public Map getSysSigMap() {
		return sysSigMap;
	}
	
	public String getDevName() {
		return devName;
	}
	
	public void setUniqDevId(long uniqDevId) {
		this.uniqDevId = uniqDevId;
	}
	
	public boolean setSysSig(DevSigData devSigData) {

		error = new BPError();
		Map sigVal;
		Iterator entries;
		boolean ret = true;
		Integer key = null;
		Short value = null;
		BPValue tmp = null;

		sigVal = devSigData.get2ByteDataMap();
		try {

			if (null != sigVal) {
				entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					key = (Integer) entry.getKey();
					value = (Short) entry.getValue();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					tmp.setVal(value);
				}
			}

			sigVal = devSigData.get4ByteDataMap();
			if (null != sigVal) {
				entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					key = (Integer) entry.getKey();
					value = (Short) entry.getValue();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					tmp.setVal(value);
				}
			}

			sigVal = devSigData.getxByteDataMap();
			if (null != sigVal) {
				entries = sigVal.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					key = (Integer) entry.getKey();
					value = (Short) entry.getValue();
					tmp = sysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					tmp.setVal(value);
				}
			}
		} catch (SigIdNonExistException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			
			error.setErrId(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
			error.setSigIdLst(new ArrayList<Integer>());
			error.getSigIdLst().add(key);
			ret = false;
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
	
	public void setDevName(String devName) {
		this.devName = devName;
	}
	
	public void dumpSysSig() {
		Iterator entries = sysSigMap.entrySet().iterator(); 
		while (entries.hasNext()) {    
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer key = (Integer)entry.getKey();  
		    BPValue value = (BPValue)entry.getValue();  
		    logger.info("{}=>{}", key, value.getValStr()); 
		}  
	}
	public void setSysSigMap(Map<Integer, Byte[]> sysSigMap) {
		if(null == mapDist2SysSigMap) {
			mapDist2SysSigMap = sysSigMap;
		} else {
			Iterator entries = sysSigMap.entrySet().iterator(); 
			while (entries.hasNext()) {    
			    Map.Entry entry = (Map.Entry) entries.next();
			    Integer key = (Integer)entry.getKey();  
			    Byte[] value = (Byte[])entry.getValue();  
			    mapDist2SysSigMap.put(key, value);  
			  
			}  
		}
	}
	
	public void initSysSigValDefault() {
		sysSigMap.clear();
		Iterator entries = mapDist2SysSigMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			Integer key = (Integer)entry.getKey();
			Byte[] value = (Byte[])entry.getValue();
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
