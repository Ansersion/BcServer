package db;

public class CustomSignalEnumLangInfoHbn {
    private Long id;
    private Integer enumKey;
    private String enumVal;
    private Integer cusSigEnmId;
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
	public String getEnumVal() {
		return enumVal;
	}
	public void setEnumVal(String enumVal) {
		this.enumVal = enumVal;
	}
	public Integer getCusSigEnmId() {
		return cusSigEnmId;
	}
	public void setCusSigEnmId(Integer cusSigEnmId) {
		this.cusSigEnmId = cusSigEnmId;
	}
	@Override
	public String toString() {
		return "CustomSignalEnumLangInfoHbn [id=" + id + ", enumKey=" + enumKey + ", enumVal=" + enumVal
				+ ", cusSigEnmId=" + cusSigEnmId + "]";
	}
    
    
}
