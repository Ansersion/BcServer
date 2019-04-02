/**
 * 
 */
package bp_packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.ServerChainHbn;
import db.SignalInfoUnitInterface;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import other.BPError;
import other.Util;


/**
 * @author Ansersion
 *
 */
public class Payload {
	
	final class MyEntry<K, V> implements Map.Entry<K, V> {
	    private final K key;
	    private V value;

	    public MyEntry(K key, V value) {
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public K getKey() {
	        return key;
	    }

	    @Override
	    public V getValue() {
	        return value;
	    }

	    @Override
	    public V setValue(V value) {
	        V old = this.value;
	        this.value = value;
	        return old;
	    }
	}
	
	public static final byte SYSTEM_SIGNAL_MAP_END_MASK = 0x01;
	
	public static final byte CUSTOM_SIGNAL_ALARM_FLAG_MASK = 0x01;
	public static final byte CUSTOM_SIGNAL_STATISTICS_FLAG_MASK = 0x02;
	public static final byte CUSTOM_SIGNAL_RW_FLAG_MASK = 0x04;
	public static final byte CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_MAX_NUM = 7;
	public static final byte CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_END_MASK = 0x08;
	private static final Logger logger = LoggerFactory.getLogger(Payload.class);

	// byte[] userName = null;
	// byte[] password = null;
	String userName;
	String password;
	String devName = null;
	int clntIdLen;
	int clntId;
	int symSetVer;
	private long devUniqId;
	DevSigData sigData = null;
	
	private String SN;
	
	private BPError error;
	
	Map<Integer, List<Integer> > mapDevId2SigIdLst = new HashMap<>();
	Map<Integer, Byte[]> mapDist2SysSigMap = new HashMap<>();
	private List<SystemSignalInfoUnit> systemSignalInfoUnitLst;
	private List<CustomSignalInfoUnit> customSignalInfoUnitLst;
	private List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitLst;
	private List<Integer> systemSignalEnabledList;
	private Map<Integer, Object> sysSigValMap;
	private Map<Integer, Map.Entry<Byte, Object>> cusSigValMap;
	private Map<Integer, Map.Entry<Byte, Object>> sigValMap;
	private List<Long> deviceIdList;
	private Map<Long, Long> deviceIdMap;
	private int customSignalLangSupportMask;
	private long sigMapCheckSum;
	private byte[] relayData;
	private ServerChainHbn serverChainHbn;
	private List<Integer> signalLst;
	
	public BPError getError() {
		return error;
	}

	public void setError(BPError error) {
		this.error = error;
	}

	/*
	public void setUserName(byte[] userName) {	
		this.userName = userName;
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public void getUserName(byte[] userName) {
		if(userName.length < this.userName.length) {
			return;
		}
		for(int i = 0; i < this.userName.length; i++) {
			userName[i] = this.userName[i];
		}
	}
	
	public byte[] getUserName() {
		byte[] userNameTmp = new byte[userName.length];
		for(int i = 0; i < userName.length; i++) {
			userNameTmp[i] = userName[i];
		}
		return userNameTmp;
	}
	
	public void getPassword(byte[] password) {
		if(password.length < this.password.length) {
			return;
		}
		for(int i = 0; i < this.password.length; i++) {
			password[i] = this.password[i];
		}
	}
	public byte[] getPassword() {
		byte[] passwordTmp = new byte[password.length];
		for(int i = 0; i < password.length; i++) {
			passwordTmp[i] = password[i];
		}
		return passwordTmp;
	}
	*/
	
	
	
