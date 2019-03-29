/**
 * 
 */
package db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPParseException;
import other.Util;

/**
 * @author Ansersion
 *
 */

class DevClientIds {
	
	private int adminUserId;
	private int adminDevClntId;
	private Map<Integer, Integer> comUser2ClntIdMap;
	
	public DevClientIds() {
		adminUserId = 0;
		adminDevClntId = ClientIDDB.CLIENT_ID_INVALID;
		comUser2ClntIdMap = new HashMap<>();
	}

	public DevClientIds(int admin) {
		adminUserId = admin;
		adminDevClntId = ClientIDDB.CLIENT_ID_INIT;
		comUser2ClntIdMap = new HashMap<>();
	}

	public DevClientIds(int admin, int adminDevId) {
		adminUserId = admin;
		adminDevClntId = adminDevId;
		comUser2ClntIdMap = new HashMap<>();
	}

	public int getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}

	public int getAdminDevClntId() {
		return adminDevClntId;
	}

	public void setAdminDevClntId(int adminDevClntId) {
		this.adminDevClntId = adminDevClntId;
	}

	public Map<Integer, Integer> getComUser2ClntIdMap() {
		return comUser2ClntIdMap;
	}

	public void setComUser2ClntIdMap(Map<Integer, Integer> comUser2ClntIdMap) {
		this.comUser2ClntIdMap = comUser2ClntIdMap;
	}
	
	
}

public class ClientIDDB {
	private static final Logger logger = LoggerFactory.getLogger(ClientIDDB.class);
	
	private static ClientIDDB cidDb = null;
	private static short netxClientId = 1;
	public static final int MAX_DEV_NUM_FOR_USER = 64;
	public static final int CLIENT_ID_INVALID = 0xFFFFFFFF;
	public static final int CLIENT_ID_INIT = 0x1;

	private Map<Long, DevClientIds> uniqDevId2DevClntIdsMap = new HashMap<>();
	private Map<Integer, List<Integer>> user2ClntIdLstMap = new HashMap<>();

	private int getAdminUser(long uniqId) {
		return 0;
	}

	// used for dev-client to get id
	public int allocClntId(long uniqId, int clntId) {
		int newClntId = 0;
		try {
			int adminUserId = getAdminUser(uniqId);
			if(0 == adminUserId) {
				throw new BPParseException("no admin user");
			}
			DevClientIds devClntIds;
			List<Integer> userDevClntIdLst;
			if (!uniqDevId2DevClntIdsMap.containsKey(uniqId)) {
				devClntIds = new DevClientIds(adminUserId);
				uniqDevId2DevClntIdsMap.put(uniqId, devClntIds);
			} else {
				devClntIds = uniqDevId2DevClntIdsMap.get(uniqId);
			}
			
			if(!user2ClntIdLstMap.containsKey(adminUserId)) {
				userDevClntIdLst = new ArrayList<>();
				user2ClntIdLstMap.put(adminUserId, userDevClntIdLst);
			} else {
				userDevClntIdLst = user2ClntIdLstMap.get(adminUserId);
			}
			
			if(userDevClntIdLst.size() > MAX_DEV_NUM_FOR_USER) {
				throw new BPParseException("Err: MAX_DEV_NUM_FOR_USER");
			}
			
			newClntId = allocClntId(userDevClntIdLst, devClntIds);
			
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			newClntId = CLIENT_ID_INVALID;
		}

		// User2ClntIdLstMap

		return newClntId;
	}
	
	public void removeClntId(long uniqId)
	{
		if (uniqDevId2DevClntIdsMap.containsKey(uniqId)) {
			uniqDevId2DevClntIdsMap.remove(uniqId);
		}
		
	}
	
	private int allocClntId(List<Integer> userDevClntIdLst, DevClientIds devClientIds)
	{
		int newClntId;
		if(userDevClntIdLst.isEmpty()) {
			newClntId = CLIENT_ID_INIT;
		} else {
			newClntId = userDevClntIdLst.get(userDevClntIdLst.size() - 1) + 1;
		}
		userDevClntIdLst.add(newClntId);
		devClientIds.setAdminDevClntId(newClntId);
			
		return newClntId;
	}

	private ClientIDDB() {
		String s = "Info: Link to ClientID DB";
		logger.info(s);
		netxClientId = 1;

	}

	public static ClientIDDB getInstance() {
		if (null == cidDb) {
			cidDb = new ClientIDDB();
		}
		return cidDb;
	}

	public static int distributeID(int applyForId) {
		if (applyForId != 0) {
			return applyForId;
		} else {
			return netxClientId++;
		}
	}

}
