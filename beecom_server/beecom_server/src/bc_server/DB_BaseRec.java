/**
 * 
 */
package bc_server;

import java.sql.Connection;

/**
 * @author Ansersion
 *
 */
public class DB_BaseRec {
	
	boolean IsDirty;
	
	public DB_BaseRec() {
		IsDirty = false;
	}
	
	public void setDirty() {
		IsDirty = true;
	}
	
	public void clrDirty() {
		IsDirty = false;
	}

	public boolean updateRec(Connection con) {
		// TODO: overload this function
		return false;
	}
}
