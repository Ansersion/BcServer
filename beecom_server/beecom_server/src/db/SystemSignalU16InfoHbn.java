package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class SystemSignalU16InfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Integer minVal;
    private Integer maxVal;
    private Integer defVal;
    private Integer groupLangId;
    private Boolean enStatistics;
    private Long systemSignalId;
    private Integer unitLangId;
    
	public SystemSignalU16InfoHbn() {
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
	public Integer getMinVal() {
		return minVal;
	}
	public void setMinVal(Integer minVal) {
		this.minVal = minVal;
	}
	public Integer getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Integer maxVal) {
		this.maxVal = maxVal;
	}
	public Integer getDefVal() {
		return defVal;
	}
	public void setDefVal(Integer defVal) {
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
	public Integer getUnitLangId() {
		return unitLangId;
	}
	@Override
	public void setUnitLangId(Integer unitLangId) {
		this.unitLangId = unitLangId;
	}
	
	@Override
	public String toString() {
		return "SystemSignalU16InfoHbn [id=" + id + ", permission=" + permission + ", minVal=" + minVal + ", maxVal="
				+ maxVal + ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", enStatistics=" + enStatistics
				+ ", systemSignalId=" + systemSignalId + ", unitLangId=" + unitLangId + "]";
	}
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_UINT16;
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
