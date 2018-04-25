package db;

public class CustomSignalI16InfoHbn {
    private Long id;
    private Long cusSigNameLangId;
    private Long cusSigUnitLangId;
    private Short permission;
    private Short minVal;
    private Short maxVal;
    private Short defVal;
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
		return "CustomSignalI16InfoHbn [id=" + id + ", cusSigNameLangId=" + cusSigNameLangId + ", cusSigUnitLangId="
				+ cusSigUnitLangId + ", permission=" + permission + ", minVal=" + minVal + ", maxVal=" + maxVal
				+ ", defVal=" + defVal + ", groupLangId=" + groupLangId + ", cusGroupLangId=" + cusGroupLangId
				+ ", enStatistics=" + enStatistics + ", customSignalId=" + customSignalId + "]";
	}
    
    
}
