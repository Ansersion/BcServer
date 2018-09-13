package db;

public class AdminDevRelInfoUnit implements UserDevRelInfoInterface {

	private Long userId;
	private Long snId;
	private Short auth;
	
	public AdminDevRelInfoUnit(Long userId, Long snId, Short auth) {
		super();
		this.userId = userId;
		this.snId = snId;
		this.auth = auth;
	}

	@Override
	public boolean ifAdminRelationship() {
		return true;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	@Override
	public Long getSnId() {
		return snId;
	}

	@Override
	public Short getAuth() {
		return auth;
	}

}
