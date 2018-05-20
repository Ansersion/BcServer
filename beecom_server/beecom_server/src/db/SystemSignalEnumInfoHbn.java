package db;

public class SystemSignalEnumInfoHbn extends SystemSignalInterface {
    private Long id;
    private Short permission;
    private Integer defVal;
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
	public Integer getDefVal() {
		return defVal;
	}
	public void setDefVal(Integer defVal) {
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
		return "SystemSignalEnumInfoHbn [id=" + id + ", permission=" + permission + ", defVal=" + defVal
				+ ", groupLangId=" + groupLangId + ", enStatistics=" + enStatistics + ", systemSignalId="
				+ systemSignalId + "]";
	}
    
	@Override
	public int getValType() {
		return SystemSignalInterface.VAL_TYPE_ENUM;
	}
}
