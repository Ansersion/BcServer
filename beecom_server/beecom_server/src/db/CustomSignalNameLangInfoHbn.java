package db;

public class CustomSignalNameLangInfoHbn {
	private Long id;
    private String customSignalName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCustomSignalName() {
		return customSignalName;
	}
	public void setCustomSignalName(String customSignalName) {
		this.customSignalName = customSignalName;
	}
	@Override
	public String toString() {
		return "CustomSignalNameLangInfoHbn [id=" + id + ", customSignalName=" + customSignalName + "]";
	}
    
    
}
