package db;

import org.hibernate.Session;

import bp_packet.BPPacket;

public class CustomSignalU32InfoHbn extends SignalInterface {
    private Long id;
    private Long cusSigNameLangId;
    private Long cusSigUnitLangId;
    private Short permission;
    private Long minVal;
    private Long maxVal;
    private Long defVal;
    private Integer groupLangId;
    private Long cusGroupLangId;
    private Boolean enStatistics;
    private Long customSignalId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCusSigNameLangId() {
		return cusSigNameLangId;
	}
	public void setCusSigNameLangId(Long cusSigNameLangId) {
		this.cusSigNameLangId = cusSigNameLangId;
	}
	public Long getCusSigUnitLangId() {
		return cusSigUnitLangId;
	}
	public void setCusSigUnitLangId(Long cusSigUnitLangId) {
		this.cusSigUnitLangId = cusSigUnitLangId;
	}
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
	public Integer getGroupLangId() {
		return groupLangId;
	}
	public void setGroupLangId(Integer groupLangId) {
		this.groupLangId = groupLangId;
	}
	public Long getCusGroupLangId() {
		return cusGroupLangId;
	}
	public void setCusGroupLangId(Long cusGroupLangId) {
		this.cusGroupLangId = cusGroupLangId;
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
		return "CustomSignalU32InfoHbn [id=" + id + ", cusSigNameLangId=" + cusSigNameLangId + ", cusSigUnitLangId="
				+ cusSigUnitLangId + ", permission=" + permission + ", minVal=" + minVal + ", maxVal=" + maxVal
				+ ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", cusGroupLangId=" + cusGroupLangId
				+ ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
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
}
