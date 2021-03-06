package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalBooleanInfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Boolean defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
	public CustomSignalBooleanInfoHbn() {
		super();
		this.permission = BPPacket.SIGNAL_PERMISSION_CODE_RO;
		this.defVal = false;
		this.enStatistics = true;
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
	public Boolean getDefVal() {
		return defVal;
	}
	public void setDefVal(Boolean defVal) {
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
		return "CustomSignalBooleanInfoHbn [id=" + id + ", permission=" + permission + ", defVal=" + defVal
				+ ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_BOOLEAN;
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
	
    
}
