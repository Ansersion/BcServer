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
import java.util.LinkedList;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.procedure.internal.Util.ResultClassesResolutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPDeviceSession;
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
	private static final Long INVALID_LANGUAGE_ID = 0L;
	private static final Long INVALID_ADMIN_ID = 0L;
	private static final Long INVALID_SIG_MAP_CHECKSUM = 0x7FFFFFFFFFFFFFFFL;
	

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
	private Map<Long, BPSession> userId2SessionMap;
	/* maybe not always be updated, dynamically updated */
	private Map<Long, List<UserDevRelInfoInterface> > userId2UserDevRelInfoListMap;
	/* must always be updated */
	private Map<Long, List<UserDevRelInfoInterface> > snId2UserDevRelInfoListMap;
	private List<UserDevRelInfoInterface> userDevRelInfoHbnList;
	
	private SessionFactory sessionFactory;
	
	public static enum LoginErrorEnum {
		LOGIN_OK,
		USER_INVALID,
		PASSWORD_INVALID,
		USER_OR_PASSWORD_INVALID,
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
		userId2SessionMap = new HashMap<>();

		userId2UserDevRelInfoListMap = new HashMap<>();
		snId2UserDevRelInfoListMap = new HashMap<>();
		userDevRelInfoHbnList = new LinkedList<>();
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
			/*
			devInfoHbn = (DevInfoHbn)session.createQuery("from DevInfoHbn where id = :id_ and adminId = :admin_id")
					.setParameter("id_", devUniqId)
					.setParameter("admin_id", userId).uniqueResult();
			if(null == devInfoHbn) {
				UserDevRelInfoHbn userDevRelInfoHbn = null;
				userDevRelInfoHbn = (UserDevRelInfoHbn)session.createQuery("from UserDevRelInfoHbn where snId = :sn_id and userId = :user_id")
						.setParameter("sn_id", devInfoHbn.getSnId())
						.setParameter("user_id", userId).uniqueResult();
				
				if(null == userDevRelInfoHbn) {
					return false;
				}
			}
			*/
			DevInfoHbn devInfoHbn = session.get(DevInfoHbn.class, devUniqId);
			if(null == devInfoHbn) {
				return false;
			}
			if(devInfoHbn.getAdminId() == userId) {
				return true;
			} else {
				UserDevRelInfoHbn userDevRelInfoHbn = null;
				userDevRelInfoHbn = (UserDevRelInfoHbn)session.createQuery("from UserDevRelInfoHbn where snId = :sn_id and userId = :user_id")
						.setParameter("sn_id", devInfoHbn.getSnId())
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
	
    /* for system signal info */
    private SignalInterface getSignalInterface(short valueType, SystemSignalInfoHbn systemSignalInfoHbn) {
    	SignalInterface signalInterface = null;
    	if(null == systemSignalInfoHbn) {
    		return signalInterface;
    	}
    	if(!BPPacket.ifSigTypeValid(valueType)) {
    		return signalInterface;
    	}
    	/* construct a hql like "from <table> where systemSignalId =: system_signal_id"*/
    	StringBuilder hql = new StringBuilder();
    	hql.append("from ");
		String tableName;
		switch(valueType) {
		case BPPacket.VAL_TYPE_UINT32:
			tableName = "SystemSignalU32InfoHbn";
			break;
		case BPPacket.VAL_TYPE_UINT16:
			tableName = "SystemSignalU16InfoHbn";
			break;
		case BPPacket.VAL_TYPE_IINT32:
			tableName = "SystemSignalI32InfoHbn";
			break;
		case BPPacket.VAL_TYPE_IINT16:
			tableName = "SystemSignalI16InfoHbn";
			break;
		case BPPacket.VAL_TYPE_ENUM:
			tableName = "SystemSignalEnumInfoHbn";
			break;
		case BPPacket.VAL_TYPE_FLOAT:
			tableName = "SystemSignalFloatInfoHbn";
			break;
		case BPPacket.VAL_TYPE_STRING:
			tableName = "SystemSignalStringInfoHbn";
			break;
		case BPPacket.VAL_TYPE_BOOLEAN:
			tableName = "SystemSignalBooleanInfoHbn";
			break;
		default:
			logger.error("Inner error: invalid value type");
			return signalInterface;
		}
		hql.append(tableName);
		hql.append(" where systemSignalId =: system_signal_id");
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			signalInterface = (SignalInterface)session.createQuery(hql.toString())
		            .setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();
		    
			// tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			signalInterface = null;
		}
		
		return signalInterface;
		
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
		if(systemSignalInfoHbnLst.get(0).getCustomFlags() == 0) {
			BPSysSigTable bpSysSigTable = BPSysSigTable.getSysSigTableInstance();
			int systemSignalIdOffset = signalId - BPPacket.SYS_SIG_START_ID;
			SysSigInfo sysSigInfo = bpSysSigTable.getSysSigInfo(systemSignalIdOffset);
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

	public Map<Long, BPSession> getUserId2SessionMap() {
		return userId2SessionMap;
	}

	public Map<Long, List<UserDevRelInfoInterface>> getUserId2UserDevRelInfoListMap() {
		return userId2UserDevRelInfoListMap;
	}
	
	public List<UserDevRelInfoInterface> getUserId2UserDevRelInfoList(Long userId) {
		// TODO:
		return null;
	}

	public Map<Long, List<UserDevRelInfoInterface>> getSnId2UserDevRelInfoListMap() {
		return snId2UserDevRelInfoListMap;
	}
	
	public List<UserDevRelInfoInterface> getSn2UserDevRelInfoList(Long snId) {
		return snId2UserDevRelInfoListMap.get(snId);
	}

	public long getDeviceUniqId(String sn, DeviceInfoUnit deviceInfoUnit) {
		if(!ifSnFormal(sn)) {
			return 0;
		}

		long deviceUniqId = 0;
		SnInfoHbn snInfoHbn = null;
		DevInfoHbn devInfoHbn = null;
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
		    snInfoHbn = (SnInfoHbn)session  
		            .createQuery(" from SnInfoHbn where sn = ? ")
		            .setParameter(0, sn)
		            .uniqueResult();  
		    if(snInfoHbn != null) {
			    devInfoHbn = (DevInfoHbn)session  
			            .createQuery(" from DevInfoHbn where snId = :sn_id ")
			            .setParameter("sn_id", snInfoHbn.getId())
			            .uniqueResult();  
			    if(null != devInfoHbn) {
			    	deviceUniqId = devInfoHbn.getId();
			    }
		    }
		    
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			deviceUniqId = 0;
		}
		if(null != deviceInfoUnit && deviceUniqId > 0) {
			deviceInfoUnit.setDevInfoHbn(devInfoHbn);
			deviceInfoUnit.setSnInfoHbn(snInfoHbn);
			
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
		List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList;
		while(itSI.hasNext()) {
			systemSignalEnumLangInfoList = null;
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
					ifConfigDef = systemSignalInfoHbn.getCustomFlags() == 0;
					SignalInterface systemSignalInterface = null;
					if(!ifConfigDef) {
						BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
						
						Transaction tx = null;
						switch(sysSigTab.getSysSigInfo(signalId - BPPacket.SYS_SIG_START_ID).getValType()) {
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
					/* TODO: set the systemSignalEnumLangInfoList */
					systemSignalInfoUnitLst.add(new SystemSignalInfoUnit(signalId, ifNotifying, ifConfigDef, systemSignalEnumLangInfoList, systemSignalInterface));
					break;
				}
			}
		}
		
		return systemSignalInfoUnitLst;
	}
	
	private void putLangIntoMap(Map<Integer, String> map, int langSupportMask, SignalLanguageInterface signalLanguageInterface) {
		int langMaskSet = 0;
		
		langMaskSet = 0x01 << BPLanguageId.CHINESE;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.CHINESE, signalLanguageInterface.getChinese());
		}
		langMaskSet = 0x01 << BPLanguageId.ENGLISH;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.ENGLISH, signalLanguageInterface.getEnglish());
		}
		langMaskSet = 0x01 << BPLanguageId.FRENCH;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.FRENCH, signalLanguageInterface.getFrench());
		}
		langMaskSet = 0x01 << BPLanguageId.RUSSIAN;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.RUSSIAN, signalLanguageInterface.getRussian());
		}
		langMaskSet = 0x01 << BPLanguageId.ARABIC;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.ARABIC, signalLanguageInterface.getArabic());
		}
		langMaskSet = 0x01 << BPLanguageId.SPANISH;
		if((langSupportMask & langMaskSet) == langMaskSet) {
			map.put(BPLanguageId.SPANISH, signalLanguageInterface.getSpanish());
		}

	}
	
	private Map<Integer, String> getCustomSignalNameLangMap(long customSignalNameLangId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customSignalNameLangId) {
			return null;
		}
		
		Map<Integer, String> customSignalLangMap = null;
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = (CustomSignalNameLangInfoHbn)session  
		    //        .createQuery("from CustomSignalNameLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		    //        .setParameter("id", custonSignalNameLangId).list();
			/*
			CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = session.get(CustomSignalNameLangInfoHbn.class, custonSignalNameLangId);
			if(null == customSignalNameLangInfoHbn) {
				tx.commit();
				return null;
			}
			*/
			CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn = session.get(CustomSignalNameLangEntityInfoHbn.class, customSignalNameLangId);
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
	
	private Map<Integer, String> getCustomUnitLangMap(long customUnitLangId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customUnitLangId) {
			return null;
		}
		Map<Integer, String> customUnitLangMap = null;
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = (CustomSignalNameLangInfoHbn)session  
		    //        .createQuery("from CustomSignalNameLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		    //        .setParameter("id", custonSignalNameLangId).list();
			/*
			CustomUnitLangInfoHbn customUnitLangInfoHbn = session.get(CustomUnitLangInfoHbn.class, customUnitLangId);
			if(null == customUnitLangInfoHbn) {
				tx.commit();
				return null;
			}
			*/
			CustomUnitLangEntityInfoHbn customUnitLangEntityInfoHbn = session.get(CustomUnitLangEntityInfoHbn.class, customUnitLangId);
			if(null == customUnitLangEntityInfoHbn) {
				tx.commit();
				return null;
			}
			customUnitLangMap = new HashMap<>();
			putLangIntoMap(customUnitLangMap, langSupportMask, customUnitLangEntityInfoHbn);
			
			/* TODO: add support for other language */
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		
		return customUnitLangMap;
	}
	
	private Map<Integer, String> getCustomGroupLangMap(long customGroupLangId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customGroupLangId) {
			return null;
		}
		Map<Integer, String> customGroupLangMap = null;
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = (CustomSignalNameLangInfoHbn)session  
		    //        .createQuery("from CustomSignalNameLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		    //        .setParameter("id", custonSignalNameLangId).list();
			/*
			CustomGroupLangInfoHbn customGroupLangInfoHbn = session.get(CustomGroupLangInfoHbn.class, customGroupLangId);
			if(null == customGroupLangInfoHbn) {
				tx.commit();
				return null;
			}
			*/
			CustomSignalGroupLangEntityInfoHbn customSignalGroupLangEntityInfoHbn = session.get(CustomSignalGroupLangEntityInfoHbn.class, customGroupLangId);
			if(null == customSignalGroupLangEntityInfoHbn) {
				tx.commit();
				return null;
			}
			customGroupLangMap = new HashMap<>();
			putLangIntoMap(customGroupLangMap, langSupportMask, customSignalGroupLangEntityInfoHbn);
			
			/* TODO: add support for other language */
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		
		return customGroupLangMap;
	}
	
	private Map<Integer, Map<Integer, String> > getCustomSignalEnumLangMap(long customSignalId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customSignalId) {
			return null;
		}
		Map<Integer, Map<Integer, String> > customSignalEnumLangMap = new HashMap<>();
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			CustomSignalEnumInfoHbn customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn)session  
		           .createQuery("from CustomSignalEnumInfoHbn where customSignalId = :custom_signal_id")
		           .setParameter("custom_signal_id", customSignalId).uniqueResult();
			if(null == customSignalEnumInfoHbn) {
				tx.commit();
				return null;
			}
			List<CustomSignalEnumLangInfoHbn> customSignalEnumLangInfoHbnList = session  
		           .createQuery("from CustomSignalEnumLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		           .setParameter("cus_sig_enm_id", customSignalEnumInfoHbn.getId()).list();
			if(null == customSignalEnumLangInfoHbnList) {
				tx.commit();
				return null;
			}
			Iterator<CustomSignalEnumLangInfoHbn> it = customSignalEnumLangInfoHbnList.iterator();
			while(it.hasNext()) {
				CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = it.next();
				CustomSignalEnumLangEntityInfoHbn customSignalEnumLangEntityInfoHbn = session.get(CustomSignalEnumLangEntityInfoHbn.class, customSignalEnumLangInfoHbn.getEnumValLangId());
				if(null == customSignalEnumLangEntityInfoHbn) {
					tx.commit();
					return null;
				}
				Map<Integer, String> customSignalEnumLangMapEntry = new HashMap<>();
				putLangIntoMap(customSignalEnumLangMapEntry, langSupportMask, customSignalEnumLangEntityInfoHbn);
				customSignalEnumLangMap.put(customSignalEnumLangInfoHbn.getEnumKey(), customSignalEnumLangMapEntry);
				
			}
			
			/* TODO: add support for other language */
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			customSignalEnumLangMap = null;
		}
		
		return customSignalEnumLangMap;
	}
	
	private Map<Integer, String> getCustomDefaultStringLangMap(long customDefaultStringLangId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customDefaultStringLangId) {
			return null;
		}
		Map<Integer, String> customDefaultStringLangMap = null;
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = (CustomSignalNameLangInfoHbn)session  
		    //        .createQuery("from CustomSignalNameLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
		    //        .setParameter("id", custonSignalNameLangId).list();
			CustomSignalStringDefaultValueEntityInfoHbn customSignalStringDefaultValueEntityInfoHbn = session.get(CustomSignalStringDefaultValueEntityInfoHbn.class, customDefaultStringLangId);
			if(null == customSignalStringDefaultValueEntityInfoHbn) {
				tx.commit();
				return null;
			}

			customDefaultStringLangMap = new HashMap<>();
			putLangIntoMap(customDefaultStringLangMap, langSupportMask, customSignalStringDefaultValueEntityInfoHbn);
			
			/* TODO: add support for other language */
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		
		return customDefaultStringLangMap;
	}
	
	private CustomAlarmInfoUnit getCustomSignalAlmInfoUnit(long customSignalId, int langSupportMask) {
		if(INVALID_LANGUAGE_ID == customSignalId) {
			return null;
		}
		
		CustomAlarmInfoUnit customAlarmInfoUnit = null;
		Map<Integer, String> customAlarmNameLangMap = null;
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			CustomSignalAlmInfoHbn customSignalAlmInfoHbn = (CustomSignalAlmInfoHbn)session  
		           .createQuery("from CustomSignalAlmInfoHbn where customSignalId = :custom_signal_id")
		           .setParameter("custom_signal_id", customSignalId).uniqueResult();
			if(null == customSignalAlmInfoHbn) {
				tx.commit();
				return null;
			}
			CustomAlarmNameLangEntityInfoHbn customAlarmNameLangEntityInfoHbn = session.get(CustomAlarmNameLangEntityInfoHbn.class, customSignalAlmInfoHbn.getCusSigNameLangId());
			if(null == customAlarmNameLangEntityInfoHbn) {
				tx.commit();
				return null;
			}
			customAlarmNameLangMap = new HashMap<>();
			putLangIntoMap(customAlarmNameLangMap, langSupportMask, customAlarmNameLangEntityInfoHbn);
			
			/* TODO: add support for other language */
			customAlarmInfoUnit = new CustomAlarmInfoUnit(customAlarmNameLangMap, customSignalAlmInfoHbn);
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			customAlarmInfoUnit = null;
		}
		
		return customAlarmInfoUnit;
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
		boolean ifDisplay;
		CustomAlarmInfoUnit customAlarmInfoUnit = null;
		Map<Integer, String> cusSignalNameLangMap = null;
		Map<Integer, String> cusSignalUnitLangMap = null;
		Map<Integer, String> cusSignalGroupLangMap = null;
		Map<Integer, Map<Integer, String> > cusSignalEnumLangMap = null;
		int groupLangId = BPPacket.INVALID_LANGUAGE_ID;
		
		while(itSI.hasNext()) {
			SignalInfoHbn signalInfoHbn = itSI.next();
			itCSI = customSignalInfoHbnLst.iterator();
			while(itCSI.hasNext()) {
				customAlarmInfoUnit = null;
				cusSignalNameLangMap = null;
				cusSignalUnitLangMap = null;
				cusSignalGroupLangMap = null;
				cusSignalEnumLangMap = null;
				groupLangId = BPPacket.INVALID_LANGUAGE_ID;
				CustomSignalInfoHbn customSignalInfoHbn = itCSI.next();

				if (customSignalInfoHbn.getSignalId() == signalInfoHbn.getId()) {
					signalId = signalInfoHbn.getSignalId();
					if (signalId < BPPacket.CUS_SIG_START_ID || signalId > BPPacket.CUS_SIG_END_ID) {
						return null;
					}
					ifNotifying = signalInfoHbn.getNotifying();
					ifDisplay = signalInfoHbn.getDisplay();
					ifAlarm = customSignalInfoHbn.getIfAlarm();
					if(ifAlarm) {
						customAlarmInfoUnit = getCustomSignalAlmInfoUnit(customSignalInfoHbn.getId(), langSupportMask);
					}
					
					SignalInterface signalInterface = null;
					cusSignalNameLangMap = getCustomSignalNameLangMap(customSignalInfoHbn.getCusSigNameLangId(), langSupportMask);
					cusSignalUnitLangMap = getCustomUnitLangMap(customSignalInfoHbn.getCusSigUnitLangId(), langSupportMask);
					cusSignalGroupLangMap = getCustomGroupLangMap(customSignalInfoHbn.getCusGroupLangId(), langSupportMask);
					
					if(BPPacket.VAL_TYPE_ENUM == customSignalInfoHbn.getValType()) {
						cusSignalEnumLangMap = getCustomSignalEnumLangMap(customSignalInfoHbn.getId(), langSupportMask);
					}

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
						return null;
					}

					customSignalInfoUnitLst.add(new CustomSignalInfoUnit(signalId, ifNotifying, ifAlarm, signalInfoHbn.getAlmClass(), signalInfoHbn.getAlmDlyBef(), signalInfoHbn.getAlmDlyAft(), ifDisplay, cusSignalNameLangMap, cusSignalUnitLangMap, cusSignalGroupLangMap, cusSignalEnumLangMap, signalInterface));
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
		Map<Integer, Integer> enumLangMap;
		while(itSSI.hasNext()) {
			SystemSignalInfoHbn systemSignalInfoHbn = itSSI.next();
			itSI = signalInfoHbnLst.iterator();
			
			while(itSI.hasNext()) {
				enumLangMap = null;
				SignalInfoHbn signalInfoHbn = itSI.next();
				
				if(systemSignalInfoHbn.getSignalId() == signalInfoHbn.getId()) {
					signalId = signalInfoHbn.getSignalId();
					if(signalId < BPPacket.SYS_SIG_START_ID || signalId > BPPacket.MAX_SIG_ID) {
						return null;
					}
					SignalInterface systemSignalInterface = null;
					BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();

					Transaction tx = null;
					switch (sysSigTab.getSysSigInfo(signalId - BPPacket.SYS_SIG_START_ID).getValType()) {
					case BPPacket.VAL_TYPE_UINT32:
						try (Session session = sessionFactory.openSession()) {
							tx = session.beginTransaction();
							systemSignalInterface = (SystemSignalU32InfoHbn) session
									.createQuery("from SystemSignalU32InfoHbn where systemSignalId = :system_signal_id")
									.setParameter("system_signal_id", systemSignalInfoHbn.getId()).uniqueResult();

							tx.commit();
						} catch (Exception e) {
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
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

							if ((systemSignalInfoHbn.getCustomFlags()
									& BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
								List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoHbnList = session
										.createQuery(
												"from SystemSignalEnumLangInfoHbn where sysSigEnmId = :sys_sig_enm_id")
										.setParameter("sys_sig_enm_id", systemSignalInterface.getId()).list();
								if(null != systemSignalEnumLangInfoHbnList && !systemSignalEnumLangInfoHbnList.isEmpty()) {
									int size = systemSignalEnumLangInfoHbnList.size();
									enumLangMap = new HashMap<>();
									SystemSignalEnumLangInfoHbn systemSignalEnumLangInfoHbnTmp;
									for(int i = 0; i < size; i++) {
										systemSignalEnumLangInfoHbnTmp = systemSignalEnumLangInfoHbnList.get(i);
										enumLangMap.put(systemSignalEnumLangInfoHbnTmp.getEnumKey(), systemSignalEnumLangInfoHbnTmp.getEnumVal());
									}
								}
							}
							tx.commit();
						} catch (Exception e) {
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
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
							Util.logger(logger, Util.ERROR, e);
							systemSignalInterface = null;
						}
						break;
					default:
						return null;
					}
					systemSignalCustomInfoUnitLst.add(new SystemSignalCustomInfoUnit(signalId, signalInfoHbn.getAlmClass(), signalInfoHbn.getAlmDlyBef(), signalInfoHbn.getAlmDlyAft(), systemSignalInfoHbn.getCustomFlags(), enumLangMap, systemSignalInterface));
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
			hql.append(" and customFlags <> 0");
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

			DevInfoHbn devInfoHbn = (DevInfoHbn) session.get(DevInfoHbn.class, uniqDevId);
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
    
    public Map<Long, Long> getDeviceIDMap(String userName) {

		if(null == userName) {
			return null;
		}
		List<DevInfoHbn> deviceInfoList = null;
		Map<Long, Long> deviceInfoMap = null;
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
			
			List<DevInfoHbn> deviceIdListAdminTmp = session  
		            .createQuery("from DevInfoHbn where adminId = :user_id")
		            .setParameter("user_id", userId).list();
			List<Long> snIdListTmp = session  
		            .createQuery("select snId from UserDevRelInfoHbn where userId = :user_id")
		            .setParameter("user_id", userId).list();
			
			List<DevInfoHbn> deviceInfoListTmp = null;
			Iterator<Long> itSn = snIdListTmp.iterator();
			Long snTmp;
			DevInfoHbn deviceInfoTmp;
			while(itSn.hasNext()) {
				snTmp = itSn.next();
				deviceInfoTmp = (DevInfoHbn)session.createQuery("from DevInfoHbn where snId = :sn_id and adminId <> :admin_id")
				.setParameter("sn_id", snTmp)
				.setParameter("admin_id", userId).uniqueResult();
				if(null != deviceInfoTmp) {
					if(null == deviceInfoListTmp) {
						deviceInfoListTmp = new ArrayList<>();
					}
					deviceInfoListTmp.add(deviceInfoTmp);
				}
	
			}
			
			if(null == deviceIdListAdminTmp && null == deviceInfoListTmp) {
				return null;
			} else if(null == deviceIdListAdminTmp) {
				deviceInfoList = deviceInfoListTmp;
			} else if(null == deviceInfoListTmp) {
				deviceInfoList = deviceIdListAdminTmp;
			} else {
				deviceIdListAdminTmp.addAll(deviceInfoListTmp);
				deviceInfoList = deviceIdListAdminTmp;
			}
			
			deviceInfoMap = new HashMap<>();
			Iterator<DevInfoHbn> devInfoIt = deviceInfoList.iterator();
			while(devInfoIt.hasNext()) {
				deviceInfoTmp = devInfoIt.next();
				deviceInfoMap.put(deviceInfoTmp.getId(), deviceInfoTmp.getSigMapChksum());
			}
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			deviceInfoMap = null;
		}
    	return deviceInfoMap;
    }
    
    public int getDeviceLangSupportMask(long uniqDevId) {

    	int ret = 0;
		if(uniqDevId < 0) {
			return ret;
		}

		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			ret = (Short)session /* tinyint unsigned must be cast to short */
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

			DevInfoHbn devInfoHbn = (DevInfoHbn) session.get(DevInfoHbn.class, uniqDevId);

			if (null == devInfoHbn) {
				logger.error("Internal error: null == devInfoHbn");
				return ret;
			}
			if(checksum != devInfoHbn.getSigMapChksum()) {
				devInfoHbn.setSigMapChksum(checksum);
				session.update(devInfoHbn);
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
				signalInfoHbn.setSignalId(systemSignalEnabledList.get(i) + BPPacket.SYS_SIG_START_ID);
				signalInfoHbn.setDevId(uniqDevId);
				session.saveOrUpdate(signalInfoHbn);
				
				SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn) session
						.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id", signalInfoHbn.getId()).uniqueResult();
				if(null == systemSignalInfoHbn) {
					systemSignalInfoHbn = new SystemSignalInfoHbn(signalInfoHbn.getId());
					session.save(systemSignalInfoHbn);
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

			Map<String, CustomSignalGroupLangEntityInfoHbn> customSignalGroupLangEntityInfoHbnMap = new HashMap<>();
			Map<String, CustomUnitLangEntityInfoHbn> customUnitLangEntityInfoHbnMap = new HashMap<>();
			
			for(int i = 0; i < customSignalUnitInfoList.size(); i++) {
				CustomSignalInfoUnit customSignalInfoUnit = customSignalUnitInfoList.get(i); 
				int valueType = customSignalInfoUnit.getCustomSignalInterface().getValType();
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn) session
						.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
						.setParameter("signal_id", customSignalInfoUnit.getCusSigId())
						.setParameter("dev_id", uniqDevId).uniqueResult();
				if (null == signalInfoHbn) {
					signalInfoHbn = new SignalInfoHbn();
					signalInfoHbn.setSignalId(customSignalInfoUnit.getCusSigId());
					signalInfoHbn.setDevId(uniqDevId);
				}
	
				signalInfoHbn.setDisplay(customSignalInfoUnit.ifDisplay());
				signalInfoHbn.setNotifying(customSignalInfoUnit.ifNotifying());
				signalInfoHbn.setAlmClass(customSignalInfoUnit.getAlarmClass());
				signalInfoHbn.setAlmDlyBef(customSignalInfoUnit.getAlarmDelayBef());
				signalInfoHbn.setAlmDlyAft(customSignalInfoUnit.getAlarmDelayAft());
				session.saveOrUpdate(signalInfoHbn);
				
				Long signalKeyId = signalInfoHbn.getId();
				CustomSignalInfoHbn customSignalInfoHbn = (CustomSignalInfoHbn)session
						.createQuery("from CustomSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id",  signalKeyId).uniqueResult();
				if(null == customSignalInfoHbn) {
					customSignalInfoHbn = new CustomSignalInfoHbn();
					customSignalInfoHbn.setSignalId(signalKeyId);
				}
				customSignalInfoHbn.setIfAlarm(customSignalInfoUnit.isIfAlarm());
				customSignalInfoHbn.setValType((short)(customSignalInfoUnit.getCustomSignalInterface().getValType() & 0xFFFF));
				Map<Integer, String> langMap = customSignalInfoUnit.getSignalNameLangMap();
				if(null == langMap || langMap.isEmpty()) {
					customSignalInfoHbn.setCusSigNameLangId(0L);
				} else {
					Iterator<Map.Entry<Integer, String> > itLangEntity = langMap.entrySet().iterator();
					CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn = new CustomSignalNameLangEntityInfoHbn();
					while(itLangEntity.hasNext()) {
						Map.Entry<Integer, String> entry = itLangEntity.next();
						switch(entry.getKey()) {
						case BPLanguageId.CHINESE:
							customSignalNameLangEntityInfoHbn.setChinese(entry.getValue());
							break;
						case BPLanguageId.ENGLISH:
							customSignalNameLangEntityInfoHbn.setEnglish(entry.getValue());
							break;
						case BPLanguageId.FRENCH:
							customSignalNameLangEntityInfoHbn.setFrench(entry.getValue());
							break;
						case BPLanguageId.RUSSIAN:
							customSignalNameLangEntityInfoHbn.setRussian(entry.getValue());
							break;
						case BPLanguageId.ARABIC:
							customSignalNameLangEntityInfoHbn.setArabic(entry.getValue());
							break;
						case BPLanguageId.SPANISH:
							customSignalNameLangEntityInfoHbn.setSpanish(entry.getValue());
							break;
						}
					}
					session.save(customSignalNameLangEntityInfoHbn);
					/*
					CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = new CustomSignalNameLangInfoHbn(customSignalNameLangEntityInfoHbn.getId()); 
					session.save(customSignalNameLangInfoHbn);
					customSignalInfoHbn.setCusSigNameLangId(customSignalNameLangInfoHbn.getId());
					*/
					customSignalInfoHbn.setCusSigNameLangId(customSignalNameLangEntityInfoHbn.getId());
				}
				langMap = customSignalInfoUnit.getGroupLangMap();
				if(null == langMap || langMap.isEmpty()) {
					customSignalInfoHbn.setCusGroupLangId(0L);
				} else {
					Iterator<Map.Entry<Integer, String> > itLangEntity = langMap.entrySet().iterator();
					CustomSignalGroupLangEntityInfoHbn customSignalGroupLangEntityInfoHbn = new CustomSignalGroupLangEntityInfoHbn();
					while(itLangEntity.hasNext()) {
						Map.Entry<Integer, String> entry = itLangEntity.next();
						switch(entry.getKey()) {
						case BPLanguageId.CHINESE:
							customSignalGroupLangEntityInfoHbn.setChinese(entry.getValue());
							break;
						case BPLanguageId.ENGLISH:
							customSignalGroupLangEntityInfoHbn.setEnglish(entry.getValue());
							break;
						case BPLanguageId.FRENCH:
							customSignalGroupLangEntityInfoHbn.setFrench(entry.getValue());
							break;
						case BPLanguageId.RUSSIAN:
							customSignalGroupLangEntityInfoHbn.setRussian(entry.getValue());
							break;
						case BPLanguageId.ARABIC:
							customSignalGroupLangEntityInfoHbn.setArabic(entry.getValue());
							break;
						case BPLanguageId.SPANISH:
							customSignalGroupLangEntityInfoHbn.setSpanish(entry.getValue());
							break;
						}
					}
					if(customSignalGroupLangEntityInfoHbnMap.containsKey(customSignalGroupLangEntityInfoHbn.toString())) {
						customSignalGroupLangEntityInfoHbn = customSignalGroupLangEntityInfoHbnMap.get(customSignalGroupLangEntityInfoHbn.toString());
					} else {
						customSignalGroupLangEntityInfoHbnMap.put(customSignalGroupLangEntityInfoHbn.toString(), customSignalGroupLangEntityInfoHbn);
						session.save(customSignalGroupLangEntityInfoHbn);
					}
					/*
					CustomGroupLangInfoHbn customGroupLangInfoHbn = new CustomGroupLangInfoHbn(customSignalGroupLangEntityInfoHbn.getId()); 
					session.save(customGroupLangInfoHbn);
					customSignalInfoHbn.setCusGroupLangId(customGroupLangInfoHbn.getId());
					*/
					customSignalInfoHbn.setCusGroupLangId(customSignalGroupLangEntityInfoHbn.getId());
				}
				langMap = customSignalInfoUnit.getSignalUnitLangMap();
				if(null == langMap || langMap.isEmpty()) {
					customSignalInfoHbn.setCusSigUnitLangId(0L);
				} else {
					Iterator<Map.Entry<Integer, String> > itLangEntity = langMap.entrySet().iterator();
					CustomUnitLangEntityInfoHbn customUnitLangEntityInfoHbn = new CustomUnitLangEntityInfoHbn();
					while(itLangEntity.hasNext()) {
						Map.Entry<Integer, String> entry = itLangEntity.next();
						switch(entry.getKey()) {
						case BPLanguageId.CHINESE:
							customUnitLangEntityInfoHbn.setChinese(entry.getValue());
							break;
						case BPLanguageId.ENGLISH:
							customUnitLangEntityInfoHbn.setEnglish(entry.getValue());
							break;
						case BPLanguageId.FRENCH:
							customUnitLangEntityInfoHbn.setFrench(entry.getValue());
							break;
						case BPLanguageId.RUSSIAN:
							customUnitLangEntityInfoHbn.setRussian(entry.getValue());
							break;
						case BPLanguageId.ARABIC:
							customUnitLangEntityInfoHbn.setArabic(entry.getValue());
							break;
						case BPLanguageId.SPANISH:
							customUnitLangEntityInfoHbn.setSpanish(entry.getValue());
							break;
						}
					}
					
					if(customUnitLangEntityInfoHbnMap.containsKey(customUnitLangEntityInfoHbn.toString())) {
						customUnitLangEntityInfoHbn = customUnitLangEntityInfoHbnMap.get(customUnitLangEntityInfoHbn.toString());
					} else {
						customUnitLangEntityInfoHbnMap.put(customUnitLangEntityInfoHbn.toString(), customUnitLangEntityInfoHbn);
						session.save(customUnitLangEntityInfoHbn);
					}
					
					/*
					CustomUnitLangInfoHbn customUnitLangInfoHbn = new CustomUnitLangInfoHbn(customUnitLangEntityInfoHbn.getId()); 
					session.save(customUnitLangInfoHbn);
					customSignalInfoHbn.setCusSigUnitLangId(customUnitLangInfoHbn.getId());
					*/
					customSignalInfoHbn.setCusSigUnitLangId(customUnitLangEntityInfoHbn.getId());
				}

				session.saveOrUpdate(customSignalInfoHbn);
				
				SignalInterface signalInterface = customSignalInfoUnit.getCustomSignalInterface();
				signalInterface.setCustomSignalId(customSignalInfoHbn.getId());
				if(signalInterface.saveToDb(session) < 0) {
					logger.error("Internal error: null == devInfoHbn");
					return ret;
				}
				
				if(BPPacket.VAL_TYPE_ENUM == valueType) {
					Map<Integer, Map<Integer, String> > enumLangMap = customSignalInfoUnit.getSignalEnumLangMap();
					if(null != enumLangMap && !enumLangMap.isEmpty()) {
						Iterator<Map.Entry<Integer, Map<Integer, String> > > enumLangIterator = enumLangMap.entrySet().iterator();
						Map.Entry<Integer, Map<Integer, String>> enumLangEntry;
						Iterator<Map.Entry<Integer, String>> langIterator;
						Map.Entry<Integer, String> langEntry;
						while(enumLangIterator.hasNext()) {
							enumLangEntry = enumLangIterator.next();
							langIterator = enumLangEntry.getValue().entrySet().iterator();
							CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = new CustomSignalEnumLangInfoHbn(); 
							CustomSignalEnumLangEntityInfoHbn customSignalEnumLangEntityInfoHbn = new CustomSignalEnumLangEntityInfoHbn();
							while(langIterator.hasNext()) {
								langEntry = langIterator.next();
								switch(langEntry.getKey()) {
								case BPLanguageId.CHINESE:
									customSignalEnumLangEntityInfoHbn.setChinese(langEntry.getValue());
									break;
								case BPLanguageId.ENGLISH:
									customSignalEnumLangEntityInfoHbn.setEnglish(langEntry.getValue());
									break;
								case BPLanguageId.FRENCH:
									customSignalEnumLangEntityInfoHbn.setFrench(langEntry.getValue());
									break;
								case BPLanguageId.RUSSIAN:
									customSignalEnumLangEntityInfoHbn.setRussian(langEntry.getValue());
									break;
								case BPLanguageId.ARABIC:
									customSignalEnumLangEntityInfoHbn.setArabic(langEntry.getValue());
									break;
								case BPLanguageId.SPANISH:
									customSignalEnumLangEntityInfoHbn.setSpanish(langEntry.getValue());
									break;
								}
							}
							session.save(customSignalEnumLangEntityInfoHbn);
							customSignalEnumLangInfoHbn.setEnumKey(enumLangEntry.getKey());
							customSignalEnumLangInfoHbn.setEnumValLangId(customSignalEnumLangEntityInfoHbn.getId());
							customSignalEnumLangInfoHbn.setCusSigEnmId(signalInterface.getId());
							session.save(customSignalEnumLangInfoHbn);
						}
					}
				}
				
			}

			tx.commit();
			ret = true;
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = false;
		}
    	return ret;
    }
    
    public boolean putSystemCustomSignalInfoMap(long uniqDevId, List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitList) {
    	boolean ret = false;
    	if(null == systemSignalCustomInfoUnitList) {
    		return ret;
    	}
    	
		Transaction tx = null;
		int customInfo;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			SystemSignalCustomInfoUnit systemSignalCustomInfoUnitTmp;
			for(int i = 0; i < systemSignalCustomInfoUnitList.size(); i++) {
				systemSignalCustomInfoUnitTmp = systemSignalCustomInfoUnitList.get(i);
				SignalInfoHbn signalInfoHbn = (SignalInfoHbn) session
						.createQuery("from SignalInfoHbn where signalId = :signal_id and devId = :dev_id")
						.setParameter("signal_id", systemSignalCustomInfoUnitTmp.getSysSigId())
						.setParameter("dev_id", uniqDevId).uniqueResult();
				if (null == signalInfoHbn) {
					logger.error("Internal error: null == signalInfoHbn");
					return ret;
				}
				
				Long signalKeyId = signalInfoHbn.getId();
				SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn)session
						.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
						.setParameter("signal_id",  signalKeyId).uniqueResult();
				if(null == systemSignalInfoHbn) {
					logger.error("Internal error: null == systemSignalInfoHbn");
					return ret;
				}
				customInfo = systemSignalCustomInfoUnitTmp.getCustomFlags();

				systemSignalInfoHbn.setCustomFlags(customInfo);
				
				session.update(systemSignalInfoHbn);
				
				SignalInterface signalInterface = systemSignalCustomInfoUnitTmp.getSignalInterface();
				signalInterface.setSystemSignalId(systemSignalInfoHbn.getId());
				if(signalInterface.saveToDb(session) < 0) {
					logger.error("Internal error: null == devInfoHbn");
					return ret;
				}
				
				if ((customInfo & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
					Map<Integer, Integer> enumLangMap = systemSignalCustomInfoUnitTmp.getEnumLangMap();
					if(null != enumLangMap) {
						Iterator<Map.Entry<Integer, Integer>> it = enumLangMap.entrySet().iterator();
						while(it.hasNext()) {
							Map.Entry<Integer, Integer> entry = it.next();
							session.save(new SystemSignalEnumLangInfoHbn(entry.getKey(), entry.getValue(), signalInterface.getId()));
						}
					}
				}
				if ((customInfo & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM) != 0) {
					if((customInfo & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_CLASS) != 0) {
						signalInfoHbn.setAlmClass(systemSignalCustomInfoUnitTmp.getAlarmClass());
					}
					if((customInfo & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_BEF) != 0) {
						signalInfoHbn.setAlmDlyBef(systemSignalCustomInfoUnitTmp.getDelayBeforeAlarm());
					}
					if((customInfo & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_AFT) != 0) {
						signalInfoHbn.setAlmDlyAft(systemSignalCustomInfoUnitTmp.getDelayAfterAlarm());
					}
				}
				
				session.update(signalInfoHbn);
			}

			tx.commit();
			ret = true;
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
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
	
    
    public void clearDeviceSignalInfo(Long uniqDevId) {
		if(uniqDevId <= 0) {
			return;
		}
		  
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			DevInfoHbn devInfoHbn = session.get(DevInfoHbn.class, uniqDevId);
			if(null == devInfoHbn) {
				return;
			}
			List<SignalInfoHbn> signalInfoHbnList = session  
		            .createQuery("from SignalInfoHbn where devId = :dev_id")
		            .setParameter("dev_id", uniqDevId).list();
			if(null == signalInfoHbnList) {
				return;
			}
			Iterator<SignalInfoHbn> it = signalInfoHbnList.iterator();
			while(it.hasNext()) {
				SignalInfoHbn signalInfoHbn = it.next();
				try {
					if (signalInfoHbn.getSignalId() >= 0 && signalInfoHbn.getSignalId() < BPPacket.SYS_SIG_START_ID) {
						CustomSignalInfoHbn customSignalInfoHbn = (CustomSignalInfoHbn) session
								.createQuery("from CustomSignalInfoHbn where signalId = :signal_id")
								.setParameter("signal_id", signalInfoHbn.getId()).uniqueResult();
						if (null == customSignalInfoHbn) {
							session.delete(signalInfoHbn);
							continue;
						}
						if (null != customSignalInfoHbn) {
							if (0 != customSignalInfoHbn.getCusSigNameLangId()) {
								/*
								CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = session.get(
										CustomSignalNameLangInfoHbn.class, customSignalInfoHbn.getCusSigNameLangId());
								if (null != customSignalNameLangInfoHbn) {
									CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn = session.get(
											CustomSignalNameLangEntityInfoHbn.class,
											customSignalNameLangInfoHbn.getCustomSignalName());
									if (null != customSignalNameLangEntityInfoHbn) {
										session.delete(customSignalNameLangEntityInfoHbn);
									}
									session.delete(customSignalNameLangInfoHbn);
								}
								*/
								
								CustomSignalNameLangEntityInfoHbn customSignalNameLangEntityInfoHbn = session.get(CustomSignalNameLangEntityInfoHbn.class,
										customSignalInfoHbn.getCusSigNameLangId());
								if (null != customSignalNameLangEntityInfoHbn) {
									session.delete(customSignalNameLangEntityInfoHbn);
								}
							}
							if (0 != customSignalInfoHbn.getCusGroupLangId()) {
								/*
								CustomGroupLangInfoHbn customGroupLangInfoHbn = session
										.get(CustomGroupLangInfoHbn.class, customSignalInfoHbn.getCusGroupLangId());
								if (null != customGroupLangInfoHbn) {
									CustomSignalGroupLangEntityInfoHbn customSignalGroupLangEntityInfoHbn = session.get(
											CustomSignalGroupLangEntityInfoHbn.class,
											customGroupLangInfoHbn.getGroupLang());
									if (null != customSignalGroupLangEntityInfoHbn) {
										session.delete(customSignalGroupLangEntityInfoHbn);
									}
									session.delete(customGroupLangInfoHbn);
								}
								*/
								CustomSignalGroupLangEntityInfoHbn customSignalGroupLangEntityInfoHbn = session.get(CustomSignalGroupLangEntityInfoHbn.class,
										customSignalInfoHbn.getCusGroupLangId());
								if (null != customSignalGroupLangEntityInfoHbn) {
									session.delete(customSignalGroupLangEntityInfoHbn);
								}
							}
							if (0 != customSignalInfoHbn.getCusSigUnitLangId()) {
								/*
								CustomUnitLangInfoHbn customUnitLangInfoHbn = session.get(CustomUnitLangInfoHbn.class,
										customSignalInfoHbn.getCusSigUnitLangId());
								if (null != customUnitLangInfoHbn) {
									CustomUnitLangEntityInfoHbn customUnitLangEntityInfoHbn = session.get(
											CustomUnitLangEntityInfoHbn.class, customUnitLangInfoHbn.getUnitLang());
									if (null != customUnitLangEntityInfoHbn) {
										session.delete(customUnitLangEntityInfoHbn);
									}
									session.delete(customSignalInfoHbn);
								}
								*/
								CustomUnitLangEntityInfoHbn customUnitLangEntityInfoHbn = session.get(CustomUnitLangEntityInfoHbn.class,
										customSignalInfoHbn.getCusSigUnitLangId());
								if (null != customUnitLangEntityInfoHbn) {
									session.delete(customUnitLangEntityInfoHbn);
								}
							}
							switch (customSignalInfoHbn.getValType()) {
							case BPPacket.VAL_TYPE_UINT32: {
								CustomSignalU32InfoHbn customSignalU32InfoHbn = (CustomSignalU32InfoHbn) session
										.createQuery(
												"from CustomSignalU32InfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalU32InfoHbn) {
									session.delete(customSignalU32InfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_UINT16: {
								CustomSignalU16InfoHbn customSignalU16InfoHbn = (CustomSignalU16InfoHbn) session
										.createQuery(
												"from CustomSignalU16InfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalU16InfoHbn) {
									session.delete(customSignalU16InfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_IINT32: {
								CustomSignalI32InfoHbn customSignalI32InfoHbn = (CustomSignalI32InfoHbn) session
										.createQuery(
												"from CustomSignalI32InfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalI32InfoHbn) {
									session.delete(customSignalI32InfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_IINT16: {
								CustomSignalI16InfoHbn customSignalI16InfoHbn = (CustomSignalI16InfoHbn) session
										.createQuery(
												"from CustomSignalI16InfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalI16InfoHbn) {
									session.delete(customSignalI16InfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_ENUM: {
								CustomSignalEnumInfoHbn customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn) session
										.createQuery(
												"from CustomSignalEnumInfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalEnumInfoHbn) {
									List<CustomSignalEnumLangInfoHbn> customSignalEnumLangInfoHbnList = session
											.createQuery(
													"from CustomSignalEnumLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
											.setParameter("cus_sig_enm_id", customSignalEnumInfoHbn.getId()).list();
									if (null != customSignalEnumLangInfoHbnList) {
										Iterator<CustomSignalEnumLangInfoHbn> itCustomSignalEnumLangInfoHbn = customSignalEnumLangInfoHbnList
												.iterator();
										while (itCustomSignalEnumLangInfoHbn.hasNext()) {
											CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = itCustomSignalEnumLangInfoHbn
													.next();
											CustomSignalEnumLangEntityInfoHbn customSignalEnumLangEntityInfoHbn = session
													.get(CustomSignalEnumLangEntityInfoHbn.class,
															customSignalEnumLangInfoHbn.getEnumValLangId());
											if (null != customSignalEnumLangEntityInfoHbn) {
												session.delete(customSignalEnumLangEntityInfoHbn);
											}
											session.delete(customSignalEnumLangInfoHbn);
										}

									}

									session.delete(customSignalEnumInfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_FLOAT: {
								CustomSignalFloatInfoHbn customSignalFloatInfoHbn = (CustomSignalFloatInfoHbn) session
										.createQuery(
												"from CustomSignalFloatInfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalFloatInfoHbn) {
									session.delete(customSignalFloatInfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_STRING: {
								CustomSignalStringInfoHbn customSignalStringInfoHbn = (CustomSignalStringInfoHbn) session
										.createQuery(
												"from CustomSignalStringInfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalStringInfoHbn) {
									session.delete(customSignalStringInfoHbn);
								}
								break;
							}
							case BPPacket.VAL_TYPE_BOOLEAN: {
								CustomSignalBooleanInfoHbn customSignalBooleanInfoHbn = (CustomSignalBooleanInfoHbn) session
										.createQuery(
												"from CustomSignalBooleanInfoHbn where customSignalId = :custom_signal_id")
										.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
								if (null != customSignalBooleanInfoHbn) {
									session.delete(customSignalBooleanInfoHbn);
								}
								break;
							}
							default:
								// do nothing
							}
							session.delete(customSignalInfoHbn);
						}

					} else if (signalInfoHbn.getSignalId() >= BPPacket.SYS_SIG_START_ID
							&& signalInfoHbn.getSignalId() <= BPPacket.MAX_SIG_ID) {
						SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn) session
								.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
								.setParameter("signal_id", signalInfoHbn.getId()).uniqueResult();
						if (null != systemSignalInfoHbn) {
							if (systemSignalInfoHbn.getCustomFlags() != 0) {
								SysSigInfo sysSigInfo = BPSysSigTable.getSysSigTableInstance()
										.getSysSigInfo(signalInfoHbn.getSignalId() - BPPacket.SYS_SIG_START_ID);
								if (null != sysSigInfo) {
									short sigType = sysSigInfo.getValType();
									switch (sigType) {
									case BPPacket.VAL_TYPE_UINT32: {
										SystemSignalU32InfoHbn systemSignalU32InfoHbn = (SystemSignalU32InfoHbn) session
												.createQuery(
														"from SystemSignalU32InfoHbn where systemSignalId = :system_signal_id")
												.setParameter("custom_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalU32InfoHbn) {
											session.delete(systemSignalU32InfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_UINT16: {
										SystemSignalU16InfoHbn systemSignalU16InfoHbn = (SystemSignalU16InfoHbn) session
												.createQuery(
														"from SystemSignalU16InfoHbn where systemSignalId = :system_signal_id")
												.setParameter("system_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalU16InfoHbn) {
											session.delete(systemSignalU16InfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_IINT32: {
										SystemSignalI32InfoHbn systemSignalI32InfoHbn = (SystemSignalI32InfoHbn) session
												.createQuery(
														"from SystemSignalI32InfoHbn where systemSignalId = :system_signal_id")
												.setParameter("system_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalInfoHbn) {
											session.delete(systemSignalInfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_IINT16: {
										SystemSignalI16InfoHbn systemSignalI16InfoHbn = (SystemSignalI16InfoHbn) session
												.createQuery(
														"from SystemSignalI16InfoHbn where systemSignalId = :system_signal_id")
												.setParameter("custom_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalI16InfoHbn) {
											session.delete(systemSignalI16InfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_ENUM: {
										SystemSignalEnumInfoHbn systemSignalEnumInfoHbn = (SystemSignalEnumInfoHbn) session
												.createQuery(
														"from SystemSignalEnumInfoHbn where systemSignalId = :system_signal_id")
												.setParameter("system_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalEnumInfoHbn) {
											List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoHbnList = session
													.createQuery(
															"from SystemSignalEnumLangInfoHbn where sysSigEnmId = :cus_sig_enm_id")
													.setParameter("cus_sig_enm_id", systemSignalEnumInfoHbn.getId())
													.list();
											if (null != systemSignalEnumLangInfoHbnList) {
												Iterator<SystemSignalEnumLangInfoHbn> itSystemSignalEnumLangInfoHbn = systemSignalEnumLangInfoHbnList
														.iterator();
												while (itSystemSignalEnumLangInfoHbn.hasNext()) {
													SystemSignalEnumLangInfoHbn systemSignalEnumLangInfoHbn = itSystemSignalEnumLangInfoHbn
															.next();
													session.delete(systemSignalEnumLangInfoHbn);
												}

											}

											session.delete(systemSignalEnumInfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_FLOAT: {
										SystemSignalFloatInfoHbn systemSignalFloatInfoHbn = (SystemSignalFloatInfoHbn) session
												.createQuery(
														"from SystemSignalFloatInfoHbn where systemSignalId = :system_signal_id")
												.setParameter("system_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalFloatInfoHbn) {
											session.delete(systemSignalFloatInfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_STRING: {
										SystemSignalStringInfoHbn systemSignalStringInfoHbn = (SystemSignalStringInfoHbn) session
												.createQuery(
														"from SystemSignalStringInfoHbn where systemSignalId = :system_signal_id")
												.setParameter("system_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalStringInfoHbn) {
											session.delete(systemSignalStringInfoHbn);
										}
										break;
									}
									case BPPacket.VAL_TYPE_BOOLEAN: {
										SystemSignalBooleanInfoHbn systemSignalBooleanInfoHbn = (SystemSignalBooleanInfoHbn) session
												.createQuery(
														"from SystemSignalBooleanInfoHbn where systemSignalId = :system_signal_id")
												.setParameter("custom_signal_id", systemSignalInfoHbn.getId())
												.uniqueResult();
										if (null != systemSignalBooleanInfoHbn) {
											session.delete(systemSignalBooleanInfoHbn);
										}
										break;
									}
									default:
										// do nothing
									}
								}

							}
							session.delete(systemSignalInfoHbn);
						}
					}
				} catch (Exception e) {
					Util.logger(logger, Util.ERROR, e);
				}
				session.delete(signalInfoHbn);

			}

			devInfoHbn.setSigMapChksum(INVALID_SIG_MAP_CHECKSUM);
			session.saveOrUpdate(devInfoHbn);
			tx.commit();
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
    }
    
    private boolean saveLanguageLang(Map<Integer, String> langMap) {
    	return false;
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

    public boolean getSignalInfoUnitInterfaceMap(BPDeviceSession bpDeviceSession) {
    	boolean ret = false;
    	if(null == bpDeviceSession) {
			return ret;
		}
    	long devUniqId = bpDeviceSession.getUniqDevId();
    	if(devUniqId <= 0) {
    		return ret;
    	}
    	Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap = new HashMap<>();

		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();

			List<SignalInfoHbn> signalInfoHbnList = session
					.createQuery("from SignalInfoHbn where devId = :dev_id")
					.setParameter("dev_id", devUniqId).list();
			
			Iterator<SignalInfoHbn> itSih = signalInfoHbnList.iterator();
			BPSysSigTable bpSysSigTable = BPSysSigTable.getSysSigTableInstance();
			int signalIdTmp;
			SysSigInfo sysSigInfoTmp;
			short valueType;
			SignalInterface signalInterfaceTmp;
			String tableName;
	    	StringBuilder hql = new StringBuilder();
	    	String tag;
	    	List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList;
			// CustomSignalNameLangInfoHbn customSignalNameLangInfoHbn = null;
			// CustomGroupLangInfoHbn customGroupLangInfoHbn = null;
			// CustomUnitLangInfoHbn customUnitLangInfoHbn = null;
			CustomSignalEnumInfoHbn customSignalEnumInfoHbn = null;
			while(itSih.hasNext()) {
				signalInterfaceTmp = null;
				SignalInfoHbn signalInfoHbn = itSih.next();
				signalIdTmp = signalInfoHbn.getSignalId();
				if(signalIdTmp < BPPacket.SYS_SIG_START_ID) {
					/* "from CustomSignalInfoHbn where signalId=:signal_id" */
					hql.setLength(0);
					hql.append("from ");
					tableName = "CustomSignalInfoHbn";
					hql.append(tableName);
					tag = "signal_id";
					hql.append(" where signalId=:");
					hql.append(tag);
					CustomSignalInfoHbn customSignalInfoHbn = (CustomSignalInfoHbn)session
							.createQuery(hql.toString())
							.setParameter(tag, signalInfoHbn.getId()).uniqueResult();
					if(null == customSignalInfoHbn) {
						logger.error("Inner error: null == customSignalInfoHbn");
						return ret;
					}
					valueType = customSignalInfoHbn.getValType();
					signalInterfaceTmp = null;
			    	if(!BPPacket.ifSigTypeValid(valueType)) {
			    		logger.error("Inner error: !BPPacket.ifSigTypeValid(valueType)");
			    		return ret;
			    	}
			    	/* construct a hql like "from <table> where customSignalId=:custom_signal_id"*/
					hql.setLength(0);
					hql.append("from ");
					switch(valueType) {
					case BPPacket.VAL_TYPE_UINT32:
						tableName = "CustomSignalU32InfoHbn";
						break;
					case BPPacket.VAL_TYPE_UINT16:
						tableName = "CustomSignalU16InfoHbn";
						break;
					case BPPacket.VAL_TYPE_IINT32:
						tableName = "CustomSignalI32InfoHbn";
						break;
					case BPPacket.VAL_TYPE_IINT16:
						tableName = "CustomSignalI16InfoHbn";
						break;
					case BPPacket.VAL_TYPE_ENUM:
						tableName = "CustomSignalEnumInfoHbn";
						break;
					case BPPacket.VAL_TYPE_FLOAT:
						tableName = "CustomSignalFloatInfoHbn";
						break;
					case BPPacket.VAL_TYPE_STRING:
						tableName = "CustomSignalStringInfoHbn";
						break;
					case BPPacket.VAL_TYPE_BOOLEAN:
						tableName = "CustomSignalBooleanInfoHbn";
						break;
					default:
						logger.error("Inner error: invalid value type");
						return ret;
					}
					hql.append(tableName);
					tag = "custom_signal_id";
					hql.append(" where customSignalId=:");
					hql.append(tag);
					
					signalInterfaceTmp = (SignalInterface)session.createQuery(hql.toString())
				            .setParameter(tag, customSignalInfoHbn.getId()).uniqueResult();
					if(null == signalInterfaceTmp) {
						logger.error("Inner error: null == signalInterfaceTmp");
						return ret;
					}
					/*
					customSignalNameLangInfoHbn = null;
					customGroupLangInfoHbn = null;
					customUnitLangInfoHbn = null;
					customSignalEnumInfoHbn = null;
				
					if(0 != customSignalInfoHbn.getCusSigNameLangId()) {
						customSignalNameLangInfoHbn = session.get(CustomSignalNameLangInfoHbn.class,  customSignalInfoHbn.getCusSigNameLangId());
					}
					if(0 != customSignalInfoHbn.getCusGroupLangId()) {
						customGroupLangInfoHbn = session.get(CustomGroupLangInfoHbn.class,  customSignalInfoHbn.getCusGroupLangId());
					}
					if(0 != customSignalInfoHbn.getCusSigUnitLangId()) {
						customUnitLangInfoHbn = session.get(CustomUnitLangInfoHbn.class,  customSignalInfoHbn.getCusSigUnitLangId());
					}
					if (valueType == BPPacket.VAL_TYPE_ENUM) {
						customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn) session
								.createQuery("from CustomSignalEnumInfoHbn where customSignalId = :custom_signal_id")
								.setParameter("custom_signal_id", customSignalInfoHbn.getId()).uniqueResult();
						if (null != customSignalEnumInfoHbn) {
							List<CustomSignalEnumLangInfoHbn> customSignalEnumLangInfoHbnList = session
									.createQuery("from CustomSignalEnumLangInfoHbn where cusSigEnmId = :cus_sig_enm_id")
									.setParameter("cus_sig_enm_id", customSignalEnumInfoHbn.getId()).list();
							if (null != customSignalEnumLangInfoHbnList) {
								Iterator<CustomSignalEnumLangInfoHbn> itCustomSignalEnumLangInfoHbn = customSignalEnumLangInfoHbnList
										.iterator();
								while (itCustomSignalEnumLangInfoHbn.hasNext()) {
									CustomSignalEnumLangInfoHbn customSignalEnumLangInfoHbn = itCustomSignalEnumLangInfoHbn
											.next();
									CustomSignalEnumLangEntityInfoHbn customSignalEnumLangEntityInfoHbn = session.get(
											CustomSignalEnumLangEntityInfoHbn.class,
											customSignalEnumLangInfoHbn.getCusSigEnmId());
								}
							}
						}
					}
					*/
					// TODO: re-construct alarm data
					// 			re-construct language resources
		
					CustomSignalInfoUnit customSignalInfoUnit = new CustomSignalInfoUnit(signalIdTmp, signalInfoHbn.getNotifying(), false, BPPacket.ALARM_CLASS_NONE, BPPacket.ALARM_DELAY_DEFAULT, BPPacket.ALARM_DELAY_DEFAULT, signalInfoHbn.getDisplay(), null, null, null, null, signalInterfaceTmp);
					signalId2InfoUnitMap.put(signalIdTmp, customSignalInfoUnit);
				} else {
					systemSignalEnumLangInfoList = null;
					/* "from SystemSignalInfoHbn where signalId=:signal_id" */
					hql.setLength(0);
					hql.append("from ");
					tableName = "SystemSignalInfoHbn";
					hql.append(tableName);
					tag = "signal_id";
					hql.append(" where signalId=:");
					hql.append(tag);
					SystemSignalInfoHbn systemSignalInfoHbn = (SystemSignalInfoHbn)session
							.createQuery(hql.toString())
							.setParameter(tag, signalInfoHbn.getId()).uniqueResult();
					if(null == systemSignalInfoHbn) {
						logger.error("Inner error: null == systemSignalInfoHbn");
						return ret;
					}
					if(systemSignalInfoHbn.getCustomFlags() != 0) {
						sysSigInfoTmp = bpSysSigTable.getSysSigInfo(signalIdTmp - BPPacket.SYS_SIG_START_ID);
						valueType = sysSigInfoTmp.getValType();
						
						signalInterfaceTmp = null;
				    	if(!BPPacket.ifSigTypeValid(valueType)) {
				    		logger.error("Inner error: !BPPacket.ifSigTypeValid(valueType)");
				    		return ret;
				    	}
				    	
				    	/* construct a hql like "from <table> where systemSignalId=:system_signal_id"*/
						hql.setLength(0);
						hql.append("from ");
						switch(valueType) {
						case BPPacket.VAL_TYPE_UINT32:
							tableName = "SystemSignalU32InfoHbn";
							break;
						case BPPacket.VAL_TYPE_UINT16:
							tableName = "SystemSignalU16InfoHbn";
							break;
						case BPPacket.VAL_TYPE_IINT32:
							tableName = "SystemSignalI32InfoHbn";
							break;
						case BPPacket.VAL_TYPE_IINT16:
							tableName = "SystemSignalI16InfoHbn";
							break;
						case BPPacket.VAL_TYPE_ENUM:
							tableName = "SystemSignalEnumInfoHbn";
							break;
						case BPPacket.VAL_TYPE_FLOAT:
							tableName = "SystemSignalFloatInfoHbn";
							break;
						case BPPacket.VAL_TYPE_STRING:
							tableName = "SystemSignalStringInfoHbn";
							break;
						case BPPacket.VAL_TYPE_BOOLEAN:
							tableName = "SystemSignalBooleanInfoHbn";
							break;
						default:
							logger.error("Inner error: invalid value type");
							return ret;
						}
						hql.append(tableName);
						tag = "system_signal_id";
						hql.append(" where systemSignalId=:");
						hql.append(tag);

						signalInterfaceTmp = (SignalInterface)session.createQuery(hql.toString())
						            .setParameter(tag, systemSignalInfoHbn.getId()).uniqueResult();
						if(null == signalInterfaceTmp) {
							logger.error("Inner error: null == signalInterfaceTmp");
							return ret;
						}
						
						if(BPPacket.VAL_TYPE_ENUM == valueType) {
							hql.setLength(0);
							hql.append("from ");
							tableName = "SystemSignalEnumLangInfoHbn";
							hql.append(tableName);
							tag = "sys_sig_enm_id";
							hql.append(" where sysSigEnmId=:");
							hql.append(tag);
							systemSignalEnumLangInfoList = session.createQuery(hql.toString())
						            .setParameter(tag, signalInterfaceTmp.getId()).list();
						}
							
					}
					SystemSignalInfoUnit systemSignalInfoUnit = new SystemSignalInfoUnit(signalIdTmp, signalInfoHbn.getNotifying(), systemSignalInfoHbn.getCustomFlags() != 0, systemSignalEnumLangInfoList, signalInterfaceTmp);
					signalId2InfoUnitMap.put(signalIdTmp, systemSignalInfoUnit);
				}
			}

			ret = true;
			bpDeviceSession.setSignalId2InfoUnitMap(signalId2InfoUnitMap);
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
    
    /* update 'userId2UserDevRelInfoListMap' */
    public void updateUserDevRel(UserInfoHbn userInfoHbn) {
    	if(null == userInfoHbn) {
    		return;
    	}
    	if(userId2UserDevRelInfoListMap.containsKey(userInfoHbn.getId())) {
    		return;
    	}
    	
    	Transaction tx = null;
	    try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			
			List<UserDevRelInfoInterface> userDevRelInfoInterfaceList = session.createQuery("from UserDevRelInfoHbn where userId = :user_id")
			.setParameter("user_id", userInfoHbn.getId())
			.list();
			if(null == userDevRelInfoInterfaceList) {
				return;
			}
			userId2UserDevRelInfoListMap.put(userInfoHbn.getId(), userDevRelInfoInterfaceList);
			
			List<DevInfoHbn> devInfoHbnList = session.createQuery("from DevInfoHbn where adminId = :admin_id")
			.setParameter("admin_id", userInfoHbn.getId())
			.list();
			if(null != devInfoHbnList) {
				int size = devInfoHbnList.size();
				for(int i = 0; i < size; i++) {
					DevInfoHbn devInfoHbn = devInfoHbnList.get(i);
					userDevRelInfoInterfaceList.add(new AdminDevRelInfoUnit(userInfoHbn.getId(), devInfoHbn.getSnId(), BPPacket.USER_AUTH_ALL));
				}
				
			}
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
	    
    }

    public void updateUserDevRel(DevInfoHbn devInfoHbn) {
    	if(null == devInfoHbn) {
    		return;
    	}
    	if(snId2UserDevRelInfoListMap.containsKey(devInfoHbn.getSnId())) {
    		List<UserDevRelInfoInterface> udriiList = snId2UserDevRelInfoListMap.get(devInfoHbn.getSnId());
    		int size = udriiList.size();
    		long adminId = devInfoHbn.getAdminId();
    		for(int i = 0; i < size; i++) {
    			if(udriiList.get(i).getUserId() == adminId) {
    				return;
    			}
    		}
    		udriiList.add(new AdminDevRelInfoUnit(adminId, devInfoHbn.getSnId(), BPPacket.USER_AUTH_ALL));
			return;
    	}
    	
    	Transaction tx = null;
	    try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			
			List<UserDevRelInfoInterface> userDevRelInfoList = session.createQuery("from UserDevRelInfoHbn where snId = :sn_id")
			.setParameter("sn_id", devInfoHbn.getSnId())
			.list();
			if(null == userDevRelInfoList) {
				userDevRelInfoList = new ArrayList<>();
			}
			
			userDevRelInfoList.add(new AdminDevRelInfoUnit(devInfoHbn.getAdminId(), devInfoHbn.getSnId(), BPPacket.USER_AUTH_ALL));
			snId2UserDevRelInfoListMap.put(devInfoHbn.getSnId(), userDevRelInfoList);
			
			tx.commit();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
	    
    }
}
