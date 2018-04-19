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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.BPValue;
import other.Util;

/**
 * @author Ansersion
 * 
 */

public class BPSysSigTable {
	
	private static final Logger logger = LoggerFactory.getLogger(BPSysSigTable.class);
	
	
	public static int SID_ID_RESERVED = 0x0000;
	
	
	private List<SysSigInfo> sysSigInfoLst;

	static BPSysSigTable sysSigTab = null;
	public static int BP_SYS_SIG_SET_VERSION = 0;

	public static BPSysSigTable getSysSigTableInstance() {
		if (null == sysSigTab) {
			sysSigTab = new BPSysSigTable();
		}
		return sysSigTab;
	}

	protected BPSysSigTable() {
		sysSigInfoLst = new ArrayList<>();
	}
	
	public List<SysSigInfo> getSysSigInfoLst() {
		return sysSigInfoLst;
	}

	public boolean loadTab() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(
				"config/sys_sig_info_basic.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader sysSigIn = new BufferedReader(isr);

		sysSigInfoLst.clear();
		boolean ret = false;
		String str_tmp = new String("");

		boolean is_alm;
		/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
		byte val_type;
		int unit_lang_res;
		/* 0-ro, 1-rw */
		byte permission;
		byte accuracy;
		BPValue val_min;
		BPValue val_max;
		BPValue val_def;
		int classLangRes;
		Map<Integer, Integer> map_enm_lang_res = null;
		boolean en_statistics;
		// 0-note, 1-warning, 2-serious, 3-emergency
		byte alm_class = (byte) 0xFF;
		int dly_bef_alm = -1;
		int dly_aft_alm = -1;

		try {
			String s;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			int index;
			s = sysSigIn.readLine();
			s = sysSigIn.readLine();

			while ((s = sysSigIn.readLine()) != null) {
				Matcher m = r.matcher(s);
				index = 0;
				if (m.find()) {
					if (m.group(2).length() == 0 || m.group(3).length() == 0
							|| m.group(4).length() == 0
							|| m.group(5).length() == 0
							|| m.group(6).length() == 0
							|| m.group(7).length() == 0
							|| m.group(8).length() == 0
							|| m.group(9).length() == 0
							|| m.group(10).length() == 0
							|| m.group(11).length() == 0
							|| m.group(12).length() == 0
							|| m.group(13).length() == 0
							|| m.group(14).length() == 0
							|| m.group(15).length() == 0
							|| m.group(16).length() == 0
							|| m.group(17).length() == 0) {
						break;
					} else {
						if (0 == m.group(4).compareToIgnoreCase("YES")) {
							is_alm = true;
						} else if (0 == m.group(4).compareToIgnoreCase("NO")) {
							is_alm = false;
						} else {
							throw new Exception(m.group(1) + ": is alarm error");
						}
						/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
						if (0 == m.group(5).compareToIgnoreCase("UINT32")) {
							val_type = 0;
						} else if (0 == m.group(5)
								.compareToIgnoreCase("UINT16")) {
							val_type = 1;
						} else if (0 == m.group(5).compareToIgnoreCase("INT32")) {
							val_type = 2;
						} else if (0 == m.group(5).compareToIgnoreCase("INT16")) {
							val_type = 3;
						} else if (0 == m.group(5).compareToIgnoreCase("ENUM")) {
							val_type = 4;
						} else if (0 == m.group(5).compareToIgnoreCase("FLOAT")) {
							val_type = 5;
						} else if (0 == m.group(5)
								.compareToIgnoreCase("STRING")) {
							val_type = 6;
						} else {
							throw new Exception(m.group(1)
									+ ": value type error");
						}

						str_tmp = m.group(6);
						Scanner scanner_unit = new Scanner(str_tmp)
								.useDelimiter("ULR");
						
						try {
							if (scanner_unit.hasNext() == false) {
								throw new Exception(m.group(1)
										+ ": unit language resource error");
							}
							unit_lang_res = scanner_unit.nextInt();

							if (0 == m.group(7).compareToIgnoreCase("RO")) {
								permission = 0;
							} else if (0 == m.group(7)
									.compareToIgnoreCase("RW")) {
								permission = 1;
							} else {
								throw new Exception(m.group(1)
										+ ": is alarm error");
							}
						} catch (Exception e) {
							throw e;
						} finally {
							scanner_unit.close();
						}

						accuracy = (byte) Integer.parseInt(m.group(8));
						
						val_min = new BPValue(val_type);
						val_max = new BPValue(val_type);
						val_def = new BPValue(val_type);
						
						if(Util.isNull(m.group(9))) {
							val_min.setLimitValid(false);
						} else {
							val_min.setValStr(m.group(9));
						}
						
						if(Util.isNull(m.group(10))) {
							val_max.setLimitValid(false);
						} else {
							val_max.setValStr(m.group(10));
						}
						if(Util.isNull(m.group(11))) {
							val_def.setLimitValid(false);
						} else {
							val_def.setValStr(m.group(11));
						}
						
						if(!m.group(12).equals("NULL")) { 
							classLangRes = 1;
						} else {
							classLangRes = 0;
						}

						if(null != map_enm_lang_res) {
							map_enm_lang_res = null;
						}
						
						if (4 == val_type) {
							str_tmp = m.group(13);
							Scanner scanner_enm = new Scanner(str_tmp)
									.useDelimiter("/");
							try {
								map_enm_lang_res = new HashMap<Integer, Integer>();
								String enm_pattern_str = "\\[(\\d+)\\s*=\\s*ELR(\\d+)\\]";
								Pattern enm_pattern = Pattern
										.compile(enm_pattern_str);
								while (scanner_enm.hasNext()) {
									str_tmp = scanner_enm.next();
									Matcher enm_mat = enm_pattern
											.matcher(str_tmp);
									if (enm_mat.find()) {
										int enum_index = Integer
												.parseInt(enm_mat.group(1));
										int enum_lang_res_index = Integer
												.parseInt(enm_mat.group(2));
										map_enm_lang_res.put(enum_index,
												enum_lang_res_index);
									} else {
										scanner_enm.close();
										throw new Exception(
												"Error: parse enumeration error");
									}
								}
							} catch (Exception e) {
								throw e;
							} finally {
								scanner_enm.close();
							}
						}

						if (0 == m.group(14).compareToIgnoreCase("YES")) {
							en_statistics = true;
						} else if (0 == m.group(14).compareToIgnoreCase("NO")) {
							en_statistics = false;
						} else {
							throw new Exception(m.group(1) + ": enable statistics error");
						}
						if (is_alm) {
							alm_class = Byte.parseByte(m.group(15));
							dly_bef_alm = Integer.parseInt(m.group(16));
							dly_aft_alm = Integer.parseInt(m.group(17));
						}
					}
					sysSigInfoLst.add(new SysSigInfo(is_alm, val_type,
							unit_lang_res, permission, accuracy, val_min,
							val_max, val_def, classLangRes, map_enm_lang_res, en_statistics,
							alm_class, dly_bef_alm, dly_aft_alm));
				} else {
					logger.warn("NO MATCH lang");
					break;
				}
			}

			String s_debug = new String("");
			for (int i = 0; i < sysSigInfoLst.size(); i++) {

				s_debug += "" + i + ":" + sysSigInfoLst.get(i).IsAlm + ","
						+ sysSigInfoLst.get(i).ValType + ","
						+ sysSigInfoLst.get(i).UnitLangRes + ","
						+ sysSigInfoLst.get(i).Permission + ","
						+ sysSigInfoLst.get(i).Accuracy + ","
						+ sysSigInfoLst.get(i).ValMin.getValStr() + ","
						+ sysSigInfoLst.get(i).ValMax.getValStr() + ","
						+ sysSigInfoLst.get(i).ValDef.getValStr() + ",";
				if (null != sysSigInfoLst.get(i).MapEnmLangRes) {
					Iterator<Map.Entry<Integer, Integer>> entries = sysSigInfoLst
							.get(i).MapEnmLangRes.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Integer> entry = entries.next();
						s_debug += "" + entry.getKey() + "=>"
								+ entry.getValue() + ",";
					}
				} else {
					s_debug += "NULL,";
				}
				s_debug += sysSigInfoLst.get(i).EnStatistics + ","
						+ sysSigInfoLst.get(i).AlmClass + ","
						+ sysSigInfoLst.get(i).DlyBefAlm + ","
						+ sysSigInfoLst.get(i).DlyAftAlm + "\n";
			}
			logger.debug(s_debug);

			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				sysSigIn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}
}
