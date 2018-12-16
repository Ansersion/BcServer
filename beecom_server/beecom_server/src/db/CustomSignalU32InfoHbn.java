package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalU32InfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Long minVal;
    private Long maxVal;
    private Long defVal;
    private Boolean enStatistics;
    private Long customSignalId;
    
	public CustomSignalU32InfoHbn() {
		super();
		permission = BPPacket.SIGNAL_PERMISSION_CODE_RO; // read
		minVal = BPPacket.VAL_U32_UNLIMIT;
		maxVal = BPPacket.VAL_U32_UNLIMIT;
		defVal = 0L;
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
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Long getMinVal() {
		return minVal;
	}
	public void setMinVal(Long minVal) {
		this.minVal = minVal;
	}
	public Long getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Long maxVal) {
		this.maxVal = maxVal;
	}
	public Long getDefVal() {
		return defVal;
	}
	public void setDefVal(Long defVal) {
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
		return "CustomSignalU32InfoHbn [id=" + id + ", permission=" + permission + ", minVal=" + minVal + ", maxVal="
				+ maxVal + ", defVal=" + defVal + ", enStatistics=" + enStatistics + ", customSignalId="
				+ customSignalId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_UINT32;
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
