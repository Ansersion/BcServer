/**
 * 
 */
package bc_server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Ansersion
 *
 */
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
	}
	
	public String getDevName() {
		return DevName;
	}
	
	public void setDevName(String dev_name) {
		DevName = dev_name;
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
	
}
