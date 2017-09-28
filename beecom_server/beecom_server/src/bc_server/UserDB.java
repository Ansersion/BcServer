/**
 * 
 */
package bc_server;

import java.io.UnsupportedEncodingException;


/**
 * @author Ansersion
 *
 */
public class UserDB {
	
	static UserDB UDB = null;
	private UserDB() {
		System.out.println("Info: Create UserDB");
	}
	
	static UserDB getInstance() {
		if(null == UDB) {
			UDB = new UserDB();
		}
		return UDB;
	}
	
	static public boolean ChkUserName(String name) throws UnsupportedEncodingException {
		String test_name = new String("Ansersion");
		return test_name.equals(name);
	}
	
	static public boolean ChkUserPwd(String name, byte[] password) {
		byte[] pwd = (new String("AnsersionPWD")).getBytes();
		for(int i = 0; i < pwd.length; i++) {
			if(pwd[i] != password[i]) {
				return false;
			}
		}
		return true;
	}
}
