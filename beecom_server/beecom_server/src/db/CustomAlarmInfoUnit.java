package db;

import java.util.Map;

public class CustomAlarmInfoUnit {
	
	private Map<Integer, String> customAlarmNameLangMap;
	private CustomSignalAlmInfoHbn customSignalAlmInfoHbn;
	
	public CustomAlarmInfoUnit(Map<Integer, String> customAlarmNameLangMap,
			CustomSignalAlmInfoHbn customSignalAlmInfoHbn) {
		super();
		this.customAlarmNameLangMap = customAlarmNameLangMap;
		this.customSignalAlmInfoHbn = customSignalAlmInfoHbn;
	}
	public Map<Integer, String> getCustomAlarmNameLangMap() {
		return customAlarmNameLangMap;
	}
	public CustomSignalAlmInfoHbn getCustomSignalAlmInfoHbn() {
		return customSignalAlmInfoHbn;
	}

	

    
    
	
	
}
