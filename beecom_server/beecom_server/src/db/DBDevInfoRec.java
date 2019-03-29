/**
 * 
 */
package db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;


/**
 * @author Ansersion
 * 
 */
public class DBDevInfoRec extends DBBaseRec {
	private static final Logger logger = LoggerFactory.getLogger(DBDevInfoRec.class);
	
	long devUniqId;
	int userId;
	byte[] devPwd;
	int devId;
	long sysSigTabId;
	String devName;

	public DBDevInfoRec() {
		devUniqId = 0;
		userId = 0;
		devPwd = new byte[32];
		devId = 0;
		sysSigTabId = 0;
		devName = "";
	}

	public DBDevInfoRec(long devUniqId, int userId, byte[] devPwd,
			int devId, long sysSigTabId, String devName) {
		this.devUniqId = devUniqId;
		this.userId = userId;
		this.devPwd = devPwd;
		this.devId = devId;
		this.sysSigTabId = sysSigTabId;
		this.devName = devName;
	}

	public long getDevUniqId() {
		return devUniqId;
	}

	public int getUserId() {
		return userId;
	}

	public byte[] getDevPwd() {
		return devPwd;
	}

	public int getDevId() {
		return devId;
	}

	public long getSysSigTabId() {
		return sysSigTabId;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevUniqId(long devUniqId) {
		this.devUniqId = devUniqId;
		setDirty();
	}

	public void setUserId(int userId) {
		this.userId = userId;
		setDirty();
	}

	public void setDevPwd(byte[] devPwd) {
		this.devPwd = devPwd;
		setDirty();
	}

	public void setDevId(int devId) {
		this.devId = devId;
		setDirty();
	}

	public void setSysSigTabId(long sysSigTabId) {
		this.sysSigTabId = sysSigTabId;
		setDirty();
	}

	public void setDevName(String devName) {
		this.devName = devName;
		setDirty();
	}

	public void dumpRec() {
		logger.debug("DevUniqId: {}, UserId: {}, DevPwd: {}, DevId: {}, SysSigTabId: {}, DevName: {}", devUniqId, userId, new String(devPwd), devId, sysSigTabId, devName);
	}

	@Override
	public boolean updateRec(Connection con) {
		String sql = "";
		sql += "update dev_info set";
		// devUniqId | userId | dev_password | devId | devName |
		// sysSigTabId |
		sql += " devUniqId=" + devUniqId;
		sql += ",userId=" + userId;
		sql += ",dev_password=" + "\"" + new String(devPwd) + "\"";
		sql += ",devId=" + devId;
		sql += ",devName=" + "\"" + devName + "\"";
		sql += ",sysSigTabId=" + sysSigTabId;
		sql += " where devUniqId=" + devUniqId;
		
		try (Statement st = con.createStatement()) {
			st.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		} 

		return false;
	}
}
