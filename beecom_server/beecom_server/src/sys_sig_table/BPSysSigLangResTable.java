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

import bp_packet.BPPacket;
import other.Util;

/**
 * @author Ansersion
 * 
 */
public class BPSysSigLangResTable {
	
	private static final Logger logger = LoggerFactory.getLogger(BPSysSigLangResTable.class);
	
	
	List<List<List<String>>> sysSigLangResLst;
	static BPSysSigLangResTable sysSigLangTab = null;
	static final int SYS_SIG_START_ADDR = 0xE000;

	public static BPSysSigLangResTable getSysSigLangResTable() {
		if (null == sysSigLangTab) {
			sysSigLangTab = new BPSysSigLangResTable();
		}
		return sysSigLangTab;
	}

	protected BPSysSigLangResTable() {
		sysSigLangResLst = new ArrayList<>();
	}

	public boolean loadTab() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream("config/sys_signal_language_resource.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		sysSigLangResLst.clear();
		boolean ret = false;
		String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
		Pattern r = Pattern.compile(pattern);

		try (BufferedReader sysSigLangIn = new BufferedReader(isr)){
			String s;
			s = sysSigLangIn.readLine();
			for (int i = 0; i < 16; i++) {

				List<List<String>> sigLangResTmp = new ArrayList<>();
				boolean foundDistLastSig = false;

				while (!foundDistLastSig && (s = sysSigLangIn.readLine()) != null) {
					List<String> langResTmp = new ArrayList<>();
					Matcher m = r.matcher(s);
					if(!m.find()) {
						break;
					}
	
					langResTmp.add(m.group(2));
					langResTmp.add(m.group(3));
					langResTmp.add(m.group(4));
					langResTmp.add(m.group(5));
					langResTmp.add(m.group(6));
					langResTmp.add(m.group(7));
					sigLangResTmp.add(langResTmp);

					int sigId = Integer.parseInt(m.group(1), 16);

					if (SYS_SIG_START_ADDR + i * BPPacket.SYS_SIG_DIST_STEP - 1 == sigId) {
						foundDistLastSig = true;
					}
				}
				sysSigLangResLst.add(sigLangResTmp);
			}
			
			Util.bcDump3DepthList(sysSigLangResLst);
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			ret = "NULL";
		}
		return ret;
	}
	
	public String getLangRes(int sysSigId, int lang) {
		int dist = (sysSigId - SYS_SIG_START_ADDR) / 0x200;
		int offset = (sysSigId - SYS_SIG_START_ADDR) % 0x200;
		
		return getLangRes(dist, offset, lang);
	}
}
