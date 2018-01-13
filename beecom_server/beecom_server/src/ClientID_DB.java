/**
 * 
 */
package bc_server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ansersion
 *
 */

class DevClientIds {
	// public long DevUniqId;
	public String Admin;
	public int AdminDevClntId;
	public Map<String, Integer> ComUser2ClntIdMap;

	public DevClientIds() {
		Admin = new String("");
		AdminDevClntId = ClientID_DB.CLIENT_ID_INVALID;
		ComUser2ClntIdMap = new HashMap<String, Integer>();
	}

	public DevClientIds(String admin) {
		Admin = admin;
		AdminDevClntId = ClientID_DB.CLIENT_ID_INIT;
		ComUser2ClntIdMap = new HashMap<String, Integer>();
	}

	public DevClientIds(String admin, int admin_dev_id) {
		Admin = admin;
		AdminDevClntId = admin_dev_id;
		ComUser2ClntIdMap = new HashMap<String, Integer>();
	}
}

public class ClientID_DB {

	public static ClientID_DB CID_DB = null;
	public static short NextClientId = 1;
	public static final int MAX_DEV_NUM_FOR_USER = 64;
	public static final int CLIENT_ID_INVALID = 0xFFFFFFFF;
	public static final int CLIENT_ID_INIT = 0x1;

	private Map<Long, DevClientIds> UniqDevId2DevClntIdsMap = new HashMap<Long, DevClientIds>();
	private Map<String, List<Integer>> User2ClntIdLstMap = new HashMap<String, List<Integer>>();

	// TODO:
	private String getAdminUser(long uniq_id) {
		// search from DB
		return "";
	}

	/*
	 * private int allockAdminClntId(String admin) {
	 * if(!UserClntIdMap.containsKey(admin)) { return CLIENT_ID_INVALID; } return
	 * UserClntIdMap.get(admin); }
	 */

	// used for dev-client to get id
	public int allocClntId(long uniq_id, int clnt_id) {
		int new_clnt_id = 0;
		try {
			String admin_user = getAdminUser(uniq_id);
			DevClientIds dev_clnt_ids;
			List<Integer> user_dev_clnt_id_lst;
			if (!UniqDevId2DevClntIdsMap.containsKey(uniq_id)) {
				dev_clnt_ids = new DevClientIds(admin_user);
				UniqDevId2DevClntIdsMap.put(uniq_id, dev_clnt_ids);
			} else {
				dev_clnt_ids = UniqDevId2DevClntIdsMap.get(uniq_id);
			}
			
			if(!User2ClntIdLstMap.containsKey(admin_user)) {
				user_dev_clnt_id_lst = new ArrayList<Integer>();
				User2ClntIdLstMap.put(admin_user, user_dev_clnt_id_lst);
			} else {
				user_dev_clnt_id_lst = User2ClntIdLstMap.get(admin_user);
			}
			
			if(user_dev_clnt_id_lst.size() > MAX_DEV_NUM_FOR_USER) {
				throw new Exception("Err: MAX_DEV_NUM_FOR_USER");
			}
			
			new_clnt_id = allocClntId(user_dev_clnt_id_lst, dev_clnt_ids);
			
		} catch (Exception e) {
			e.printStackTrace();
			new_clnt_id = CLIENT_ID_INVALID;
		}

		// User2ClntIdLstMap

		return new_clnt_id;
	}
	
	private int allocClntId(List<Integer> user_dev_clnt_id_lst, DevClientIds dev_client_ids)
	{
		int new_clnt_id;
		if(user_dev_clnt_id_lst.isEmpty()) {
			new_clnt_id = CLIENT_ID_INIT;
		} else {
			new_clnt_id = user_dev_clnt_id_lst.get(user_dev_clnt_id_lst.size() - 1) + 1;
		}
		user_dev_clnt_id_lst.add(new_clnt_id);
		dev_client_ids.AdminDevClntId = new_clnt_id;
			
		return new_clnt_id;
	}

	private ClientID_DB() {
		System.out.println("Info: Link to ClientID DB");
		// TODO: read from database
		NextClientId = 1;

	}

	static ClientID_DB getInstance() {
		if (null == CID_DB) {
			CID_DB = new ClientID_DB();
		}
		return CID_DB;
	}

	static int distributeID(int apply_for_id) {
		if (apply_for_id != 0) {
			// TODO: check the id applied for
			return apply_for_id;
		} else {
			return NextClientId++;
		}
	}

}
