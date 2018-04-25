package db;

public class UserDevRelInfoHbn {
	private Long id;
    private Long userId;
    private Long devId;
    private Short auth;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDevId() {
		return devId;
	}
	public void setDevId(Long devId) {
		this.devId = devId;
	}
	public Short getAuth() {
		return auth;
	}
	public void setAuth(Short auth) {
		this.auth = auth;
	}
	@Override
	public String toString() {
		return "UserDevRelInfoHbn [id=" + id + ", userId=" + userId + ", devId=" + devId + ", auth=" + auth + "]";
	}
    
    
}
