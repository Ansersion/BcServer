/**
 * 
 */
package db;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 * 
 */
public class DBDevAuthRec extends DBBaseRec {
	
	private static final Logger logger = LoggerFactory.getLogger(DBDevAuthRec.class);
	
	
	long devUniqId;
	int adminUserId;
	byte adminUserAuth;
	int userId1;
	byte userAuth1;
	int userId2;
	byte userAuth2;
	int userId3;
	byte userAuth3;
	int userId4;
	byte userAuth4;

	public DBDevAuthRec() {
		devUniqId = 0;
		adminUserId = 0;
		adminUserAuth = 0x7;
		userId1 = 0;
		userAuth1 = 0;
		userId2 = 0;
		userAuth2 = 0;
		userId3 = 0;
		userAuth3 = 0;
		userId4 = 0;
		userAuth4 = 0;
	}

	public DBDevAuthRec(long devUniqId, int adminUserId, byte adminUserAuth,
			int userId1, byte userAuth1, int userId2, byte userAuth2,
			int userId3, byte userAuth3, int userId4, byte userAuth4) {
		this.devUniqId = devUniqId;
		this.adminUserId = adminUserId;
		this.adminUserAuth = adminUserAuth;
		this.userId1 = userId1;
		this.userAuth1 = userAuth1;
		this.userId2 = userId2;
		this.userAuth2 = userAuth2;
		this.userId3 = userId3;
		this.userAuth3 = userAuth3;
		this.userId4 = userId4;
		this.userAuth4 = userAuth4;
	}

	public long getDevUniqId() {
		return devUniqId;
	}
	
	public int getAdminUserId() {
		return adminUserId;
	}
	
	public byte getAdminUserAuth() {
		return adminUserAuth;
	}

	public int getUserId2() {
		return userId2;
	}

	public byte getUserAuth2() {
		return userAuth2;
	}

	public int getUserId3() {
		return userId3;
	}

	public byte getUserAuth3() {
		return userAuth3;
	}

	public int getUserId4() {
		return userId4;
	}

	public byte getUserAuth4() {
		return userAuth4;
	}

	public int getUserId1() {
		return userId1;
	}

	public byte getUserAuth1() {
		return userAuth1;
	}


	public void setDevUniqId(long devUniqId) {
		this.devUniqId = devUniqId;
		setDirty();
	}

	public void setAdminUserId(int userId) {
		this.adminUserId = userId;
		setDirty();
	}

	public void setAdminUserAuth(byte auth) {
		this.adminUserAuth = auth;
		setDirty();
	}


	public void setUserId1(int userId) {
		this.userId1 = userId;
		setDirty();
	}

	public void setUserAuth1(byte auth) {
		this.userAuth1 = auth;
		setDirty();
	}

	public void setUserId2(int userId) {
		this.userId2 = userId;
		setDirty();
	}

	public void setUserAuth2(byte auth) {
		this.userAuth2 = auth;
		setDirty();
	}

	public void setUserId3(int userId) {
		this.userId3 = userId;
		setDirty();
	}

	public void setUserAuth3(byte auth) {
		this.userAuth3 = auth;
		setDirty();
	}

	public void setUserId4(int userId) {
		this.userId4 = userId;
		setDirty();
	}

	public void setUserAuth4(byte auth) {
		this.userAuth4 = auth;
		setDirty();
	}

	public void dumpRec() {
		logger.info("DevUniqId: {}, AdminUserId: {}, AdminUserAuth: {}, UserId1: {}, UserAuth1: {}, UserId2: {}, UserAuth2: {}, UserId3: {}, UserAuth3: {}, UserId4: {}, UserAuth4: {}", 
				devUniqId, adminUserId, adminUserAuth, userId1, userAuth1, userId2, userAuth2, userId3, userAuth3, userId4, userAuth4);
	}
	

	@Override
	public boolean updateRec(Connection con) {
		return false;
	}
}
