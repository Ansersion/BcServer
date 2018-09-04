package db;


public class SystemSignalInfoHbn {
    private Long id;
    private Boolean ifConfigDef;
    
    private Long signalId;
    
    public SystemSignalInfoHbn() {
    	
    }
    public SystemSignalInfoHbn(Long signalId) {
    	ifConfigDef = true;
    	this.signalId = signalId;
    }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIfConfigDef() {
		return ifConfigDef;
	}
	public void setIfConfigDef(Boolean ifConfigDef) {
		this.ifConfigDef = ifConfigDef;
	}
	public Long getSignalId() {
		return signalId;
	}
	public void setSignalId(Long signalId) {
		this.signalId = signalId;
	}
	@Override
	public String toString() {
		return "SystemSignalInfoHbn [id=" + id + ", ifConfigDef=" + ifConfigDef + ", signalId=" + signalId + "]";
	}
    
    
}
