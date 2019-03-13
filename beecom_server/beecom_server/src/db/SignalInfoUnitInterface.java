/**
 * 
 */
package db;

import java.util.Map;


/**
 * @author Ansersion
 *
 */
public interface SignalInfoUnitInterface {
	/* for system/custom signal*/
	boolean ifNotifying();
	int getSignalId();
	SignalInterface getSignalInterface();
	
	/* only for system signal */
	boolean ifConfigDef();
	
	/* only for custom signal */
	boolean ifAlarm();
	boolean ifDisplay();
	short getAlarmClass();
	short getAlarmDelayBef();
	short getAlarmDelayAft();
	Map<Integer, String> getSignalNameLangMap();
	Map<Integer, String> getGignalUnitLangMap();
	Map<Integer, String> getGroupLangMap();
	Map<Integer, Map<Integer, String> > getSignalEnumLangMap();
	boolean checkSignalValueUnformed(Byte valueType, Object value);
	boolean putSignalValue(Map.Entry<Integer, Map.Entry<Byte, Object>> entry);
	Object getSignalValue();
	void setSignalValue(Object value);


}
