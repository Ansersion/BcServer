/**
 * 
 */
package bc_server;

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
}
