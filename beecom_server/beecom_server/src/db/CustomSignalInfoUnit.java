package db;

import java.util.Map;

public class CustomSignalInfoUnit {
	private int cusSigId;
	private boolean ifNotifing;
	private boolean ifAlarm;
	private boolean ifDisplay;
	/* system group language ID */
	private int groupLangId;
	/* custom signal name language map */
	private Map<Integer, String> signalNameLangMap;
	/* custom signal unit language map */
	private Map<Integer, String> signalUnitLangMap;
	/* custom group language map */
	private Map<Integer, String> groupLangMap;
	/* custom enumerate signal language map */
	private Map<Integer, Map<Integer, String> > signalEnumLangMap;
	/* custom alarm info */
	private CustomAlarmInfoUnit customAlarmInfoUnit;
	
	private SignalInterface customSignalInterface;
	
	public CustomSignalInfoUnit(int cusSigId, boolean ifNotifing, boolean ifAlarm, boolean ifDisplay, int groupLangId,
			Map<Integer, String> signalNameLangMap, Map<Integer, String> signalUnitLangMap,
			Map<Integer, String> groupLangMap, Map<Integer, Map<Integer, String>> signalEnumLangMap,
			CustomAlarmInfoUnit customAlarmInfoUnit, SignalInterface customSignalInterface) {
		super();
		this.cusSigId = cusSigId;
		this.ifNotifing = ifNotifing;
		this.ifAlarm = ifAlarm;
		this.ifDisplay = ifDisplay;
		this.groupLangId = groupLangId;
		this.signalNameLangMap = signalNameLangMap;
		this.signalUnitLangMap = signalUnitLangMap;
		this.groupLangMap = groupLangMap;
		this.signalEnumLangMap = signalEnumLangMap;
		this.customAlarmInfoUnit = customAlarmInfoUnit;
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
	public boolean isIfDisplay() {
		return ifDisplay;
	}
	public SignalInterface getCustomSignalInterface() {
		return customSignalInterface;
	}
	public int getGroupLangId() {
		return groupLangId;
	}
	public Map<Integer, String> getSignalNameLangMap() {
		return signalNameLangMap;
	}
	public Map<Integer, String> getSignalUnitLangMap() {
		return signalUnitLangMap;
	}
	public Map<Integer, String> getGroupLangMap() {
		return groupLangMap;
	}
	public Map<Integer, Map<Integer, String>> getSignalEnumLangMap() {
		return signalEnumLangMap;
	}

	public CustomAlarmInfoUnit getCustomAlarmInfoUnit() {
		return customAlarmInfoUnit;
	}
	@Override
	public String toString() {
		return "CustomSignalInfoUnit [cusSigId=" + cusSigId + ", ifNotifing=" + ifNotifing + ", ifAlarm=" + ifAlarm
				+ ", ifDisplay=" + ifDisplay + ", groupLangId=" + groupLangId + ", signalNameLangMap="
				+ signalNameLangMap + ", signalUnitLangMap=" + signalUnitLangMap + ", groupLangMap=" + groupLangMap
				+ ", signalEnumLangMap=" + signalEnumLangMap + ", customAlarmInfoUnit=" + customAlarmInfoUnit
				+ ", customSignalInterface=" + customSignalInterface + "]";
	}

	


}
