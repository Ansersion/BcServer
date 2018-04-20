/**
 * 
 */
package db;

import java.sql.Connection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;

import java.util.List;

/**
 * @author Ansersion
 * 
 */
public class BeecomDB {
	
	private static final Logger logger = LoggerFactory.getLogger(BeecomDB.class);
	

	static BeecomDB bcDb = null;

	List<DBUserInfoRec> userInfoRecLst;
	List<DBDevInfoRec> devInfoRecLst;
	List<DBDevAuthRec> devAuthRecLst;
	List<DBSysSigRec> sysSigRecLst;
	Map<String, Long> name2IDMap;

	Connection con;
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/bc_server_db?useSSL=false";
	static String user = "root";
	static String password = "Ansersion";

	private BeecomDB() {
		logger.info("Info: Create BeecomDB");
		userInfoRecLst = new ArrayList<>();
		devInfoRecLst = new ArrayList<>();
		devAuthRecLst = new ArrayList<>();
		sysSigRecLst = new ArrayList<>();

		name2IDMap = new HashMap<>();

		DBUserInfoRec record0Blank = new DBUserInfoRec();
		// DB_UserDev
		userInfoRecLst.add(record0Blank);
		ResultSet rs = null;
		try (Statement statement = con.createStatement()) {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed()) {
				logger.info("Succeeded connecting to the Database!");
			}
			

			try {
				String sql = "select * from user_info";
				rs = statement.executeQuery(sql);

				long id;
				String name;
				String eMail;
				String phone;
				String passwordTmp;

				while (rs.next()) {
					id = rs.getInt("ID");
					name = rs.getString("name");
					eMail = rs.getString("eMail");
					phone = rs.getString("phone");
					passwordTmp = rs.getString("password");
					userInfoRecLst.add(new DBUserInfoRec(id, name, eMail,
							phone, passwordTmp));
					name2IDMap.put(name, id);
				}
				rs.close();
			} finally {
				if(null != rs) {
					rs.close();
				}
				rs = null;
			}

			try {
				String sql = "select * from dev_info";
				rs = statement.executeQuery(sql);

				long devUniqId;
				int userId;
				byte[] devPwd = new byte[32];
				int devId;
				long sysSigTabId;
				String devName;

				while (rs.next()) {
					devUniqId = rs.getLong("devUniqId");
					userId = rs.getInt("userId");
					devPwd = rs.getBytes("dev_password");
					devId = rs.getInt("devId");
					sysSigTabId = rs.getInt("sysSigTabId");
					devName = rs.getString("devName");
					if (devUniqId > Integer.MAX_VALUE) {
						throw new Exception("Error: devUniqId too big");
					}
					devInfoRecLst.add(new DBDevInfoRec(devUniqId, userId,
							devPwd, devId, sysSigTabId, devName));
				}
				rs.close();

			} finally {
				if(null != rs) {
					rs.close();
				}
				rs = null;
			}

			try {
				String sql = "select * from dev_auth";
				rs = statement.executeQuery(sql);

				long devUniqId;
				int adminUserId;
				byte adminUserAuth;
				int userId1;
				byte userAuth1;
				int userId2;
				byte userAuth2;
				int userId3;
				byte userAuth3;
				int userId4;
				byte userAuth4;

				while (rs.next()) {
					devUniqId = rs.getLong("devUniqId");
					adminUserId = rs.getInt("admin_user");
					adminUserAuth = (byte) rs.getShort("admin_auth");
					userId1 = rs.getInt("user_id1");
					userAuth1 = (byte) rs.getShort("user_id1_auth");
					userId2 = rs.getInt("user_id2");
					userAuth2 = (byte) rs.getShort("user_id2_auth");
					userId3 = rs.getInt("user_id3");
					userAuth3 = (byte) rs.getShort("user_id3_auth");
					userId4 = rs.getInt("user_id4");
					userAuth4 = (byte) rs.getShort("user_id4_auth");
					if (devUniqId > Integer.MAX_VALUE) {
						throw new Exception("Error: devUniqId too big");
					}
					devAuthRecLst.add(new DBDevAuthRec(devUniqId,
							adminUserId, adminUserAuth, userId1,
							userAuth1, userId2, userAuth2, userId3,
							userAuth3, userId4, userAuth4));
				}
				rs.close();

			} finally {
				if(null != rs) {
					rs.close();
				}
				rs = null;
			}

			try {
				String sql = "select * from sysSigTab";
				rs = statement.executeQuery(sql);

				int sysSigTabId;

				while (rs.next()) {
					sysSigTabId = rs.getInt("sysSigTabId");
					List<Byte[]> sysSigEnableLst = new ArrayList<>();
					for(int i = 0; i < BPPacket.MAX_SYS_SIG_DIST_NUM; i++) {
						byte[] tmpB1 = rs.getBytes(i+2);
						if(null != tmpB1) {
							Byte[] tmpB2 = new Byte[tmpB1.length];
							for(int j = 0; j < tmpB2.length; j++) {
								tmpB2[j] = tmpB1[j];
							}
							sysSigEnableLst.add(tmpB2);
						} else {
							sysSigEnableLst.add(null);
						}
					}					
					sysSigRecLst.add(new DBSysSigRec(sysSigTabId, sysSigEnableLst));
				}
				rs.close();

				for (int i = 0; i < sysSigRecLst.size(); i++) {
					sysSigRecLst.get(i).dumpRec();
				}
			} finally {
				if(null != rs) {
					rs.close();
				}
				rs = null;
			}

		} catch (SQLException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		} catch (ClassNotFoundException e) {
			logger.error("Sorry,can`t find the Driver!");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		} 
	}

	public Map<String, Long> getName2IDMap() {
		return name2IDMap;
	}

	public List<DBUserInfoRec> getUserInfoRecLst() {
		return userInfoRecLst;
	}

	public List<DBDevInfoRec> getDevInfoRecLst() {
		return devInfoRecLst;
	}
	
	public List<DBSysSigRec> getSysSigRecLst() {
		return sysSigRecLst;
	}

	public DBDevInfoRec getDevInfoRec(int devUniqId) {
		if (devUniqId <= 0 || devUniqId > devInfoRecLst.size()) {
			return null;
		}
		return devInfoRecLst.get(devUniqId - 1);
	}

	public boolean setDevInfoRec(long devUniqId, DBDevInfoRec devInfoRec) {
		if (devUniqId <= 0 || devUniqId > devInfoRecLst.size()) {
			return false;
		}
		devInfoRecLst.set((int) devUniqId - 1, devInfoRec);
		return true;
	}

	public static BeecomDB getInstance() {
		if (null == bcDb) {
			bcDb = new BeecomDB();
		}
		return bcDb;
	}

	public static boolean chkUserName(String name) {
		BeecomDB db = getInstance();
		return db.getName2IDMap().containsKey(name);
	}

	public static boolean chkUserPwd(String name, byte[] password) {
		BeecomDB db = getInstance();
		if (!db.getName2IDMap().containsKey(name)) {
			return false;
		}
		long id = db.getName2IDMap().get(name);

		// Maybe truncate ID
		DBUserInfoRec userDbRecord = db.getUserInfoRecLst().get((int) id);
		String strTmp = new String(password);
		logger.info("PWD mysql: {}", userDbRecord.getPassword());
		return strTmp.equals(userDbRecord.getPassword());
	}

	public static boolean chkDevUniqId(long devUniqId) {
		BeecomDB db = getInstance();
		return devUniqId <= db.getDevInfoRecLst().size() + 1 && devUniqId > 0;
	}

	public static boolean chkDevPwd(long devUniqId, byte[] password) {
		BeecomDB db = getInstance();
		if (devUniqId > db.getDevInfoRecLst().size() + 1 || devUniqId <= 0) {
			return false;
		}
		// Maybe truncate ID

		DBDevInfoRec rec = db.getDevInfoRecLst().get((int) (devUniqId - 1));
		String strTmp = new String(password);
		logger.info("Dev PWD mysql: {}", new String(rec.getDevPwd()));
		return strTmp.equals(new String(rec.getDevPwd()));
	}

	public static boolean updateDevInfoRec(DBDevInfoRec devInfoRec) {
		BeecomDB db = getInstance();
		long devUniqId = devInfoRec.getDevUniqId();
		if (devUniqId <= 0 || devUniqId > db.devInfoRecLst.size()) {
			return false;
		}
		db.setDevInfoRec(devUniqId, devInfoRec);
		return true;
	}
	
	public Connection getConn() {
		return con;
	}
	
	public boolean updateDevInfoRecLst() {
		for(int i = 0; i < devInfoRecLst.size(); i++) {
		}
		return true;
	}

	public void dumpDevInfo() {
		for(int i = 0; i < devInfoRecLst.size(); i++) {
			devInfoRecLst.get(i).dumpRec();
		}
	}
}
