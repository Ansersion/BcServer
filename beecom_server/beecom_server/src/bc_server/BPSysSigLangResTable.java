/**
 * 
 */
package bc_server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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
public class BPSysSigLangResTable {
	List<List<List<String>>> SysSigLangRes_Lst;
	static BPSysSigLangResTable SysSigLangTab = null;
	static final int SYS_SIG_START_ADDR = 0xE000;

	public static BPSysSigLangResTable getSysSigLangResTable() {
		if (null == SysSigLangTab) {
			SysSigLangTab = new BPSysSigLangResTable();
		}
		return SysSigLangTab;
	}

	protected BPSysSigLangResTable() {
		SysSigLangRes_Lst = new ArrayList<List<List<String>>>();
	}

	public boolean loadTab() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(
				"/mnt/hgfs/share/sys_signal_language_resource.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader sys_sig_lang_in = new BufferedReader(isr);

		SysSigLangRes_Lst.clear();
		boolean ret = false;

		try {
			String s, s2 = new String();
			int counter = 0;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			sys_sig_lang_in.readLine();
			for (int i = 0; i < 16; i++) {

				List<List<String>> sig_lang_res_tmp = new ArrayList<List<String>>();

				while ((s = sys_sig_lang_in.readLine()) != null) {
					List<String> lang_res_tmp = new ArrayList<String>();
					s2 = s + "\n";
					Matcher m = r.matcher(s);
					if (m.find()) {

						if (m.group(2).length() == 0
								&& m.group(3).length() == 0
								&& m.group(4).length() == 0
								&& m.group(5).length() == 0
								&& m.group(6).length() == 0
								&& m.group(7).length() == 0) {
							continue;
						}
						lang_res_tmp.add(m.group(2));
						lang_res_tmp.add(m.group(3));
						lang_res_tmp.add(m.group(4));
						lang_res_tmp.add(m.group(5));
						lang_res_tmp.add(m.group(6));
						lang_res_tmp.add(m.group(7));
						sig_lang_res_tmp.add(lang_res_tmp);

						int sig_id = Integer.parseInt(m.group(1), 16);
						if (SYS_SIG_START_ADDR + i * 0x200 - 1 == sig_id) {
							System.out.println("Found value lang: " + sig_id);
							break;
						}

					} else {
						System.out.println("NO MATCH lang");
						break;
					}
				}
				SysSigLangRes_Lst.add(sig_lang_res_tmp);
			}
			
			/*
			for(int i = 0; i < SysSigLangRes_Lst.size(); i++) {
				for(int j = 0; j < SysSigLangRes_Lst.get(i).size(); j++) {
					for(int k = 0; k < SysSigLangRes_Lst.get(i).get(j).size(); k++) {
						System.out.println(""+i+","+j+","+k+":"+SysSigLangRes_Lst.get(i).get(j).get(k));
					}
				}
			}
			*/
			sys_sig_lang_in.close();
			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
	}
	
	public String getLangRes(int dist, int offset, int lang) {
		String ret = new String("");
		try {
			ret = SysSigLangRes_Lst.get(dist).get(offset).get(lang);
		}catch(Exception e) {
			e.printStackTrace();
			ret = "NULL";
		}
		return ret;
	}
	
	public String getLangRes(int sys_sig_id, int lang) {
		int dist = (sys_sig_id - SYS_SIG_START_ADDR) / 0x200;
		int offset = (sys_sig_id - SYS_SIG_START_ADDR) % 0x200;
		
		return getLangRes(dist, offset, lang);
	}
}
