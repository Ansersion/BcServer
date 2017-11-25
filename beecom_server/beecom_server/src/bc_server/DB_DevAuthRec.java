/**
 * 
 */
package bc_server;

import java.sql.Connection;

/**
 * @author Ansersion
 * 
 */
public class DB_DevAuthRec extends DB_BaseRec {
	long DevUniqId;
	int AdminUserId;
	byte AdminUserAuth;
	int UserId1;
	byte UserAuth1;
	int UserId2;
	byte UserAuth2;
	int UserId3;
	byte UserAuth3;
	int UserId4;
	byte UserAuth4;

	public DB_DevAuthRec() {
		DevUniqId = 0;
		AdminUserId = 0;
		AdminUserAuth = 0x7;
		UserId1 = 0;
		UserAuth1 = 0;
		UserId2 = 0;
		UserAuth2 = 0;
		UserId3 = 0;
		UserAuth3 = 0;
		UserId4 = 0;
		UserAuth4 = 0;
	}

	public DB_DevAuthRec(long dev_uniq_id, int admin_user_id, byte admin_user_auth,
			int user_id_1, byte user_auth_1, int user_id_2, byte user_auth_2,
			int user_id_3, byte user_auth_3, int user_id_4, byte user_auth_4) {
		DevUniqId = dev_uniq_id;
		AdminUserId = admin_user_id;
		AdminUserAuth = admin_user_auth;
		UserId1 = user_id_1;
		UserAuth1 = user_auth_1;
		UserId2 = user_id_2;
		UserAuth2 = user_auth_2;
		UserId3 = user_id_3;
		UserAuth3 = user_auth_3;
		UserId4 = user_id_4;
		UserAuth4 = user_auth_4;
	}

	public long getDevUniqId() {
		return DevUniqId;
	}
	
	public int getAdminUserId() {
		return AdminUserId;
	}
	
	public byte getAdminUserAuth() {
		return AdminUserAuth;
	}

	public int getUserId2() {
		return UserId2;
	}

	public byte getUserAuth2() {
		return UserAuth2;
	}

	public int getUserId3() {
		return UserId3;
	}

	public byte getUserAuth3() {
		return UserAuth3;
	}

	public int getUserId4() {
		return UserId4;
	}

	public byte getUserAuth4() {
		return UserAuth4;
	}

	public int getUserId1() {
		return UserId1;
	}

	public byte getUserAuth1() {
		return UserAuth1;
	}


	public void setDevUniqId(long dev_uniq_id) {
		DevUniqId = dev_uniq_id;
		setDirty();
	}

	public void setAdminUserId(int user_id) {
		AdminUserId = user_id;
		setDirty();
	}

	public void setAdminUserAuth(byte auth) {
		AdminUserAuth = auth;
		setDirty();
	}


	public void setUserId1(int user_id) {
		UserId1 = user_id;
		setDirty();
	}

	public void setUserAuth1(byte auth) {
		UserAuth1 = auth;
		setDirty();
	}

	public void setUserId2(int user_id) {
		UserId2 = user_id;
		setDirty();
	}

	public void setUserAuth2(byte auth) {
		UserAuth2 = auth;
		setDirty();
	}

	public void setUserId3(int user_id) {
		UserId3 = user_id;
		setDirty();
	}

	public void setUserAuth3(byte auth) {
		UserAuth3 = auth;
		setDirty();
	}

	public void setUserId4(int user_id) {
		UserId4 = user_id;
		setDirty();
	}

	public void setUserAuth4(byte auth) {
		UserAuth4 = auth;
		setDirty();
	}

	public void dumpRec() {
		System.out.println("DevUniqId: " + DevUniqId + 
				", AdminUserId: " + AdminUserId + 
				", AdminUserAuth: " + AdminUserAuth + 
				", UserId1: " + UserId1	+ 
				", UserAuth1: " + UserAuth1 + 
				", UserId2: " + UserId2	+ 
				", UserAuth2: " + UserAuth2 + 
				", UserId3: " + UserId3	+ 
				", UserAuth3: " + UserAuth3 + 
				", UserId4: " + UserId4	+ 
				", UserAuth4: " + UserAuth4);
	}
	

	public boolean updateRec(Connection con) {
		// TODO: overload this function
		return false;
	}
}
