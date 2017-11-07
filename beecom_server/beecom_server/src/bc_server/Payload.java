/**
 * 
 */
package bc_server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Ansersion
 *
 */
public class Payload {

	byte[] UserName = null;
	byte[] Password = null;
	int ClntIdLen;
	int ClntId;
	int SymSetVer;
	
	Map<Integer, List<Integer> > MapDevId2SigIdLst = new HashMap<Integer, List<Integer> >();
	
	public void setUserName(byte[] user_name) {
		UserName = user_name;
	}
	
	public void setPassword(byte[] password) {
		Password = password;
	}
	
	public void getUserName(byte[] user_name) {
		if(user_name.length < UserName.length) {
			return;
		}
		for(int i = 0; i < UserName.length; i++) {
			user_name[i] = UserName[i];
		}
	}
	
	public byte[] getUserName() {
		byte[] user_name = new byte[UserName.length];
		for(int i = 0; i < UserName.length; i++) {
			user_name[i] = UserName[i];
		}
		return user_name;
	}
	
	public void getPassword(byte[] password) {
		if(password.length < Password.length) {
			return;
		}
		for(int i = 0; i < Password.length; i++) {
			password[i] = Password[i];
		}
	}
	public byte[] getPassword() {
		byte[] password = new byte[Password.length];
		for(int i = 0; i < Password.length; i++) {
			password[i] = Password[i];
		}
		return password;
	}
	
	public void reset() {
		UserName = null;
		Password = null;
	}
	
	public int getClntIdLen() {
		return ClntIdLen;
	}
	
	public int getClntId() {
		return ClntId;
	}
	
	public int getSymSetVer() {
		return SymSetVer;
	}
	
	public void setClientId(int id) {
		ClntId = id;
	}
	
	public void setClientIdLen(int len) {
		ClntIdLen = len;
	}
	
	public void setClientIdLen() {
		ClntIdLen = 2;
	}
	
	public void clrMapDevId2SigIdList() {
		MapDevId2SigIdLst.clear();
	}
	
	public Map<Integer, List<Integer> > getMapDev2SigLst() {
		return MapDevId2SigIdLst;
	}
}
