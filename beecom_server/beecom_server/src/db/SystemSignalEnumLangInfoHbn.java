package db;

public class SystemSignalEnumLangInfoHbn {
    private Long id;
    private Integer enumKey;
    private String enumVal;
    private Long sysSigEnmId;
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
	public Long getSysSigEnmId() {
		return sysSigEnmId;
	}
	public void setSysSigEnmId(Long sysSigEnmId) {
		this.sysSigEnmId = sysSigEnmId;
	}
	@Override
	public String toString() {
		return "SystemSignalEnumLangInfoHbn [id=" + id + ", enumKey=" + enumKey + ", enumVal=" + enumVal
				+ ", sysSigEnmId=" + sysSigEnmId + "]";
	}
    
    
}
