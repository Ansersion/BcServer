package bp_packet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

public class BPDeviceSession extends BPSession {
	private Long uniqDeviceId;
	private String password;
	private Map<Integer, List<Object> > signalValueMap;
	
	public BPDeviceSession(IoSession session) {
		super(session);
		uniqDeviceId = 0L;
		password = "";
		signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
	}
	
	public BPDeviceSession(IoSession session, Long uniqDeviceId, String password) {
		super(session);
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
		this.signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
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
