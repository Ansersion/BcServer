package db;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.util.Pair;

public class SystemSignalInfoUnit implements SignalInfoUnitInterface {

	private int sysSigId;
	private boolean ifNotifing;
	private boolean ifConfigDef;
	private List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList;
	private SignalInterface systemSignalInterface;
	private Object signalValue;

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

	
	
	
}
