/**
 * 
 */
package bc_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	int ClientId = 0;
	byte[] UserName = null;
	byte[] Password = null;
	boolean IsUserLogin = false;
	boolean IsDevLogin = false;
	int SeqIdDevClnt = 0;
	int SeqIdUsrClnt = 0;
	String DevName;
	Map<Integer, Byte[]> MapDist2SysSigMap = null;
	Map<Integer, BPValue> SysSigMap;
	BPError Error;
	
	public BPSession(byte[] usr_name, byte[] password, int client_id, boolean usr_login, boolean dev_login) {
		IsDevLogin = dev_login;
		IsUserLogin = usr_login;
		ClientId = client_id;
		
		UserName = new byte[usr_name.length];
		for(int i = 0; i < usr_name.length; i++) {
			UserName[i] = usr_name[i];
		}
		
		Password = new byte[password.length];
		for(int i = 0; i < password.length; i++) {
			Password[i] = password[i];
		}
		SeqIdDevClnt = 0;
		SeqIdUsrClnt = 0;
		SysSigMap = new HashMap<Integer, BPValue>();
		
		Error = Error = new BPError();;
	}
	
	public int getClntId() {
		return ClientId;
	}
	
	public Map getSysSigMap() {
		return SysSigMap;
	}
	
	public String getDevName() {
		return DevName;
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
					tmp = SysSigMap.get(key);
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
					tmp = SysSigMap.get(key);
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
					tmp = SysSigMap.get(key);
					if (null == tmp) {
						throw new SigIdNonExistException();
					}
					tmp.setVal(value);
				}
			}
		} catch (SigIdNonExistException e) {
			e.printStackTrace();
			
			Error.setErrId(BPPacket_RPRTACK.RET_CODE_SIG_ID_INVALID);
			Error.SigIdLst = new ArrayList<Integer>();
			Error.SigIdLst.add(key);
			ret = false;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	public boolean setSysSig(Integer sig_id, BPValue val) {
		boolean ret = false;
		if(SysSigMap.containsKey(sig_id)) {
			SysSigMap.put(sig_id, val);
			ret = true;
		}
		return ret;
	}
	
	public BPValue getSysSigVal(Integer sig_id) {
		BPValue val = null;
		if(SysSigMap.containsKey(sig_id)) {
			BPValue tmp = SysSigMap.get(sig_id);
			val = new BPValue(tmp.getType());
			val.setValStr(tmp.getValStr());
		}
		return val;
	}
	
	public void setDevName(String dev_name) {
		DevName = dev_name;
	}
	
	public void dumpSysSig() {
		Iterator entries = SysSigMap.entrySet().iterator(); 
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
		SysSigMap.clear();
		Iterator entries = MapDist2SysSigMap.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			Integer key = (Integer)entry.getKey();
			Byte[] value = (Byte[])entry.getValue();
			int dist_sig_start_id = BPPacket.SYS_SIG_START_ID + key * BPPacket.SYS_SIG_DIST_STEP;
			BPSysSigTable sys_sig_tab = BPSysSigTable.getSysSigTableInstance();
			for(int i = 0; i < value.length; i++) {
				if(((1 << 0) & value[i]) == (1 << 0)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 7, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 7).ValDef);
				}
				if(((1 << 1) & value[i]) == (1 << 1)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 6, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 6).ValDef);
				}
				if(((1 << 2) & value[i]) == (1 << 2)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 5, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 5).ValDef);
				}
				if(((1 << 3) & value[i]) == (1 << 3)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 4, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 4).ValDef);
				}
				if(((1 << 4) & value[i]) == (1 << 4)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 3, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 3).ValDef);
				}
				if(((1 << 5) & value[i]) == (1 << 5)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 2, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 2).ValDef);
				}
				if(((1 << 6) & value[i]) == (1 << 6)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 1, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 1).ValDef);
				}
				if(((1 << 7) & value[i]) == (1 << 7)) {
					SysSigMap.put(dist_sig_start_id + i * 8 + 0, sys_sig_tab.getSysSigInfoLst().get(i * 8 + 0).ValDef);
				}
			}
		}
	}
	
}
