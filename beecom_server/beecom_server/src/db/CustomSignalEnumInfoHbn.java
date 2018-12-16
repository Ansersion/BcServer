package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalEnumInfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Integer defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
	public CustomSignalEnumInfoHbn() {
		super();
		this.permission = BPPacket.SIGNAL_PERMISSION_CODE_RO;
		this.defVal = 0;
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
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Integer getDefVal() {
		return defVal;
	}
	public void setDefVal(Integer defVal) {
		this.defVal = defVal;
	}
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	public Long getCustomSignalId() {
		return customSignalId;
	}
	public void setCustomSignalId(Long customSignalId) {
		this.customSignalId = customSignalId;
	}

	@Override
	public String toString() {
		return "CustomSignalEnumInfoHbn [id=" + id + ", permission=" + permission + ", defVal=" + defVal
				+ ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_ENUM;
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
