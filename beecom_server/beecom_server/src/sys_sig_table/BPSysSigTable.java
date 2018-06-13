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
			String pattern = "^(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+),(.+)$";
			Pattern r = Pattern.compile(pattern);
			s = sysSigIn.readLine();
			s = sysSigIn.readLine();

			while ((s = sysSigIn.readLine()) != null) {
				Matcher m = r.matcher(s);
				if (!m.find()) {
					break;
				}
				if (0 == m.group(4).compareToIgnoreCase("YES")) {
					alm = true;
				} else {
					alm = false;
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
					throw new BPParseCsvFileException(m.group(1) + ": value type error");
				}

				strTmp = m.group(6);

				try (Scanner scannerUnit = new Scanner(strTmp)) {
					scannerUnit.useDelimiter("ULR");
					if (!scannerUnit.hasNext()) {
						throw new BPParseCsvFileException(m.group(1) + ": unit language resource error");
					}
					unitLangRes = scannerUnit.nextInt();

					if (0 == m.group(7).compareToIgnoreCase("RO")) {
						permission = 0;
					} else {
						permission = 1;
					}
				} catch (BPParseCsvFileException e) {
					Util.bcLog(e, logger);
				}

				accuracy = (byte) Integer.parseInt(m.group(8));

				if (Util.isNull(m.group(9))) {
					valMin = BPValue.setVal(valType, null, valMin);
				} else {
					valMin = BPValue.setVal(valType, m.group(9), valMin);
				}

				if (Util.isNull(m.group(10))) {
					valMax = BPValue.setVal(valType, null, valMax);
				} else {
					valMax = BPValue.setVal(valType, m.group(10), valMax);
				}
				if (Util.isNull(m.group(11))) {
					valDef = BPValue.setVal(valType, null, valDef);
				} else {
					valDef = BPValue.setVal(valType, m.group(11), valDef);
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
								throw new BPParseCsvFileException("Error: parse enumeration error");
							}
						}
					} catch (Exception e) {
						Util.bcLog(e, logger);
					}
				}

				if (0 == m.group(14).compareToIgnoreCase("YES")) {
					enStatistics = true;
				} else {
					enStatistics = false;
				}
				
				if (alm) {
					almClass = Byte.parseByte(m.group(15));
					dlyBefAlm = Integer.parseInt(m.group(16));
					dlyAftAlm = Integer.parseInt(m.group(17));
				}

				sysSigInfoLst.add(new SysSigInfo(alm, valType, unitLangRes, permission, accuracy, valMin, valMax,
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
				+ sysSigInfo.getAccuracy() + "," + sysSigInfo.getValMin().toString() + ","
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
