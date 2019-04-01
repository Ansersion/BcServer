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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import other.Util;

/**
 * @author Ansersion
 * 
 */
public class BPSysSigLangResTable {
	
	private static final Logger logger = LoggerFactory.getLogger(BPSysSigLangResTable.class);
	public static final int SYSTEM_SIGNAL_LANG_COLUMN_NUM = 7;
	
	List<List<List<String>>> sysSigLangResLst;
	static BPSysSigLangResTable sysSigLangTab = null;

	public static BPSysSigLangResTable getSysSigLangResTable() {
		if (null == sysSigLangTab) {
			sysSigLangTab = new BPSysSigLangResTable();
		}
		return sysSigLangTab;
	}

	protected BPSysSigLangResTable() {
		sysSigLangResLst = new ArrayList<>();
	}
	
	public boolean loadTab() {
		boolean ret = true;
		List<String> systemSignalTableList = new ArrayList<>();
		/* must be in order */
		/* 1st. basic system signal language resource */
		systemSignalTableList.add("config/sys_sig_info_basic_language_resource.csv");
		/* 2nd. temperature and humidity signal language resource */
		systemSignalTableList.add("config/sys_sig_info_temp_humidity_language_resource.csv");

		sysSigLangResLst.clear();
		try {
			for(int i = 0; i < systemSignalTableList.size(); i++) {
				if(!loadTab(systemSignalTableList.get(i))) {
					ret = false;
					break;
				}
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = false;
		}
		Util.bcDump3DepthList(sysSigLangResLst);
		
		return ret;
	}

	public boolean loadTab(String systemSignalLanguageResource) throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(systemSignalLanguageResource);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		boolean ret = false;
		String pattern = "^";
		for(int i = 0; i < SYSTEM_SIGNAL_LANG_COLUMN_NUM-1; i++) {
			pattern += "(.+),";
		}
		pattern += "(.+)$";
		Pattern r = Pattern.compile(pattern);

		try (BufferedReader sysSigLangIn = new BufferedReader(isr)){
			String s;
			s = sysSigLangIn.readLine();
			s = sysSigLangIn.readLine();
			List<List<String>> sigLangResTmp = new ArrayList<>();

			while ((s = sysSigLangIn.readLine()) != null) {
				List<String> langResTmp = new ArrayList<>();
				Matcher m = r.matcher(s);
				if (!m.find()) {
					break;
				}

				langResTmp.add(m.group(2));
				langResTmp.add(m.group(3));
				langResTmp.add(m.group(4));
				langResTmp.add(m.group(5));
				langResTmp.add(m.group(6));
				langResTmp.add(m.group(7));
				sigLangResTmp.add(langResTmp);

				// int sigId = Integer.parseInt(m.group(1), 16);
			}
			sysSigLangResLst.add(sigLangResTmp);
			ret = true;

		} catch (Exception e) {
            Util.bcLog(e, logger);
			ret = false;
		} 

		return ret;
	}
	
	public String getLangRes(int dist, int offset, int lang) {
		String ret;
		try {
			ret = sysSigLangResLst.get(dist).get(offset).get(lang);
		}catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = "NULL";
		}
		return ret;
	}
	
	public String getLangRes(int sysSigId, int lang) {
		int dist = (sysSigId - BPPacket.SYS_SIG_START_ID) / 0x200;
		int offset = (sysSigId - BPPacket.SYS_SIG_START_ID) % 0x200;
		
		return getLangRes(dist, offset, lang);
	}
}
