/**
 * 
 */
package sys_sig_table;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;

/**
 * @author Ansersion
 * 
 */

public class BPSysEnmLangResTable {

	private static final Logger logger = LoggerFactory.getLogger(BPSysEnmLangResTable.class);

	List<List<String>> sysEnmLangResLst;
	static BPSysEnmLangResTable sysEnmLangTab = null;

	public static BPSysEnmLangResTable getSysEnmLangResTable() {
		if (null == sysEnmLangTab) {
			sysEnmLangTab = new BPSysEnmLangResTable();
		}
		return sysEnmLangTab;
	}

	protected BPSysEnmLangResTable() {
		sysEnmLangResLst = new ArrayList<>();
	}

	public boolean loadTab() throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream("config/sys_enum_language_resource.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

		sysEnmLangResLst.clear();
		boolean ret = false;

		try (BufferedReader sysEnumLangIn = new BufferedReader(isr)) {
			String s;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			s = sysEnumLangIn.readLine();
			s = sysEnumLangIn.readLine();

			while ((s = sysEnumLangIn.readLine()) != null) {
				List<String> langResTmp = new ArrayList<>();
				Matcher m = r.matcher(s);
				if (!m.find() || (m.group(2).length() == 0 && m.group(3).length() == 0 && m.group(4).length() == 0
						&& m.group(5).length() == 0 && m.group(6).length() == 0 && m.group(7).length() == 0)) {
					break;
				}

				langResTmp.add(m.group(2));
				langResTmp.add(m.group(3));
				langResTmp.add(m.group(4));
				langResTmp.add(m.group(5));
				langResTmp.add(m.group(6));
				langResTmp.add(m.group(7));
				sysEnmLangResLst.add(langResTmp);
			}

			for (int i = 0; i < sysEnmLangResLst.size(); i++) {
				for (int j = 0; j < sysEnmLangResLst.get(i).size(); j++) {
					logger.info("{},{}: {}", i, j, sysEnmLangResLst.get(i).get(j));
				}
			}

			ret = true;

		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = false;
		}

		return ret;
	}

	public String getLangRes(int offset, int lang) {
		String ret;
		try {
			ret = sysEnmLangResLst.get(offset).get(lang);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = "NULL";
		}
		return ret;
	}

}
