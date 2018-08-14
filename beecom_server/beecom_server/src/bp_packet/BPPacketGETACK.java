/**
 * 
 */
package bp_packet;

import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.CustomSignalInfoUnit;
import db.CustomSignalU32InfoHbn;
import db.SystemSignalInfoUnit;
import other.CrcChecksum;

import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
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
		int packSeq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);

		return false;
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;
		try {

			VariableHeader vrb = getVrbHead();
			if (vrb.getReqAllDeviceId()) {
				return true;
			}

			Payload pld = getPld();
			IoBuffer buffer = getIoBuffer();
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
			if(vrb.getCusSigMapFlag()) {
				int langSupportMask = pld.getCustomSignalLangSupportMask();
				List<CustomSignalInfoUnit> customSignalInfoUnitList = pld.getCustomSignalInfoUnitLst();
				if (null == customSignalInfoUnitList || customSignalInfoUnitList.isEmpty()) {
					/* no custom signal */
					buffer.putUnsignedShort(0);
				} else if(langSupportMask <= 0 || (langSupportMask & 0xFF) == 0) { 
					/* no supported language */
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
						if(customSignalInfoUnit.isIfAlarm()) {
							cusSignalFlag |= Payload.CUSTOM_SIGNAL_ALARM_FLAG_MASK;
						}
						if(customSignalInfoUnit.isIfNotifing()) {
							cusSignalFlag |= Payload.CUSTOM_SIGNAL_STATISTICS_FLAG_MASK;
						}
						switch(customSignalInfoUnit.getCustomSignalInterface().getPermission()) {
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
						if(valType < 0 || valType >= BPPacket.MAX_VAL_TYPE_NUM) {
							throw new Exception("inner error: invalid valType=" + valType) ;
						}
						cusSignalFlag |= (valType << 4);
						buffer.put(cusSignalFlag);
						// buffer.put((byte)(langSupportMask & 0xFF));
						
						// TODO: not support other language now(2018.08.14)
						byte langSupportMaskByte = 0;
						for (int i = 7; i > 0; i--) {
							if(((langSupportMask >> i) & 0x01) == 0) {
								continue;
							} else {
								langSupportMaskByte = 0;
								if((langSupportMask & (~(1 << i))) == 0) {
									/* 0x01 end flag*/
									langSupportMaskByte = 0x01;
								}
							}
							buffer.put(langSupportMaskByte);
							switch (valType) {
							case BPPacket.VAL_TYPE_UINT32:
								CustomSignalU32InfoHbn customSignalU32InfoHbn = (CustomSignalU32InfoHbn) customSignalInfoUnit
										.getCustomSignalInterface();
								break;
							case BPPacket.VAL_TYPE_UINT16:
								break;
							case BPPacket.VAL_TYPE_IINT32:
								break;
							case BPPacket.VAL_TYPE_IINT16:
								break;
							case BPPacket.VAL_TYPE_ENUM:
								break;
							case BPPacket.VAL_TYPE_FLOAT:
								break;
							case BPPacket.VAL_TYPE_STRING:
								break;
							case BPPacket.VAL_TYPE_BOOLEAN:
								break;
							}
						}
					}
					

				}
				
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}


		/*
		int devNum = vctDevSigData.size() & 0x0000FFFF;
		getIoBuffer().putUnsignedShort(devNum);
		String s;

		for (int i = 0; i < devNum; i++) {
			DevSigData sigDataAck = vctDevSigData.get(i);
			int sigNum;
			sigNum = sigDataAck.get1ByteDataMap().size();

			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 1 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					getIoBuffer().put(encodedByte);
					Map<Integer, Byte> map = sigDataAck.get1ByteDataMap();

					Iterator<Map.Entry<Integer, Byte>> it = map.entrySet().iterator();
					while (it.hasNext()) {

						Map.Entry<Integer, Byte> entry = it.next();
						getIoBuffer().putUnsignedShort((Integer) entry.getKey());
						getIoBuffer().put((Byte) entry.getValue());
					}

				}
			}

			sigNum = sigDataAck.get2ByteDataMap().size();
			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 2 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encodedByte |= 0x40;
					getIoBuffer().put(encodedByte);
					Map<Integer, Short> map = sigDataAck.get2ByteDataMap();
					Iterator<Map.Entry<Integer, Short>> it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Short> entry = it.next();
						getIoBuffer().putUnsignedShort(entry.getKey());
						getIoBuffer().putShort((Short) entry.getValue());
					}

				}
			}

			sigNum = sigDataAck.get4ByteDataMap().size();
			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 4 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encodedByte |= 0x80;
					getIoBuffer().put(encodedByte);
					Map<Integer, Integer> map = sigDataAck.get4ByteDataMap();
					Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Integer> entry = it.next();
						getIoBuffer().putUnsignedShort(entry.getKey());
						getIoBuffer().putInt((Integer) entry.getValue());
					}

				}
			}
		}
		*/

		return false;
	}

}
