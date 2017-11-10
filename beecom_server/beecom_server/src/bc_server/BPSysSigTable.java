/**
 * 
 */
package bc_server;

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

/**
 * @author Ansersion
 * 
 */

class SysSigInfo {
	// int SigId;
	// byte[] MacroName;
	boolean IsAlm;
	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
	byte ValType;
	int UnitLangRes;
	/* 0-ro, 1-rw */
	byte Permission;
	byte Accuracy;
	BPValue ValMin;
	BPValue ValMax;
	BPValue ValDef;
	Map<Integer, Integer> MapEnmLangRes;
	boolean EnStatistics;
	// 0-note, 1-warning, 2-serious, 3-emergency
	byte AlmClass;
	int DlyBefAlm;
	int DlyAftAlm;

	public SysSigInfo(boolean is_alm, byte val_type, int unit_lang_res,
			byte permission, byte accuracy, BPValue val_min, BPValue val_max,
			BPValue val_def, Map<Integer, Integer> map_enm_lang_res,
			boolean en_statistics, byte alm_class, int dly_bef_alm,
			int dly_aft_alm) {

		IsAlm = is_alm;
		ValType = val_type;
		UnitLangRes = unit_lang_res;
		Permission = permission;
		Accuracy = accuracy;
		ValMin = val_min;
		ValMax = val_max;
		ValDef = val_def;
		MapEnmLangRes = map_enm_lang_res;
		EnStatistics = en_statistics;
		AlmClass = alm_class;
		DlyBefAlm = dly_bef_alm;
		DlyAftAlm = dly_aft_alm;
	}

	// public static
}

public class BPSysSigTable {
	List<SysSigInfo> SysSigInfo_Lst;

	static BPSysSigTable SysSigTab = null;

	public static BPSysSigTable getSysSigTable() {
		if (null == SysSigTab) {
			SysSigTab = new BPSysSigTable();
		}
		return SysSigTab;
	}

	protected BPSysSigTable() {
		SysSigInfo_Lst = new ArrayList<SysSigInfo>();
	}

	public boolean loadTab() throws FileNotFoundException,
			UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(
				"/mnt/hgfs/share/sys_sig_info.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader sys_sig_in = new BufferedReader(isr);

		SysSigInfo_Lst.clear();
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
		Map<Integer, Integer> map_enm_lang_res = null;
		boolean en_statistics;
		// 0-note, 1-warning, 2-serious, 3-emergency
		byte alm_class = (byte) 0xFF;
		int dly_bef_alm = -1;
		int dly_aft_alm = -1;

		try {
			String s;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			sys_sig_in.readLine();
			sys_sig_in.readLine();

			while ((s = sys_sig_in.readLine()) != null) {
				// List<String> lang_res_tmp = new ArrayList<String>();
				Matcher m = r.matcher(s);
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
							|| m.group(16).length() == 0) {
						break;
					} else {
						if (0 == m.group(4).compareToIgnoreCase("YES")) {
							is_alm = true;
						} else if (0 == m.group(4).compareToIgnoreCase("NO")) {
							is_alm = false;
						} else {
							sys_sig_in.close();
							// is_alm = false;
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
							sys_sig_in.close();
							throw new Exception(m.group(1)
									+ ": value type error");
						}

						str_tmp = m.group(6);
						Scanner scanner_unit = new Scanner(str_tmp)
								.useDelimiter("ULR");
						
						try {
							if (scanner_unit.hasNext() == false) {
								sys_sig_in.close();
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
								sys_sig_in.close();
								throw new Exception(m.group(1)
										+ ": is alarm error");
							}
						} catch (Exception e) {
							throw e;
						} finally {
							scanner_unit.close();
						}

						accuracy = (byte) Integer.parseInt(m.group(8));
						
						val_min = new BPValue();
						val_max = new BPValue();
						val_def = new BPValue();
						
						val_min.setValueType(val_type);
						val_max.setValueType(val_type);
						val_def.setValueType(val_type);

						val_min.setVal(m.group(9));
						val_max.setVal(m.group(10));
						val_def.setVal(m.group(11));

						if (4 == val_type) {
							str_tmp = m.group(12);
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
										sys_sig_in.close();
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

						if (0 == m.group(13).compareToIgnoreCase("YES")) {
							en_statistics = true;
						} else if (0 == m.group(13).compareToIgnoreCase("NO")) {
							en_statistics = false;
						} else {
							sys_sig_in.close();
							throw new Exception(m.group(1) + ": enable statistics error");
							// en_statistics = false;
						}
						if (is_alm) {
							alm_class = Byte.parseByte(m.group(14));
							dly_bef_alm = Integer.parseInt(m.group(15));
							dly_aft_alm = Integer.parseInt(m.group(16));
						}
					}
					SysSigInfo_Lst.add(new SysSigInfo(is_alm, val_type,
							unit_lang_res, permission, accuracy, val_min,
							val_max, val_def, map_enm_lang_res, en_statistics,
							alm_class, dly_bef_alm, dly_aft_alm));
				} else {
					System.out.println("NO MATCH lang");
					break;
				}
			}

			String s_debug = new String("");
			for (int i = 0; i < SysSigInfo_Lst.size(); i++) {

				s_debug += "" + i + ":" + SysSigInfo_Lst.get(i).IsAlm + ","
						+ SysSigInfo_Lst.get(i).ValType + ","
						+ SysSigInfo_Lst.get(i).UnitLangRes + ","
						+ SysSigInfo_Lst.get(i).Permission + ","
						+ SysSigInfo_Lst.get(i).Accuracy + ","
						+ SysSigInfo_Lst.get(i).ValMin.getVal() + ","
						+ SysSigInfo_Lst.get(i).ValMax.getVal() + ","
						+ SysSigInfo_Lst.get(i).ValDef.getVal() + ",";
				Iterator<Map.Entry<Integer, Integer>> entries = SysSigInfo_Lst
						.get(i).MapEnmLangRes.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Integer, Integer> entry = entries.next();
					s_debug += "" + entry.getKey() + "=>" + entry.getValue()
							+ ",";
				}
				s_debug += SysSigInfo_Lst.get(i).EnStatistics + ","
						+ SysSigInfo_Lst.get(i).AlmClass + ","
						+ SysSigInfo_Lst.get(i).DlyBefAlm + ","
						+ SysSigInfo_Lst.get(i).DlyAftAlm + "\n";
			}
			System.out.println(s_debug);

			sys_sig_in.close();
			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
	}
	/*
	 * public String getSysSigTab(int dist, int offset) { String ret = new
	 * String(""); try { ret = SysEnmLangRes_Lst.get(offset).get(lang); } catch
	 * (Exception e) { e.printStackTrace(); ret = "NULL"; } return ret; }
	 */
}
