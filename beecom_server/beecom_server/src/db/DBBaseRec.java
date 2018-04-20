/**
 * 
 */
package db;

import java.sql.Connection;

/**
 * @author Ansersion
 *
 */
public class DBBaseRec {
	
	boolean isDirty;
	
	public DBBaseRec() {
		isDirty = false;
	}
	
	public void setDirty() {
		isDirty = true;
	}
	
	public void clrDirty() {
		isDirty = false;
	}

	public boolean updateRec(Connection con) {
		return false;
	}
}
