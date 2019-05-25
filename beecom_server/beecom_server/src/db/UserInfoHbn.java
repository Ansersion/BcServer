package db;

import java.sql.Timestamp;

public class UserInfoHbn {
    private Long id;
    private Timestamp mTime;
    private String name;
    private String eMail;
    private String phone;
    private Boolean ifDeveloper;
    private String password;
    
	public UserInfoHbn() {
		super();
	}
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isIfDeveloper() {
		return ifDeveloper;
	}
	public void setIfDeveloper(boolean ifDeveloper) {
		this.ifDeveloper = ifDeveloper;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Timestamp getmTime() {
		return mTime;
	}

	public void setmTime(Timestamp mTime) {
		this.mTime = mTime;
	}

	@Override
	public String toString() {
		return "UserInfoHbn [id=" + id + ", mTime=" + mTime + ", name=" + name + ", eMail=" + eMail + ", phone=" + phone
				+ ", ifDeveloper=" + ifDeveloper + ", password=" + password + "]";
	}


}
