package db;

public class SystemSignalCustomInfoUnit {
	
	private int sysSigId;
	private Short alarmClass;
	private Short delayBeforeAlarm;
	private Short delayAfterAlarm;
	private int customFlags;
	private SignalInterface signalInterface;

	public SystemSignalCustomInfoUnit(int sysSigId, Short alarmClass, Short delayBeforeAlarm, Short delayAfterAlarm,
			int customFlags, SignalInterface signalInterface) {
		super();
		this.sysSigId = sysSigId;
		this.alarmClass = alarmClass;
		this.delayBeforeAlarm = delayBeforeAlarm;
		this.delayAfterAlarm = delayAfterAlarm;
		this.customFlags = customFlags;
		this.signalInterface = signalInterface;
	}

	public int getSysSigId() {
		return sysSigId;
	}

	public void setSysSigId(int sysSigId) {
		this.sysSigId = sysSigId;
	}

	public SignalInterface getSignalInterface() {
		return signalInterface;
	}

	public void setSignalInterface(SignalInterface signalInterface) {
		this.signalInterface = signalInterface;
	}

	public Short getAlarmClass() {
		return alarmClass;
	}

	public void setAlarmClass(Short alarmClass) {
		this.alarmClass = alarmClass;
	}

	public Short getDelayBeforeAlarm() {
		return delayBeforeAlarm;
	}

	public void setDelayBeforeAlarm(Short delayBeforeAlarm) {
		this.delayBeforeAlarm = delayBeforeAlarm;
	}

	public Short getDelayAfterAlarm() {
		return delayAfterAlarm;
	}

	public void setDelayAfterAlarm(Short delayAfterAlarm) {
		this.delayAfterAlarm = delayAfterAlarm;
	}

	public int getCustomFlags() {
		return customFlags;
	}

	public void setCustomFlags(int customFlags) {
		this.customFlags = customFlags;
	}

	@Override
	public String toString() {
		return "SystemSignalCustomInfoUnit [sysSigId=" + sysSigId + ", alarmClass=" + alarmClass + ", delayBeforeAlarm="
				+ delayBeforeAlarm + ", delayAfterAlarm=" + delayAfterAlarm + ", customFlags=" + customFlags
				+ ", signalInterface=" + signalInterface + "]";
	}





	
}
