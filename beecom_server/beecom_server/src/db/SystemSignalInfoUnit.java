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
	private boolean ifDisplay;
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
		
		try {
			BPSysSigTable sysSigTab = BPSysSigTable.getSysSigTableInstance();
			SysSigInfo sysSigInfo = sysSigTab.getSysSigInfo(sysSigId - BPPacket.SYS_SIG_START_ID);
			if (null == sysSigInfo) {
				return;
			}
			ifDisplay = sysSigInfo.isIfDisplay();
			alarmClass = sysSigInfo.getAlmClass();
			alarmDelayBef = (short)sysSigInfo.getDlyBefAlm();
			alarmDelayAft = (short)sysSigInfo.getDlyAftAlm();
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
	}

	public SystemSignalInfoUnit(int sysSigId, boolean ifNotifing, boolean ifConfigDef, boolean ifDisplay, short alarmClass, short alarmDelayBef, short alarmDelayAft, 
			List<SystemSignalEnumLangInfoHbn> systemSignalEnumLangInfoList, SignalInterface systemSignalInterface) {
		super();
		this.sysSigId = sysSigId;
		this.ifNotifing = ifNotifing;
		this.ifConfigDef = ifConfigDef;
		this.ifDisplay = ifDisplay;
		this.alarmClass = alarmClass;
		this.alarmDelayBef = alarmDelayBef;
		this.alarmDelayAft = alarmDelayAft;
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
		return alarmClass != BPPacket.ALARM_CLASS_NONE;
	}
	
	@Override
	public boolean ifDisplay() {
		return ifDisplay;
	}

	@Override
	public Map<Integer, String> getSignalNameLangMap() {
		return null;
	}

	@Override
	public Map<Integer, String> getSignalUnitLangMap() {
		/* in systemSignalInterface */
		return null;
	}

	@Override
	public Map<Integer, String> getGroupLangMap() {
		/* in systemSignalInterface */
		return null;
	}

	@Override
	public Map<Integer, Map<Integer, String>> getSignalEnumLangMap() {
		/* in systemSignalInterface */
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
		boolean ret = true;
		try {
			BPSysSigTable bpSysSigTable = BPSysSigTable.getSysSigTableInstance();
			int systemSignalIdOffset = sysSigId - BPPacket.SYS_SIG_START_ID;
			SysSigInfo sysSigInfo = bpSysSigTable.getSysSigInfo(systemSignalIdOffset);
			if(null == sysSigInfo) {
				return ret;
			}
			
			switch (valueType & BPPacket.VAL_TYPE_MASK) {
			case BPPacket.VAL_TYPE_UINT32: {
				long min = (long)sysSigInfo.getValMin();
				long max = (long)sysSigInfo.getValMax();
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					SystemSignalU32InfoHbn systemSignalU32InfoHbn = (SystemSignalU32InfoHbn)systemSignalInterface;
					min = systemSignalU32InfoHbn.getMinVal();
				}
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					SystemSignalU32InfoHbn systemSignalU32InfoHbn = (SystemSignalU32InfoHbn)systemSignalInterface;
					max = systemSignalU32InfoHbn.getMaxVal();
				}
				Long v = (Long)value;
				if(v >= min && v <= max) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_UINT16: {
				int min = (int)sysSigInfo.getValMin();
				int max = (int)sysSigInfo.getValMax();
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					SystemSignalU16InfoHbn systemSignalU16InfoHbn = (SystemSignalU16InfoHbn)systemSignalInterface;
					min = systemSignalU16InfoHbn.getMinVal();
				}
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					SystemSignalU16InfoHbn systemSignalU16InfoHbn = (SystemSignalU16InfoHbn)systemSignalInterface;
					max = systemSignalU16InfoHbn.getMaxVal();
				}
				Integer v = (Integer)value;
				if(v >= min && v <= max) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_IINT32: {
				int min = (int)sysSigInfo.getValMin();
				int max = (int)sysSigInfo.getValMax();
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					SystemSignalI32InfoHbn systemSignalI32InfoHbn = (SystemSignalI32InfoHbn)systemSignalInterface;
					min = systemSignalI32InfoHbn.getMinVal();
				}
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					SystemSignalI32InfoHbn systemSignalI32InfoHbn = (SystemSignalI32InfoHbn)systemSignalInterface;
					max = systemSignalI32InfoHbn.getMaxVal();
				}
				Integer v = (Integer)value;
				if(v >= min && v <= max) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_IINT16: {
				short min = (short)sysSigInfo.getValMin();
				short max = (short)sysSigInfo.getValMax();
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					SystemSignalI16InfoHbn systemSignalI16InfoHbn = (SystemSignalI16InfoHbn)systemSignalInterface;
					min = systemSignalI16InfoHbn.getMinVal();
				}
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					SystemSignalI16InfoHbn systemSignalI16InfoHbn = (SystemSignalI16InfoHbn)systemSignalInterface;
					max = systemSignalI16InfoHbn.getMaxVal();
				}
				Short v = (Short)value;
				if(v >= min && v <= max) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_ENUM: {
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
					// SystemSignalEnumInfoHbn systemSignalEnumInfoHbn = (SystemSignalEnumInfoHbn)systemSignalInterface;
					int size = systemSignalEnumLangInfoList.size();
					Integer v = (Integer)value;
					for(int i = 0; i < size; i++) {
						if(v == systemSignalEnumLangInfoList.get(i).getEnumKey()) {
							ret = false;
							break;
						}
					}
				} else {
					Map<Integer, Integer> signalEnumLangMap = sysSigInfo.getMapEnmLangRes();
					if(signalEnumLangMap.containsKey(value)) {
						ret = false;
					}
				}
				
				break;
			}
			case BPPacket.VAL_TYPE_FLOAT: {
				float min = (float)sysSigInfo.getValMin();
				float max = (float)sysSigInfo.getValMax();
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
					SystemSignalFloatInfoHbn systemSignalFloatInfoHbn = (SystemSignalFloatInfoHbn)systemSignalInterface;
					min = systemSignalFloatInfoHbn.getMinVal();
				}
				if((customFlags & BPPacket.SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
					SystemSignalFloatInfoHbn systemSignalFloatInfoHbn = (SystemSignalFloatInfoHbn)systemSignalInterface;
					max = systemSignalFloatInfoHbn.getMaxVal();
				}
				Float v = (Float)value;
				if(v >= min && v <= max) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_STRING: {
				String v = (String)value;
				if(v.length() <= BPPacket.MAX_STR_LENGTH) {
					ret = false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_BOOLEAN: {
				if(value instanceof Boolean) {
					ret = false;
				}
				
				break;
			}
			default:
				/* do nothing */
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = true;
		}
		return ret;
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
	
	

	public boolean isIfDisplay() {
		return ifDisplay;
	}

	public void setIfDisplay(boolean ifDisplay) {
		this.ifDisplay = ifDisplay;
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
