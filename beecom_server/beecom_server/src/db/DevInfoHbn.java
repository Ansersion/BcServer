package db;

public class DevInfoHbn {
    private Long id;
    private Long snId;
    private Long adminId;
    private String password;
    private Short dailySigTabChangeTimes;
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
	public Short getDailySigTabChangeTimes() {
		return dailySigTabChangeTimes;
	}
	public void setDailySigTabChangeTimes(Short dailySigTabChangeTimes) {
		this.dailySigTabChangeTimes = dailySigTabChangeTimes;
	}
	@Override
	public String toString() {
		return "DevInfoHbn [id=" + id + ", snId=" + snId + ", adminId=" + adminId + ", password=" + password
				+ ", dailySigTabChangeTimes=" + dailySigTabChangeTimes + "]";
	}
    
    
}
