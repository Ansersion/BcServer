/**
 * 
 */
package bc_server;

import java.sql.Connection;

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
	Map<String, Long> Name2IDMap;
	// Map<Long, Long> DevUniqId2IndexMap;

	static Connection con;
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/bc_server_db?useSSL=false";
	static String user = "root";
	static String password = "Ansersion";

	private BeecomDB() {
		System.out.println("Info: Create BeecomDB");
		// Id2UserRecord = new HashMap<Long, User_DB_Record>();
		UserInfoRecLst = new ArrayList<DB_UserInfoRec>();
		DevInfoRecLst = new ArrayList<DB_DevInfoRec>();

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
			}

			for (int i = 0; i < DevInfoRecLst.size(); i++) {
				DevInfoRecLst.get(i).dumpRec();
			}

			con.close();
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
		DevInfoRecLst.set((int)dev_uniq_id - 1, dev_info_rec);
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
		if(dev_uniq_id <= 0 || dev_uniq_id > db.DevInfoRecLst.size()) {
			return false;
		}
		db.setDevInfoRec(dev_uniq_id, dev_info_rec);
		/*Statement stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		stmt.executeUpdate("update dbo.signal set value=2 where id=1"); //如果后面不跟where条件，则更新所有列的value字段
		stmt.close();*/
		return true;
	}

	// static public short nextNewClientId() {
	// return NextClientId++;
	// }
}
