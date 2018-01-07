/**
 * 
 */
package bc_server;

import java.sql.Connection;

import java.sql.Blob;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

/**
 * @author Ansersion
 * 
 */
public class BeecomDB {

	static BeecomDB BC_DB = null;

	List<DB_UserInfoRec> UserInfoRecLst;
	List<DB_DevInfoRec> DevInfoRecLst;
	List<DB_DevAuthRec> DevAuthRecLst;
	List<DB_SysSigRec> SysSigRecLst;
	Map<String, Long> Name2IDMap;
	// Map<Long, Long> DevUniqId2IndexMap;

	Connection con;
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/bc_server_db?useSSL=false";
	static String user = "root";
	static String password = "Ansersion";

	private BeecomDB() {
		System.out.println("Info: Create BeecomDB");
		// Id2UserRecord = new HashMap<Long, User_DB_Record>();
		UserInfoRecLst = new ArrayList<DB_UserInfoRec>();
		DevInfoRecLst = new ArrayList<DB_DevInfoRec>();
		DevAuthRecLst = new ArrayList<DB_DevAuthRec>();
		SysSigRecLst = new ArrayList<DB_SysSigRec>();

		Name2IDMap = new HashMap<String, Long>();
		// DevUniqId2IndexMap = new HashMap<Long, Long>();

		DB_UserInfoRec record_0_blank = new DB_UserInfoRec();
		// DB_UserDev
		UserInfoRecLst.add(record_0_blank);

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed()) {
				System.out.println("Succeeded connecting to the Database!");
			}
			Statement statement = con.createStatement();

			{
				String sql = "select * from user_info";
				ResultSet rs = statement.executeQuery(sql);

				long id;
				String name;
				String e_mail;
				String phone;
				String password;

				while (rs.next()) {
					id = rs.getInt("ID");
					name = rs.getString("name");
					e_mail = rs.getString("e_mail");
					phone = rs.getString("phone");
					password = rs.getString("password");
					UserInfoRecLst.add(new DB_UserInfoRec(id, name, e_mail,
							phone, password));
					Name2IDMap.put(name, id);
				}
				rs.close();
			}

			{
				String sql = "select * from dev_info";
				ResultSet rs = statement.executeQuery(sql);

				long dev_uniq_id;
				int user_id;
				byte[] dev_pwd = new byte[32];
				int dev_id;
				long sys_sig_tab_id;
				String dev_name;

				while (rs.next()) {
					dev_uniq_id = rs.getLong("dev_uniq_id");
					user_id = rs.getInt("user_id");
					dev_pwd = rs.getBytes("dev_password");
					dev_id = rs.getInt("dev_id");
					sys_sig_tab_id = rs.getInt("sys_sig_tab_id");
					dev_name = rs.getString("dev_name");
					if (dev_uniq_id > Integer.MAX_VALUE) {
						// System.out.println("Error: dev_uniq_id too big");
						throw new Exception("Error: dev_uniq_id too big");
					}
					DevInfoRecLst.add(new DB_DevInfoRec(dev_uniq_id, user_id,
							dev_pwd, dev_id, sys_sig_tab_id, dev_name));
				}
				rs.close();

				/*
				 * for (int i = 0; i < DevInfoRecLst.size(); i++) {
				 * DevInfoRecLst.get(i).dumpRec(); }
				 */
			}

			{
				String sql = "select * from dev_auth";
				ResultSet rs = statement.executeQuery(sql);

				long dev_uniq_id;
				int admin_user_id;
				byte admin_user_auth;
				int user_id_1;
				byte user_auth_1;
				int user_id_2;
				byte user_auth_2;
				int user_id_3;
				byte user_auth_3;
				int user_id_4;
				byte user_auth_4;

				while (rs.next()) {
					dev_uniq_id = rs.getLong("dev_uniq_id");
					admin_user_id = rs.getInt("admin_user");
					admin_user_auth = (byte) rs.getShort("admin_auth");
					user_id_1 = rs.getInt("user_id1");
					user_auth_1 = (byte) rs.getShort("user_id1_auth");
					user_id_2 = rs.getInt("user_id2");
					user_auth_2 = (byte) rs.getShort("user_id2_auth");
					user_id_3 = rs.getInt("user_id3");
					user_auth_3 = (byte) rs.getShort("user_id3_auth");
					user_id_4 = rs.getInt("user_id4");
					user_auth_4 = (byte) rs.getShort("user_id4_auth");
					if (dev_uniq_id > Integer.MAX_VALUE) {
						// System.out.println("Error: dev_uniq_id too big");
						throw new Exception("Error: dev_uniq_id too big");
					}
					DevAuthRecLst.add(new DB_DevAuthRec(dev_uniq_id,
							admin_user_id, admin_user_auth, user_id_1,
							user_auth_1, user_id_2, user_auth_2, user_id_3,
							user_auth_3, user_id_4, user_auth_4));
				}
				rs.close();

				/*
				 * for (int i = 0; i < DevAuthRecLst.size(); i++) {
				 * DevAuthRecLst.get(i).dumpRec(); }
				 */
			}

