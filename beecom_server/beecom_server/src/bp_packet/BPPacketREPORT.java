/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_server.BcDecoder;
import db.CustomSignalBooleanInfoHbn;
import db.CustomSignalEnumInfoHbn;
import db.CustomSignalFloatInfoHbn;
import db.CustomSignalI16InfoHbn;
import db.CustomSignalI32InfoHbn;
import db.CustomSignalInfoUnit;
import db.CustomSignalStringInfoHbn;
import db.CustomSignalU16InfoHbn;
import db.CustomSignalU32InfoHbn;
import db.SignalInterface;
import db.SystemSignalBooleanInfoHbn;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalEnumInfoHbn;
import db.SystemSignalFloatInfoHbn;
import db.SystemSignalI16InfoHbn;
import db.SystemSignalI32InfoHbn;
import db.SystemSignalStringInfoHbn;
import db.SystemSignalU16InfoHbn;
import db.SystemSignalU32InfoHbn;
import other.CrcChecksum;
import other.Util;
import sys_sig_table.BPSysSigTable;
import sys_sig_table.SysSigInfo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author Ansersion
 * 
 */
public class BPPacketREPORT extends BPPacket {
	
	public static final int RET_CODE_INVALID_DEVICE_ID_ERR = 0x02;
	public static final int RET_CODE_SIGNAL_MAP_CHECKSUM_ERR = 0x05;
	public static final int RET_CODE_SIGNAL_MAP_ERR = 0x07;

	int packSeq;
	int devNameLen;
	Vector<BPPartitation> partitation;
	byte[] devName;
	private byte[] signalValueRelay;

	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 
	
	BPPacketREPORT() {
		super();
		packSeq = 0;
		devNameLen = 0;
		devName = new byte[256];
	}

