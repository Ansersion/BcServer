/**
 * 
 */
package db;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import bp_packet.BPSession;
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
	
	public static LoginErrorEnum checkDeviceUniqId(long devUniqId) {
		return LoginErrorEnum.LOGIN_OK;
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
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = getSysSigInfoHbnLst(signalInfoHbnLst);
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
	
	public List<CustomSignalInfoUnit> getCustomSignalUnitLst(long uniqDeviceId, List<CustomSignalInfoUnit> customSignalInfoUnitLst) {
		if(null == customSignalInfoUnitLst) {
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
							signalInterface = (CustomSignalIFloatInfoHbn) session
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
	
	public List<SystemSignalInfoHbn> getSysSigInfoHbnLst(List<SignalInfoHbn> signalInfoHbnLst) {
		List<SystemSignalInfoHbn> systemSignalInfoHbnLst = new ArrayList<SystemSignalInfoHbn>();
		
		Transaction tx = null;
		try (Session session = sessionFactory.openSession()) {
			Iterator<SignalInfoHbn> it = signalInfoHbnLst.iterator();
			SystemSignalInfoHbn systemSignalInfoHbn = null;
			tx = session.beginTransaction();
			while(it.hasNext()) {

				systemSignalInfoHbn = (SystemSignalInfoHbn)session.createQuery("from SystemSignalInfoHbn where signalId = :signal_id")
				.setParameter("signal_id", it.next().getId())
				.uniqueResult();
				
				if(null == systemSignalInfoHbn) {
					return null;
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
			systemSignalInfoHbnLst = null;
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
	
    private static SessionFactory buildSessionFactory() {  
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
