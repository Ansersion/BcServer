package db;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import other.Util;

public class CustomSignalStringInfoHbn extends SignalInterface {
	private static final Logger logger = LoggerFactory.getLogger(CustomSignalStringInfoHbn.class); 
    private Long id;
    private Short permission;
    private String defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
    
	public CustomSignalStringInfoHbn() {
		super();
		permission = BPPacket.SIGNAL_PERMISSION_CODE_RO; // read
		defVal = "";
		enStatistics = true;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Short getPermission() {
		return permission;
	}
	@Override
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public String getDefVal() {
		return defVal;
	}
	public void setDefVal(String defVal) {
		this.defVal = defVal;
	}
	@Override
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	@Override
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	public Long getCustomSignalId() {
		return customSignalId;
	}
	@Override
	public void setCustomSignalId(Long customSignalId) {
		this.customSignalId = customSignalId;
	}

	@Override
	public String toString() {
		return "CustomSignalStringInfoHbn [id=" + id + ", permission=" + permission + ", defVal=" + defVal
				+ ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_STRING;
	}
	@Override
	public long saveToDb(Session session) {
		Long ret;
		try {
			ret = (Long)session.save(this);
		} catch(Exception e) {
			ret = -1L;
		}
		return ret;
	}
	@Override
	public Object getDefaultValue() {
		return getDefVal();
	}
	
	
	@Override
	public boolean checkSignalValueUnformed(Object v) {
		boolean ret = false;
		try {
			String value = (String) v;
			if(value.getBytes().length > BPPacket.MAX_STR_LENGTH) {
				ret = true;
			}
		} catch (Exception e) {
			ret = true;
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}
}
