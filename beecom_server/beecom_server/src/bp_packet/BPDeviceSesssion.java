package bp_packet;

public class BPDeviceSesssion extends BPSession {
	private Long uniqDeviceId;
	private String sn;
	private String password;
	
	public BPDeviceSesssion() {
		super();
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
	public String getSn() {
		return sn;
	}

	@Override
	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

}
