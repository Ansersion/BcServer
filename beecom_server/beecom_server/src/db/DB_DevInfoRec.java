/**
 * 
 */
package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Ansersion
 * 
 */
public class DB_DevInfoRec extends DB_BaseRec {
	long DevUniqId;
	int UserId;
	byte[] DevPwd;
	int DevId;
	long SysSigTabId;
	String DevName;

	public DB_DevInfoRec() {
		DevUniqId = 0;
		UserId = 0;
		DevPwd = new byte[32];
		DevId = 0;
		SysSigTabId = 0;
		DevName = new String("");
	}

	public DB_DevInfoRec(long dev_uniq_id, int user_id, byte[] dev_pwd,
			int dev_id, long sys_sig_tab_id, String dev_name) {
		DevUniqId = dev_uniq_id;
		UserId = user_id;
		DevPwd = dev_pwd;
		DevId = dev_id;
		SysSigTabId = sys_sig_tab_id;
		DevName = dev_name;
	}

	public long getDevUniqId() {
		return DevUniqId;
	}

	public int getUserId() {
		return UserId;
	}

	public byte[] getDevPwd() {
		return DevPwd;
	}

	public int getDevId() {
		return DevId;
	}

	public long getSysSigTabId() {
		return SysSigTabId;
	}

	public String getDevName() {
		return DevName;
	}

	public void setDevUniqId(long dev_uniq_id) {
		DevUniqId = dev_uniq_id;
		setDirty();
	}

	public void setUserId(int user_id) {
		UserId = user_id;
		setDirty();
	}

	public void setDevPwd(byte[] dev_pwd) {
		DevPwd = dev_pwd;
		setDirty();
	}

	public void setDevId(int dev_id) {
		DevId = dev_id;
		setDirty();
	}

	public void setSysSigTabId(long sys_sig_tab_id) {
		SysSigTabId = sys_sig_tab_id;
		setDirty();
	}

	public void setDevName(String dev_name) {
		DevName = dev_name;
		setDirty();
	}

	public void dumpRec() {
		System.out.println("DevUniqId: " + DevUniqId + ", UserId: " + UserId
				+ ", DevPwd: " + new String(DevPwd) + ", DevId: " + DevId
				+ ", SysSigTabId: " + SysSigTabId + ", DevName: " + DevName);
	}

	public boolean updateRec(Connection con) {
		String sql = new String("");
		sql += "update dev_info set";
		// dev_uniq_id | user_id | dev_password | dev_id | dev_name |
		// sys_sig_tab_id |
		sql += " dev_uniq_id=" + DevUniqId;
		sql += ",user_id=" + UserId;
		sql += ",dev_password=" + "\"" + new String(DevPwd) + "\"";
		sql += ",dev_id=" + DevId;
		sql += ",dev_name=" + "\"" + DevName + "\"";
		sql += ",sys_sig_tab_id=" + SysSigTabId;
		sql += " where dev_uniq_id=" + DevUniqId;
		try {
			Statement st = con.createStatement();
			st.executeUpdate(sql);
			st.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
