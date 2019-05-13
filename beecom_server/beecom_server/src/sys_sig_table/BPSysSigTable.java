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
import db.SystemSignalInfoUnit;
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
	private static final SysSigInfo SYS_SIG_INFO_DEFAULT = new SysSigInfo(false, (byte)0, 0, (byte)0, false, (byte)0, Long.valueOf(0), Long.valueOf(1),
			Long.valueOf(0), 0, null, true, (byte)0x7f, 5, 5);

	private List<SysSigInfo> sysSigInfoLst;

	static BPSysSigTable sysSigTab = null;
	public static final int BP_SYS_SIG_SET_VERSION = 1;

	public static BPSysSigTable getSysSigTableInstance() {
		if (null == sysSigTab) {
			sysSigTab = new BPSysSigTable();
		}
		return sysSigTab;
	}

	protected BPSysSigTable() {
		sysSigInfoLst = new ArrayList<>();
	}
	
	public SysSigInfo getSysSigInfo(int signalIdOffset) {
		if(signalIdOffset < sysSigInfoLst.size()) {
			return sysSigInfoLst.get(signalIdOffset);
		} else {
			return null;
		}
	}
	
	public SystemSignalInfoUnit createNewSystemSignalInfoUnit(int signalIdOffset) {
		SystemSignalInfoUnit ret = null;

		SysSigInfo sysSigInfo = getSysSigInfo(signalIdOffset);
		if (null == sysSigInfo) {
			return ret;
		}
		ret = new SystemSignalInfoUnit(signalIdOffset);
		if (null == sysSigInfo.getValDef()) {
			logger.error("Inner Error: null == sysSigInfo.getValDef()");
			return ret;
		}
		ret.setSignalValue(sysSigInfo.getValDef());
		return ret;
	}
	
	public boolean loadTab() {
		boolean ret = true;
		List<String> systemSignalTableList = new ArrayList<>();
		/* must be in order */
		/* 1st. config/sys_sig_info_basic.csv */
		systemSignalTableList.add("config/sys_sig_info_basic.csv");
		/* 2nd. config/sys_sig_info_basic.csv */
		systemSignalTableList.add("config/sys_sig_info_temp_humidity.csv");

		sysSigInfoLst.clear();
		try {
			for(int i = 0; i < systemSignalTableList.size(); i++) {
				if(!loadTab(systemSignalTableList.get(i))) {
					ret = false;
					break;
				}
			}
			
			for (int i = 0; i < sysSigInfoLst.size(); i++) {
				dumpOneLineOfSysSigTab(i, sysSigInfoLst.get(i));
			}
			
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = false;
		}
		
		return ret;
	}

	public boolean loadTab(String system_signal_table) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(system_signal_table);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);

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
			StringBuilder bld = new StringBuilder();
			bld.append("^");
			for(int i = 0; i < SYSTEM_SIGNAL_ATTR_COLUMN_NUM-1; i++) {
				bld.append("(.+),");
			}
			bld.append("(.+)$");
			Pattern r = Pattern.compile(bld.toString());
			s = sysSigIn.readLine();
			s = sysSigIn.readLine();
			int groupIndex = 0;

			while ((s = sysSigIn.readLine()) != null) {
				Matcher m = r.matcher(s);
				if (!m.find()) {
					sysSigInfoLst.add(SYS_SIG_INFO_DEFAULT);
					continue;
				}
				groupIndex = 4;
				if(Util.isNull(m.group(groupIndex))) {
					alm = false;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
					alm = true;
				} else if(0 == m.group(groupIndex).compareToIgnoreCase("NO")){
					alm = false;
				} else {
					throw new BPParseCsvFileException(m.group(1) + ":(" + m.group(groupIndex) + ") is alarm error");
				}
				/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string */
				groupIndex++;
				if(Util.isNull(m.group(groupIndex))) {
					valType = 0;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("UINT32")) {
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

				if(Util.isNull(m.group(groupIndex))) {
					unitLangRes = 0;
				} else {
					try (Scanner scannerUnit = new Scanner(strTmp)) {
						scannerUnit.useDelimiter("ULR");
						if (!scannerUnit.hasNext()) {
							throw new BPParseCsvFileException(m.group(1) + ": unit language resource error");
						}
						unitLangRes = scannerUnit.nextInt();

					} catch (BPParseCsvFileException e) {
						Util.logger(logger, Util.ERROR, e);
						throw e;
					}
				}
				
				groupIndex++;
				if(Util.isNull(m.group(groupIndex))) {
					permission = 0;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("RO")) {
					permission = 0;
				} else if(0 == m.group(groupIndex).compareToIgnoreCase("RW")) {
					permission = 1;
				} else {
					throw new BPParseCsvFileException(m.group(1) + ":(" + m.group(groupIndex) + ") permission error");
				}
				
				groupIndex++;
				if(Util.isNull(m.group(groupIndex))) {
					ifDisplay = false;
				} else if (0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
					ifDisplay = true;
				} else if(0 == m.group(groupIndex).compareToIgnoreCase("NO")) {
					ifDisplay = false;
				} else {
					throw new BPParseCsvFileException(m.group(1) + ":(" + m.group(groupIndex) + ") ifDisplay error");
				}

				groupIndex++;
				if(Util.isNull(m.group(groupIndex))) {
					accuracy = 0;
				} else {
					accuracy = (byte) Integer.parseInt(m.group(groupIndex));
				}

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
				
				if(Util.isNull(m.group(groupIndex))) {
					classLangRes = 0;
				} else {
					try (Scanner scannerUnit = new Scanner(strTmp)) {
						scannerUnit.useDelimiter("GLR");
						if (!scannerUnit.hasNext()) {
							throw new BPParseCsvFileException(m.group(1) + ": group language resource error");
						}
						classLangRes = scannerUnit.nextInt();

					} catch (BPParseCsvFileException e) {
						Util.logger(logger, Util.ERROR, e);
						throw e;
					}
				}

				if (null != mapEnumLangRes) {
					mapEnumLangRes = null;
				}

				if (4 == valType) {
					groupIndex++;
					strTmp = m.group(groupIndex);
					if(Util.isNull(m.group(groupIndex))) {
						mapEnumLangRes = null;
					} else {
						try(Scanner scannerEnum = new Scanner(strTmp).useDelimiter("/")) {
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
							Util.logger(logger, Util.ERROR, e);
							throw e;
						}
					}
				}

				groupIndex++;
				if(Util.isNull(m.group(groupIndex))) {
					enStatistics = true;
				} else if(0 == m.group(groupIndex).compareToIgnoreCase("YES")) {
					enStatistics = true;
				} else if(0 == m.group(groupIndex).compareToIgnoreCase("NO")){
					enStatistics = false;
				} else {
					throw new BPParseCsvFileException(m.group(1) + ":(" + m.group(groupIndex) + ") enStatistics error");
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
		
		logger.info("{}", debugStr.toString());
	}
}