	public void reset() {
		userName = null;
		password = null;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getClntIdLen() {
		return clntIdLen;
	}
	
	public int getClntId() {
		return clntId;
	}
	
	public int getSymSetVer() {
		return symSetVer;
	}
	
	public String getDevName() {
		return devName;
	}
	
	public DevSigData getSigData() {
		return sigData;
	}
	
	public void setClientId(int id) {
		clntId = id;
	}
	
	public void setClientIdLen(int len) {
		clntIdLen = len;
	}
	
	public void setClientIdLen() {
		clntIdLen = 2;
	}
	
	public void setDevName(byte[] devName) {
		try {
			this.devName= new String(devName);
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}
	
	public void setSigData(DevSigData sigData) {
		this.sigData = sigData;
	}
	
	public void clrMapDevId2SigIdList() {
		mapDevId2SigIdLst.clear();
	}
	
	public Map<Integer, List<Integer> > getMapDev2SigLst() {
		return mapDevId2SigIdLst;
	}
	
	public Map<Integer, Byte[]> getMapDist2SysSigMap() {
		return mapDist2SysSigMap;
	}
	
	public void setDevUniqId(long devUniqId) {
		this.devUniqId = devUniqId;
	}
	
	public long getUniqDevId() {
		return devUniqId;
	}
	
	public boolean packSysSigMap(long uniqDevId) {
		BeecomDB beecomDB = BeecomDB.getInstance();
		systemSignalInfoUnitLst = new ArrayList<SystemSignalInfoUnit>();
		systemSignalInfoUnitLst = beecomDB.getSystemSignalUnitLst(uniqDevId, systemSignalInfoUnitLst);
		if(null == systemSignalInfoUnitLst || systemSignalInfoUnitLst.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean packCusSigMap(long uniqDevId) {
		BeecomDB beecomDB = BeecomDB.getInstance();
		customSignalInfoUnitLst = new ArrayList<CustomSignalInfoUnit>();
		customSignalInfoUnitLst = beecomDB.getCustomSignalUnitLst(uniqDevId, customSignalInfoUnitLst, customSignalLangSupportMask);
		if(null == customSignalInfoUnitLst || customSignalInfoUnitLst.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean packSysSigCusInfo(long uniqDevId) {
		boolean ret = false;
		try {
			BeecomDB beecomDB = BeecomDB.getInstance();
			systemSignalCustomInfoUnitLst = new ArrayList<>();
			systemSignalCustomInfoUnitLst = beecomDB.getSystemSignalCustomInfoUnitLst(uniqDevId,
						systemSignalCustomInfoUnitLst);
			if(null != systemSignalCustomInfoUnitLst && !systemSignalCustomInfoUnitLst.isEmpty()) {
				ret = true;
			} 
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}

		return ret;
	}
	
	public List<Integer> getSysSig() {
		return null;
	}
	
	public List<Integer> getCusSig() {
		return null;
	}
	
	public boolean packSysSignal(long uniqDevId, List<Integer> sysSigLst) {
		return true;
	}
	
	public boolean packSysSignalValues(List<Integer> sysSigLst, BPDeviceSession bpDeviceSession, BPError bpError) {
		boolean ret = false;
		if (null == sysSigLst || null == bpDeviceSession) {
			return ret;
		}

		try {
			sysSigValMap = new HashMap<Integer, Object>();
			Iterator<Integer> it = sysSigLst.iterator();
			Map<Integer, SignalInfoUnitInterface> signalInfoUnitInterfaceMap = bpDeviceSession
					.getSignalId2InfoUnitMap();
			SignalInfoUnitInterface signalInfoUnitInterfaceTmp;
			if (null == signalInfoUnitInterfaceMap) {
				if (null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_INNER_ERR);
				}
				return ret;
			}

			while (it.hasNext()) {
				int sysSigId = it.next();
				if (sysSigId < BPPacket.SYS_SIG_START_ID || sysSigId > BPPacket.MAX_SIG_ID) {
					if (null != bpError) {
						bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
						bpError.setSigId(sysSigId);
					}
					return ret;
				}

				if (!signalInfoUnitInterfaceMap.containsKey(sysSigId)) {
					if (null != bpError) {
						bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
						bpError.setSigId(sysSigId);
					}
					return ret;
				}
				if (sysSigValMap.containsKey(sysSigId)) {
					if (null != bpError) {
						bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_REPEAT_ERR);
						bpError.setSigId(sysSigId);
					}
					return ret;
				}
				signalInfoUnitInterfaceTmp = signalInfoUnitInterfaceMap.get(sysSigId);
				if(signalInfoUnitInterfaceTmp.getSignalInterface().getEnStatistics()) {
					sysSigValMap.put(sysSigId, signalInfoUnitInterfaceTmp.getSignalValue());
				} else {
					bpError.setErrorCode(BPError.BP_ERROR_STATISTICS_NONE_SIGNAL);
					bpError.putStatisticsNoneSignalId(sysSigId);
				}
			}
			ret = true;
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = false;
			if(null != bpError) {
				bpError.setErrorCode(BPPacketGET.RET_CODE_INNER_ERR);
			}
		}
		return ret;
	}
	
	public boolean packSignalValues(List<Integer> sigLst, BPDeviceSession bpDeviceSession, BPError bpError) {
		boolean ret = false;
		if(null == sigLst || null == bpDeviceSession) {
			return ret;
		}
		sigValMap = new HashMap<Integer, Map.Entry<Byte, Object>>();
		
		Iterator<Integer> it = sigLst.iterator();
		Map<Integer, Map.Entry<Byte, Object> > customSignalValueMap;
		customSignalValueMap = bpDeviceSession.getCustomSignalValueMap();
		while(it.hasNext()) {
			int sigId = it.next();
			if(sigId < 0 || sigId > BPPacket.MAX_SIG_ID) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(sigId);
				}
				return ret;
			}
			
			if(!customSignalValueMap.containsKey(sigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(sigId);
				}
				return ret;
			}
			if(sigValMap.containsKey(sigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_REPEAT_ERR);
					bpError.setSigId(sigId);
				}
				return ret;
			}
			sigValMap.put(sigId, customSignalValueMap.get(sigId));
		}
		ret = true;
		return ret;
	}
	
	public boolean packCusSignal(long uniqDevId, List<Integer> sysSigLst, byte langFlags) {
		return true;
	}
	
	public Map<Integer, SignalAttrInfo> getSysSigAttrMap() {
		return null;
	}
	
	public Map<Integer, SignalAttrInfo> getCusSigAttrMap() {
		return null;
	}

	public Map<Integer, Object> getSysSigValMap() {
		return sysSigValMap;
	}

	public Map<Integer, Map.Entry<Byte, Object>> getCusSigValMap() {
		return cusSigValMap;
	}
	
	public Map<Integer, Object> putSysSigValMap(Integer sigId, Object value) {
		if(null == sigId || null == value) {
			return sysSigValMap;
		}
		if(null == sysSigValMap) {
			sysSigValMap = new HashMap<Integer, Object>();
		}
		sysSigValMap.put(sigId,  value);
		return sysSigValMap;
	}
	
	public Map<Integer, Map.Entry<Byte, Object> > putCusSigValMap(Integer sigId, Byte sigType, Object value) {
		if(null == sigId || null == sigType || null == value) {
			return cusSigValMap;
		}
		if(null == cusSigValMap) {
			cusSigValMap = new HashMap<Integer, Map.Entry<Byte, Object>>();
		}
		cusSigValMap.put(sigId, new MyEntry<Byte, Object>(sigType, value));
		return cusSigValMap;
	}
	
	
	public void setUnsupportedSignalId(int id) {
		if(null == error) {
			error = new BPError();
		}
		error.setSigId(id);
	}

	public List<Integer> getSystemSignalEnabledList() {
		return systemSignalEnabledList;
	}

	public void setSystemSignalEnabledList(List<Integer> systemSignalEnabledList) {
		this.systemSignalEnabledList = systemSignalEnabledList;
	}

	public List<CustomSignalInfoUnit> getCustomSignalInfoUnitLst() {
		return customSignalInfoUnitLst;
	}

	public void setCustomSignalInfoUnitLst(List<CustomSignalInfoUnit> customSignalInfoUnitLst) {
		this.customSignalInfoUnitLst = customSignalInfoUnitLst;
	}

	public List<SystemSignalCustomInfoUnit> getSystemSignalCustomInfoUnitLst() {
		return systemSignalCustomInfoUnitLst;
	}

	public void setSystemSignalCustomInfoUnitLst(List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitLst) {
		this.systemSignalCustomInfoUnitLst = systemSignalCustomInfoUnitLst;
	}

	public List<Long> getDeviceIdList() {
		return deviceIdList;
	}

	public void setDeviceIdList(List<Long> deviceIdList) {
		this.deviceIdList = deviceIdList;
	}

	public Map<Long, Long> getDeviceIdMap() {
		return deviceIdMap;
	}

	public void setDeviceIdMap(Map<Long, Long> deviceIdMap) {
		this.deviceIdMap = deviceIdMap;
	}

	public List<SystemSignalInfoUnit> getSystemSignalInfoUnitLst() {
		return systemSignalInfoUnitLst;
	}

	public int getCustomSignalLangSupportMask() {
		return customSignalLangSupportMask;
	}

	public void setCustomSignalLangSupportMask(int customSignalLangSupportMask) {
		this.customSignalLangSupportMask = customSignalLangSupportMask;
	}

	public long getSigMapCheckSum() {
		return sigMapCheckSum;
	}

	public void setSigMapCheckSum(long sigMapCheckSum) {
		this.sigMapCheckSum = sigMapCheckSum;
	}

	public Map<Integer, Map.Entry<Byte, Object> > putSigValMap(Integer sigId, Byte sigType, Object value) {
		if(null == sigId || null == sigType || null == value) {
			return sigValMap;
		}
		sigValMap.put(sigId, new MyEntry<Byte, Object>(sigType, value));
		return sigValMap;
	}
	
	public void initSigValMap() {
		if(null == sigValMap) {
			sigValMap = new HashMap<Integer, Map.Entry<Byte, Object>>();
		}
		sigValMap.clear();
	}

	public Map<Integer, Map.Entry<Byte, Object>> getSigValMap() {
		return sigValMap;
	}

	public byte[] getRelayData() {
		return relayData;
	}

	public void setRelayData(byte[] relayData) {
		this.relayData = relayData;
	}

	public ServerChainHbn getServerChainHbn() {
		return serverChainHbn;
	}

	public void setServerChainHbn(ServerChainHbn serverChainHbn) {
		this.serverChainHbn = serverChainHbn;
	}

	public String getSN() {
		return SN;
	}

	public void setSN(String sN) {
		SN = sN;
	}

	public List<Integer> getSignalLst() {
		return signalLst;
	}

	public void setSignalLst(List<Integer> signalLst) {
		this.signalLst = signalLst;
	}


}
