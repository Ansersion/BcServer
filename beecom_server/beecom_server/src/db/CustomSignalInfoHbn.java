package db;

public class CustomSignalInfoHbn {
    private Long id;
    private Boolean ifAlarm;
    private Short valType;
    private Long signalId;
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
	@Override
	public String toString() {
		return "CustomSignalInfoHbn [id=" + id + ", ifAlarm=" + ifAlarm + ", valType=" + valType + ", signalId="
				+ signalId + "]";
	}
    
    
}