			{
				String sql = "select * from sys_sig_tab";
				ResultSet rs = statement.executeQuery(sql);

				int sys_sig_tab_id;

				while (rs.next()) {
					sys_sig_tab_id = rs.getInt("sys_sig_tab_id");
					List<Byte[]> sys_sig_enable_lst = new ArrayList<Byte[]>();
					// for(int i = 0; i < DB_SysSigRec.MAX_DIST_NUM; i++) {
					for(int i = 0; i < BPPacket.MAX_SYS_SIG_DIST_NUM; i++) {
						byte[] tmp_b = rs.getBytes(i+2);
						if(null != tmp_b) {
							Byte[] tmp_B = new Byte[tmp_b.length];
							for(int j = 0; j < tmp_B.length; j++) {
								tmp_B[j] = tmp_b[j];
							}
							sys_sig_enable_lst.add(tmp_B);
						} else {
							sys_sig_enable_lst.add(null);
						}
					}					
					SysSigRecLst.add(new DB_SysSigRec(sys_sig_tab_id, sys_sig_enable_lst));
				}
				rs.close();

				for (int i = 0; i < SysSigRecLst.size(); i++) {
					SysSigRecLst.get(i).dumpRec();
				}
			}
			statement.close();
			// con.close();
		} catch (SQLException e) {
			// 数据库连接失败异常处理
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// 数据库驱动类异常处理
			System.out.println("Sorry,can`t find the Driver!");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public Map<String, Long> getName2IDMap() {
		return Name2IDMap;
	}

	public List<DB_UserInfoRec> getUserInfoRecLst() {
		return UserInfoRecLst;
	}

	public List<DB_DevInfoRec> getDevInfoRecLst() {
		return DevInfoRecLst;
	}
	
	public List<DB_SysSigRec> getSysSigRecLst() {
		return SysSigRecLst;
	}

	public DB_DevInfoRec getDevInfoRec(int dev_uniq_id) {
		if (dev_uniq_id <= 0 || dev_uniq_id > DevInfoRecLst.size()) {
			return null;
		}
		return DevInfoRecLst.get(dev_uniq_id - 1);
	}

	public boolean setDevInfoRec(long dev_uniq_id, DB_DevInfoRec dev_info_rec) {
		if (dev_uniq_id <= 0 || dev_uniq_id > DevInfoRecLst.size()) {
			return false;
		}
		DevInfoRecLst.set((int) dev_uniq_id - 1, dev_info_rec);
		return true;
	}

	static BeecomDB getInstance() {
		if (null == BC_DB) {
			BC_DB = new BeecomDB();
		}
		return BC_DB;
	}

	static public boolean ChkUserName(String name) {
		BeecomDB db = getInstance();
		return db.getName2IDMap().containsKey(name);
	}

	static public boolean ChkUserPwd(String name, byte[] password) {
		BeecomDB db = getInstance();
		if (!db.getName2IDMap().containsKey(name)) {
			return false;
		}
		long id = db.getName2IDMap().get(name);

		// Maybe truncate ID
		DB_UserInfoRec user_db_record = db.getUserInfoRecLst().get((int) id);
		String str_p = new String(password);
		System.out.println("PWD mysql: " + user_db_record.getPassword());
		return str_p.equals(user_db_record.getPassword());
	}

	static public boolean ChkDevUniqId(long dev_uniq_id) {
		BeecomDB db = getInstance();
		if (dev_uniq_id > db.getDevInfoRecLst().size() + 1 || dev_uniq_id <= 0) {
			return false;
		} else {
			return true;
		}
	}

	static public boolean ChkDevPwd(long dev_uniq_id, byte[] password) {
		BeecomDB db = getInstance();
		if (dev_uniq_id > db.getDevInfoRecLst().size() + 1 || dev_uniq_id <= 0) {
			return false;
		}
		// Maybe truncate ID

		DB_DevInfoRec rec = db.getDevInfoRecLst().get((int) (dev_uniq_id - 1));
		String str_p = new String(password);
		System.out.println("Dev PWD mysql: " + new String(rec.getDevPwd()));
		return str_p.equals(new String(rec.getDevPwd()));
	}

	static public boolean updateDevInfoRec(DB_DevInfoRec dev_info_rec) {
		BeecomDB db = getInstance();
		long dev_uniq_id = dev_info_rec.getDevUniqId();
		if (dev_uniq_id <= 0 || dev_uniq_id > db.DevInfoRecLst.size()) {
			return false;
		}
		db.setDevInfoRec(dev_uniq_id, dev_info_rec);
		/*
		 * Statement
		 * stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet
		 * .CONCUR_READ_ONLY);
		 * stmt.executeUpdate("update dbo.signal set value=2 where id=1");
		 * //如果后面不跟where条件，则更新所有列的value字段 stmt.close();
		 */
		return true;
	}
	
	public Connection getConn() {
		return con;
	}
	
	public boolean updateDevInfoRecLst() {
		BeecomDB db = getInstance();
		DB_BaseRec rec;
		for(int i = 0; i < DevInfoRecLst.size(); i++) {
			rec = DevInfoRecLst.get(i);
			
		}
		/*
		 * Statement
		 * stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet
		 * .CONCUR_READ_ONLY);
		 * stmt.executeUpdate("update dbo.signal set value=2 where id=1");
		 * //如果后面不跟where条件，则更新所有列的value字段 stmt.close();
		 */
		return true;
	}

	public void dumpDevInfo() {
		for(int i = 0; i < DevInfoRecLst.size(); i++) {
			DevInfoRecLst.get(i).dumpRec();
		}
	}
}
