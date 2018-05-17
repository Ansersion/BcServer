package bp_packet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPDeviceSession extends BPSession {
	private Long uniqDeviceId;
	private String password;
	private Map<Integer, List<Object> > signalValueMap;
	
	public BPDeviceSession() {
		super();
		uniqDeviceId = 0L;
		password = "";
		signalValueMap = new HashMap<>();
	}
	
	public BPDeviceSession(Long uniqDeviceId, String password) {
		super();
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
		this.signalValueMap = new HashMap<>();
	}
	
	@Override
	public Map<Integer, List<Object> > getSignalValueMap() {
		return signalValueMap;
	}
	
	@Override
	public boolean ifUserSession() {
		return false;
	}

	@Override
	public Long getUniqDeviceId() {
		return uniqDeviceId;
	}

	@Override
	public void setUniqDeviceId(Long uniqDeviceId) {
		this.uniqDeviceId = uniqDeviceId;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return uniqDeviceId.toString();
	}
}
