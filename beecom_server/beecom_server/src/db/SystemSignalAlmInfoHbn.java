package db;

public class SystemSignalAlmInfoHbn {
    private Long id;
    private Byte almClass;
    private Short dlyBeforeAlm;
    private Short dlyAfterAlm;
    private Long systemSignalId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Byte getAlmClass() {
		return almClass;
	}
	public void setAlmClass(Byte almClass) {
		this.almClass = almClass;
	}
	public Short getDlyBeforeAlm() {
		return dlyBeforeAlm;
	}
	public void setDlyBeforeAlm(Short dlyBeforeAlm) {
		this.dlyBeforeAlm = dlyBeforeAlm;
	}
	public Short getDlyAfterAlm() {
		return dlyAfterAlm;
	}
	public void setDlyAfterAlm(Short dlyAfterAlm) {
		this.dlyAfterAlm = dlyAfterAlm;
	}
	public Long getSystemSignalId() {
		return systemSignalId;
	}
	public void setSystemSignalId(Long systemSignalId) {
		this.systemSignalId = systemSignalId;
	}
	@Override
	public String toString() {
		return "SystemSignalAlmInfoHbn [id=" + id + ", almClass=" + almClass + ", dlyBeforeAlm=" + dlyBeforeAlm
				+ ", dlyAfterAlm=" + dlyAfterAlm + ", customSignalId=" + systemSignalId + "]";
	}
    
    
}
