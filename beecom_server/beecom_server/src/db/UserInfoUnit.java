package db;

public class UserInfoUnit {
	private UserInfoHbn userInfoHbn;
	
	public UserInfoUnit() {
		this.userInfoHbn = null;
	}

	public UserInfoUnit(UserInfoHbn userInfoHbn) {
		super();
		this.userInfoHbn = userInfoHbn;
	}

	public UserInfoHbn getUserInfoHbn() {
		return userInfoHbn;
	}

	public void setUserInfoHbn(UserInfoHbn userInfoHbn) {
		this.userInfoHbn = userInfoHbn;
	}

	@Override
	public String toString() {
		return "UserInfoUnit [userInfoHbn=" + userInfoHbn + "]";
	}
	
	
}
