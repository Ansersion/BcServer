package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_server.BcServerHandler;
import db.UserInfoUnit;

public class BPUserSession extends BPSession {
	private static final Logger logger = LoggerFactory.getLogger(BPUserSession.class);
	
	private String userName;
	private String email;
	private String phone;
	private String password;
	private UserInfoUnit userInfoUnit;
	
	/*
	public BPUserSession() {
		super();
		this.userName = "";
		this.password = "";
		this.email = "";
		this.phone = "";
				
	}
	
	public BPUserSession(String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.email = "";
		this.phone = "";
	}
	*/
	
	public BPUserSession(IoSession session, UserInfoUnit userInfoUnit) {
		super(session);
		this.userInfoUnit = userInfoUnit;
		try {
			this.userName = this.userInfoUnit.getUserInfoHbn().getName();
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
	}
	
	@Override
	public boolean ifUserSession() {
		return true;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return userName;
	}

	public UserInfoUnit getUserInfoUnit() {
		return userInfoUnit;
	}
	
	
	
	
}
