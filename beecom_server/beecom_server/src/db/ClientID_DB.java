/**
 * 
 */
package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 *
 */

class DevClientIds {
	
	public int AdminUserId;
	public int AdminDevClntId;
	public Map<Integer, Integer> ComUser2ClntIdMap;

	public DevClientIds() {
		AdminUserId = 0;
		AdminDevClntId = ClientID_DB.CLIENT_ID_INVALID;
		ComUser2ClntIdMap = new HashMap<Integer, Integer>();
	}

	public DevClientIds(int admin) {
		AdminUserId = admin;
		AdminDevClntId = ClientID_DB.CLIENT_ID_INIT;
		ComUser2ClntIdMap = new HashMap<Integer, Integer>();
	}

	public DevClientIds(int admin, int admin_dev_id) {
		AdminUserId = admin;
		AdminDevClntId = admin_dev_id;
		ComUser2ClntIdMap = new HashMap<Integer, Integer>();
	}
}

public class ClientID_DB {
	private static final Logger logger = LoggerFactory.getLogger(ClientID_DB.class);
	
	public static ClientID_DB CID_DB = null;
	public static short NextClientId = 1;
	public static final int MAX_DEV_NUM_FOR_USER = 64;
	public static final int CLIENT_ID_INVALID = 0xFFFFFFFF;
	public static final int CLIENT_ID_INIT = 0x1;

	private Map<Long, DevClientIds> UniqDevId2DevClntIdsMap = new HashMap<Long, DevClientIds>();
	private Map<Integer, List<Integer>> User2ClntIdLstMap = new HashMap<Integer, List<Integer>>();

	// TODO:
	private int getAdminUser(long uniq_id) {
		// search from DB
		return 0;
	}

	// used for dev-client to get id
	public int allocClntId(long uniq_id, int clnt_id) {
		int new_clnt_id = 0;
		try {
			int admin_user_id = getAdminUser(uniq_id);
			if(0 == admin_user_id) {
				throw new Exception("no admin user");
			}
			DevClientIds dev_clnt_ids;
			List<Integer> user_dev_clnt_id_lst;
			if (!UniqDevId2DevClntIdsMap.containsKey(uniq_id)) {
				dev_clnt_ids = new DevClientIds(admin_user_id);
				UniqDevId2DevClntIdsMap.put(uniq_id, dev_clnt_ids);
			} else {
				dev_clnt_ids = UniqDevId2DevClntIdsMap.get(uniq_id);
			}
			
			if(!User2ClntIdLstMap.containsKey(admin_user_id)) {
				user_dev_clnt_id_lst = new ArrayList<Integer>();
				User2ClntIdLstMap.put(admin_user_id, user_dev_clnt_id_lst);
			} else {
				user_dev_clnt_id_lst = User2ClntIdLstMap.get(admin_user_id);
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
	
	public void removeClntId(long uniq_id)
	{
		if (UniqDevId2DevClntIdsMap.containsKey(uniq_id)) {
			UniqDevId2DevClntIdsMap.remove(uniq_id);
		}
		
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
		logger.info("Info: Link to ClientID DB");
		// TODO: read from database
		NextClientId = 1;

	}

	public static ClientID_DB getInstance() {
		if (null == CID_DB) {
			CID_DB = new ClientID_DB();
		}
		return CID_DB;
	}

	public static int distributeID(int apply_for_id) {
		if (apply_for_id != 0) {
			// TODO: check the id applied for
			return apply_for_id;
		} else {
			return NextClientId++;
		}
	}

}
