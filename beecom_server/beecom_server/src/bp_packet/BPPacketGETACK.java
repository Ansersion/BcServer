/**
 * 
 */
package bp_packet;

import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.CustomAlarmInfoUnit;
import db.CustomSignalAlmInfoHbn;
import db.CustomSignalEnumInfoHbn;
import db.CustomSignalFloatInfoHbn;
import db.CustomSignalI16InfoHbn;
import db.CustomSignalI32InfoHbn;
import db.CustomSignalInfoUnit;
import db.CustomSignalStringInfoHbn;
import db.CustomSignalU16InfoHbn;
import db.CustomSignalU32InfoHbn;
import db.SignalInterface;
import db.SystemSignalCustomInfoUnit;
import db.SystemSignalFloatInfoHbn;
import db.SystemSignalI16InfoHbn;
import db.SystemSignalI32InfoHbn;
import db.SystemSignalInfoUnit;
import db.SystemSignalU16InfoHbn;
import db.SystemSignalU32InfoHbn;
import other.CrcChecksum;

import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ansersion
 * 
 */
public class BPPacketGETACK extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketGETACK.class); 
	
	int deviceNum;
	Vector<DevSigData> vctDevSigData;

	protected BPPacketGETACK(FixedHeader fxHeader) {
		super(fxHeader);
	}

	protected BPPacketGETACK(FixedHeader fxHeader, int devNum) {
		super(fxHeader);
		deviceNum = devNum;
	}

	
	protected BPPacketGETACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.GETACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	public void setDevNum(int num) {
		deviceNum = num;
	}
	
	@Override
	public int parseVariableHeader() {

		try {
			// flags(1 byte) + client ID(2 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);

			int seqId = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seqId);
			
			byte retCode = getIoBuffer().get();
			getVrbHead().setRetCode(retCode);
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {
			String s;
			if(getVrbHead().getRetCode() != 0) {
				return -1;
			}
			int sigTabNum = getIoBuffer().get();
			DevSigData sigData = new DevSigData();
			for(int i = 0; i < sigTabNum; i++) {
				boolean ret = sigData.parseSigDataTab(getIoBuffer());
				if(!ret) {
					s = "Error(GETACK): parsePayload error";
					logger.error(s);
					return -1;
				}
				
			}
			getPld().setSigData(sigData);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}
		return 0;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte flags = getVrbHead().getFlags();
		getIoBuffer().put(flags);
		if(getVrbHead().getCusSigMapFlag()) {
			getIoBuffer().put(getVrbHead().getLangFlags());
		}
		int packSeq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);

		return true;	
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;
		boolean ret = false;
		try {
			VariableHeader vrb = getVrbHead();
			if (vrb.getReqAllDeviceId()) {
				return true;
			}
			if(vrb.getRetCode() != 0) {
				return true;
			}

			Payload pld = getPld();
			IoBuffer buffer = getIoBuffer();
			buffer.putUnsignedInt(pld.getUniqDevId());
			if (vrb.getSysSigMapFlag()) {
				List<SystemSignalInfoUnit> systemSignalInfoUnitList = pld.getSystemSignalInfoUnitLst();
				if (null == systemSignalInfoUnitList || systemSignalInfoUnitList.isEmpty()) {
					buffer.put(Payload.SYSTEM_SIGNAL_MAP_END_MASK);
				} else {
					int classInfo = 0;
					int classInfoTmp = 0;
					byte[] systemDistMask = new byte[BPPacket.BYTE_NUM_OF_A_DIST];
					SystemSignalInfoUnit systemSignalInfoUnitTmp = null;
					boolean endFlag = false;
					for (int i = 0; i < BPPacket.MAX_SYS_SIG_DIST_NUM; i++) {
						classInfo = 0;
						Arrays.fill(systemDistMask, (byte)0);
						Iterator<SystemSignalInfoUnit> it = systemSignalInfoUnitList.iterator();
						
						while (it.hasNext()) {
							systemSignalInfoUnitTmp = it.next();
							if (BPPacket.inDist(i, systemSignalInfoUnitTmp.getSysSigId())) {
								classInfoTmp = BPPacket.whichClass(i, systemSignalInfoUnitTmp.getSysSigId());
								/* set the biggest classInfoTmp */
								if(classInfoTmp > classInfo) {
									classInfo = classInfoTmp;
								}
								BPPacket.setSystemSignalBit(i, systemSignalInfoUnitTmp.getSysSigId(), systemDistMask);
								it.remove();
							}
							
						}
						if (systemSignalInfoUnitList.isEmpty()) {
							endFlag = true;
						}
						if(classInfo > 0) {
							byte systemSignalMapHeader = (byte)((i << 4) | (classInfo << 1));
							if(endFlag) {
								systemSignalMapHeader |= Payload.SYSTEM_SIGNAL_MAP_END_MASK;
							}
							buffer.put(systemSignalMapHeader);
							int systemSignalBytesNum = 1 << (classInfo - 1);
							for(int j = 0; j < systemSignalBytesNum; j++) {
								buffer.put(systemDistMask[j]);
							}
						}
						if(endFlag) {
							break;
						}
					}
				}
			}
			int langSupportMask = pld.getCustomSignalLangSupportMask();
			if(vrb.getSysSigMapCustomInfo()) {
				List<SystemSignalCustomInfoUnit> systemSignalCustomInfoUnitList = pld.getSystemSignalCustomInfoUnitLst();
				if(null == systemSignalCustomInfoUnitList || systemSignalCustomInfoUnitList.isEmpty()) {
					buffer.putUnsignedShort(0);
				} else {
					Iterator<SystemSignalCustomInfoUnit> it = systemSignalCustomInfoUnitList.iterator();
					buffer.putUnsignedShort(systemSignalCustomInfoUnitList.size());
					SystemSignalCustomInfoUnit systemSignalCustomInfoUnitTmp;
					Map<Integer, Integer> enumLangMap;
					while(it.hasNext()) {
						systemSignalCustomInfoUnitTmp = it.next();
						int customFlags = systemSignalCustomInfoUnitTmp.getCustomFlags();
						enumLangMap = systemSignalCustomInfoUnitTmp.getEnumLangMap();
						SignalInterface signalInterfaceTmp = systemSignalCustomInfoUnitTmp.getSignalInterface();
						buffer.putUnsignedShort(systemSignalCustomInfoUnitTmp.getSysSigId());
						buffer.putUnsigned(customFlags & 0xFF);
						buffer.putUnsigned((customFlags >> 8) & 0xFF);
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_STATISTICS) != 0) {
							if(systemSignalCustomInfoUnitTmp.getSignalInterface().getEnStatistics()) {
								buffer.putUnsigned(1);
							} else {
								buffer.putUnsigned(0);
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG) != 0) {
							if(null != enumLangMap) {
								int enumLangNum = enumLangMap.size();
								if(enumLangNum > 255) {
									return false;
								}
								buffer.putUnsigned(enumLangNum);
								for(Map.Entry<Integer, Integer> entry: enumLangMap.entrySet()) {
									buffer.putUnsignedShort(entry.getKey());
									buffer.putInt(entry.getValue());
								}
							} else {
								buffer.putUnsigned(0);
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_GROUP_LANG) != 0) {
							if(signalInterfaceTmp != null) {
								buffer.putUnsignedShort(signalInterfaceTmp.getGroupLangId());
							} else {
								// "0" default group language id
								buffer.putUnsignedShort(0);
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ACCURACY) != 0) {
							if(signalInterfaceTmp != null) {
								buffer.putUnsigned(signalInterfaceTmp.getAccuracy());
							} else {
								// "0" default accuracy
								buffer.putUnsigned(0);
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN) != 0) {
							if(null == signalInterfaceTmp) {
								return ret;
							} 
							switch(signalInterfaceTmp.getValType()) {
							case BPPacket.VAL_TYPE_UINT32:
							{
								SystemSignalU32InfoHbn systemSignalU32InfoHbnTmp = (SystemSignalU32InfoHbn)signalInterfaceTmp;
								if(null == systemSignalU32InfoHbnTmp) {
									return ret;
								}
								buffer.putUnsignedInt(systemSignalU32InfoHbnTmp.getMinVal());
								break;
							}
							case BPPacket.VAL_TYPE_UINT16:
							{
								SystemSignalU16InfoHbn systemSignalU16InfoHbnTmp = (SystemSignalU16InfoHbn)signalInterfaceTmp;
								if(null == systemSignalU16InfoHbnTmp) {
									return ret;
								}
								buffer.putUnsignedShort(systemSignalU16InfoHbnTmp.getMinVal());
								break;
							}
							case BPPacket.VAL_TYPE_IINT32:
							{
								SystemSignalI32InfoHbn systemSignalI32InfoHbnTmp = (SystemSignalI32InfoHbn)signalInterfaceTmp;
								if(null == systemSignalI32InfoHbnTmp) {
									return ret;
								}
								buffer.putInt(systemSignalI32InfoHbnTmp.getMinVal());
								break;
							}
							case BPPacket.VAL_TYPE_IINT16:
							{
								SystemSignalI16InfoHbn systemSignalI16InfoHbnTmp = (SystemSignalI16InfoHbn)signalInterfaceTmp;
								if(null == systemSignalI16InfoHbnTmp) {
									return ret;
								}
								buffer.putShort(systemSignalI16InfoHbnTmp.getMinVal());
								break;
							}
							case BPPacket.VAL_TYPE_FLOAT:
							{
								SystemSignalFloatInfoHbn systemSignalFloatInfoHbnTmp = (SystemSignalFloatInfoHbn)signalInterfaceTmp;
								if(null == systemSignalFloatInfoHbnTmp) {
									return ret;
								}
								buffer.putFloat(systemSignalFloatInfoHbnTmp.getMinVal());
								break;
							}
							default:
								return ret;
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX) != 0) {
							if(null == signalInterfaceTmp) {
								return ret;
							} 
							switch(signalInterfaceTmp.getValType()) {
							case BPPacket.VAL_TYPE_UINT32:
							{
								SystemSignalU32InfoHbn systemSignalU32InfoHbnTmp = (SystemSignalU32InfoHbn)signalInterfaceTmp;
								if(null == systemSignalU32InfoHbnTmp) {
									return ret;
								}
								buffer.putUnsignedInt(systemSignalU32InfoHbnTmp.getMaxVal());
								break;
							}
							case BPPacket.VAL_TYPE_UINT16:
							{
								SystemSignalU16InfoHbn systemSignalU16InfoHbnTmp = (SystemSignalU16InfoHbn)signalInterfaceTmp;
								if(null == systemSignalU16InfoHbnTmp) {
									return ret;
								}
								buffer.putUnsignedShort(systemSignalU16InfoHbnTmp.getMaxVal());
								break;
							}
							case BPPacket.VAL_TYPE_IINT32:
							{
								SystemSignalI32InfoHbn systemSignalI32InfoHbnTmp = (SystemSignalI32InfoHbn)signalInterfaceTmp;
								if(null == systemSignalI32InfoHbnTmp) {
									return ret;
								}
								buffer.putInt(systemSignalI32InfoHbnTmp.getMaxVal());
								break;
							}
							case BPPacket.VAL_TYPE_IINT16:
							{
								SystemSignalI16InfoHbn systemSignalI16InfoHbnTmp = (SystemSignalI16InfoHbn)signalInterfaceTmp;
								if(null == systemSignalI16InfoHbnTmp) {
									return ret;
								}
								buffer.putShort(systemSignalI16InfoHbnTmp.getMaxVal());
								break;
							}
							case BPPacket.VAL_TYPE_FLOAT:
							{
								SystemSignalFloatInfoHbn systemSignalFloatInfoHbnTmp = (SystemSignalFloatInfoHbn)signalInterfaceTmp;
								if(null == systemSignalFloatInfoHbnTmp) {
									return ret;
								}
								buffer.putFloat(systemSignalFloatInfoHbnTmp.getMaxVal());
								break;
							}
							default:
								return ret;
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF) != 0) {
							if(null == signalInterfaceTmp) {
								return false;
							} 
							switch(signalInterfaceTmp.getValType()) {
							case BPPacket.VAL_TYPE_UINT32:
							{
								buffer.putUnsignedInt((Long)signalInterfaceTmp.getDefaultValue());
								break;
							}
							case BPPacket.VAL_TYPE_UINT16:
							{
								buffer.putUnsignedShort((Integer)signalInterfaceTmp.getDefaultValue());
								break;
							}
							case BPPacket.VAL_TYPE_IINT32:
							{
								buffer.putUnsignedShort((Integer)signalInterfaceTmp.getDefaultValue());
								break;
							}
							case BPPacket.VAL_TYPE_IINT16:
							{
								buffer.putShort((Short)signalInterfaceTmp.getDefaultValue());
								break;
							}
							case BPPacket.VAL_TYPE_FLOAT:
							{
								buffer.putFloat((Float)signalInterfaceTmp.getDefaultValue());
								break;
							}
							case BPPacket.VAL_TYPE_BOOLEAN:
							{
								buffer.putUnsigned((Boolean)signalInterfaceTmp.getDefaultValue() ? 1 : 0);
								break;
							}
							case BPPacket.VAL_TYPE_STRING:
							{
								String stringTmp = (String)signalInterfaceTmp.getDefaultValue();
								
								
								if(null != stringTmp) {
									byte[] stringTmpBytes = stringTmp.getBytes();
									int len = stringTmpBytes.length;
									if(len > MAX_CUSTOM_SIGNAL_STRING_LENGTH) {
										len = MAX_CUSTOM_SIGNAL_STRING_LENGTH;
									}
									buffer.putUnsigned(len);
									if(len > 0) {
										buffer.put(stringTmpBytes, 0, len);
									}
								} else {
									buffer.putUnsigned(0);
								}
								break;
							}
							default:
								return ret;
							}
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_CLASS) != 0) {
							buffer.putUnsigned(systemSignalCustomInfoUnitTmp.getAlarmClass());
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_BEF) != 0) {
							buffer.putUnsigned(systemSignalCustomInfoUnitTmp.getDelayBeforeAlarm());
						}
						if((customFlags & SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_AFT) != 0) {
							buffer.putUnsigned(systemSignalCustomInfoUnitTmp.getDelayAfterAlarm());
						}
						
					}
				}
				
			}
			if(vrb.getCusSigMapFlag()) {
				List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
				if (null == customSignalInfoUnitList || customSignalInfoUnitList.isEmpty()) {
					/* no custom signal */
					logger.info("no custom signal");
					buffer.putUnsignedShort(0);
				} else if(langSupportMask <= 0 || (langSupportMask & 0xFF) == 0) { 
					/* no supported language */
					logger.info("no supported language");
					buffer.putUnsignedShort(0);
				} else {
					buffer.putUnsignedShort(customSignalInfoUnitList.size());
					Iterator<CustomSignalInfoUnit> it = customSignalInfoUnitList.iterator();
					byte cusSignalFlag = 0;
					int signalIdTmp = 0;
					int valType = BPPacket.VAL_TYPE_INVALID;

					while(it.hasNext()) {
						CustomSignalInfoUnit customSignalInfoUnit = it.next();
						signalIdTmp = customSignalInfoUnit.getCusSigId();
						cusSignalFlag = 0;
						if (customSignalInfoUnit.isIfAlarm()) {
							cusSignalFlag |= Payload.CUSTOM_SIGNAL_ALARM_FLAG_MASK;
						}
						if (customSignalInfoUnit.isIfNotifing()) {
							cusSignalFlag |= Payload.CUSTOM_SIGNAL_STATISTICS_FLAG_MASK;
						}
						switch (customSignalInfoUnit.getCustomSignalInterface().getPermission()) {
						case BPPacket.SIGNAL_PERMISSION_CODE_RO:
							/* need do nothing */
							break;
						case BPPacket.SIGNAL_PERMISSION_CODE_RW:
							cusSignalFlag |= Payload.CUSTOM_SIGNAL_RW_FLAG_MASK;
							break;
						default:
							logger.warn("inner warning: unknown permission code, force to set it RO");

						}
						valType = customSignalInfoUnit.getCustomSignalInterface().getValType();
						if (valType < 0 || valType >= BPPacket.MAX_VAL_TYPE_NUM) {
							throw new Exception("inner error: invalid valType=" + valType);
						}
						cusSignalFlag |= (valType << 4);
						buffer.putUnsignedShort(signalIdTmp);
						buffer.put(cusSignalFlag);
						/* default to add language end flag */
						// langSupportMask |= 0x01;
						// buffer.put((byte)(langSupportMask & 0xFF));

						// TODO: not support other language now(2018.08.14)
						Map<Integer, String> nameLangMap = customSignalInfoUnit.getSignalNameLangMap();
						if (null == nameLangMap) {
							logger.error("inner error: null == nameLangMap");
						}
						Map<Integer, String> unitLangMap = customSignalInfoUnit.getSignalUnitLangMap();
						Map<Integer, String> groupLangMap = customSignalInfoUnit.getGroupLangMap();
						Map<Integer, Map<Integer, String>> enumLangMap = customSignalInfoUnit.getSignalEnumLangMap();
						// CustomAlarmInfoUnit customAlarmInfoUnit = customSignalInfoUnit.getCustomAlarmInfoUnit();
						int langSupportMaskByte = 0;
						int langSupportMaskTmp = langSupportMask;
						for (int i = 7; i > 0; i--) {
							langSupportMaskByte = 0x01 << i;
							if ((langSupportMaskTmp & langSupportMaskByte) == 0) {
								continue;
							} else {
								if ((langSupportMaskTmp & (~langSupportMaskByte)) == 0) {
									/* 0x01 end flag */
									langSupportMaskByte |= 0x01;
								}
							}
							langSupportMaskTmp &= (~langSupportMaskByte);
							buffer.putUnsigned(langSupportMaskByte);
							String name = nameLangMap.get(i);
							if (null == name || name.getBytes().length > BPPacket.MAX_CUSTOM_SIGNAL_NAME_LENGTH) {
								logger.error("inner error: null == nameLangMap.get({})", i);
								break;
							}
							buffer.putUnsigned(name.getBytes().length & 0xFF);
							buffer.put(name.getBytes());
							if (null == unitLangMap) {
								/* 2 bytes */
								/* TODO: temporary no support the system unit language unit */
								// buffer.put(BPPacket.SYSTEM_UNIT_LANGUAGE_FLAG & 0x02);
								buffer.putUnsigned(0);
							} else {
								String unit = unitLangMap.get(i);
								if (null == unit || unit.getBytes().length > BPPacket.MAX_CUSTOM_SIGNAL_UNIT_LENGTH) {
									logger.error("inner error: null == nameLangMap.get({})", i);
									break;
								}
								buffer.putUnsigned(unit.getBytes().length & 0xFF);
								buffer.put(unit.getBytes());
							}
							if (null == groupLangMap) {
								/* 2 bytes */
								/* TODO: temporary no support the system unit language unit */
								// buffer.put(BPPacket.SYSTEM_UNIT_LANGUAGE_FLAG & 0x02);
								buffer.putUnsigned(0);
							} else {
								String group = groupLangMap.get(i);
								if (null == group || group.getBytes().length > BPPacket.MAX_CUSTOM_SIGNAL_UNIT_LENGTH) {
									logger.error("inner error: null == nameLangMap.get({})", i);
									break;
								}
								buffer.putUnsigned(group.getBytes().length & 0xFF);
								buffer.put(group.getBytes());
							}
							if (customSignalInfoUnit.isIfAlarm()) {
								//if (null == customAlarmInfoUnit || null == customAlarmInfoUnit.getCustomAlarmNameLangMap()) {
									/* 2 bytes */
									/* TODO: temporary no support the system unit language unit */
									// buffer.put(BPPacket.SYSTEM_UNIT_LANGUAGE_FLAG & 0x02);
									//buffer.putUnsigned(0);
								//} else {
								//	String alarmLang = customAlarmInfoUnit.getCustomAlarmNameLangMap().get(i);
								//	if (null == alarmLang || alarmLang.getBytes().length > BPPacket.MAX_CUSTOM_SIGNAL_UNIT_LENGTH) {
								//		logger.error("inner error: null == nameLangMap.get({})", i);
								//		break;
								//	}
								//	buffer.put((byte) (alarmLang.getBytes().length & 0xFF));
								//	buffer.put(alarmLang.getBytes());
								//}
							}


							if (BPPacket.VAL_TYPE_ENUM == valType) {
								int enumLangNum = enumLangMap.size();
								buffer.put((byte) enumLangNum);
								for (int j = 0; j < enumLangNum; j++) {
									Map<Integer, String> enumLangMapEntity = enumLangMap.get(j);
									if (null == enumLangMapEntity) {
										logger.error("inner error: null == enumLangMapEntity");
										break;
									}
									String enumVal = enumLangMapEntity.get(i);
									if (null == enumVal) {
										logger.error("inner error: null == enumVal");
										enumVal = "";
									}
									buffer.put((byte) (enumVal.getBytes().length & 0xFF));
									buffer.put(enumVal.getBytes());

								}
							} else {
								buffer.putUnsigned(0);
							}
				
						}

						switch (valType) {
						case BPPacket.VAL_TYPE_UINT32: {
							CustomSignalU32InfoHbn customSignalU32InfoHbn = (CustomSignalU32InfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalU32InfoHbn) {
								logger.error("inner error: null == customSignalU32InfoHbn");
								break;
							}

							/* accuracy 0 */
							buffer.put((byte) 0);
							buffer.putUnsignedInt(customSignalU32InfoHbn.getMinVal());
							buffer.putUnsignedInt(customSignalU32InfoHbn.getMaxVal());
							buffer.putUnsignedInt(customSignalU32InfoHbn.getDefVal());

							break;
						}
						case BPPacket.VAL_TYPE_UINT16: {
							CustomSignalU16InfoHbn customSignalU16InfoHbn = (CustomSignalU16InfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalU16InfoHbn) {
								logger.error("inner error: null == customSignalU16InfoHbn");
								break;
							}
							/* accuracy 0 */
							buffer.put((byte) 0);
							buffer.putUnsignedShort(customSignalU16InfoHbn.getMinVal());
							buffer.putUnsignedShort(customSignalU16InfoHbn.getMaxVal());
							buffer.putUnsignedShort(customSignalU16InfoHbn.getDefVal());
							break;
						}
						case BPPacket.VAL_TYPE_IINT32: {
							CustomSignalI32InfoHbn customSignalI32InfoHbn = (CustomSignalI32InfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalI32InfoHbn) {
								logger.error("inner error: null == customSignalI32InfoHbn");
								break;
							}

							/* accuracy 0 */
							buffer.put((byte) 0);
							buffer.putInt(customSignalI32InfoHbn.getMinVal());
							buffer.putInt(customSignalI32InfoHbn.getMaxVal());
							buffer.putInt(customSignalI32InfoHbn.getDefVal());
							break;
						}
						case BPPacket.VAL_TYPE_IINT16: {
							CustomSignalI16InfoHbn customSignalI16InfoHbn = (CustomSignalI16InfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalI16InfoHbn) {
								logger.error("inner error: null == customSignalI16InfoHbn");
								break;
							}

							/* accuracy 0 */
							buffer.put((byte) 0);
							buffer.putShort(customSignalI16InfoHbn.getMinVal());
							buffer.putShort(customSignalI16InfoHbn.getMaxVal());
							buffer.putShort(customSignalI16InfoHbn.getDefVal());
							break;
						}
						case BPPacket.VAL_TYPE_ENUM: {
							CustomSignalEnumInfoHbn customSignalEnumInfoHbn = (CustomSignalEnumInfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalEnumInfoHbn) {
								logger.error("inner error: null == customSignalEnumInfoHbn");
								break;
							}

							/* default value */
							buffer.putUnsignedShort(customSignalEnumInfoHbn.getDefVal());
							break;
						}
						case BPPacket.VAL_TYPE_FLOAT: {
							CustomSignalFloatInfoHbn customSignalFloatInfoHbn = (CustomSignalFloatInfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalFloatInfoHbn) {
								logger.error("inner error: null == customSignalFloatInfoHbn");
								break;
							}

							/* accuracy 0 */
							buffer.put((byte) (customSignalFloatInfoHbn.getAccuracy() & 0xFF));
							buffer.putFloat(customSignalFloatInfoHbn.getMinVal());
							buffer.putFloat(customSignalFloatInfoHbn.getMaxVal());
							buffer.putFloat(customSignalFloatInfoHbn.getDefVal());
							break;
						}
						case BPPacket.VAL_TYPE_STRING: {
							CustomSignalStringInfoHbn customSignalStringInfoHbn = (CustomSignalStringInfoHbn) customSignalInfoUnit
									.getCustomSignalInterface();
							if (null == customSignalStringInfoHbn) {
								logger.error("inner error: null == customSignalStringInfoHbn");
								break;
							}
							
							String defVal = customSignalStringInfoHbn.getDefVal();
							int strlen = customSignalStringInfoHbn.getDefVal().getBytes().length;
							if (strlen > BPPacket.MAX_CUSTOM_SIGNAL_STRING_LENGTH) {
								strlen = BPPacket.MAX_CUSTOM_SIGNAL_STRING_LENGTH;
							}
							buffer.putUnsigned(strlen);
							if(strlen > 0) {
								buffer.put(defVal.getBytes(), 0, strlen);
							}
							break;
						}
						case BPPacket.VAL_TYPE_BOOLEAN:
							/* TODO: */
							logger.error("VAL_TYPE_BOOLEAN not supported");
							break;
						default:
							if (customSignalInfoUnit.isIfAlarm()) {
								/* TODO: */
								logger.error("ALARM VALUE not supported");
							}
							logger.error("inner error: unknown value type");
							break;
						}
						if (customSignalInfoUnit.isIfAlarm()) {
							// if (null == customAlarmInfoUnit || null == customAlarmInfoUnit.getCustomSignalAlmInfoHbn()) {
								/* 2 bytes */
								/* TODO: temporary no support the system unit language unit */
								// buffer.put(BPPacket.SYSTEM_UNIT_LANGUAGE_FLAG & 0x02);
							//	buffer.putUnsigned(BPPacket.ALARM_CLASS_NONE);
							//	buffer.putUnsigned(BPPacket.ALARM_DELAY_DEFAULT);
							//	buffer.putUnsigned(BPPacket.ALARM_DELAY_DEFAULT);
								
							//} else {
							//	CustomSignalAlmInfoHbn customSignalAlmInfoHbnTmp = customAlarmInfoUnit.getCustomSignalAlmInfoHbn();
							//	buffer.putUnsigned(customSignalAlmInfoHbnTmp.getAlmClass());
							//	buffer.putUnsigned(customSignalAlmInfoHbnTmp.getDlyBeforeAlm());
							//	buffer.putUnsigned(customSignalAlmInfoHbnTmp.getDlyAfterAlm());
							//}	
						}
					}
				}
			}
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
