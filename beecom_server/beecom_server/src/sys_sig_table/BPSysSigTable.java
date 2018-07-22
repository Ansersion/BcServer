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

import bp_packet.BPParseCsvFileException;
import other.BPValue;
import other.Util;

/**
 * @author Ansersion
 * 
 */

public class BPSysSigTable {

	private static final Logger logger = LoggerFactory.getLogger(BPSysSigTable.class);

	public static final int SYSTEM_SIGNAL_ATTR_COLUMN_NUM = 18;
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
		int unitLangRes = 0;
		/* 0-ro, 1-rw */
		byte permission = 0;
		boolean ifDisplay = false;
		byte accuracy;
		Object valMin = null;
		Object valMax = null;
		Object valDef = null;
		int classLangRes;
		Map<Integer, Integer> mapEnumLangRes = null;
		boolean enStatistics;
		// 0-note, 1-warning, 2-serious, 3-emergency
		byte almClass = (byte) 0xFF;
		int dlyBefAlm = -1;
		int dlyAftAlm = -1;

		try (BufferedReader sysSigIn = new BufferedReader(isr)) {
			String s;
			// String pattern = "^(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+)$";
			String pattern = "^";
			for(int i = 0; i < SYSTEM_SIGNAL_ATTR_COLUMN_NUM-1; i++) {
				pattern += "(.+),";
			}
			pattern += "(.+)$";
			Pattern r = Pattern.compile(pattern);
			s = sysSigIn.readLine();
			s = sysSigIn.readLine();
			int groupIndex = 0;

			while ((s = sysSigIn.readLine()) != null) {
				Matcher m = r.matcher(s);
				if (!m.find()) {
					break;
				}
				groupIndex = 4;
				if (0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
					alm = true;
				} else {
					alm = false;
				}
				/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
				groupIndex++;
				if (0 == m.group(groupIndex).compareToIgnoreCase("UINT32")) {
					valType = 0;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("UINT16")) {
					valType = 1;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("INT32")) {
					valType = 2;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("INT16")) {
					valType = 3;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("ENUM")) {
					valType = 4;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("FLOAT")) {
					valType = 5;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("STRING")) {
					valType = 6;
				} else {
					throw new BPParseCsvFileException(m.group(1) + ":(" + m.group(groupIndex) + ") value type error");
				}

				groupIndex++;
				strTmp = m.group(groupIndex);

				try (Scanner scannerUnit = new Scanner(strTmp)) {
					scannerUnit.useDelimiter("ULR");
					if (!scannerUnit.hasNext()) {
						throw new BPParseCsvFileException(m.group(1) + ": unit language resource error");
					}
					unitLangRes = scannerUnit.nextInt();

					groupIndex++;
					if (0 == m.group(groupIndex).compareToIgnoreCase("RO")) {
						permission = 0;
					} else {
						permission = 1;
					}
					groupIndex++;
					if (0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
						ifDisplay = true;
					} else {
						ifDisplay = false;
					}
				} catch (BPParseCsvFileException e) {
					Util.bcLog(e, logger);
				}

				groupIndex++;
				accuracy = (byte) Integer.parseInt(m.group(groupIndex));

				groupIndex++;
				if (Util.isNull(m.group(groupIndex))) {
					valMin = BPValue.setVal(valType, null, valMin);
				} else {
					valMin = BPValue.setVal(valType, m.group(groupIndex), valMin);
				}

				groupIndex++;
				if (Util.isNull(m.group(groupIndex))) {
					valMax = BPValue.setVal(valType, null, valMax);
				} else {
					valMax = BPValue.setVal(valType, m.group(groupIndex), valMax);
				}
				
				groupIndex++;
				if (Util.isNull(m.group(groupIndex))) {
					valDef = BPValue.setVal(valType, null, valDef);
				} else {
					valDef = BPValue.setVal(valType, m.group(groupIndex), valDef);
				}

				groupIndex++;
				if (!m.group(groupIndex).equals("NULL")) {
					classLangRes = 1;
				} else {
					classLangRes = 0;
				}

				if (null != mapEnumLangRes) {
					mapEnumLangRes = null;
				}

				if (4 == valType) {
					groupIndex++;
					strTmp = m.group(groupIndex);

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
								throw new BPParseCsvFileException("Error: parse enumeration error");
							}
						}
					} catch (Exception e) {
						Util.bcLog(e, logger);
					}
				}

				groupIndex++;
				if (0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
					enStatistics = true;
				} else {
					enStatistics = false;
				}
				
				if (alm) {
					groupIndex++;
					almClass = Byte.parseByte(m.group(groupIndex));
					groupIndex++;
					dlyBefAlm = Integer.parseInt(m.group(groupIndex));
					groupIndex++;
					dlyAftAlm = Integer.parseInt(m.group(groupIndex));
				}

				sysSigInfoLst.add(new SysSigInfo(alm, valType, unitLangRes, permission, ifDisplay, accuracy, valMin, valMax,
						valDef, classLangRes, mapEnumLangRes, enStatistics, almClass, dlyBefAlm, dlyAftAlm));
			}

			
			for (int i = 0; i < sysSigInfoLst.size(); i++) {
				dumpOneLineOfSysSigTab(i, sysSigInfoLst.get(i));
			}

			ret = true;

		} catch (Exception e) {
			Util.bcLog(e, logger);
			ret = false;
		}

		return ret;
	}
	
	public static void dumpOneLineOfSysSigTab(int index, SysSigInfo sysSigInfo) {
		StringBuilder debugStr = new StringBuilder();
		debugStr.append("" + index + ":" + sysSigInfo.isAlm() + "," + sysSigInfo.getValType()
				+ "," + sysSigInfo.getUnitLangRes() + "," + sysSigInfo.getPermission() + ","
				+ sysSigInfo.isIfDisplay() + "," + sysSigInfo.getAccuracy() + "," + sysSigInfo.getValMin().toString() + ","
				+ sysSigInfo.getValMax().toString() + ","
				+ sysSigInfo.getValDef().toString() + ",");
		if (null != sysSigInfo.getMapEnmLangRes()) {
			Iterator<Map.Entry<Integer, Integer>> entries = sysSigInfo.getMapEnmLangRes().entrySet()
					.iterator();
			while (entries.hasNext()) {
				Map.Entry<Integer, Integer> entry = entries.next();
				debugStr.append("" + entry.getKey() + "=>" + entry.getValue() + ",");
			}
		} else {
			debugStr.append("NULL,");
		}
		debugStr.append(sysSigInfo.isEnStatistics() + "," + sysSigInfo.getAlmClass() + ","
				+ sysSigInfo.getDlyBefAlm() + "," + sysSigInfo.getDlyAftAlm());
		
		logger.info(debugStr.toString());
	}
}
