/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 * 
 */
public class DB_UserInfoRec {
	long ID;
	String Name;
	String E_Mail;
	String PhoneNumber;
	String Password;

	public DB_UserInfoRec() {
		ID = 0;
		Name = new String("");
		E_Mail = new String("");
		PhoneNumber = new String("");
		Password = new String("");
	}

	public DB_UserInfoRec(long id, String name, String e_mail,
			String phone_number, String password) {
		ID = id;
		Name = name;
		E_Mail = e_mail;
		PhoneNumber = phone_number;
		Password = password;
	}

	public long getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public String getEMail() {
		return E_Mail;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public String getPassword() {
		return new String(Password);
	}

	public void setID(long id) {
		ID = id;
	}

	public void setName(String name) {
		Name = new String(name);
	}

	public void setEMail(String e_mail) {
		E_Mail = new String(e_mail);
	}

	public void setPhoneNumber(String phone_number) {
		PhoneNumber = new String(phone_number);
	}

	public void setPassword(String password) {
		Password = new String(password);
	}
}
