/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class DB_DevInfoRec {
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
	
	public DB_DevInfoRec(long dev_uniq_id, int user_id, byte[] dev_pwd, int dev_id, long sys_sig_tab_id, String dev_name) {
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
	}
	
	public void setUserId(int user_id) {
		UserId = user_id;
	}

	public void setDevPwd(byte[] dev_pwd) {
		DevPwd = dev_pwd;
	}
	
	public void setDevId(int dev_id) {
		DevId = dev_id;
	}
	
	public void setSysSigTabId(long sys_sig_tab_id) {
		SysSigTabId = sys_sig_tab_id;
	}
	
	public void setDevName(String dev_name) {
		DevName = dev_name;
	}
	
	public void dumpRec() {
		System.out.println(
				"UserId: " + UserId +
				", DevUniqId: " + DevUniqId + 
				", DevPwd: " + new String(DevPwd) + 
				", DevId: " + DevId +
				", SysSigTabId: " + SysSigTabId +
				", DevName: " + DevName);
	}
}
