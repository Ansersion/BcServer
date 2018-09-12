package db;


public class SystemSignalInfoHbn {
    private Long id;
    private Integer customFlags;
    private Long signalId;
    
    public SystemSignalInfoHbn() {
    	
    }
    public SystemSignalInfoHbn(Long signalId) {
    	customFlags = 0;
    	this.signalId = signalId;
    }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getCustomFlags() {
		return customFlags;
	}
	public void setCustomFlags(Integer customFlags) {
		this.customFlags = customFlags;
	}
	public Long getSignalId() {
		return signalId;
	}
	public void setSignalId(Long signalId) {
		this.signalId = signalId;
	}
	@Override
	public String toString() {
		return "SystemSignalInfoHbn [id=" + id + ", customFlags=" + customFlags + ", signalId=" + signalId + "]";
	}
	
    
    
}
