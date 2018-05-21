package db;

public class CustomSignalInfoUnit {
	private int cusSigId;
	private boolean ifNotifing;
	private boolean ifAlarm;
	private SignalInterface customSignalInterface;
	public CustomSignalInfoUnit(int cusSigId, boolean ifNotifing, boolean ifAlarm,
			SignalInterface customSignalInterface) {
		super();
		this.cusSigId = cusSigId;
		this.ifNotifing = ifNotifing;
		this.ifAlarm = ifAlarm;
		this.customSignalInterface = customSignalInterface;
	}
	public int getCusSigId() {
		return cusSigId;
	}
	public boolean isIfNotifing() {
		return ifNotifing;
	}
	public boolean isIfAlarm() {
		return ifAlarm;
	}
	public SignalInterface getCustomSignalInterface() {
		return customSignalInterface;
	}
	@Override
	public String toString() {
		return "CustomSignalInfoUnit [cusSigId=" + cusSigId + ", ifNotifing=" + ifNotifing + ", ifAlarm=" + ifAlarm
				+ ", customSignalInterface=" + customSignalInterface + "]";
	}

	
}
