/**
 * 
 */
package bp_packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	public int clientId = 0;
	long uniqDevId = 0;
	public byte[] userName = null;
	byte[] password = null;
	boolean isUserLogin = false;
	boolean isDevLogin = false;
	public int seqIdDevClnt = 0;
	int seqIdUsrClnt = 0;
	String DevName;
	Map<Integer, Byte[]> MapDist2SysSigMap = null;
	Map<Integer, BPValue> sysSigMap;
	public BPError Error;
	
	public BPSession(byte[] userName, byte[] password, int clientId, boolean userLogin, boolean devLogin, long uniqDevId) {
		isDevLogin = devLogin;
		isUserLogin = userLogin;
		clientId = clientId;
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
		
		Error = new BPError();;
	}
	
	public long getUniqDevId() {
		return uniqDevId;
	}
	
	public int getClntId() {
		return clientId;
	}
	
	public Map getSysSigMap() {
		return sysSigMap;
	}
	
	public String getDevName() {
		return DevName;
	}
	
	public void setUniqDevId(long uniqDevId) {
		this.uniqDevId = uniqDevId;
	}
	
	public boolean setSysSig(DevSigData dev_sig_data) {

		Error = new BPError();
		Map sig_val;
		Iterator entries;
		boolean ret = true;
		Integer key = null;
		Short value = null;
		BPValue tmp = null;

		sig_val = dev_sig_data.get2ByteDataMap();
		try {

			if (null != sig_val) {
				entries = sig_val.entrySet().iterator();
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

			sig_val = dev_sig_data.get4ByteDataMap();
			if (null != sig_val) {
				entries = sig_val.entrySet().iterator();
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

			sig_val = dev_sig_data.getxByteDataMap();
			if (null != sig_val) {
				entries = sig_val.entrySet().iterator();
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
			e.printStackTrace();
			
			Error.setErrId(BPPacketRPRTACK.RET_CODE_SIG_ID_INVALID);
			Error.setSigIdLst(new ArrayList<Integer>());
			Error.getSigIdLst().add(key);
			ret = false;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	public boolean setSysSig(Integer sig_id, BPValue val) {
		boolean ret = false;
		if(sysSigMap.containsKey(sig_id)) {
			sysSigMap.put(sig_id, val);
			ret = true;
		}
		return ret;
	}
	
	public BPValue getSysSigVal(Integer sig_id) {
		BPValue val = null;
		if(sysSigMap.containsKey(sig_id)) {
			BPValue tmp = sysSigMap.get(sig_id);
			val = new BPValue(tmp.getType());
			val.setValStr(tmp.getValStr());
		}
		return val;
	}
	
	public void setDevName(String dev_name) {
		DevName = dev_name;
	}
	
	public void dumpSysSig() {
		Iterator entries = sysSigMap.entrySet().iterator(); 
		while (entries.hasNext()) {    
		    Map.Entry entry = (Map.Entry) entries.next();
		    Integer key = (Integer)entry.getKey();  
		    BPValue value = (BPValue)entry.getValue();  
		    System.out.println(key + "=>" + value.getValStr()); 
		}  
	}
	public void setSysSigMap(Map<Integer, Byte[]> sys_sig_map) {
		if(null == MapDist2SysSigMap) {
			MapDist2SysSigMap = sys_sig_map;
		} else {
			Iterator entries = sys_sig_map.entrySet().iterator(); 
			while (entries.hasNext()) {    
			    Map.Entry entry = (Map.Entry) entries.next();
			    Integer key = (Integer)entry.getKey();  
			    Byte[] value = (Byte[])entry.getValue();  
			    MapDist2SysSigMap.put(key, value);  
			  
			}  
		}
	}
	
	public void initSysSigValDefault() {
		sysSigMap.clear();
		Iterator entries = MapDist2SysSigMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			Integer key = (Integer)entry.getKey();
			Byte[] value = (Byte[])entry.getValue();
			int dist_sig_start_id = BPPacket.SYS_SIG_START_ID + key * BPPacket.SYS_SIG_DIST_STEP;
			BPSysSigTable sys_sig_tab = BPSysSigTable.getSysSigTableInstance();
			for(int i = 0; i < value.length; i++) {
				if(((1 << 0) & value[i]) == (1 << 0)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 7, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 7).ValDef);
				}
				if(((1 << 1) & value[i]) == (1 << 1)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 6, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 6).ValDef);
				}
				if(((1 << 2) & value[i]) == (1 << 2)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 5, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 5).ValDef);
				}
				if(((1 << 3) & value[i]) == (1 << 3)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 4, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 4).ValDef);
				}
				if(((1 << 4) & value[i]) == (1 << 4)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 3, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 3).ValDef);
				}
				if(((1 << 5) & value[i]) == (1 << 5)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 2, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 2).ValDef);
				}
				if(((1 << 6) & value[i]) == (1 << 6)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 1, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 1).ValDef);
				}
				if(((1 << 7) & value[i]) == (1 << 7)) {
					sysSigMap.put(dist_sig_start_id + i * 8 + 0, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 0).ValDef);
				}
			}
		}
	}
	
}
