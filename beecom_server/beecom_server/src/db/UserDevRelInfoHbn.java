package db;

public class UserDevRelInfoHbn implements UserDevRelInfoInterface {
	private Long id;
    private Long userId;
    private Long snId;
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

	public Long getSnId() {
		return snId;
	}
	public void setSnId(Long snId) {
		this.snId = snId;
	}
	public Short getAuth() {
		return auth;
	}
	public void setAuth(Short auth) {
		this.auth = auth;
	}
	@Override
	public String toString() {
		return "UserDevRelInfoHbn [id=" + id + ", userId=" + userId + ", snId=" + snId + ", auth=" + auth + "]";
	}
	@Override
	public boolean ifAdminRelationship() {
		return false;
	}

    
    
}
