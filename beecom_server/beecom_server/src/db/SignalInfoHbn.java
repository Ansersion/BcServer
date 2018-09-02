package db;

public class SignalInfoHbn {
	private Long id;
    private Integer signalId;
    private Long devId;
    private Boolean notifying;
    private Boolean display;
    private Short almClass;
    private Short almDlyBef;
    private Short almDlyAft;
    
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
	public Short getAlmClass() {
		return almClass;
	}
	public void setAlmClass(Short almClass) {
		this.almClass = almClass;
	}
	public Short getAlmDlyBef() {
		return almDlyBef;
	}
	public void setAlmDlyBef(Short almDlyBef) {
		this.almDlyBef = almDlyBef;
	}
	public Short getAlmDlyAft() {
		return almDlyAft;
	}
	public void setAlmDlyAft(Short almDlyAft) {
		this.almDlyAft = almDlyAft;
	}
	@Override
	public String toString() {
		return "SignalInfoHbn [id=" + id + ", signalId=" + signalId + ", devId=" + devId + ", notifying=" + notifying
				+ ", display=" + display + ", almClass=" + almClass + ", almDlyBef=" + almDlyBef + ", almDlyAft="
				+ almDlyAft + "]";
	}
	
}
