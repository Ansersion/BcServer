package db;

public class SignalInfoHbn {
	private Long id;
    private Integer signalId;
    private Long devId;
    private Boolean notifying;
    private Boolean display;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getSignalId() {
		return signalId;
	}
	public void setSignalId(Integer signalId) {
		this.signalId = signalId;
	}
	public Long getDevId() {
		return devId;
	}
	public void setDevId(Long devId) {
		this.devId = devId;
	}
	public Boolean getNotifying() {
		return notifying;
	}
	public void setNotifying(Boolean notifying) {
		this.notifying = notifying;
	}
	public Boolean getDisplay() {
		return display;
	}
	public void setDisplay(Boolean display) {
		this.display = display;
	}
	@Override
	public String toString() {
		return "SignalInfoHbn [id=" + id + ", signalId=" + signalId + ", devId=" + devId + ", notifying=" + notifying
				+ ", display=" + display + "]";
	}
}
