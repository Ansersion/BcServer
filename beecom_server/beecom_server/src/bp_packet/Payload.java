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
	
	private static final Logger logger = LoggerFactory.getLogger(Payload.class);

	// byte[] userName = null;
	// byte[] password = null;
	String userName;
	String password;
	String devName = null;
	int clntIdLen;
	int clntId;
	int symSetVer;
	DevSigData sigData = null;
	
	private BPError error;
	
	Map<Integer, List<Integer> > mapDevId2SigIdLst = new HashMap<>();
	Map<Integer, Byte[]> mapDist2SysSigMap = new HashMap<>();
	private List<SystemSignalInfoUnit> systemSignalInfoUnitLst;
	private List<CustomSignalInfoUnit> customSignalInfoUnitLst;
	private List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitLst;
	private Map<Integer, Object> sysSigValMap;
	private Map<Integer, Pair<Byte, Object>> cusSigValMap;
	
	
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
		
	}
	
	public long getUniqDevId() {
		return 0;
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
		customSignalInfoUnitLst = beecomDB.getCustomSignalUnitLst(uniqDevId, customSignalInfoUnitLst);
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
					systemSignalCustomInfoUnitLst.add(new SystemSignalCustomInfoUnit(systemSignalInfoUnit.getSysSigId(), systemSignalInfoUnit.getSystemSignalInterface()));
				}
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
	
	public long getSigMapChecksum() {
		return 0;
	}

	public Map<Integer, Object> getSysSigValMap() {
		return sysSigValMap;
	}
	
	
}
