package db;

public class CustomSignalNameLangInfoHbn {
	private Long id;
    private Long customSignalName;
    
    public CustomSignalNameLangInfoHbn() {
    	
    }
    

	public CustomSignalNameLangInfoHbn(Long customSignalName) {
		super();
		this.customSignalName = customSignalName;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCustomSignalName() {
		return customSignalName;
	}
	public void setCustomSignalName(Long customSignalName) {
		this.customSignalName = customSignalName;
	}
	@Override
	public String toString() {
		return "CustomSignalNameLangInfoHbn [id=" + id + ", customSignalName=" + customSignalName + "]";
	}
    
    
}
