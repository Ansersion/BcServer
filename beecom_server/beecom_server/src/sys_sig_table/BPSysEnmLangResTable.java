/**
 * 
 */
package sys_sig_table;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ansersion
 * 
 */
public class BPSysEnmLangResTable {
	List<List<String>> SysEnmLangRes_Lst;
	static BPSysEnmLangResTable SysEnmLangTab = null;

	public static BPSysEnmLangResTable getSysEnmLangResTable() {
		if (null == SysEnmLangTab) {
			SysEnmLangTab = new BPSysEnmLangResTable();
		}
		return SysEnmLangTab;
	}

	protected BPSysEnmLangResTable() {
		SysEnmLangRes_Lst = new ArrayList<List<String>>();
	}

	public boolean loadTab() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(
				// "/mnt/hgfs/share/sys_enum_language_resource.csv");
				"config/sys_enum_language_resource.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader sys_enm_lang_in = new BufferedReader(isr);

		SysEnmLangRes_Lst.clear();
		boolean ret = false;

		try {
			String s;
			int counter = 0;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			sys_enm_lang_in.readLine();
			sys_enm_lang_in.readLine();

			while ((s = sys_enm_lang_in.readLine()) != null) {
				List<String> lang_res_tmp = new ArrayList<String>();
				Matcher m = r.matcher(s);
				if (m.find()) {
					if (m.group(2).length() == 0 && m.group(3).length() == 0
							&& m.group(4).length() == 0
							&& m.group(5).length() == 0
							&& m.group(6).length() == 0
							&& m.group(7).length() == 0) {
						break;
					}
					lang_res_tmp.add(m.group(2));
					lang_res_tmp.add(m.group(3));
					lang_res_tmp.add(m.group(4));
					lang_res_tmp.add(m.group(5));
					lang_res_tmp.add(m.group(6));
					lang_res_tmp.add(m.group(7));
					SysEnmLangRes_Lst.add(lang_res_tmp);
				} else {
					System.out.println("NO MATCH lang");
					break;
				}
			}

			for (int i = 0; i < SysEnmLangRes_Lst.size(); i++) {
				for (int j = 0; j < SysEnmLangRes_Lst.get(i).size(); j++) {
					System.out.println("" + i + "," + j + ":"
							+ SysEnmLangRes_Lst.get(i).get(j));
				}
			}

			sys_enm_lang_in.close();
			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
	}

	public String getLangRes(int offset, int lang) {
		String ret = new String("");
		try {
			ret = SysEnmLangRes_Lst.get(offset).get(lang);
		} catch (Exception e) {
			e.printStackTrace();
			ret = "NULL";
		}
		return ret;
	}

}
