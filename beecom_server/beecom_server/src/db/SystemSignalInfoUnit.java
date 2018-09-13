package db;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.util.Pair;

public class SystemSignalInfoUnit implements SignalInfoUnitInterface {

	private int sysSigId;
	private boolean ifNotifing;
	private boolean ifConfigDef;
	private int customFlags;
	private short alarmClass;
	private short alarmDelayBef;
	private short alarmDelayAft;
	private List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList;
	private SignalInterface systemSignalInterface;
	private Object signalValue;
	
	public SystemSignalInfoUnit(int sysSigId) {
		super();
		this.sysSigId = sysSigId;
		this.ifNotifing = true;
		this.ifConfigDef = false;
		this.systemSignalEnumLangInfoList = null;
		this.systemSignalInterface = null;
	}

	public SystemSignalInfoUnit(int sysSigId, boolean ifNotifing, boolean ifConfigDef,
			List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList, SignalInterface systemSignalInterface) {
		super();
		this.sysSigId = sysSigId;
		this.ifNotifing = ifNotifing;
		this.ifConfigDef = ifConfigDef;
		this.systemSignalEnumLangInfoList = systemSignalEnumLangInfoList;
		this.systemSignalInterface = systemSignalInterface;
	}

	public int getSysSigId() {
		return sysSigId;
	}

	public boolean isIfNotifing() {
		return ifNotifing;
	}

	public boolean isIfConfigDef() {
		return ifConfigDef;
	}

	public List<SystemSignalEnumLangInfoHbn> getSystemSignalEnumLangInfoList() {
		return systemSignalEnumLangInfoList;
	}

	public SignalInterface getSystemSignalInterface() {
		return systemSignalInterface;
	}


	@Override
	public String toString() {
		return "SystemSignalInfoUnit [sysSigId=" + sysSigId + ", ifNotifing=" + ifNotifing + ", ifConfigDef="
				+ ifConfigDef + ", systemSignalEnumLangInfoList=" + systemSignalEnumLangInfoList
				+ ", systemSignalInterface=" + systemSignalInterface + "]";
	}

	@Override
	public boolean ifNotifying() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSignalId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SignalInterface getSignalInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean ifConfigDef() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ifAlarm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ifDisplay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<Integer, String> getSignalNameLangMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, String> getGignalUnitLangMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, String> getGroupLangMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Map<Integer, String>> getSignalEnumLangMap() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean putSignalValue(Entry<Integer, Pair<Byte, Object>> entry) {
		boolean ret = false;
		try {
			signalValue = entry.getValue().getValue();
			ret = true;
		} catch(Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	@Override
	public boolean checkSignalValueUnformed(Byte valueType, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getSignalValue() {
		return signalValue;
	}

	public void setSignalValue(Object signalValue) {
		this.signalValue = signalValue;
	}

	public void setSysSigId(int sysSigId) {
		this.sysSigId = sysSigId;
	}

	public void setIfNotifing(boolean ifNotifing) {
		this.ifNotifing = ifNotifing;
	}

	public void setIfConfigDef(boolean ifConfigDef) {
		this.ifConfigDef = ifConfigDef;
	}

	public void setSystemSignalEnumLangInfoList(List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList) {
		this.systemSignalEnumLangInfoList = systemSignalEnumLangInfoList;
	}

	public void setSystemSignalInterface(SignalInterface systemSignalInterface) {
		this.systemSignalInterface = systemSignalInterface;
	}
	
	

	public int getCustomFlags() {
		return customFlags;
	}

	public void setCustomFlags(int customFlags) {
		this.customFlags = customFlags;
	}

	public void setAlarmClass(short alarmClass) {
		this.alarmClass = alarmClass;
	}

	public void setAlarmDelayBef(short alarmDelayBef) {
		this.alarmDelayBef = alarmDelayBef;
	}

	public void setAlarmDelayAft(short alarmDelayAft) {
		this.alarmDelayAft = alarmDelayAft;
	}

	@Override
	public short getAlarmClass() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getAlarmDelayBef() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getAlarmDelayAft() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	
}
