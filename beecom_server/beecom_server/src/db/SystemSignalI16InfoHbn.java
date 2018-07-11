package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class SystemSignalI16InfoHbn extends SignalInterface {
    private Long id;
    private Short permission;
    private Short minVal;
    private Short maxVal;
    private Short defVal;
    private Integer groupLangId;
    private Boolean enStatistics;
    private Long systemSignalId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Short getPermission() {
		return permission;
	}
	public void setPermission(Short permission) {
		this.permission = permission;
	}
	public Short getMinVal() {
		return minVal;
	}
	public void setMinVal(Short minVal) {
		this.minVal = minVal;
	}
	public Short getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Short maxVal) {
		this.maxVal = maxVal;
	}
	public Short getDefVal() {
		return defVal;
	}
	public void setDefVal(Short defVal) {
		this.defVal = defVal;
	}
	public Integer getGroupLangId() {
		return groupLangId;
	}
	public void setGroupLangId(Integer groupLangId) {
		this.groupLangId = groupLangId;
	}
	public Boolean getEnStatistics() {
		return enStatistics;
	}
	public void setEnStatistics(Boolean enStatistics) {
		this.enStatistics = enStatistics;
	}
	public Long getSystemSignalId() {
		return systemSignalId;
	}
	public void setSystemSignalId(Long systemSignalId) {
		this.systemSignalId = systemSignalId;
	}
	@Override
	public String toString() {
		return "SystemSignalI16InfoHbn [id=" + id + ", permission=" + permission + ", minVal=" + minVal + ", maxVal="
				+ maxVal + ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", enStatistics=" + enStatistics
				+ ", systemSignalId=" + systemSignalId + "]";
	}
    
	@Override
	public int getValType() {
		return BPPacket.VAL_TYPE_IINT16;
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
    
}
