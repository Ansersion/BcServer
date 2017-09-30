/**
 * 
 */
package bc_server;

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
	}
	
}