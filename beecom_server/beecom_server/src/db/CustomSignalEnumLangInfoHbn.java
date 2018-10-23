package db;

public class CustomSignalEnumLangInfoHbn {
    private Long id;
    private Integer enumKey;
    private Long enumValLangId;
    private Long cusSigEnmId;
    
	public CustomSignalEnumLangInfoHbn() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getEnumKey() {
		return enumKey;
	}
	public void setEnumKey(Integer enumKey) {
		this.enumKey = enumKey;
	}
	public Long getEnumValLangId() {
		return enumValLangId;
	}
	public void setEnumValLangId(Long enumValLangId) {
		this.enumValLangId = enumValLangId;
	}
	public Long getCusSigEnmId() {
		return cusSigEnmId;
	}
	public void setCusSigEnmId(Long cusSigEnmId) {
		this.cusSigEnmId = cusSigEnmId;
	}
	@Override
	public String toString() {
		return "CustomSignalEnumLangInfoHbn [id=" + id + ", enumKey=" + enumKey + ", enumVal=" + enumValLangId
				+ ", cusSigEnmId=" + cusSigEnmId + "]";
	}
    
    
}