	@Override
	public int parseVariableHeader() {

		try {
			// flags(1 byte) + sequence id(2 byte)
			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int packSeqTmp = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(packSeqTmp);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {

			VariableHeader vrb = getVrbHead();
			Payload pld = getPld();
			IoBuffer ioBuffer = getIoBuffer();
			
			if (vrb.getSigValFlag()) {
				signalValueRelay = null;
				pld.initSigValMap();
				int signalValuePositionStart = ioBuffer.position();
				final int sigNum = ioBuffer.get();
				for(int i = 0; i < sigNum; i++) {
					int sigId = ioBuffer.getUnsignedShort();
					byte sigType = ioBuffer.get();
					switch(sigType & BPPacket.VAL_TYPE_MASK) {
					case VAL_TYPE_UINT32:
						pld.putSigValMap(sigId, sigType, ioBuffer.getUnsignedInt());
						break;
					case VAL_TYPE_UINT16:
						pld.putSigValMap(sigId, sigType, ioBuffer.getUnsignedShort());
						break;
					case VAL_TYPE_IINT32:
						pld.putSigValMap(sigId, sigType, ioBuffer.getInt());
						break;
					case VAL_TYPE_IINT16:
						pld.putSigValMap(sigId, sigType, ioBuffer.getShort());
						break;
					case VAL_TYPE_ENUM:
						pld.putSigValMap(sigId, sigType, ioBuffer.getUnsignedShort());
						break;
					case VAL_TYPE_FLOAT:
						pld.putSigValMap(sigId, sigType, ioBuffer.getFloat());
						break;
					case VAL_TYPE_STRING:
					{
						int strLen = ioBuffer.get();
						byte[] strBytes = new byte[strLen];
						ioBuffer.get(strBytes);
						String value = new String(strBytes, StandardCharsets.UTF_8);
						pld.putSigValMap(sigId, sigType, value);
						break;
					}
					case VAL_TYPE_BOOLEAN:
					{
						Boolean value;
						if(ioBuffer.get() == 0) {
							value = false;
						} else {
							value = true;
						}
						pld.putSigValMap(sigId, sigType, value);
						break;
					}
					default:
						break;
					}
				}
				int signalValuePositionEnd = ioBuffer.position();
				ioBuffer.rewind();
				ioBuffer.position(signalValuePositionStart);
				signalValueRelay = new byte[signalValuePositionEnd - signalValuePositionStart];
				ioBuffer.get(signalValueRelay);
			} else {
				if (vrb.getSysSigMapFlag()) {
					byte distAndClass;
					int dist;
					int sysSigClass;
					int mapNum;
					List<Integer> systemSignalEnabledList = new ArrayList<>();
					do {
						distAndClass = getIoBuffer().get();
						dist = (distAndClass >> 4) & 0x0F;
						sysSigClass = (distAndClass >> 1) & 0x07;
						if (sysSigClass > 0x07) {
							throw new BPParsePldException("Error: System signal class 0x7");
						}
						if (0 == sysSigClass) {
							break;
						}
						mapNum = 0x01 << (sysSigClass - 1);
						byte tmp;
						for (int i = 0; i < mapNum; i++) {
							tmp = getIoBuffer().get();
							for(int j = 0; j < 8; j++) {
								if(((tmp & 0xFF) & (1 << j)) != 0) {
									systemSignalEnabledList.add(dist * BPPacket.SYS_SIG_DIST_STEP + i * 8 + j);
								}
							}
						}

					} while ((distAndClass & VariableHeader.DIST_END_FLAG_MSK) != VariableHeader.DIST_END_FLAG_MSK);
					pld.setSystemSignalEnabledList(systemSignalEnabledList);
			
				}
				
				if(vrb.getSysSigMapCustomInfo()) {
					int signalNum = ioBuffer.getUnsignedShort();
					int signalId;
					int customInfoFlags;
					// boolean ifNotifing;
					// boolean ifAlarm;
					// boolean ifStatistics;
					// boolean ifDisplay;
					// short perm;
					// int groupLangId;
					// int accuracy;
					int sigType;
					short alarmClass;
					short delayBeforeAlarm;
					short delayAfterAlarm;
					Map<Integer, Integer> enumLangMap;
					SignalInterface signalInterface;
					List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitList = null;
					for (int i = 0; i < signalNum; i++) {
						enumLangMap = null;
						if (null == systemSignalCustomInfoUnitList) {
							systemSignalCustomInfoUnitList = new ArrayList<>();
						}
						signalInterface = null;
						// ifAlarm = false;
						alarmClass = BPPacket.ALARM_CLASS_NONE;
						delayBeforeAlarm = BPPacket.ALARM_DELAY_DEFAULT;
						delayAfterAlarm = BPPacket.ALARM_DELAY_DEFAULT;
						signalId = ioBuffer.getUnsignedShort();
						SysSigInfo sysSigInfo = BPSysSigTable.getSysSigTableInstance().getSysSigInfo(signalId - BPPacket.SYS_SIG_START_ID);
						if(null == sysSigInfo) {
							throw new Exception("null == sysSigInfo");
						}
						sigType = sysSigInfo.getValType();
						customInfoFlags = ioBuffer.getUnsigned() & 0xFF;
						customInfoFlags |= ((ioBuffer.getUnsigned() & 0xFF)<< 8);
						if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_STATISTICS) != 0) {
							// ifStatistics = ioBuffer.get() != 0;
						}
						if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
							enumLangMap = new HashMap<>();
							int enumLangNum = ioBuffer.getUnsigned();
							int key;
							int val;
							for (int j = 0; j < enumLangNum; j++) {
								key = ioBuffer.getUnsignedShort();
								val = ioBuffer.getInt();
								enumLangMap.put(key, val);
							}
						}
						if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_GROUP_LANG) != 0) {
							// groupLangId = ioBuffer.getUnsignedShort();
						}
						switch (sigType) {
						case BPPacket.VAL_TYPE_UINT32: {
							SystemSignalU32InfoHbn systemSignalU32InfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
								if (null == systemSignalU32InfoHbn) {
									systemSignalU32InfoHbn = new SystemSignalU32InfoHbn();
								}
								systemSignalU32InfoHbn.setMinVal(ioBuffer.getUnsignedInt());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
								if (null == systemSignalU32InfoHbn) {
									systemSignalU32InfoHbn = new SystemSignalU32InfoHbn();
								}
								systemSignalU32InfoHbn.setMaxVal(ioBuffer.getUnsignedInt());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalU32InfoHbn) {
									systemSignalU32InfoHbn = new SystemSignalU32InfoHbn();
								}
								systemSignalU32InfoHbn.setDefVal(ioBuffer.getUnsignedInt());
							}
							signalInterface = systemSignalU32InfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_UINT16: {
							SystemSignalU16InfoHbn systemSignalU16InfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
								if (null == systemSignalU16InfoHbn) {
									systemSignalU16InfoHbn = new SystemSignalU16InfoHbn();
								}
								systemSignalU16InfoHbn.setMinVal(ioBuffer.getUnsignedShort());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
								if (null == systemSignalU16InfoHbn) {
									systemSignalU16InfoHbn = new SystemSignalU16InfoHbn();
								}
								systemSignalU16InfoHbn.setMaxVal(ioBuffer.getUnsignedShort());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalU16InfoHbn) {
									systemSignalU16InfoHbn = new SystemSignalU16InfoHbn();
								}
								systemSignalU16InfoHbn.setDefVal(ioBuffer.getUnsignedShort());
							}
							signalInterface = systemSignalU16InfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_IINT32: {
							SystemSignalI32InfoHbn systemSignalI32InfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
								if (null == systemSignalI32InfoHbn) {
									systemSignalI32InfoHbn = new SystemSignalI32InfoHbn();
								}
								systemSignalI32InfoHbn.setMinVal(ioBuffer.getInt());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
								if (null == systemSignalI32InfoHbn) {
									systemSignalI32InfoHbn = new SystemSignalI32InfoHbn();
								}
								systemSignalI32InfoHbn.setMaxVal(ioBuffer.getInt());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalI32InfoHbn) {
									systemSignalI32InfoHbn = new SystemSignalI32InfoHbn();
								}
								systemSignalI32InfoHbn.setDefVal(ioBuffer.getInt());
							}
							signalInterface = systemSignalI32InfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_IINT16: {
							SystemSignalI16InfoHbn systemSignalI16InfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
								if (null == systemSignalI16InfoHbn) {
									systemSignalI16InfoHbn = new SystemSignalI16InfoHbn();
								}
								systemSignalI16InfoHbn.setMinVal(ioBuffer.getShort());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
								if (null == systemSignalI16InfoHbn) {
									systemSignalI16InfoHbn = new SystemSignalI16InfoHbn();
								}
								systemSignalI16InfoHbn.setMaxVal(ioBuffer.getShort());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalI16InfoHbn) {
									systemSignalI16InfoHbn = new SystemSignalI16InfoHbn();
								}
								systemSignalI16InfoHbn.setDefVal(ioBuffer.getShort());
							}
							signalInterface = systemSignalI16InfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_ENUM: {
							SystemSignalEnumInfoHbn systemSignalEnumInfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
								if (null == systemSignalEnumInfoHbn) {
									systemSignalEnumInfoHbn = new SystemSignalEnumInfoHbn();
								}
							}
							if((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null != systemSignalEnumInfoHbn) {
									systemSignalEnumInfoHbn.setDefVal(ioBuffer.getUnsignedShort());
								} else {
									/* skip the default value */
									ioBuffer.getUnsignedShort();
								}
								
							}
							signalInterface = systemSignalEnumInfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_FLOAT: {
							SystemSignalFloatInfoHbn systemSignalFloatInfoHbn = null;
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ACCURACY) != 0) {
								if (null == systemSignalFloatInfoHbn) {
									systemSignalFloatInfoHbn = new SystemSignalFloatInfoHbn();
								}
								systemSignalFloatInfoHbn.setAccuracy(ioBuffer.getUnsigned());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
								if (null == systemSignalFloatInfoHbn) {
									systemSignalFloatInfoHbn = new SystemSignalFloatInfoHbn();
								}
								systemSignalFloatInfoHbn.setMinVal(ioBuffer.getFloat());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
								if (null == systemSignalFloatInfoHbn) {
									systemSignalFloatInfoHbn = new SystemSignalFloatInfoHbn();
								}
								systemSignalFloatInfoHbn.setMaxVal(ioBuffer.getFloat());
							}
							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalFloatInfoHbn) {
									systemSignalFloatInfoHbn = new SystemSignalFloatInfoHbn();
								}
								systemSignalFloatInfoHbn.setDefVal(ioBuffer.getFloat());
							}
							signalInterface = systemSignalFloatInfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_STRING: {
							SystemSignalStringInfoHbn systemSignalStringInfoHbn = null;

							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalStringInfoHbn) {
									systemSignalStringInfoHbn = new SystemSignalStringInfoHbn();
								}
								int langLen = ioBuffer.getUnsigned();
								byte[] bytes = new byte[langLen];
								ioBuffer.get(bytes);
								systemSignalStringInfoHbn.setDefVal(new String(bytes));
							}
							signalInterface = systemSignalStringInfoHbn;
							break;
						}
						case BPPacket.VAL_TYPE_BOOLEAN: {
							SystemSignalBooleanInfoHbn systemSignalBooleanInfoHbn = null;

							if ((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
								if (null == systemSignalBooleanInfoHbn) {
									systemSignalBooleanInfoHbn = new SystemSignalBooleanInfoHbn();
								}
								systemSignalBooleanInfoHbn.setDefVal(ioBuffer.get() != 0);
							}
							signalInterface = systemSignalBooleanInfoHbn;
							break;
						}
						default:
							Util.logger(logger, Util.ERROR, "invalid signal type");
							break;
						}
						if((customInfoFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM) != 0) {
							alarmClass = ioBuffer.getUnsigned();
							delayBeforeAlarm = ioBuffer.getUnsigned();
							delayAfterAlarm = ioBuffer.getUnsigned();
						}
						systemSignalCustomInfoUnitList.add(new SystemSignalCustomInfoUnit(signalId, alarmClass, delayBeforeAlarm, delayAfterAlarm, customInfoFlags, enumLangMap, signalInterface));

					}
					
					pld.setSystemSignalCustomInfoUnitLst(systemSignalCustomInfoUnitList);
				}
				
				if(vrb.getCusSigMapFlag()) {
					int signalNum = ioBuffer.getUnsignedShort();
					List<CustomSignalInfoUnit> customSignalInfoUnitList = new ArrayList<>(signalNum);
					int flagsTmp;
					int sigType;
					short perm;
					int cusSigId;
					boolean ifNotifing = false;
					boolean ifAlarm;
					short alarmClass = BPPacket.ALARM_CLASS_NONE;
					short alarmDelayBef = BPPacket.ALARM_DELAY_DEFAULT;
					short alarmDelayAft = BPPacket.ALARM_DELAY_DEFAULT;
					boolean ifStatistics;
					boolean ifDisplay;
					// int groupLangId;
					Map<Integer, String> signalNameLangMap = null;
					Map<Integer, String> signalUnitLangMap = null;
					Map<Integer, String> groupLangMap = null;
					Map<Integer, Map<Integer, String> > signalEnumLangMap = null;
					// CustomAlarmInfoUnit customAlarmInfoUnit;
					SignalInterface customSignalInterface;
					for(int i = 0; i < signalNum; i++) {
						cusSigId = ioBuffer.getUnsignedShort();
						flagsTmp = ioBuffer.getUnsigned();
						sigType = (flagsTmp >> 4) & 0x0F;
						if(!BPPacket.ifSigTypeValid(sigType)) {
							logger.info("REPORT Parse Payload Error: !BPPacket.ifSigTypeValid(sigType)");
							return -1;
						}
						signalNameLangMap = null;
						signalUnitLangMap = null;
						groupLangMap = null;
						signalEnumLangMap = null;
						customSignalInterface = null;
						// customAlarmInfoUnit = null;
						perm = (short)((flagsTmp >> 3) & 0x01);
						ifStatistics = ((flagsTmp >> 2) & 0x01) != 0;
						ifAlarm = ((flagsTmp >> 1) & 0x01) != 0;
						ifDisplay = (flagsTmp & 0x01) != 0;
						do {
							flagsTmp = ioBuffer.getUnsigned();
							int langSupportMaskByte;
							int langLen;
							String lang;
							for (int j = 7; j > 0; j--) {
								langSupportMaskByte = 0x01 << j;
								if ((flagsTmp & langSupportMaskByte) == 0) {
									continue;
								}
								langLen = ioBuffer.getUnsigned();
								if (langLen != 0) {
									byte[] bytes = new byte[langLen];
									ioBuffer.get(bytes);
									lang = new String(bytes);
									if (null == signalNameLangMap) {
										signalNameLangMap = new HashMap<>();
									}
									signalNameLangMap.put(j, lang);
								}
								langLen = ioBuffer.getUnsigned();
								if (langLen != 0) {
									byte[] bytes = new byte[langLen];
									ioBuffer.get(bytes);
									lang = new String(bytes);
									if (null == signalUnitLangMap) {
										signalUnitLangMap = new HashMap<>();
									}
									signalUnitLangMap.put(j, lang);
								}
								langLen = ioBuffer.getUnsigned();
								if (langLen != 0) {
									byte[] bytes = new byte[langLen];
									ioBuffer.get(bytes);
									lang = new String(bytes);
									if (null == groupLangMap) {
										groupLangMap = new HashMap<>();
									}
									groupLangMap.put(j, lang);
								}

								if (BPPacket.VAL_TYPE_ENUM == sigType) {
									int enumLangNum = ioBuffer.getUnsigned();
									int enumKey;
									if(enumLangNum != 0) {
										if(null == signalEnumLangMap) {
											signalEnumLangMap = new HashMap<>();
										}
										for(int k = 0; k < enumLangNum; k++) {
											enumKey = ioBuffer.getUnsignedShort();
											langLen = ioBuffer.getUnsigned();
											if (langLen != 0) {
												if(!signalEnumLangMap.containsKey(enumKey)) {
													signalEnumLangMap.put(enumKey, new HashMap<Integer, String>());
												}
												byte[] bytes = new byte[langLen];
												ioBuffer.get(bytes);
												lang = new String(bytes);
												Map<Integer, String> enumLangMap = signalEnumLangMap.get(enumKey);
												enumLangMap.put(j, lang);
											}
										
										}
									}
								}	
							}

							
						} while((flagsTmp != 0) && (flagsTmp & 0x01) != 0x01);
						
						switch(sigType) {
						case BPPacket.VAL_TYPE_UINT32:
							CustomSignalU32InfoHbn customSignalU32InfoHbn = new CustomSignalU32InfoHbn();
							customSignalU32InfoHbn.setMinVal(ioBuffer.getUnsignedInt());
							customSignalU32InfoHbn.setMaxVal(ioBuffer.getUnsignedInt());
							customSignalU32InfoHbn.setDefVal(ioBuffer.getUnsignedInt());
							customSignalInterface = customSignalU32InfoHbn;
							break;
						case BPPacket.VAL_TYPE_UINT16:
							CustomSignalU16InfoHbn customSignalU16InfoHbn = new CustomSignalU16InfoHbn();
							customSignalU16InfoHbn.setMinVal(ioBuffer.getUnsignedShort());
							customSignalU16InfoHbn.setMaxVal(ioBuffer.getUnsignedShort());
							customSignalU16InfoHbn.setDefVal(ioBuffer.getUnsignedShort());
							customSignalInterface = customSignalU16InfoHbn;
							break;
						case BPPacket.VAL_TYPE_IINT32:
							CustomSignalI32InfoHbn customSignalI32InfoHbn = new CustomSignalI32InfoHbn();
							customSignalI32InfoHbn.setMinVal(ioBuffer.getInt());
							customSignalI32InfoHbn.setMaxVal(ioBuffer.getInt());
							customSignalI32InfoHbn.setDefVal(ioBuffer.getInt());
							customSignalInterface = customSignalI32InfoHbn;
							break;
						case BPPacket.VAL_TYPE_IINT16:
							CustomSignalI16InfoHbn customSignalI16InfoHbn = new CustomSignalI16InfoHbn();
							customSignalI16InfoHbn.setMinVal(ioBuffer.getShort());
							customSignalI16InfoHbn.setMaxVal(ioBuffer.getShort());
							customSignalI16InfoHbn.setDefVal(ioBuffer.getShort());
							customSignalInterface = customSignalI16InfoHbn;
							break;
						case BPPacket.VAL_TYPE_ENUM:
							CustomSignalEnumInfoHbn customSignalEnumInfoHbn = new CustomSignalEnumInfoHbn();
							customSignalEnumInfoHbn.setDefVal(ioBuffer.getUnsignedShort());
							customSignalInterface = customSignalEnumInfoHbn;
							break;
						case BPPacket.VAL_TYPE_FLOAT:
							CustomSignalFloatInfoHbn customSignalFloatInfoHbn = new CustomSignalFloatInfoHbn();
							customSignalFloatInfoHbn.setAccuracy(ioBuffer.getUnsigned());
							customSignalFloatInfoHbn.setMinVal(ioBuffer.getFloat());
							customSignalFloatInfoHbn.setMaxVal(ioBuffer.getFloat());
							customSignalFloatInfoHbn.setDefVal(ioBuffer.getFloat());
							customSignalInterface = customSignalFloatInfoHbn;
							break;
						case BPPacket.VAL_TYPE_STRING:
							CustomSignalStringInfoHbn customSignalStringInfoHbn = new CustomSignalStringInfoHbn();
							int langLen = ioBuffer.getUnsigned();
							if(langLen > 0) {
								byte[] bytes = new byte[langLen];
								ioBuffer.get(bytes);
								customSignalStringInfoHbn.setDefVal(new String(bytes));
							}
							customSignalInterface = customSignalStringInfoHbn;
							break;
						case BPPacket.VAL_TYPE_BOOLEAN:
							CustomSignalBooleanInfoHbn customSignalBooleanInfoHbn = new CustomSignalBooleanInfoHbn();
							customSignalBooleanInfoHbn.setDefVal(ioBuffer.getUnsigned() != 0);
							customSignalInterface = customSignalBooleanInfoHbn;
							break;
						default:
							Util.logger(logger, Util.ERROR, "invalid signal type");
							break;
						}
						
						customSignalInterface.setEnStatistics(ifStatistics);
						customSignalInterface.setPermission(perm);
						
						if(ifAlarm) {
							alarmClass = ioBuffer.getUnsigned();
							alarmDelayBef = ioBuffer.getUnsigned();
							alarmDelayAft = ioBuffer.getUnsigned();
						}
						
						customSignalInfoUnitList.add(new CustomSignalInfoUnit(cusSigId, ifNotifing, ifAlarm, alarmClass, alarmDelayBef, alarmDelayAft, ifDisplay, signalNameLangMap, signalUnitLangMap, groupLangMap, signalEnumLangMap, customSignalInterface));
					}
					pld.setCustomSignalInfoUnitLst(customSignalInfoUnitList);
				}
				if(getFxHead().getCrcChk() == CrcChecksum.CRC16) {
					pld.setSigMapCheckSum(ioBuffer.getUnsignedShort());
				} else {
					pld.setSigMapCheckSum(ioBuffer.getUnsignedInt());
				}
				if(vrb.getSigMapChecksumOnly()) {
					return 0;
				}
			}

		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return 0;
	}
	
	public Vector<BPPartitation> getPartitation() {
		return partitation;
	}

	@Override
	public byte[] getSignalValueRelay() {
		return signalValueRelay;
	}
	
	
}
