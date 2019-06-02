/**
 * 
 */
package sys_sig_table;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

public class BPSysLangResTable {

	private static final Logger logger = LoggerFactory.getLogger(BPSysLangResTable.class);
	
	public static BPSysLangResTable enumLangResTab;
	public static BPSysLangResTable unitLangResTab;
	public static BPSysLangResTable groupLangResTab;

	private List<List<String>> sysLangResLst;
	private BPSysLangResTable sysLangTab = null;
	private String csvFile;

	public BPSysLangResTable(String csvFilePath) {
		sysLangResLst = new ArrayList<>();
		this.csvFile = csvFilePath;
	}

	public boolean loadTab() throws FileNotFoundException {
		// FileInputStream fis = new FileInputStream("config/sys_enum_language_resource.csv");
		logger.info("loading " + csvFile);
		FileInputStream fis = new FileInputStream(csvFile);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);

		sysLangResLst.clear();
		boolean ret = false;

		try (BufferedReader sysLangIn = new BufferedReader(isr)) {
			String s;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			s = sysLangIn.readLine();
			s = sysLangIn.readLine();

			while ((s = sysLangIn.readLine()) != null) {
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
				sysLangResLst.add(langResTmp);
			}

			for (int i = 0; i < sysLangResLst.size(); i++) {
				for (int j = 0; j < sysLangResLst.get(i).size(); j++) {
					logger.info("{},{}: {}", i, j, sysLangResLst.get(i).get(j));
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
			ret = sysLangResLst.get(offset).get(lang);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = "NULL";
		}
		return ret;
	}

}
