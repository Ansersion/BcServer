package db;

public class DevInfoHbn {
    private Long id;
    private Long snId;
    private Long adminId;
    private String password;
    private Long sigMapChksum;
    private Short dailySigTabChangeTimes;
    private Byte langSupportMask;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSnId() {
		return snId;
	}
	public void setSnId(Long snId) {
		this.snId = snId;
	}
	public Long getAdminId() {
		return adminId;
	}
	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getSigMapChksum() {
		return sigMapChksum;
	}
	public void setSigMapChksum(Long sigMapChksum) {
		this.sigMapChksum = sigMapChksum;
	}
	public Short getDailySigTabChangeTimes() {
		return dailySigTabChangeTimes;
	}
	public void setDailySigTabChangeTimes(Short dailySigTabChangeTimes) {
		this.dailySigTabChangeTimes = dailySigTabChangeTimes;
	}
	public Byte getLangSupportMask() {
		return langSupportMask;
	}
	public void setLangSupportMask(Byte langSupportMask) {
		this.langSupportMask = langSupportMask;
	}
	@Override
	public String toString() {
		return "DevInfoHbn [id=" + id + ", snId=" + snId + ", adminId=" + adminId + ", password=" + password
				+ ", sigMapChksum=" + sigMapChksum + ", dailySigTabChangeTimes=" + dailySigTabChangeTimes
				+ ", langSupportMask=" + langSupportMask + "]";
	}
	
    
    
}
