package bp_packet;

public class BPDeviceSession extends BPSession {
	private Long uniqDeviceId;
	private String password;
	
	public BPDeviceSession() {
		super();
		uniqDeviceId = 0L;
		password = "";
	}
	
	public BPDeviceSession(Long uniqDeviceId, String password) {
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
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
