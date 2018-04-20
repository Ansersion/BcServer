/**
 * 
 */
package db;

import java.sql.Connection;

/**
 * @author Ansersion
 *
 */
public class DBUserInfoRec extends DBBaseRec {
	long id;
	String name;
	String eMail;
	String phoneNumber;
	String password;
	
	public DBUserInfoRec() {
		id = 0;
		name = "";
		eMail = "";
		phoneNumber = "";
		password = "";
	}
	
	public DBUserInfoRec(long id, String name, String eMail, String phoneNumber, String password) {
		this.id = id;
		this.name = name;
		this.eMail = eMail;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}
	
	public long getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEMail() {
		return eMail;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getPassword() {
		return password;
	}	
	
	public void setID(long id) {
		this.id = id;
		setDirty();
	}
	
	public void setName(String name) {
		this.name = name;
		setDirty();
	}
	
	public void setEMail(String eMail) {
		this.eMail = eMail;
		setDirty();
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		setDirty();
	}
	
	public void setPassword(String password) {
		this.password = password;
		setDirty();
	}
	

	@Override
	public boolean updateRec(Connection con) {
		return false;
	}
}
