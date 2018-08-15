/**
 * 
 */
package db;

import static org.junit.Assert.assertNotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.procedure.internal.Util.ResultClassesResolutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import bp_packet.BPSession;
import bp_packet.BPUserSession;
import bp_packet.SignalAttrInfo;
import other.Util;
import sys_sig_table.BPSysSigTable;
import sys_sig_table.SysSigInfo;

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
	
	private Map<Long, BPSession> devUniqId2SessionMap;
	private Map<String, BPSession> userName2SessionMap;
	private SessionFactory sessionFactory;
	
	public static enum LoginErrorEnum {
		LOGIN_OK,
		USER_INVALID,
		PASSWORD_INVALID,
	}
	
	public static enum GetSnErrorEnum {
		GET_SN_OK,
		GET_SN_USER_INVALID, 
		GET_SN_SN_INVALID,
		GET_SN_PERMISSION_DENY,
	}

	private BeecomDB() {
		sessionFactory = buildSessionFactory(); 
		String s = "Info: Create BeecomDB";
		logger.info(s);
		userInfoRecLst = new ArrayList<>();
		devInfoRecLst = new ArrayList<>();
		devAuthRecLst = new ArrayList<>();
		sysSigRecLst = new ArrayList<>();

		name2IDMap = new HashMap<>();

		DBUserInfoRec record0Blank = new DBUserInfoRec();
		userInfoRecLst.add(record0Blank);
		String sql;
		
		devUniqId2SessionMap = new HashMap<Long, BPSession>();
		userName2SessionMap = new HashMap<String, BPSession>();

		/*
		try (Statement statement = con.createStatement()) {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			
			sql = "select * from user_info";
			try (ResultSet rs = statement.executeQuery(sql)){
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
					userInfoRecLst.add(new DBUserInfoRec(id, name, eMail, phone, passwordTmp));
					name2IDMap.put(name, id);
				}
			}

			sql = "select * from dev_info";
			try (ResultSet rs = statement.executeQuery(sql)){
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
					devInfoRecLst.add(new DBDevInfoRec(devUniqId, userId, devPwd, devId, sysSigTabId, devName));
				}
			}

			sql = "select * from dev_auth";
			
			try  (ResultSet rs = statement.executeQuery(sql)){
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
					devAuthRecLst.add(new DBDevAuthRec(devUniqId,
							adminUserId, adminUserAuth, userId1,
							userAuth1, userId2, userAuth2, userId3,
							userAuth3, userId4, userAuth4));
				}
			}

			sql = "select * from sysSigTab";
			try  (ResultSet rs = statement.executeQuery(sql)){
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

				for (int i = 0; i < sysSigRecLst.size(); i++) {
					sysSigRecLst.get(i).dumpRec();
				}
			}

		} catch (SQLException|ClassNotFoundException e) {
			Util.bcLog(e, logger);
		} 
		*/
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
	
	public static LoginErrorEnum checkUserPassword(String name, String password) {
		return LoginErrorEnum.LOGIN_OK;
	}
	
	public LoginErrorEnum checkUserPassword(String name, String password, UserInfoUnit userInfoUnit) {

		if(null == name || name.isEmpty()) {
			return LoginErrorEnum.USER_INVALID;
		}
		if(null == password || password.isEmpty()) {
			return LoginErrorEnum.PASSWORD_INVALID;
		}

		LoginErrorEnum result = LoginErrorEnum.LOGIN_OK;
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			UserInfoHbn userInfoHbn = (UserInfoHbn)session  
		            .createQuery("from UserInfoHbn where name = :p_name and password = :p_password")
		            .setParameter("p_name", name)
		            .setParameter("p_password", password).uniqueResult();
		    
			tx.commit();
			if(null == userInfoHbn) {
				result = LoginErrorEnum.PASSWORD_INVALID;
			} else {
				if(null != userInfoUnit) {
					userInfoUnit.setUserInfoHbn(userInfoHbn);
				}
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			userInfoUnit = null;
			result = LoginErrorEnum.PASSWORD_INVALID;
		}
		return result;
	}
	
	public GetSnErrorEnum checkGetSNPermission(long userId, String sn) {
		GetSnErrorEnum result = GetSnErrorEnum.GET_SN_OK;
		
		if(userId <= 0) {
			return GetSnErrorEnum.GET_SN_USER_INVALID;
		}
		if(null == sn || sn.isEmpty()) {
			return GetSnErrorEnum.GET_SN_SN_INVALID;
		}

		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			SnInfoHbn snInfoHbn = null;
			snInfoHbn = (SnInfoHbn)session  
		            .createQuery("from SnInfoHbn where sn = :p_sn")
		            .setParameter("p_sn", sn).uniqueResult();
			if(null == snInfoHbn) {
				return GetSnErrorEnum.GET_SN_SN_INVALID;
			}
			long uniqDeviceId = snInfoHbn.getId();
			DevInfoHbn devInfoHbn = null;
			devInfoHbn = (DevInfoHbn)session.createQuery("from DevInfoHbn where snId = :sn_id and adminId = :admin_id")
					.setParameter("sn_id", uniqDeviceId)
					.setParameter("admin_id", userId).uniqueResult();
			if(null == devInfoHbn) {
				UserDevRelInfoHbn userDevRelInfoHbn = null;
				userDevRelInfoHbn = (UserDevRelInfoHbn)session.createQuery("from UserDevRelInfoHbn where devId = :dev_id and userId = :user_id")
						.setParameter("dev_id", uniqDeviceId)
						.setParameter("user_id", userId).uniqueResult();
				
				if(null == userDevRelInfoHbn) {
					return GetSnErrorEnum.GET_SN_PERMISSION_DENY;
				}
			}
			
			tx.commit();

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			result = GetSnErrorEnum.GET_SN_PERMISSION_DENY;
		}
		
		return result;
	}
	
	public boolean checkGetDeviceSignalMapPermission(long userId, long devUniqId) {
		boolean result = true;
		
		if(userId <= 0) {
			return false;
		}
		if(devUniqId <= 0) {
			return false;
		}

		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			DevInfoHbn devInfoHbn = null;
			devInfoHbn = (DevInfoHbn)session.createQuery("from DevInfoHbn where snId = :sn_id and adminId = :admin_id")
					.setParameter("sn_id", devUniqId)
					.setParameter("admin_id", userId).uniqueResult();
			if(null == devInfoHbn) {
				UserDevRelInfoHbn userDevRelInfoHbn = null;
				userDevRelInfoHbn = (UserDevRelInfoHbn)session.createQuery("from UserDevRelInfoHbn where devId = :dev_id and userId = :user_id")
						.setParameter("dev_id", devUniqId)
						.setParameter("user_id", userId).uniqueResult();
				
				if(null == userDevRelInfoHbn) {
					return false;
				}
			}
			
			tx.commit();

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			result = false;
		}
		
		return result;
	}
	
	public LoginErrorEnum checkDevicePassword(long devUniqId, String password, DeviceInfoUnit deviceInfoUnit) {
		LoginErrorEnum ret = LoginErrorEnum.LOGIN_OK;
		if(devUniqId <= 0) {
			return LoginErrorEnum.USER_INVALID;
		}
		if(null == deviceInfoUnit) {
			logger.error("Inner error: null == deviceInfoUnit");
			return LoginErrorEnum.USER_INVALID;
		}
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			DevInfoHbn devInfoHbn = null;
			devInfoHbn = (DevInfoHbn)session.createQuery("from DevInfoHbn where id = :dev_uniq_id and password = :_password")
					.setParameter("dev_uniq_id", devUniqId)
					.setParameter("_password", password).uniqueResult();
			if(null == devInfoHbn) {
				return LoginErrorEnum.PASSWORD_INVALID;
			}
			deviceInfoUnit.setDevInfoHbn(devInfoHbn);
			
			tx.commit();

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = LoginErrorEnum.PASSWORD_INVALID;
		}
		
		return ret;
	}
	
	public LoginErrorEnum checkDevicePassword(String userName, String password, DeviceInfoUnit deviceInfoUnit) {
		return LoginErrorEnum.LOGIN_OK;
	}
	
	public boolean checkSignalValueUnformed(long devUniqId, int sigId, byte sigType, Object sigVal) {
		boolean ret = false;
		if(devUniqId <= 0) {
			return false;
		}
		if(sigId < 0 || sigId > BPPacket.MAX_SIG_ID) {
			return false;
		}
		if(null == sigVal) {
			return false;
		}
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			SignalInfoHbn signalInfoHbn = null;
			signalInfoHbn = (SignalInfoHbn)session.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
					.setParameter("signal_id", sigId)
					.setParameter("dev_id", devUniqId).uniqueResult();
			if(null == signalInfoHbn) {
				return false;
			}
			if(sigId < BPPacket.SYS_SIG_START_ID) {
				/* TODO: check if alarm signal */
				CustomSignalInfoHbn customSignalInfoHbn = null;
				customSignalInfoHbn = (CustomSignalInfoHbn)session.createQuery("from CustomSignalInfoHbn where signalId = :signal_id and valType = :val_type")
						.setParameter("signal_id", signalInfoHbn.getId())
						.setParameter("val_type", sigType).uniqueResult();
				if(null == customSignalInfoHbn) {
					return false;
				}
			} else {
				/* TODO: check if alarm signal */
				SystemSignalInfoHbn systemSignalInfoHbn = null;
				systemSignalInfoHbn = (SystemSignalInfoHbn)session.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id", signalInfoHbn.getId()).uniqueResult();
				if(null == systemSignalInfoHbn) {
					return false;
				}
			}

			
			tx.commit();

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
		
		return ret;
	}
	
	private SignalInterface getSignalIntergaceFromDB(CustomSignalInfoHbn customSignalInfoHbn) {
		if(null == customSignalInfoHbn) {
			return null;
		}
		
		Transaction tx = null;
		SignalInterface customSignalInterface = null;
		switch(customSignalInfoHbn.getValType()) {
			case BPPacket.VAL_TYPE_UINT32:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalU32InfoHbn)session.createQuery("from CustomSignalU32InfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_UINT16:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalU16InfoHbn)session.createQuery("from CustomSignalU16InfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
				
			break;
			case BPPacket.VAL_TYPE_IINT32:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalI32InfoHbn)session.createQuery("from CustomSignalI32InfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_IINT16:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalI16InfoHbn)session.createQuery("from CustomSignalI16InfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_ENUM:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalEnumInfoHbn)session.createQuery("from CustomSignalEnumInfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_FLOAT:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalFloatInfoHbn)session.createQuery("from CustomSignalFloatInfoHbn where customSignalId = :custom_signal_id")
				           .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_STRING:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalStringInfoHbn)session.createQuery("from CustomSignalStringInfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			case BPPacket.VAL_TYPE_BOOLEAN:
				try (Session session = sessionFactory.openSession()) {
					tx = session.beginTransaction();
					customSignalInterface = (CustomSignalBooleanInfoHbn)session.createQuery("from CustomSignalBooleanInfoHbn where customSignalId = :custom_signal_id")
				            .setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
				    
					tx.commit();
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
					customSignalInterface = null;
				}
			break;
			default:
				customSignalInterface = null;
		}

		return customSignalInterface;
	}

	
	protected List<Integer> getCustomSignalEnumLangInfoEnumKeysLst(CustomSignalEnumInfoHbn customSignalEnumInfoHbn) {
		if(null == customSignalEnumInfoHbn) {
			return null;
		}
		
		Transaction tx = null;
		List<Integer> customSignalEnumLangInfoEnumKeysLst = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			customSignalEnumLangInfoEnumKeysLst = session  
		            .createQuery("select enumKey from CustomSignalEnumLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		            .setParameter("cus_sig_enm_id", customSignalEnumInfoHbn.getId()).list();
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			customSignalEnumLangInfoEnumKeysLst = null;
		}
		
		return customSignalEnumLangInfoEnumKeysLst;
		
	}
	
	public boolean checkCustomSignalValueUnformed(long devUniqId, int signalId, byte sigType, Object value) {
		if(devUniqId <= 0) {
			return false;
		}
		if(signalId < 0 || signalId >= BPPacket.SYS_SIG_START_ID) {
			return false;
		}
		if(BPPacket.VAL_TYPE_INVALID == sigType) {
			return false;
		}
		if(null == value) {
			return false;
		}
		
		List<SignalInfoHbn> signalInfoHbnLst = getSignalInfoHbnLst(devUniqId, signalId, signalId);
		List<CustomSignalInfoHbn> customSignalInfoHbnLst = getCusSigInfoHbnLst(signalInfoHbnLst);
		if(null == customSignalInfoHbnLst) {
			return false;
		}
		if(customSignalInfoHbnLst.size() != 1) {
			logger.error("Inner error: customSignalInfoHbnLst.size() != 1");
		}
		SignalInterface signalInterface = getSignalIntergaceFromDB(customSignalInfoHbnLst.get(0));
		if(null == signalInterface) {
			return false;
		}
		if(signalInterface.getValType() != sigType) {
			return false;
		}
		boolean ret = true;

		try {
			switch (signalInterface.getValType()) {
			case BPPacket.VAL_TYPE_UINT32:
			{
				CustomSignalU32InfoHbn customSignalU32InfoHbn = (CustomSignalU32InfoHbn)signalInterface;
				Long v = (Long)value;
				if(customSignalU32InfoHbn.getMaxVal() != BPPacket.VAL_U32_UNLIMIT && v > customSignalU32InfoHbn.getMaxVal()) {
					ret = false;
					break;
				}
				if(customSignalU32InfoHbn.getMinVal() != BPPacket.VAL_U32_UNLIMIT && v < customSignalU32InfoHbn.getMinVal()) {
					ret = false;
					break;
				}
				
				break;
			}
			case BPPacket.VAL_TYPE_UINT16:
			{
				CustomSignalU16InfoHbn customSignalU16InfoHbn = (CustomSignalU16InfoHbn)signalInterface;
				Integer v = (Integer)value;
				if(customSignalU16InfoHbn.getMaxVal() != BPPacket.VAL_U16_UNLIMIT && v > customSignalU16InfoHbn.getMaxVal()) {
					ret = false;
					break;
				}
				if(customSignalU16InfoHbn.getMinVal() != BPPacket.VAL_U16_UNLIMIT && v < customSignalU16InfoHbn.getMinVal()) {
					ret = false;
					break;
				}

				break;
			}
			case BPPacket.VAL_TYPE_IINT32:
			{
				CustomSignalI32InfoHbn customSignalI32InfoHbn = (CustomSignalI32InfoHbn)signalInterface;
				Integer v = (Integer)value;
				if(customSignalI32InfoHbn.getMaxVal() != BPPacket.VAL_I32_UNLIMIT && v > customSignalI32InfoHbn.getMaxVal()) {
					ret = false;
					break;
				}
				if(customSignalI32InfoHbn.getMinVal() != BPPacket.VAL_I32_UNLIMIT && v < customSignalI32InfoHbn.getMinVal()) {
					ret = false;
					break;
				}

				break;
			}
			case BPPacket.VAL_TYPE_IINT16:
			{
				CustomSignalI16InfoHbn customSignalI16InfoHbn = (CustomSignalI16InfoHbn)signalInterface;
				Short v = (Short)value;
				if(customSignalI16InfoHbn.getMaxVal() != BPPacket.VAL_I16_UNLIMIT && v > customSignalI16InfoHbn.getMaxVal()) {
					ret = false;
					break;
				}
				if(customSignalI16InfoHbn.getMinVal() != BPPacket.VAL_I16_UNLIMIT && v < customSignalI16InfoHbn.getMinVal()) {
					ret = false;
					break;
				}

				break;
			}
			case BPPacket.VAL_TYPE_ENUM:
			{
				CustomSignalEnumInfoHbn customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn)signalInterface;
				Integer v = (Integer)value;
				List<Integer> enumKeysLst = getCustomSignalEnumLangInfoEnumKeysLst(customSignalEnumInfoHbn);
				ret = enumKeysLst.contains(v);

				break;
			}
			case BPPacket.VAL_TYPE_FLOAT:
			{
				CustomSignalFloatInfoHbn customSignalFloatInfoHbn = (CustomSignalFloatInfoHbn)signalInterface;
				Float v = (Float)value;
				if(customSignalFloatInfoHbn.getMaxVal() != BPPacket.VAL_FLOAT_UNLIMIT && v > customSignalFloatInfoHbn.getMaxVal()) {
					ret = false;
					break;
				}
				if(customSignalFloatInfoHbn.getMinVal() != BPPacket.VAL_FLOAT_UNLIMIT && v < customSignalFloatInfoHbn.getMinVal()) {
					ret = false;
					break;
				}

				break;
			}
			case BPPacket.VAL_TYPE_STRING:
			{
				CustomSignalStringInfoHbn customSignalStringInfoHbn = (CustomSignalStringInfoHbn)signalInterface;
				String v = (String)value;
				if(v.length() > 0xFF) {
					ret = false;
				}

				break;
			}
			case BPPacket.VAL_TYPE_BOOLEAN:
			{
				CustomSignalU16InfoHbn customSignalU16InfoHbn = (CustomSignalU16InfoHbn)signalInterface;
				Boolean v = (Boolean)value;
				break;
			}
			default:
				ret = false;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
		
		return ret;
		
	}
	
	private boolean checkSystemSignalValueUnformed(SysSigInfo sysSigInfo, Object value)
	{
		if(null == sysSigInfo) {
			return false;
		}
		if(null == value) {
			return false;
		}
		boolean ret = true;
		
		try {
			switch (sysSigInfo.getValType()) {
			case BPPacket.VAL_TYPE_UINT32:
			{
				Long v = (Long)value;
				long maxVal = (long)sysSigInfo.getValMax();
				long minVal = (long)sysSigInfo.getValMin();
				if(BPPacket.VAL_U32_UNLIMIT != maxVal && v > maxVal) {
					ret = false;
					break;
				}
				if(BPPacket.VAL_U32_UNLIMIT != minVal && v < minVal) {
					ret = false;
					break;
				}

				break;
			}
			case BPPacket.VAL_TYPE_UINT16:
			{

				Integer v = (Integer)value;
				int maxVal = (int)sysSigInfo.getValMax();
				int minVal = (int)sysSigInfo.getValMin();
				if(BPPacket.VAL_U16_UNLIMIT != maxVal && v > maxVal) {
					ret = false;
					break;
				}
				if(BPPacket.VAL_U16_UNLIMIT != minVal && v < minVal) {
					ret = false;
					break;
				}


				break;
			}
			case BPPacket.VAL_TYPE_IINT32:
			{

				Integer v = (Integer)value;
				int maxVal = (int)sysSigInfo.getValMax();
				int minVal = (int)sysSigInfo.getValMin();
				if(BPPacket.VAL_I32_UNLIMIT != maxVal && v > maxVal) {
					ret = false;
					break;
				}
				if(BPPacket.VAL_I32_UNLIMIT != minVal && v < minVal) {
					ret = false;
					break;
				}


				break;
			}
			case BPPacket.VAL_TYPE_IINT16:
			{
				Short v = (Short)value;
				short maxVal = (short)sysSigInfo.getValMax();
				short minVal = (short)sysSigInfo.getValMin();
				if(BPPacket.VAL_I16_UNLIMIT != maxVal && v > maxVal) {
					ret = false;
					break;
				}
				if(BPPacket.VAL_I16_UNLIMIT != minVal && v < minVal) {
					ret = false;
					break;
				}


				break;
			}
			case BPPacket.VAL_TYPE_ENUM:
			{

				Integer v = (Integer)value;
				ret = sysSigInfo.getMapEnmLangRes().containsKey(v);

				break;
			}
			case BPPacket.VAL_TYPE_FLOAT:
			{

				Float v = (Float)value;
				float maxVal = (float)sysSigInfo.getValMax();
				float minVal = (float)sysSigInfo.getValMin();
				if(BPPacket.VAL_FLOAT_UNLIMIT != maxVal && v > maxVal) {
					ret = false;
					break;
				}
				if(BPPacket.VAL_FLOAT_UNLIMIT != minVal && v < minVal) {
					ret = false;
					break;
				}


				break;
			}
			case BPPacket.VAL_TYPE_STRING:
			{

				String v = (String)value;
				if(v.length() > 0xFF) {
					ret = false;
				}

				break;
			}
			case BPPacket.VAL_TYPE_BOOLEAN:
			{
				Boolean v = (Boolean)value;

				break;
			}
			default:
				ret = false;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
		
		return ret;
	}
	
	public boolean checkSystemSignalValueUnformed(long devUniqId, int signalId, Object value) {
		if(devUniqId <= 0) {
			return false;
		}
		if(signalId < BPPacket.SYS_SIG_START_ID || signalId > BPPacket.MAX_SIG_ID) {
			return false;
		}
		if(null == value) {
			return false;
		}
		
		List<SignalInfoHbn> signalInfoHbnLst = getSignalInfoHbnLst(devUniqId, signalId, signalId);
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = getSysSigInfoHbnLst(signalInfoHbnLst, null);
		if(null == systemSignalInfoHbnLst) {
			return false;
		}
		if(systemSignalInfoHbnLst.size() != 1) {
			logger.error("Inner error: systemSignalInfoHbnLst.size() != 1");
		}
		if(systemSignalInfoHbnLst.get(0).getIfConfigDef()) {
			BPSysSigTable bpSysSigTable = BPSysSigTable.getSysSigTableInstance();
			int systemSignalIdOffset = signalId - BPPacket.SYS_SIG_START_ID;
			final List<SysSigInfo> sysSigInfoLst = bpSysSigTable.getSysSigInfoLst();
			if(systemSignalIdOffset >= sysSigInfoLst.size()) {
				return false;
			}
			SysSigInfo sysSigInfo = sysSigInfoLst.get(systemSignalIdOffset);
			return checkSystemSignalValueUnformed(sysSigInfo, value);
		} else {
			
		}
		
		return false;
		
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

	public Map<Long, BPSession> getDevUniqId2SessionMap() {
		return devUniqId2SessionMap;
	}

	public Map<String, BPSession> getUserName2SessionMap() {
		return userName2SessionMap;
	}

	public long getDeviceUniqId(String sn) {
		if(!ifSnFormal(sn)) {
			return 0;
		}
		long deviceUniqId = 0;
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
		    SnInfoHbn snInfoHbn = (SnInfoHbn)session  
		            .createQuery(" from SnInfoHbn where sn = ? ")
		            .setParameter(0, sn)
		            .uniqueResult();  
		    if(snInfoHbn != null) {
		    	deviceUniqId = snInfoHbn.getId();
		    }
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			deviceUniqId = 0;
		}
		return deviceUniqId;
	}
	
	public List<SignalInfoHbn> getSysSigMapLst(long uniqDeviceId) {
		/*
		if(uniqDeviceId < 0) {
			return null;
		}

		Transaction tx = null;
		List<Integer> sysSigLst = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
		    sysSigLst = session  
		            .createQuery("select signalId from SignalInfoHbn where devId = ? and signalId > 57344")
		            .setParameter(0, uniqDeviceId).list();
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		return sysSigLst;
		*/
		return getSignalInfoHbnLst(uniqDeviceId, BPPacket.SYS_SIG_START_ID, BPPacket.MAX_SIG_ID);
	}
	
	public List<SystemSignalInfoUnit> getSystemSignalUnitLst(long uniqDeviceId, List<SystemSignalInfoUnit> systemSignalInfoUnitLst) {
		if(null == systemSignalInfoUnitLst) {
			return null;
		}
		List<SignalInfoHbn> signalInfoHbnLst = getSignalInfoHbnLst(uniqDeviceId, BPPacket.SYS_SIG_START_ID, BPPacket.MAX_SIG_ID);
		if(null == signalInfoHbnLst || signalInfoHbnLst.isEmpty()) {
			return null;
		}
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = getSysSigInfoHbnLst(signalInfoHbnLst, null);
		if(null == systemSignalInfoHbnLst || systemSignalInfoHbnLst.isEmpty()) {
			return null;
		}
		
		Iterator<SignalInfoHbn> itSI = signalInfoHbnLst.iterator();
		Iterator<SystemSignalInfoHbn> itSSI;
		int signalId;
		boolean ifNotifying;
		boolean ifConfigDef;
		while(itSI.hasNext()) {
			SignalInfoHbn signalInfoHbn = itSI.next();
			itSSI = systemSignalInfoHbnLst.iterator();
			while(itSSI.hasNext()) {
				SystemSignalInfoHbn systemSignalInfoHbn = itSSI.next();

				if(systemSignalInfoHbn.getSignalId() == signalInfoHbn.getId()) {
					signalId = signalInfoHbn.getSignalId();
					if(signalId < BPPacket.SYS_SIG_START_ID || signalId > BPPacket.MAX_SIG_ID) {
						return null;
					}
					ifNotifying = signalInfoHbn.getNotifying();
					ifConfigDef = systemSignalInfoHbn.getIfConfigDef();
					SignalInterface systemSignalInterface = null;
					if(!ifConfigDef) {
						BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
						List<SysSigInfo> sysSigInfoLst = sysSigTab.getSysSigInfoLst();
						if(signalId - BPPacket.SYS_SIG_START_ID > sysSigInfoLst.size()) {
							return null;
						}
						
						Transaction tx = null;
						switch(sysSigInfoLst.get(signalId - BPPacket.SYS_SIG_START_ID).getValType()) {
							case BPPacket.VAL_TYPE_UINT32:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalU32InfoHbn)session.createQuery("from SystemSignalU32InfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_UINT16:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalU16InfoHbn)session.createQuery("from SystemSignalU16InfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
								
							break;
							case BPPacket.VAL_TYPE_IINT32:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalI32InfoHbn)session.createQuery("from SystemSignalI32InfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_IINT16:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalI16InfoHbn)session.createQuery("from SystemSignalI16InfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_ENUM:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalEnumInfoHbn)session.createQuery("from SystemSignalEnumInfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_FLOAT:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalFloatInfoHbn)session.createQuery("from SystemSignalFloatInfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_STRING:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalStringInfoHbn)session.createQuery("from SystemSignalStringInfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							case BPPacket.VAL_TYPE_BOOLEAN:
								try (Session session = sessionFactory.openSession()) {
									tx = session.beginTransaction();
									systemSignalInterface = (SystemSignalBooleanInfoHbn)session.createQuery("from SystemSignalBooleanInfoHbn where systemSignalId = :system_signal_id")
								            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
								    
									tx.commit();
								} catch (Exception e) {
									StringWriter sw = new StringWriter();
									e.printStackTrace(new PrintWriter(sw, true));
									String str = sw.toString();
									logger.error(str);
									systemSignalInterface = null;
								}
							break;
							default:
								return null;
						}

						
					}
					systemSignalInfoUnitLst.add(new SystemSignalInfoUnit(signalId, ifNotifying, ifConfigDef, systemSignalInterface));
					break;
				}
			}
		}
		
		return systemSignalInfoUnitLst;
	}
	
	private void putLangIntoMap(Map<Integer, String> map, int langSupportMask, CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn) {
		
	}
	
	private Map<Integer, String> getCustomSignalNameLangMap(long custonSignalNameLangId, int langSupportMask) {
		Map<Integer, String> customSignalLangMap = null;
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = (CustomSignalNameLangInfoHbn)session  
		    //        .createQuery("from CustomSignalNameLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		    //        .setParameter("id", custonSignalNameLangId).list();
			CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = session.load(CustomSignalNameLangInfoHbn.class, custonSignalNameLangId);
			if(null != customSignalNameLangInfoHbn) {
				tx.commit();
				return null;
			}
			CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn = session.load(CustomSignalNameLangEntityInfoHbn.class, customSignalNameLangInfoHbn.getCustomSignalName());
			if(null == customSignalNameLangEntityInfoHbn) {
				tx.commit();
				return null;
			}
			customSignalLangMap = new HashMap<>();
			putLangIntoMap(customSignalLangMap, langSupportMask, customSignalNameLangEntityInfoHbn);
			
			/* TODO: add support for other language */
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		
		return customSignalLangMap;
	}
	
	public List<CustomSignalInfoUnit> getCustomSignalUnitLst(long uniqDeviceId, List<CustomSignalInfoUnit> customSignalInfoUnitLst, int langSupportMask) {
		if(uniqDeviceId < 0) {
			return null;
		}
		if(null == customSignalInfoUnitLst) {
			return null;
		}
		if(langSupportMask < 0 || (langSupportMask & 0xFF) == 0) {
			return null;
		}
		
		List<SignalInfoHbn> signalInfoHbnLst = getSignalInfoHbnLst(uniqDeviceId, BPPacket.CUS_SIG_START_ID, BPPacket.CUS_SIG_END_ID);
		if(null == signalInfoHbnLst || signalInfoHbnLst.isEmpty()) {
			return null;
		}
		List<CustomSignalInfoHbn> customSignalInfoHbnLst = getCusSigInfoHbnLst(signalInfoHbnLst);
		if(null == customSignalInfoHbnLst || customSignalInfoHbnLst.isEmpty()) {
			return null;
		}
		
		Iterator<SignalInfoHbn> itSI = signalInfoHbnLst.iterator();
		Iterator<CustomSignalInfoHbn> itCSI;
		int signalId;
		boolean ifNotifying;
		boolean ifAlarm;
		while(itSI.hasNext()) {
			SignalInfoHbn signalInfoHbn = itSI.next();
			itCSI = customSignalInfoHbnLst.iterator();
			while(itCSI.hasNext()) {
				CustomSignalInfoHbn customSignalInfoHbn = itCSI.next();

				if (customSignalInfoHbn.getSignalId() == signalInfoHbn.getId()) {
					signalId = signalInfoHbn.getSignalId();
					if (signalId < BPPacket.CUS_SIG_START_ID || signalId > BPPacket.CUS_SIG_END_ID) {
						return null;
					}
					ifNotifying = signalInfoHbn.getNotifying();
					ifAlarm = customSignalInfoHbn.getIfAlarm();
					SignalInterface signalInterface = null;
					// customSignalInfoHbn.getCusSigNameLangId(), langSupportMask


					Transaction tx = null;
					switch (customSignalInfoHbn.getValType()) {
					case BPPacket.VAL_TYPE_UINT32:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalU32InfoHbn) session
									.createQuery("from CustomSignalU32InfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_UINT16:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalU16InfoHbn) session
									.createQuery("from CustomSignalU16InfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}

						break;
					case BPPacket.VAL_TYPE_IINT32:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalI32InfoHbn) session
									.createQuery("from CustomSignalI32InfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_IINT16:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalI16InfoHbn) session
									.createQuery("from CustomSignalI16InfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_ENUM:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalEnumInfoHbn) session
									.createQuery(
											"from CustomSignalEnumInfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_FLOAT:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalFloatInfoHbn) session
									.createQuery(
											"from CustomSignalFloatInfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_STRING:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalStringInfoHbn) session
									.createQuery(
											"from CustomSignalStringInfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_BOOLEAN:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							signalInterface = (CustomSignalBooleanInfoHbn) session
									.createQuery(
											"from CustomSignalBooleanInfoHbn where customSignalId = :custom_signal_id")
									.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							signalInterface = null;
						}
						break;
					default:
						if(ifAlarm) {
							try (Session session = sessionFactory.openSession()) {
								tx = session.beginTransaction();
								signalInterface = (CustomSignalAlmInfoHbn) session
										.createQuery(
												"from CustomSignalAlmInfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();

								tx.commit();
							} catch (Exception e) {
								StringWriter sw = new StringWriter();
								e.printStackTrace(new PrintWriter(sw, true));
								String str = sw.toString();
								logger.error(str);
								signalInterface = null;
							}
						} else {
							return null;
						}
					}
					

					customSignalInfoUnitLst.add(new CustomSignalInfoUnit(signalId, ifNotifying, ifAlarm, signalInterface));
					break;
				}
			}
		}
		
		return customSignalInfoUnitLst;
	}
	
	public List<SystemSignalCustomInfoUnit> getSystemSignalCustomInfoUnitLst(long uniqDeviceId, List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitLst) {
		if(uniqDeviceId < 0) {
			return null;
		}
		
		if(null == systemSignalCustomInfoUnitLst) {
			return null;
		}
		
		List<SignalInfoHbn> signalInfoHbnLst = getSignalInfoHbnLst(uniqDeviceId, BPPacket.SYS_SIG_START_ID, BPPacket.MAX_SIG_ID);
		if(null == signalInfoHbnLst || signalInfoHbnLst.isEmpty()) {
			return null;
		}
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = getSysSigInfoHbnLst(signalInfoHbnLst, false);
		if(null == systemSignalInfoHbnLst || systemSignalInfoHbnLst.isEmpty()) {
			return null;
		}
		
		Iterator<SignalInfoHbn> itSI;
		Iterator<SystemSignalInfoHbn> itSSI = systemSignalInfoHbnLst.iterator();
		int signalId;
		while(itSSI.hasNext()) {
			SystemSignalInfoHbn systemSignalInfoHbn = itSSI.next();
			itSI = signalInfoHbnLst.iterator();
			
			while(itSI.hasNext()) {
				SignalInfoHbn signalInfoHbn = itSI.next();
				
				if(systemSignalInfoHbn.getSignalId() == signalInfoHbn.getId()) {
					signalId = signalInfoHbn.getSignalId();
					if(signalId < BPPacket.SYS_SIG_START_ID || signalId > BPPacket.MAX_SIG_ID) {
						return null;
					}
					SignalInterface systemSignalInterface = null;
					BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
					List<SysSigInfo> sysSigInfoLst = sysSigTab.getSysSigInfoLst();
					if (signalId - BPPacket.SYS_SIG_START_ID > sysSigInfoLst.size()) {
						return null;
					}

					Transaction tx = null;
					switch (sysSigInfoLst.get(signalId - BPPacket.SYS_SIG_START_ID).getValType()) {
					case BPPacket.VAL_TYPE_UINT32:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalU32InfoHbn) session
									.createQuery("from SystemSignalU32InfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_UINT16:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalU16InfoHbn) session
									.createQuery("from SystemSignalU16InfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}

						break;
					case BPPacket.VAL_TYPE_IINT32:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalI32InfoHbn) session
									.createQuery("from SystemSignalI32InfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_IINT16:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalI16InfoHbn) session
									.createQuery("from SystemSignalI16InfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_ENUM:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalEnumInfoHbn) session
									.createQuery(
											"from SystemSignalEnumInfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_FLOAT:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalFloatInfoHbn) session
									.createQuery(
											"from SystemSignalFloatInfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_STRING:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalStringInfoHbn) session
									.createQuery(
											"from SystemSignalStringInfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					case BPPacket.VAL_TYPE_BOOLEAN:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalBooleanInfoHbn) session
									.createQuery(
											"from SystemSignalBooleanInfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw, true));
							String str = sw.toString();
							logger.error(str);
							systemSignalInterface = null;
						}
						break;
					default:
						return null;
					}
					systemSignalCustomInfoUnitLst.add(new SystemSignalCustomInfoUnit(signalId, systemSignalInterface));
					break;
				}
			}
		}
		return systemSignalCustomInfoUnitLst;
	}
	
	protected List<SignalInfoHbn> getSignalInfoHbnLst(long uniqDeviceId, int signalIdSmallest, int signalIdBiggest) {
		if(uniqDeviceId < 0) {
			return null;
		}
		int sIdSmall = signalIdSmallest;
		if(sIdSmall < 0) {
			sIdSmall = 0;
		}
		int sIdBig = signalIdBiggest;
		if(sIdBig > BPPacket.MAX_SIG_ID) {
			sIdBig = BPPacket.MAX_SIG_ID;
		}

		Transaction tx = null;
		List<SignalInfoHbn> sigInfoHbnLst = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			sigInfoHbnLst = session  
		            .createQuery("from SignalInfoHbn where devId = :dev_id and signalId >= :signal_id_s and signalId <= :signal_id_b")
		            .setParameter("dev_id", uniqDeviceId)
		            .setParameter("signal_id_s", sIdSmall)
		            .setParameter("signal_id_b", sIdBig).list();
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		return sigInfoHbnLst;
	}
	
	public List<SignalInfoHbn> getCusSigMapLst(long uniqDeviceId) {
		/*
		if(uniqDeviceId < 0) {
			return null;
		}

		Transaction tx = null;
		List<SignalInfoHbn> cusSigLst = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
		    cusSigLst = session  
		            .createQuery("from SignalInfoHbn where devId = ? and signalId < 57344")
		            .setParameter(0, uniqDeviceId).list();
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		return cusSigLst;
		*/
		return getSignalInfoHbnLst(uniqDeviceId, 0, BPPacket.SYS_SIG_START_ID - 1);
	}
	
	public List<SystemSignalInfoHbn> getSysSigInfoHbnLst(List<SignalInfoHbn> signalInfoHbnLst, Boolean ifDef) {
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = new ArrayList<SystemSignalInfoHbn>();
		
		Transaction tx = null;
		StringBuilder hql = new StringBuilder();
		hql.append("from SystemSignalInfoHbn where signalId = :signal_id");
		if(null != ifDef) {
			hql.append(" and ifConfigDef = ");
			hql.append(ifDef.toString());
		}
		
		try (Session session = sessionFactory.openSession()) {
			Iterator<SignalInfoHbn> it = signalInfoHbnLst.iterator();
			SystemSignalInfoHbn systemSignalInfoHbn = null;
			tx = session.beginTransaction();
			while(it.hasNext()) {

				// systemSignalInfoHbn = (SystemSignalInfoHbn)session.createQuery("from SystemSignalInfoHbn where signalId = :signal_id and ifConfigDef = false")
				systemSignalInfoHbn = (SystemSignalInfoHbn)session.createQuery(hql.toString())
				.setParameter("signal_id", it.next().getId())
				.uniqueResult();
				
				if(null == systemSignalInfoHbn) {
					// return null;
					continue;
				}
				systemSignalInfoHbnLst.add(systemSignalInfoHbn);
				systemSignalInfoHbn = null;
			}

			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			// systemSignalInfoHbnLst = null;
		}
		
		return systemSignalInfoHbnLst;
	}
	
	public List<CustomSignalInfoHbn> getCusSigInfoHbnLst(List<SignalInfoHbn> signalInfoHbnLst) {
		List<CustomSignalInfoHbn> customSignalInfoHbnLst = new ArrayList<CustomSignalInfoHbn>();
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			Iterator<SignalInfoHbn> it = signalInfoHbnLst.iterator();
			CustomSignalInfoHbn customSignalInfoHbn = null;
			tx = session.beginTransaction();
			while(it.hasNext()) {

				customSignalInfoHbn = (CustomSignalInfoHbn)session.createQuery("from CustomSignalInfoHbn where signalId = :signal_id")
				.setParameter("signal_id", it.next().getId())
				.uniqueResult();
				
				if(null == customSignalInfoHbn) {
					return null;
				}
				customSignalInfoHbnLst.add(customSignalInfoHbn);
				customSignalInfoHbn = null;
			}

			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			customSignalInfoHbnLst = null;
		}
		
		return customSignalInfoHbnLst;
	}
	
    public boolean checkSignalMapChksum(long uniqDevId, long checksum) {
		boolean ret = false;
    	if(BPPacket.INVALID_SIGNAL_MAP_CHECKSUM == checksum) {
    		return ret;
    	}
		    
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			DevInfoHbn devInfoHbn = (DevInfoHbn) session
					.createQuery("from DevInfoHbn where devId = :dev_id")
					.setParameter("dev_id", uniqDevId).uniqueResult();
			if (null == devInfoHbn || devInfoHbn.getSigMapChksum() != checksum) {
				return ret;
			}
			tx.commit();
			ret = true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
    	return ret;
    }
    
    public List<Long> getDeviceIDList(String userName) {

		if(null == userName) {
			return null;
		}
		List<Long> deviceIdList = null;
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			UserInfoHbn userInfoHbn = (UserInfoHbn) session
					.createQuery("from UserInfoHbn where name = :user_name")
					.setParameter("user_name", userName).uniqueResult();
			if (null == userInfoHbn) {
				return null;
			}
			long userId = userInfoHbn.getId();
			
			List<Long> deviceIdListAdminTmp = (List<Long>)session  
		            .createQuery("select id from DevInfoHbn where adminId = :user_id")
		            .setParameter("user_id", userId).list();
			List<Long> deviceIdListTmp = (List<Long>)session  
		            .createQuery("select devId from UserDevRelInfoHbn where userId = :user_id")
		            .setParameter("user_id", userId).list();
			
			if(null == deviceIdListAdminTmp && null == deviceIdListTmp) {
				return null;
			} else if(null == deviceIdListAdminTmp) {
				deviceIdList = deviceIdListTmp;
			} else if(null == deviceIdListTmp) {
				deviceIdList = deviceIdListAdminTmp;
			} else {
				deviceIdListAdminTmp.addAll(deviceIdListTmp);
				deviceIdList = deviceIdListAdminTmp;
			}
			
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			deviceIdList = null;
		}
    	return deviceIdList;
    }
    
    public int getDeviceLangSupportMask(long uniqDevId) {

    	int ret = 0;
		if(uniqDevId < 0) {
			return ret;
		}

		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			ret = (Integer)session
					.createQuery("select langSupportMask from DevInfoHbn where id = :dev_id")
					.setParameter("dev_id", uniqDevId).uniqueResult();

			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
    	return ret;
    }
    
    public boolean putSignalMapChksum(long uniqDevId, long checksum) {
    	boolean ret = false;
    	if(BPPacket.INVALID_SIGNAL_MAP_CHECKSUM == checksum) {
    		logger.error("Internal error: BPPacket.INVALID_SIGNAL_MAP_CHECKSUM == checksum");
    		return ret;
    	}
    	
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			DevInfoHbn devInfoHbn = (DevInfoHbn) session
					.createQuery("from DevInfoHbn where devId = :dev_id")
					.setParameter("dev_id", uniqDevId).uniqueResult();
			if (null == devInfoHbn) {
				logger.error("Internal error: null == devInfoHbn");
				return ret;
			}
			if(checksum != devInfoHbn.getSigMapChksum()) {
				devInfoHbn.setSigMapChksum(checksum);
				session.save(devInfoHbn);
			}
			tx.commit();
			ret = true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
    	return ret;
    	
    }
    
    public boolean putSystemSignalEnabledMap(long uniqDevId, List<Integer> systemSignalEnabledList) {
    	boolean ret = false;
    	if(null == systemSignalEnabledList) {
    		return ret;
    	}
    	
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			for(int i = 0; i < systemSignalEnabledList.size(); i++) {
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn) session
						.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
						.setParameter("signal_id", systemSignalEnabledList.get(i))
						.setParameter("dev_id", uniqDevId).uniqueResult();
				if (null == signalInfoHbn) {
					signalInfoHbn = new SignalInfoHbn();
				}
				signalInfoHbn.setSignalId(systemSignalEnabledList.get(i));
				signalInfoHbn.setDevId(uniqDevId);
				session.save(signalInfoHbn);
			}

			tx.commit();
			ret = true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
    	return ret;
    }
    
    public boolean putSignalInterface(SignalInterface signalInterface, boolean toCommit) {
    	return true;
    }
    
    public boolean putCustomSignalMap(long uniqDevId, List<CustomSignalInfoUnit> customSignalUnitInfoList) {
    	boolean ret = false;
    	if(null == customSignalUnitInfoList) {
    		return ret;
    	}
    	
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			for(int i = 0; i < customSignalUnitInfoList.size(); i++) {
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn) session
						.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
						.setParameter("signal_id", customSignalUnitInfoList.get(i).getCusSigId())
						.setParameter("dev_id", uniqDevId).uniqueResult();
				if (null == signalInfoHbn) {
					signalInfoHbn = new SignalInfoHbn();
					signalInfoHbn.setSignalId(customSignalUnitInfoList.get(i).getCusSigId());
					signalInfoHbn.setDevId(uniqDevId);
				}
	
				signalInfoHbn.setNotifying(customSignalUnitInfoList.get(i).isIfNotifing());
				session.save(signalInfoHbn);
				
				Long signalKeyId = signalInfoHbn.getId();
				CustomSignalInfoHbn customSignalInfoHbn = (CustomSignalInfoHbn)session
						.createQuery("from CustomSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id",  signalKeyId).uniqueResult();
				if(null == customSignalInfoHbn) {
					customSignalInfoHbn = new CustomSignalInfoHbn();
					customSignalInfoHbn.setSignalId(signalKeyId);
				}
				customSignalInfoHbn.setIfAlarm(customSignalUnitInfoList.get(i).isIfAlarm());
				customSignalInfoHbn.setValType((short)(customSignalUnitInfoList.get(i).getCustomSignalInterface().getValType() & 0xFFFF));
				session.save(customSignalInfoHbn);
				
				SignalInterface signalInterface = customSignalUnitInfoList.get(i).getCustomSignalInterface();
				if(signalInterface.saveToDb(session) < 0) {
					logger.error("Internal error: null == devInfoHbn");
					return ret;
				}
				
			}

			tx.commit();
			ret = true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
    	return ret;
    }
    
    public boolean putSystemCustomSignalInfoMap(long uniqDevId, List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnit) {
    	boolean ret = false;
    	if(null == systemSignalCustomInfoUnit) {
    		return ret;
    	}
    	
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			for(int i = 0; i < systemSignalCustomInfoUnit.size(); i++) {
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn) session
						.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
						.setParameter("signal_id", systemSignalCustomInfoUnit.get(i).getSysSigId())
						.setParameter("dev_id", uniqDevId).uniqueResult();
				if (null == signalInfoHbn) {
					signalInfoHbn = new SignalInfoHbn();
					signalInfoHbn.setSignalId(systemSignalCustomInfoUnit.get(i).getSysSigId());
					signalInfoHbn.setDevId(uniqDevId);
				}

				session.save(signalInfoHbn);
				
				Long signalKeyId = signalInfoHbn.getId();
				SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn)session
						.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id",  signalKeyId).uniqueResult();
				if(null == systemSignalInfoHbn) {
					systemSignalInfoHbn = new SystemSignalInfoHbn();
					systemSignalInfoHbn.setSignalId(signalKeyId);
				}

				systemSignalInfoHbn.setIfConfigDef(false);
				
				session.save(systemSignalInfoHbn);
				
				SignalInterface signalInterface = systemSignalCustomInfoUnit.get(i).getSignalInterface();
				if(signalInterface.saveToDb(session) < 0) {
					logger.error("Internal error: null == devInfoHbn");
					return ret;
				}
				
			}

			tx.commit();
			ret = true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}
    	return ret;
    }
	
	public int modifySysSigAttrMap(long uniqDevId, Map<Integer, SignalAttrInfo> sysSigAttrMap) {
		if(null == sysSigAttrMap) {
			return 0;
		}
		  
		Iterator<Map.Entry<Integer, SignalAttrInfo>> entries = sysSigAttrMap.entrySet().iterator();  
		Map.Entry<Integer, SignalAttrInfo> entry;
		  
		while (entries.hasNext()) {    
		    entry = entries.next();  
		    
			Transaction tx = null;
		    try (Session session = sessionFactory.openSession()) {
				tx = session.beginTransaction();
				
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn)session.createQuery("from SignalInfoHbn where devId = :dev_id and signalId = :signal_id")
				.setParameter("dev_id", uniqDevId)
				.setParameter("signal_id", entry.getKey())
				.uniqueResult();
				if(null == signalInfoHbn) {
					return entry.getKey();
				}
				
				SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn)session.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
				.setParameter("signal_id", signalInfoHbn.getId())
				.uniqueResult();
				if(null == systemSignalInfoHbn) {
					logger.error("DB error: {}", signalInfoHbn.getId());
					return entry.getKey();
				}
				
				tx.commit();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String str = sw.toString();
				logger.error(str);
			}
		}  
		return 0;
	}
	
	/*
	public List<SystemSignalInfoUnit> getSysSigInfoMap(List<SystemSignalInfoHbn> systemSignalInfoHbnLst) {
		List<SystemSignalInfoUnit> systemSignalInfoUnitLst = new ArrayList<SystemSignalInfoUnit>();
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			Iterator<SystemSignalInfoHbn> it = systemSignalInfoHbnLst.iterator();
			SystemSignalInfoHbn systemSignalInfoHbn = null;
			tx = session.beginTransaction();
			while(it.hasNext()) {
				systemSignalInfoHbn = it.next();
				if(!systemSignalInfoHbn.getIfConfigDef()) {
					// TODO: support system signal info customised
					continue;
				}
				systemSignalInfoUnitLst.add(new SystemSignalInfoUnit(systemSignalInfoHbn, null));
			}

			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		
		return systemSignalInfoUnitLst;
		
	}
	*/
	
    public static SessionFactory buildSessionFactory() {  
        try {  
            // Create the SessionFactory from hibernate.cfg.xml  
            return new Configuration().configure().buildSessionFactory();  
        }  
        catch (Throwable ex) {  
            // Make sure you log the exception, as it might be swallowed  
            System.err.println("Initial SessionFactory creation failed." + ex);  
            throw new ExceptionInInitializerError(ex);  
        }  
    }  
    
    private boolean ifSnFormal(String sn) {
    	return sn != null && sn.length() > 0 && sn.length() <=64;
    }
	

}
