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

import bp_packet.BPPacket;
import bp_packet.BPParseCsvFileException;
import bp_packet.BPUtils;
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
		byte almClass;
		int dlyBefAlm;
		int dlyAftAlm;

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
				/** alarm attribute */
				groupIndex = 4;
				strTmp = m.group(groupIndex++);
				try {
					alm = BPUtils.bpStr2Boolean(strTmp);
				} catch(Exception e) {
					throw new BPParseCsvFileException(m.group(1) + ":(" + strTmp + ") is alarm error");
					
				}
				
				/** value type: 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string...*/
				strTmp = m.group(groupIndex++);
				/** default value type UINT32 */
				valType = BPPacket.VAL_TYPE_UINT32;
				if(!Util.isNull(strTmp)) {
					valType = (byte)BPUtils.bpSignalTypeStr2Int(strTmp);
				} 
				if(BPPacket.VAL_TYPE_INVALID == valType) {
					throw new BPParseCsvFileException(m.group(1) + ":(" + strTmp + ") value type error");
				}

				/** unit language */
				strTmp = m.group(groupIndex++);
				if(Util.isNull(strTmp)) {
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
				
				/** permission RW/RO */
				strTmp = m.group(groupIndex++);
				/** default permission RW */
				permission = BPPacket.SIGNAL_PERMISSION_CODE_RW;
				if(!Util.isNull(strTmp)) {
					permission = (byte)BPUtils.bpStr2Permission(strTmp);
				} 
				if(BPPacket.SIGNAL_PERMISSION_INVALID == permission) {
					throw new BPParseCsvFileException(m.group(1) + ":(" + strTmp + ") permission error");
				}
				
				/** display or not display */
				strTmp = m.group(groupIndex++);
				try {
					ifDisplay = BPUtils.bpStr2Boolean(strTmp);
				} catch(Exception e) {
					throw new BPParseCsvFileException(m.group(1) + ":(" + strTmp + ") is ifDisplay error");
				}

				/** accuracy */
				strTmp = m.group(groupIndex++);
				if(Util.isNull(strTmp)) {
					accuracy = 0;
				} else {
					accuracy = (byte) Integer.parseInt(strTmp);
				}

				/** min value */
				strTmp = m.group(groupIndex++);
				if (Util.isNull(strTmp)) {
					valMin = BPValue.getVal(valType, null);
				} else {
					valMin = BPValue.getVal(valType, strTmp);
				}

				/** max value */
				strTmp = m.group(groupIndex++);
				if (Util.isNull(strTmp)) {
					valMax = BPValue.getVal(valType, null);
				} else {
					valMax = BPValue.getVal(valType, strTmp);
				}
				
				/** default value */
				strTmp = m.group(groupIndex++);
				if (Util.isNull(strTmp)) {
					valDef = BPValue.getVal(valType, null);
				} else {
					valDef = BPValue.getVal(valType, strTmp);
				}

				/** group language resource */
				strTmp = m.group(groupIndex++);
				if(Util.isNull(strTmp)) {
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

				/** group language resource */
				strTmp = m.group(groupIndex++);
				if (null != mapEnumLangRes) {
					mapEnumLangRes = null;
				}
				if (BPPacket.VAL_TYPE_ENUM == valType) {
					if(Util.isNull(strTmp)) {
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

				/** display or not display */
				strTmp = m.group(groupIndex++);
				try {
					enStatistics = BPUtils.bpStr2Boolean(strTmp);
				} catch(Exception e) {
					throw new BPParseCsvFileException(m.group(1) + ":(" + strTmp + ") is enStatistics error");
				}
				
				/** alarm info */
				if (alm) {
					almClass = Byte.parseByte(m.group(groupIndex++));
					dlyBefAlm = Integer.parseInt(m.group(groupIndex++));
					dlyAftAlm = Integer.parseInt(m.group(groupIndex++));
				} else {
					almClass = BPPacket.ALARM_CLASS_NONE;
					dlyBefAlm = BPPacket.ALARM_DELAY_DEFAULT;
					dlyAftAlm = BPPacket.ALARM_DELAY_DEFAULT;
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
