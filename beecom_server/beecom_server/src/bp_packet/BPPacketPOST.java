/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;
import other.Util;

/**
 * @author Ansersion
 *
 */
public class BPPacketPOST extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPOST.class); 
	
	public static final int RET_CODE_INVALID_FLAGS_ERR = 0x01;
	public static final int RET_CODE_INVALID_DEVICE_ID_ERR = 0x02;
	public static final int RET_CODE_NOT_LOGIN_ERR = 0x03;
	public static final int RET_CODE_OFF_LINE_ERR = 0x06;
	public static final int RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR = 0x07;
	public static final int RET_CODE_TIMEOUT_ERR = 0x09;
	public static final int RET_CODE_BUFFER_FILLED_ERR = 0x0;
	public static final int RET_CODE_PEER_INNER_ERR = 0xFF;
	
	
	int packSeq;
	DevSigData[] sigDatas = null; 
	int deviceNum;
	private byte[] signalValueRelay;
	
	protected BPPacketPOST() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.POST);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public int parseVariableHeader() {
		try {

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
				long uniqDevId = ioBuffer.getUnsignedInt();
				pld.setDevUniqId(uniqDevId);
				final int sigNum = ioBuffer.getUnsigned();
				for (int i = 0; i < sigNum; i++) {
					int sigId = ioBuffer.getUnsignedShort();
					byte sigType = (byte) (ioBuffer.get() & BPPacket.VAL_TYPE_MASK);
					switch (sigType) {
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
					case VAL_TYPE_STRING: {
						int strLen = ioBuffer.get();
						byte[] strBytes = new byte[strLen];
						ioBuffer.get(strBytes);
						String value = new String(strBytes, "UTF-8");
						pld.putSigValMap(sigId, sigType, value);
						break;
					}
					case VAL_TYPE_BOOLEAN: {
						Boolean value;
						if (ioBuffer.get() == 0) {
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

			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return 0;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		if(0 == vrb.getPackSeq()) {
			vrb.initPackSeq();
		}
		byte flags = vrb.getFlags();
		getIoBuffer().put(flags);
		int packSeqTmp = vrb.getPackSeq();
		getIoBuffer().putUnsignedShort(packSeqTmp);	
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		if(0 != getVrbHead().getRetCode()) {
			return false;
		}
		
		byte[] relayData = getPld().getRelayData();
		if(null == relayData || 0 == relayData.length) {
			return false;
		}
		
		getIoBuffer().put(relayData);
		
		return true;
	}

	public byte[] getSignalValueRelay() {
		return signalValueRelay;
	}
	
	
}
