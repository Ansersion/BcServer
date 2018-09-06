/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.BeecomDB;
import db.CustomSignalInfoUnit;
import db.SignalInfoHbn;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalInfoUnit;
import javafx.util.Pair;
import other.BPError;


/**
 * @author Ansersion
 *
 */
public class Payload {
	
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
	
	private BPError error;
	
	Map<Integer, List<Integer> > mapDevId2SigIdLst = new HashMap<>();
	Map<Integer, Byte[]> mapDist2SysSigMap = new HashMap<>();
	private List<SystemSignalInfoUnit> systemSignalInfoUnitLst;
	private List<CustomSignalInfoUnit> customSignalInfoUnitLst;
	private List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitLst;
	private List<Integer> systemSignalEnabledList;
	private Map<Integer, Object> sysSigValMap;
	private Map<Integer, Pair<Byte, Object>> cusSigValMap;
	private Map<Integer, Pair<Byte, Object>> sigValMap;
	private List<Long> deviceIdList;
	private int customSignalLangSupportMask;
	private long sigMapCheckSum;
	
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
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
	
	public String getDeviceSn() {
		// TODO
		return "";
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
		BeecomDB beecomDB = BeecomDB.getInstance();
		systemSignalCustomInfoUnitLst = new ArrayList<SystemSignalCustomInfoUnit>();
		if(null != systemSignalInfoUnitLst) {
			Iterator<SystemSignalInfoUnit> it = systemSignalInfoUnitLst.iterator();
			SystemSignalInfoUnit systemSignalInfoUnit;
			while(it.hasNext()) {
				systemSignalInfoUnit = it.next();
				if(!systemSignalInfoUnit.isIfConfigDef()) {
					// TODO: 
					// systemSignalCustomInfoUnitLst.add(new SystemSignalCustomInfoUnit(systemSignalInfoUnit.getSysSigId(), systemSignalInfoUnit.getSystemSignalInterface()));
				}
				systemSignalCustomInfoUnitLst.add(new SystemSignalCustomInfoUnit(systemSignalInfoUnit.getSysSigId(), BPPacket.ALARM_CLASS_NONE, BPPacket.ALARM_DELAY_DEFAULT, BPPacket.ALARM_DELAY_DEFAULT, systemSignalInfoUnit.getSystemSignalInterface()));
			}
		} else {
			systemSignalCustomInfoUnitLst = beecomDB.getSystemSignalCustomInfoUnitLst(uniqDevId, systemSignalCustomInfoUnitLst);
		}
		if(null == systemSignalCustomInfoUnitLst || systemSignalCustomInfoUnitLst.isEmpty()) {
			return false;
		} else {
		return true;
		}
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
	
	public boolean packSysSignalValues(List<Integer> sysSigLst, BPSession bpSession, BPError bpError) {
		if(null == sysSigLst || null == bpSession) {
			return false;
		}
		sysSigValMap = new HashMap<Integer, Object>();
		
		Iterator<Integer> it = sysSigLst.iterator();
		Map<Integer, Object> systemSignalValueMap;
		systemSignalValueMap = bpSession.getSystemSignalValueMap();
		while(it.hasNext()) {
			int sysSigId = it.next();
			if(sysSigId < BPPacket.SYS_SIG_START_ID || sysSigId > BPPacket.MAX_SIG_ID) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(sysSigId);
				}
				return false;
			}
			
			if(!systemSignalValueMap.containsKey(sysSigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(sysSigId);
				}
				return false;
			}
			if(sysSigValMap.containsKey(sysSigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_REPEAT_ERR);
					bpError.setSigId(sysSigId);
				}
				return false;
			}
			sysSigValMap.put(sysSigId, systemSignalValueMap.get(sysSigId));
		}
		return true;
	}
	
	public boolean packCusSignalValues(List<Integer> cusSigLst, BPSession bpSession, BPError bpError) {
		if(null == cusSigLst || null == bpSession) {
			return false;
		}
		cusSigValMap = new HashMap<Integer, Pair<Byte, Object>>();
		
		Iterator<Integer> it = cusSigLst.iterator();
		Map<Integer, Pair<Byte, Object> > customSignalValueMap;
		customSignalValueMap = bpSession.getCustomSignalValueMap();
		while(it.hasNext()) {
			int cusSigId = it.next();
			if(cusSigId < BPPacket.CUS_SIG_START_ID || cusSigId > BPPacket.CUS_SIG_END_ID) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(cusSigId);
				}
				return false;
			}
			
			if(!customSignalValueMap.containsKey(cusSigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_NOT_SUPPORT_ERR);
					bpError.setSigId(cusSigId);
				}
				return false;
			}
			if(cusSigValMap.containsKey(cusSigId)) {
				if(null != bpError) {
					bpError.setErrorCode(BPPacketGET.RET_CODE_SIGNAL_REPEAT_ERR);
					bpError.setSigId(cusSigId);
				}
				return false;
			}
			cusSigValMap.put(cusSigId, customSignalValueMap.get(cusSigId));
		}
		return true;
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

	public Map<Integer, Pair<Byte, Object>> getCusSigValMap() {
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
	
	public Map<Integer, Pair<Byte, Object> > putCusSigValMap(Integer sigId, Byte sigType, Object value) {
		if(null == sigId || null == sigType || null == value) {
			return cusSigValMap;
		}
		if(null == cusSigValMap) {
			cusSigValMap = new HashMap<Integer, Pair<Byte, Object>>();
		}
		cusSigValMap.put(sigId, new Pair<Byte, Object>(sigType, value));
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

	public Map<Integer, Pair<Byte, Object> > putSigValMap(Integer sigId, Byte sigType, Object value) {
		if(null == sigId || null == sigType || null == value) {
			return sigValMap;
		}
		sigValMap.put(sigId, new Pair<Byte, Object>(sigType, value));
		return sigValMap;
	}
	
	public void initSigValMap() {
		if(null == sigValMap) {
			sigValMap = new HashMap<Integer, Pair<Byte, Object>>();
		}
		sigValMap.clear();
	}

	public Map<Integer, Pair<Byte, Object>> getSigValMap() {
		return sigValMap;
	}
		
	
	
	
}
