package db;

import java.util.Map;

public class CustomSignalInfoUnit {
	private int cusSigId;
	private boolean ifNotifing;
	private boolean ifAlarm;
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
	/* custom string signal default value language map */
	private Map<Integer, String> signalStringDefaultValueLangMap;
	/* custom alarm info */
	private CustomAlarmInfoUnit customAlarmInfoUnit;
	
	private SignalInterface customSignalInterface;
	
	public CustomSignalInfoUnit(int cusSigId, boolean ifNotifing, boolean ifAlarm, int groupLangId,
			Map<Integer, String> signalNameLangMap, Map<Integer, String> signalUnitLangMap,
			Map<Integer, String> groupLangMap, Map<Integer, Map<Integer, String>> signalEnumLangMap,
			Map<Integer, String> signalStringDefaultValueLangMap, CustomAlarmInfoUnit customAlarmInfoUnit,
			SignalInterface customSignalInterface) {
		super();
		this.cusSigId = cusSigId;
		this.ifNotifing = ifNotifing;
		this.ifAlarm = ifAlarm;
		this.groupLangId = groupLangId;
		this.signalNameLangMap = signalNameLangMap;
		this.signalUnitLangMap = signalUnitLangMap;
		this.groupLangMap = groupLangMap;
		this.signalEnumLangMap = signalEnumLangMap;
		this.signalStringDefaultValueLangMap = signalStringDefaultValueLangMap;
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

	public Map<Integer, String> getSignalStringDefaultValueLangMap() {
		return signalStringDefaultValueLangMap;
	}
	public CustomAlarmInfoUnit getCustomAlarmInfoUnit() {
		return customAlarmInfoUnit;
	}
	@Override
	public String toString() {
		return "CustomSignalInfoUnit [cusSigId=" + cusSigId + ", ifNotifing=" + ifNotifing + ", ifAlarm=" + ifAlarm
				+ ", groupLangId=" + groupLangId + ", signalNameLangMap=" + signalNameLangMap + ", signalUnitLangMap="
				+ signalUnitLangMap + ", groupLangMap=" + groupLangMap + ", signalEnumLangMap=" + signalEnumLangMap
				+ ", signalStringDefaultValueLangMap=" + signalStringDefaultValueLangMap + ", customAlarmInfoUnit="
				+ customAlarmInfoUnit + ", customSignalInterface=" + customSignalInterface + "]";
	}


}
