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
public class BPPacketPOSTACK extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPOSTACK.class);
	public static final int RET_CODE_SIGNAL_ID_UNSUPPORTED = 0x03;
	public static final int RET_CODE_SIGNAL_VALUE_UNFORMAL = 0x04;
	

	private int signalIdWithErr;
	
	protected BPPacketPOSTACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.POSTACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
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

		return true;
	}

	@Override
	public boolean assemblePayload() {
		return true;
	}
	
	@Override
	public int parseVariableHeader() {

		try {
			// flags(1 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int seqId = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seqId);
			
			byte retCode = getIoBuffer().get();
			getVrbHead().setRetCode(retCode);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {
			IoBuffer ioBuffer = getIoBuffer();
			if (RET_CODE_SIGNAL_ID_UNSUPPORTED == getVrbHead().getRetCode()
					|| RET_CODE_SIGNAL_VALUE_UNFORMAL == getVrbHead().getRetCode()) {
				int signalValuePositionStart = ioBuffer.position();
				signalIdWithErr = ioBuffer.getUnsignedShort();
				int signalValuePositionEnd = ioBuffer.position();
				ioBuffer.rewind();
				ioBuffer.position(signalValuePositionStart);
				byte[] signalValueRelay = new byte[signalValuePositionEnd - signalValuePositionStart];
				ioBuffer.get(signalValueRelay);
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return 0;
	}

	public int getSignalIdWithErr() {
		return signalIdWithErr;
	}
	
	

}
