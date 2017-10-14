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
public class User_DB {

	static User_DB UDB = null;

	List<User_DB_Record> UserRecordList;
	Map<String, Long> Name2IDMap;

	static Connection con;
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/bc_server_db?useSSL=false";
	static String user = "root";
	static String password = "Ansersion";

	private User_DB() {
		System.out.println("Info: Create UserDB");
		// Id2UserRecord = new HashMap<Long, User_DB_Record>();
		UserRecordList = new ArrayList<User_DB_Record>();
		Name2IDMap = new HashMap<String, Long>();
		User_DB_Record record_0_blank = new User_DB_Record();
		UserRecordList.add(record_0_blank);
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			if (!con.isClosed())
				System.out.println("Succeeded connecting to the Database!");
			Statement statement = con.createStatement();
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
				UserRecordList.add(new User_DB_Record(id, name, e_mail, phone, password));
				Name2IDMap.put(name, id);
			}
			rs.close();
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
	
	public List<User_DB_Record> getUserRecordList() {
		return UserRecordList;
	}

	static User_DB getInstance() {
		if (null == UDB) {
			UDB = new User_DB();
		}
		return UDB;
	}

	static public boolean ChkUserName(String name) {
		User_DB user_db = getInstance();
		return user_db.getName2IDMap().containsKey(name);
	}

	static public boolean ChkUserPwd(String name, byte[] password) {
		User_DB user_db = getInstance();
		if(!user_db.getName2IDMap().containsKey(name)) {
			return false;
		}
		long id = user_db.getName2IDMap().get(name);
		
		// Maybe truncate ID
		User_DB_Record user_db_record = user_db.getUserRecordList().get((int)id);
		String str_p = new String(password);
		System.out.println("PWD mysql: " + user_db_record.getPassword());
		return str_p.equals(user_db_record.getPassword());
	}

	// static public short nextNewClientId() {
	// return NextClientId++;
	// }
}
