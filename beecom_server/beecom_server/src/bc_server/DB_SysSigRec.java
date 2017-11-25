/**
 * 
 */
package bc_server;

import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Ansersion
 * 
 */
public class DB_SysSigRec {

	int SysSigTabId;
	List<Blob> SysSigEnableLst;

	public DB_SysSigRec() {
		SysSigTabId = 0;
		SysSigEnableLst = new ArrayList<Blob>();
	}

	public DB_SysSigRec(int sys_sig_tab_id, List<Blob> sys_sig_enable_lst) {
		SysSigTabId = sys_sig_tab_id;
		SysSigEnableLst = sys_sig_enable_lst;
	}
	
	public DB_SysSigRec(int sys_sig_tab_id, Blob sig_basic, Blob sig_temp, Blob sig_clean) {
		SysSigTabId = sys_sig_tab_id;
		SysSigEnableLst = new ArrayList<Blob>();
		SysSigEnableLst.add(sig_basic);
		SysSigEnableLst.add(sig_temp);
		SysSigEnableLst.add(sig_clean);
	}

	public int getSysSigTabId() {
		return SysSigTabId;
	}

	public List<Blob> getSysSigEnablesLst() {
		return SysSigEnableLst;
	}

	public void setSysSigTabId(int sys_sig_tab_id) {
		SysSigTabId = sys_sig_tab_id;
	}

	public void setSysSigEnableLst(List<Blob> sys_sig_enable_lst) {
		SysSigEnableLst = sys_sig_enable_lst;
	}

	public void dumpRec() {
		try {
			String dump_sys_sig = new String("");
			dump_sys_sig += "SysSigTabId: " + SysSigTabId + "\n";
			for (int i = 0; i < SysSigEnableLst.size(); i++) {
				Blob sys_sig = SysSigEnableLst.get(i);
				InputStream fis = sys_sig.getBinaryStream();
				int next_byte = fis.read();
				while(next_byte >= 0) {
					dump_sys_sig += Integer.toHexString(next_byte).toUpperCase() + " ";
				}
				dump_sys_sig += "\n";
				fis.close();
			}
			System.out.println(dump_sys_sig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
