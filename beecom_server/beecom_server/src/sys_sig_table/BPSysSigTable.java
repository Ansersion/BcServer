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

	public static final int SID_ID_RESERVED = 0x0000;

	private List<SysSigInfo> sysSigInfoLst;

	static BPSysSigTable sysSigTab = null;
	public static final int BP_SYS_SIG_SET_VERSION = 0;

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

	public boolean loadTab() throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream("config/sys_sig_info_basic.csv");
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

		sysSigInfoLst.clear();
		boolean ret = false;
		String strTmp;

		boolean alm;
		/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
		byte valType;
		int unitLangRes;
		/* 0-ro, 1-rw */
		byte permission;
		byte accuracy;
		BPValue valMin;
		BPValue valMax;
		BPValue valDef;
		int classLangRes;
		Map<Integer, Integer> mapEnumLangRes = null;
		boolean enStatistics;
		// 0-note, 1-warning, 2-serious, 3-emergency
		byte almClass = (byte) 0xFF;
		int dlyBefAlm = -1;
		int dlyAftAlm = -1;

		try (BufferedReader sysSigIn = new BufferedReader(isr)) {
			String s;
			String pattern = "^(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*),(.*)$";
			Pattern r = Pattern.compile(pattern);
			s = sysSigIn.readLine();
			s = sysSigIn.readLine();

			while ((s = sysSigIn.readLine()) != null) {
				Matcher m = r.matcher(s);
				if (!m.find() || (m.group(2).length() == 0 || m.group(3).length() == 0 || m.group(4).length() == 0
						|| m.group(5).length() == 0 || m.group(6).length() == 0 || m.group(7).length() == 0
						|| m.group(8).length() == 0 || m.group(9).length() == 0 || m.group(10).length() == 0
						|| m.group(11).length() == 0 || m.group(12).length() == 0 || m.group(13).length() == 0
						|| m.group(14).length() == 0 || m.group(15).length() == 0 || m.group(16).length() == 0
						|| m.group(17).length() == 0)) {
					break;
				} else {
					if (0 == m.group(4).compareToIgnoreCase("YES")) {
						alm = true;
					} else if (0 == m.group(4).compareToIgnoreCase("NO")) {
						alm = false;
					} else {
						throw new Exception(m.group(1) + ": is alarm error");
					}
					/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
					if (0 == m.group(5).compareToIgnoreCase("UINT32")) {
						valType = 0;
					} else if (0 == m.group(5).compareToIgnoreCase("UINT16")) {
						valType = 1;
					} else if (0 == m.group(5).compareToIgnoreCase("INT32")) {
						valType = 2;
					} else if (0 == m.group(5).compareToIgnoreCase("INT16")) {
						valType = 3;
					} else if (0 == m.group(5).compareToIgnoreCase("ENUM")) {
						valType = 4;
					} else if (0 == m.group(5).compareToIgnoreCase("FLOAT")) {
						valType = 5;
					} else if (0 == m.group(5).compareToIgnoreCase("STRING")) {
						valType = 6;
					} else {
						throw new Exception(m.group(1) + ": value type error");
					}

					strTmp = m.group(6);

					try (Scanner scannerUnit = new Scanner(strTmp)) {
						scannerUnit.useDelimiter("ULR");
						if (!scannerUnit.hasNext()) {
							throw new Exception(m.group(1) + ": unit language resource error");
						}
						unitLangRes = scannerUnit.nextInt();

						if (0 == m.group(7).compareToIgnoreCase("RO")) {
							permission = 0;
						} else if (0 == m.group(7).compareToIgnoreCase("RW")) {
							permission = 1;
						} else {
							throw new Exception(m.group(1) + ": is alarm error");
						}
					} catch (Exception e) {
						throw e;
					}

					accuracy = (byte) Integer.parseInt(m.group(8));

					valMin = new BPValue(valType);
					valMax = new BPValue(valType);
					valDef = new BPValue(valType);

					if (Util.isNull(m.group(9))) {
						valMin.setLimitValid(false);
					} else {
						valMin.setValStr(m.group(9));
					}

					if (Util.isNull(m.group(10))) {
						valMax.setLimitValid(false);
					} else {
						valMax.setValStr(m.group(10));
					}
					if (Util.isNull(m.group(11))) {
						valDef.setLimitValid(false);
					} else {
						valDef.setValStr(m.group(11));
					}

					if (!m.group(12).equals("NULL")) {
						classLangRes = 1;
					} else {
						classLangRes = 0;
					}

					if (null != mapEnumLangRes) {
						mapEnumLangRes = null;
					}

					if (4 == valType) {
						strTmp = m.group(13);

						try (Scanner scannerEnum = new Scanner(strTmp).useDelimiter("/")) {
							mapEnumLangRes = new HashMap<>();
							String enumPatternStr = "\\[(\\d+)\\s*=\\s*ELR(\\d+)\\]";
							Pattern enumPattern = Pattern.compile(enumPatternStr);
							while (scannerEnum.hasNext()) {
								strTmp = scannerEnum.next();
								Matcher enumMat = enumPattern.matcher(strTmp);
								if (enumMat.find()) {
									int enumIndex = Integer.parseInt(enumMat.group(1));
									int enumLangResIndex = Integer.parseInt(enumMat.group(2));
									mapEnumLangRes.put(enumIndex, enumLangResIndex);
								} else {
									throw new Exception("Error: parse enumeration error");
								}
							}
						} catch (Exception e) {
							throw e;
						}
					}

					if (0 == m.group(14).compareToIgnoreCase("YES")) {
						enStatistics = true;
					} else if (0 == m.group(14).compareToIgnoreCase("NO")) {
						enStatistics = false;
					} else {
						throw new Exception(m.group(1) + ": enable statistics error");
					}
					if (alm) {
						almClass = Byte.parseByte(m.group(15));
						dlyBefAlm = Integer.parseInt(m.group(16));
						dlyAftAlm = Integer.parseInt(m.group(17));
					}
				}
				sysSigInfoLst.add(new SysSigInfo(alm, valType, unitLangRes, permission, accuracy, valMin, valMax,
						valDef, classLangRes, mapEnumLangRes, enStatistics, almClass, dlyBefAlm, dlyAftAlm));
			}

			StringBuilder debugStr = new StringBuilder();
			for (int i = 0; i < sysSigInfoLst.size(); i++) {

				debugStr.append("" + i + ":" + sysSigInfoLst.get(i).isAlm() + "," + sysSigInfoLst.get(i).getValType()
						+ "," + sysSigInfoLst.get(i).getUnitLangRes() + "," + sysSigInfoLst.get(i).getPermission() + ","
						+ sysSigInfoLst.get(i).getAccuracy() + "," + sysSigInfoLst.get(i).getValMin().getValStr() + ","
						+ sysSigInfoLst.get(i).getValMax().getValStr() + ","
						+ sysSigInfoLst.get(i).getValDef().getValStr() + ",");
				if (null != sysSigInfoLst.get(i).getMapEnmLangRes()) {
					Iterator<Map.Entry<Integer, Integer>> entries = sysSigInfoLst.get(i).getMapEnmLangRes().entrySet()
							.iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Integer> entry = entries.next();
						debugStr.append("" + entry.getKey() + "=>" + entry.getValue() + ",");
					}
				} else {
					debugStr.append("NULL,");
				}
				debugStr.append(sysSigInfoLst.get(i).isEnStatistics() + "," + sysSigInfoLst.get(i).getAlmClass() + ","
						+ sysSigInfoLst.get(i).getDlyBefAlm() + "," + sysSigInfoLst.get(i).getDlyAftAlm() + "\n");
			}
			logger.info(debugStr.toString());

			ret = true;

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			ret = false;
		}

		return ret;
	}
}
