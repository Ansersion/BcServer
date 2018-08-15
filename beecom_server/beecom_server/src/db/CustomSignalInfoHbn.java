package db;

public class CustomSignalInfoHbn {
    private Long id;
    private Boolean ifAlarm;
    private Short valType;
    private Long signalId;
    private Long cusSigNameLangId;
    private Long cusSigUnitLangId;
    private Integer groupLangId;
    private Long cusGroupLangId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIfAlarm() {
		return ifAlarm;
	}
	public void setIfAlarm(Boolean ifAlarm) {
		this.ifAlarm = ifAlarm;
	}
	public Short getValType() {
		return valType;
	}
	public void setValType(Short valType) {
		this.valType = valType;
	}
	public Long getSignalId() {
		return signalId;
	}
	public void setSignalId(Long signalId) {
		this.signalId = signalId;
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
	@Override
	public String toString() {
		return "CustomSignalInfoHbn [id=" + id + ", ifAlarm=" + ifAlarm + ", valType=" + valType + ", signalId="
				+ signalId + ", cusSigNameLangId=" + cusSigNameLangId + ", cusSigUnitLangId=" + cusSigUnitLangId
				+ ", groupLangId=" + groupLangId + ", cusGroupLangId=" + cusGroupLangId + "]";
	}

    
}
