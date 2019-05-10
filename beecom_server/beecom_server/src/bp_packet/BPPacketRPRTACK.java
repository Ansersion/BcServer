/**
 * 
 */
package bp_packet;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class BPPacketRPRTACK extends BPPacket {
	public static final int RET_CODE_VRB_HEADER_FLAG_ERR = 0x01;
	public static final int RET_CODE_SIG_MAP_UNCHECK = 0x02;
	public static final int RET_CODE_SIG_ID_INVALID = 0x03;
	public static final int RET_CODE_SIG_VAL_INVALID = 0x04;
	public static final int RET_CODE_SIGNAL_MAP_CHECKSUM_ERR = 0x05;
	// public static final int RET_CODE_SIGNAL_MAP_CHECKSUM_ERR = 0x06;
	public static final int RET_CODE_SIGNAL_MAP_DAMAGED_ERR = 0x07;
	public static final int RET_CODE_SIGNAL_MAP_ERR = 0x08;
	
	protected BPPacketRPRTACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.RPRTACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte flags = getVrbHead().getFlags();
		getIoBuffer().put(flags);
		int packSeq = (byte)getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);	
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			if(null != getPld().getError()) {
				getIoBuffer().putUnsignedShort(getPld().getError().getSigId());
			}
		}

		
		return true;
	}
}
