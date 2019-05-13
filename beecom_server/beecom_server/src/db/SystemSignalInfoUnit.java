package db;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import other.Util;
import sys_sig_table.BPSysSigTable;
import sys_sig_table.SysSigInfo;


public class SystemSignalInfoUnit implements SignalInfoUnitInterface {
	private static final Logger logger = LoggerFactory.getLogger(SystemSignalInfoUnit.class); 
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

	public boolean isIfNotifing() {
		return ifNotifing;
	}

	public boolean isIfConfigDef() {
		return ifConfigDef;
	}

	public List<SystemSignalEnumLangInfoHbn> getSystemSignalEnumLangInfoList() {
		return systemSignalEnumLangInfoList;
	}


	@Override
	public String toString() {
		return "SystemSignalInfoUnit [sysSigId=" + sysSigId + ", ifNotifing=" + ifNotifing + ", ifConfigDef="
				+ ifConfigDef + ", systemSignalEnumLangInfoList=" + systemSignalEnumLangInfoList
				+ ", systemSignalInterface=" + systemSignalInterface + "]";
	}

	@Override
	public boolean ifNotifying() {
		return ifNotifing;
	}

	@Override
	public int getSignalId() {
		return sysSigId;
	}

	@Override
	public SignalInterface getSignalInterface() {
		return systemSignalInterface;
	}

	@Override
	public boolean ifConfigDef() {
		return ifConfigDef;
	}

	@Override
	public boolean ifAlarm() {
		// TODO:
		return false;
	}
	
	@Override
	public boolean ifDisplay() {
		/*
		 * TODO;
		boolean ret = false;
		try {
			if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_DISPLAY) != 0) {
				ret = systemSignalInterface.
			} else {
				BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
				SysSigInfo sysSigInfo = sysSigTab.getSysSigInfo(sysSigId - BPPacket.SYS_SIG_START_ID);
				if (null == sysSigInfo) {
					return ret;
				}
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
		*/
		return false;
	}

	@Override
	public Map<Integer, String> getSignalNameLangMap() {
		return null;
	}

	@Override
	public Map<Integer, String> getSignalUnitLangMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, String> getGroupLangMap() {
		/*
		 * TODO:
		boolean ret = false;
		try {
			if ((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_GROUP_LANG) != 0) {
				ret = systemSignalInterface.
			} else {
				BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
				SysSigInfo sysSigInfo = sysSigTab.getSysSigInfo(sysSigId - BPPacket.SYS_SIG_START_ID);
				if (null == sysSigInfo) {
					return sysSigInfo.;
				}
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
		*/
		return null;
	}

	@Override
	public Map<Integer, Map<Integer, String>> getSignalEnumLangMap() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean putSignalValue(Entry<Integer, Map.Entry<Byte, Object>> entry) {
		boolean ret = false;
		try {
			signalValue = entry.getValue().getValue();
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
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
		if(null == signalValue) {
			/* return default signal value */
			BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
			SysSigInfo sysSigInfo = sysSigTab.getSysSigInfo(sysSigId - BPPacket.SYS_SIG_START_ID);
			if (null == sysSigInfo) {
				return null;
			}
			signalValue = sysSigInfo.getValDef();
		}
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
		return alarmClass;
	}

	@Override
	public short getAlarmDelayBef() {
		return alarmDelayBef;
	}

	@Override
	public short getAlarmDelayAft() {
		return alarmDelayAft;
	}
}
