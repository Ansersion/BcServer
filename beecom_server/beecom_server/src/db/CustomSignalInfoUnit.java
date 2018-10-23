package db;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import bp_packet.BPPacket;
import javafx.util.Pair;

public class CustomSignalInfoUnit implements SignalInfoUnitInterface {
	private int cusSigId;
	private boolean ifNotifing;
	private boolean ifAlarm;
	private short alarmClass;
	private short alarmDelayBef;
	private short alarmDelayAft;
	private boolean ifDisplay;
	/* system group language ID */
	// private int groupLangId;
	/* custom signal name language map */
	private Map<Integer, String> signalNameLangMap;
	/* custom signal unit language map */
	private Map<Integer, String> signalUnitLangMap;
	/* custom group language map */
	private Map<Integer, String> groupLangMap;
	/* custom enumerate signal language map */
	private Map<Integer, Map<Integer, String> > signalEnumLangMap;
	/* custom alarm info */
	// private CustomAlarmInfoUnit customAlarmInfoUnit;
	
	private SignalInterface customSignalInterface;
	private Object signalValue;
	

	public CustomSignalInfoUnit(int cusSigId, boolean ifNotifing, boolean ifAlarm, short alarmClass,
			short alarmDelayBef, short alarmDelayAft, boolean ifDisplay, Map<Integer, String> signalNameLangMap,
			Map<Integer, String> signalUnitLangMap, Map<Integer, String> groupLangMap,
			Map<Integer, Map<Integer, String>> signalEnumLangMap, SignalInterface customSignalInterface) {
		super();
		this.cusSigId = cusSigId;
		this.ifNotifing = ifNotifing;
		this.ifAlarm = ifAlarm;
		this.alarmClass = alarmClass;
		this.alarmDelayBef = alarmDelayBef;
		this.alarmDelayAft = alarmDelayAft;
		this.ifDisplay = ifDisplay;
		this.signalNameLangMap = signalNameLangMap;
		this.signalUnitLangMap = signalUnitLangMap;
		this.groupLangMap = groupLangMap;
		this.signalEnumLangMap = signalEnumLangMap;
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

	@Override
	public String toString() {
		return "CustomSignalInfoUnit [cusSigId=" + cusSigId + ", ifNotifing=" + ifNotifing + ", ifAlarm=" + ifAlarm
				+ ", alarmClass=" + alarmClass + ", alarmDelayBef=" + alarmDelayBef + ", alarmDelayAft=" + alarmDelayAft
				+ ", ifDisplay=" + ifDisplay + ", signalNameLangMap=" + signalNameLangMap + ", signalUnitLangMap="
				+ signalUnitLangMap + ", groupLangMap=" + groupLangMap + ", signalEnumLangMap=" + signalEnumLangMap
				+ ", customSignalInterface=" + customSignalInterface + ", signalValue=" + signalValue + "]";
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
	public Map<Integer, String> getGignalUnitLangMap() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean putSignalValue(Entry<Integer, Pair<Byte, Object>> entry) {
		boolean ret = false;
		try {
			signalValue = entry.getValue().getValue();
		} catch(Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	@Override
	public boolean checkSignalValueUnformed(Byte valueType, Object value) {
		boolean ret = true;
		/* check if signal info exist */
		if(null == customSignalInterface) {
			return ret;
		}
		/* check if a alarm signal */
		if(!ifAlarm && (valueType & BPPacket.VAL_ALARM_FLAG) != 0) {
			return ret;
		}
		try {
			switch (valueType & BPPacket.VAL_TYPE_MASK) {
			case BPPacket.VAL_TYPE_UINT32: {
				CustomSignalU32InfoHbn customSignalU32InfoHbn = (CustomSignalU32InfoHbn)customSignalInterface;
				Long v = (Long)value;
				if(v >= customSignalU32InfoHbn.getMinVal() && v <= customSignalU32InfoHbn.getMaxVal()) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_UINT16: {
				CustomSignalU16InfoHbn customSignalU16InfoHbn = (CustomSignalU16InfoHbn)customSignalInterface;
				Integer v = (Integer)value;
				if(v >= customSignalU16InfoHbn.getMinVal() && v <= customSignalU16InfoHbn.getMaxVal()) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_IINT32: {
				CustomSignalI32InfoHbn customSignalI32InfoHbn = (CustomSignalI32InfoHbn)customSignalInterface;
				Integer v = (Integer)value;
				if(v >= customSignalI32InfoHbn.getMinVal() && v <= customSignalI32InfoHbn.getMaxVal()) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_IINT16: {
				CustomSignalI16InfoHbn customSignalI16InfoHbn = (CustomSignalI16InfoHbn)customSignalInterface;
				Short v = (Short)value;
				if(v >= customSignalI16InfoHbn.getMinVal() && v <= customSignalI16InfoHbn.getMaxVal()) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_ENUM: {
				CustomSignalEnumInfoHbn customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn)customSignalInterface;
				Integer v = (Integer)value;
				Iterator<Map.Entry<Integer, Map<Integer, String> > > it = signalEnumLangMap.entrySet().iterator();
				if(it.hasNext()) {
					Map<Integer, String> signalEnumValueMap = it.next().getValue();
					if(null != signalEnumValueMap && signalEnumValueMap.containsKey(v)) {
						ret = false;
					}
				}
				
				break;
			}
			case BPPacket.VAL_TYPE_FLOAT: {
				CustomSignalFloatInfoHbn customSignalFloatInfoHbn = (CustomSignalFloatInfoHbn)customSignalInterface;
				Float v = (Float)value;
				if(v >= customSignalFloatInfoHbn.getMinVal() && v <= customSignalFloatInfoHbn.getMaxVal()) {
					ret =false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_STRING: {
				String v = (String)value;
				if(v.length() <= BPPacket.MAX_CUSTOM_SIGNAL_STRING_LENGTH) {
					ret = false;
				}
				break;
			}
			case BPPacket.VAL_TYPE_BOOLEAN: {
				Boolean v = (Boolean)value;
				ret = false;
				break;
			}
			default:
				/* do nothing */
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = true;
		}
		return ret;
	}
	public Object getSignalValue() {
		return signalValue;
	}
	public void setSignalValue(Object signalValue) {
		this.signalValue = signalValue;
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
