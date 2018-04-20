/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.BPError;


/**
 * @author Ansersion
 *
 */
public class Payload {
	
	private static final Logger logger = LoggerFactory.getLogger(Payload.class);

	byte[] userName = null;
	byte[] password = null;
	String devName = null;
	int clntIdLen;
	int clntId;
	int symSetVer;
	DevSigData sigData = null;
	
	private BPError error;
	
	Map<Integer, List<Integer> > mapDevId2SigIdLst = new HashMap<>();
	Map<Integer, Byte[]> mapDist2SysSigMap = new HashMap<>();
	
	
	public BPError getError() {
		return error;
	}

	public void setError(BPError error) {
		this.error = error;
	}

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
	
	public void reset() {
		userName = null;
		password = null;
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
}
