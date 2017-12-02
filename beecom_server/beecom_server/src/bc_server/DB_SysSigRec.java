/**
 * 
 */
package bc_server;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Ansersion
 * 
 */
public class DB_SysSigRec extends DB_BaseRec {

	int SysSigTabId;
	List<Byte[]> SysSigEnableLst;
	static final int MAX_DIST_NUM = 16;

	public DB_SysSigRec() {
		SysSigTabId = 0;
		SysSigEnableLst = new ArrayList<Byte[]>();
	}

	// NOTE: ensure the capacity of sys_sig_enable_lst bigger than DIST_NUM
	public DB_SysSigRec(int sys_sig_tab_id, List<Byte[]> sys_sig_enable_lst) {
		SysSigTabId = sys_sig_tab_id;
		SysSigEnableLst = sys_sig_enable_lst;
		// SysSigEnableLst.ensu
	}

	// TODO: something wrong here
	public DB_SysSigRec(int sys_sig_tab_id, Byte[] sig_basic, Byte[] sig_temp,
			Byte[] sig_clean) {
		SysSigTabId = sys_sig_tab_id;
		SysSigEnableLst = new ArrayList<Byte[]>();
		SysSigEnableLst.add(sig_basic);
		SysSigEnableLst.add(sig_temp);
		SysSigEnableLst.add(sig_clean);
	}

	public DB_SysSigRec(int sys_sig_tab_id, byte[] sig_basic, byte[] sig_temp,
			byte[] sig_clean) {
		SysSigTabId = sys_sig_tab_id;

		if (null == sig_basic) {
			SysSigEnableLst.add(null);
		} else {
			SysSigEnableLst = new ArrayList<Byte[]>();
			Byte[] tmp1 = new Byte[sig_basic.length];
			for (int i = 0; i < sig_basic.length; i++) {
				tmp1[i] = sig_basic[i];
			}
			SysSigEnableLst.add(tmp1);
		}

		if (null == sig_temp) {
			SysSigEnableLst.add(null);
		} else {
			Byte[] tmp2 = new Byte[sig_temp.length];
			for (int i = 0; i < sig_temp.length; i++) {
				tmp2[i] = sig_temp[i];
			}
			SysSigEnableLst.add(tmp2);
		}

		if (null == sig_clean) {
			SysSigEnableLst.add(null);
		} else {
			Byte[] tmp3 = new Byte[sig_clean.length];
			for (int i = 0; i < sig_clean.length; i++) {
				tmp3[i] = sig_clean[i];
			}
			SysSigEnableLst.add(tmp3);
		}
	}

	public int getSysSigTabId() {
		return SysSigTabId;
	}

	public List<Byte[]> getSysSigEnablesLst() {
		return SysSigEnableLst;
	}

	public void setSysSigTabId(long sys_sig_tab_id) {
		SysSigTabId = (int) sys_sig_tab_id;
		setDirty();
	}

	public void setSysSigEnableLst(List<Byte[]> sys_sig_enable_lst) {
		SysSigEnableLst = sys_sig_enable_lst;
		setDirty();
	}

	public void setSysSigEnableLst(Map<Integer, Byte[]> sys_sig_enable_map) {
		Iterator<Map.Entry<Integer, Byte[]>> entries = sys_sig_enable_map
				.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte[]> entry = entries.next();
			int dist = entry.getKey();
			while (dist > SysSigEnableLst.size() - 1) {
				SysSigEnableLst.add(new Byte[1]);
			}
			SysSigEnableLst.set(dist, entry.getValue());
			setDirty();
		}
	}

	public void dumpRec() {
		try {
			String dump_sys_sig = new String("");
			dump_sys_sig += "SysSigTabId: " + SysSigTabId + "\n";
			for (int i = 0; i < SysSigEnableLst.size(); i++) {
				Byte[] sys_sig = SysSigEnableLst.get(i);
				if (null == sys_sig) {
					continue;
				}
				for (int j = 0; j < sys_sig.length; j++) {
					dump_sys_sig += Integer.toHexString(sys_sig[j])
							.toUpperCase() + " ";
				}
				dump_sys_sig += "\n";
			}
			System.out.println(dump_sys_sig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean updateRec(Connection con) {
		// TODO: overload this function
		return false;
	}

	public boolean insertRec(Connection con) {

		PreparedStatement ps = null;
		try {
			if(SysSigEnableLst.size() > MAX_DIST_NUM) {
				throw new Exception("SysSigEnableLst.size() > MAX_DIST_NUM");
			}
			String sql_key = new String("(sys_sig_tab_id");
			String sql_val = new String("values(" + SysSigTabId);
			for (int i = 0; i < SysSigEnableLst.size(); i++) {
				Byte[] sig_map_ori = SysSigEnableLst.get(i);
				if (null != sig_map_ori) {
					sql_key += "," + "ssd" + String.format("%02d", i);
					sql_val += "," + "?";
				} 
			}
			sql_key += ")";
			sql_val += ")";
			String sql = new String("insert into sys_sig_tab" + sql_key + " " + sql_val);
			ps = con.prepareStatement(sql);
			
			for (int i = 0; i < SysSigEnableLst.size(); i++) {
				Byte[] sig_map_ori = SysSigEnableLst.get(i);
				if (null != sig_map_ori) {
					byte[] sig_map = new byte[sig_map_ori.length];
					for (int j = 0; j < sig_map.length; j++) {
						sig_map[j] = sig_map_ori[j];
					}
					ps.setBytes(i + 1, sig_map);
				} 
			}
			ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
