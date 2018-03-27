/**
 * 
 */
package db;

import java.sql.Connection;

/**
 * @author Ansersion
 *
 */
public class DB_UserInfoRec extends DB_BaseRec {
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
	
	public DB_UserInfoRec(long id, String name, String e_mail, String phone_number, String password) {
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
		setDirty();
	}
	
	public void setName(String name) {
		Name = new String(name);
		setDirty();
	}
	
	public void setEMail(String e_mail) {
		E_Mail = new String(e_mail);
		setDirty();
	}
	
	public void setPhoneNumber(String phone_number) {
		PhoneNumber = new String(phone_number);
		setDirty();
	}
	
	public void setPassword(String password) {
		Password = new String(password);
		setDirty();
	}
	

	public boolean updateRec(Connection con) {
		// TODO: overload this function
		return false;
	}
}
