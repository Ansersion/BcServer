package db;

import java.util.Map;

public class SystemSignalCustomInfoUnit {
	
	private int sysSigId;
	private Short alarmClass;
	private Short delayBeforeAlarm;
	private Short delayAfterAlarm;
	private int customFlags;
	private boolean display;
	private Map<Integer, Integer> enumLangMap;
	private SignalInterface signalInterface;


	public SystemSignalCustomInfoUnit(int sysSigId, Short alarmClass, Short delayBeforeAlarm, Short delayAfterAlarm,
			int customFlags, Map<Integer, Integer> enumLangMap, SignalInterface signalInterface, boolean display) {
		super();
		this.sysSigId = sysSigId;
		this.alarmClass = alarmClass;
		this.delayBeforeAlarm = delayBeforeAlarm;
		this.delayAfterAlarm = delayAfterAlarm;
		this.customFlags = customFlags;
		this.enumLangMap = enumLangMap;
		this.signalInterface = signalInterface;
		this.display = display;
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

	public Map<Integer, Integer> getEnumLangMap() {
		return enumLangMap;
	}

	public void setEnumLangMap(Map<Integer, Integer> enumLangMap) {
		this.enumLangMap = enumLangMap;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return "SystemSignalCustomInfoUnit [sysSigId=" + sysSigId + ", alarmClass=" + alarmClass + ", delayBeforeAlarm="
				+ delayBeforeAlarm + ", delayAfterAlarm=" + delayAfterAlarm + ", customFlags=" + customFlags
				+ ", display=" + display + ", enumLangMap=" + enumLangMap + ", signalInterface=" + signalInterface
				+ "]";
	}


}
