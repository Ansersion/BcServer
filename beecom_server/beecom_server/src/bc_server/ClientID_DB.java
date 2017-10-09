/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class ClientID_DB {
	
	static ClientID_DB CID_DB = null;
	static short NextClientId = 1;
	
	private ClientID_DB() {
		System.out.println("Info: Link to ClientID DB");
		// TODO: read from database
		NextClientId = 1;

	}
	
	static ClientID_DB getInstance() {
		if(null == CID_DB) {
			CID_DB = new ClientID_DB();
		}
		return CID_DB;
	}
	
	static int distributeID(int apply_for_id) {
		if(apply_for_id != 0) {
			//TODO: check the id applied for
			return apply_for_id;
		} else {
			return NextClientId++;
		}
	}
	

}
