package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class SystemSignalU32InfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Long minVal;
    private Long maxVal;
    private Long defVal;
    private Integer groupLangId;
    private Boolean enStatistics;
    private Long systemSignalId;
    
	public SystemSignalU32InfoHbn() {
		super();
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
	@Override
	public Integer getGroupLangId() {
		return groupLangId;
	}
	public void setGroupLangId(Integer groupLangId) {
		this.groupLangId = groupLangId;
	}
	@Override
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	@Override
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	@Override
	public Long getSystemSignalId() {
		return systemSignalId;
	}
	@Override
	public void setSystemSignalId(Long systemSignalId) {
		this.systemSignalId = systemSignalId;
	}
	@Override
	public String toString() {
		return "SystemSignalU32InfoHbn [id=" + id + ", permission=" + permission + ", minVal=" + minVal + ", maxVal="
				+ maxVal + ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", enStatistics=" + enStatistics
				+ ", systemSignalId=" + systemSignalId + "]";
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
