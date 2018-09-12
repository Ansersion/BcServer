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
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_FLAGS_INVALID = 0x01;
	public static final int RET_CODE_SIG_MAP_UNCHECK = 0x02;
	public static final int RET_CODE_SIG_ID_INVALID = 0x03;
	public static final int RET_CODE_SIG_VAL_INVALID = 0x04;
	
	
	protected BPPacketRPRTACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.RPRTACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte flags = (byte)getVrbHead().getFlags();
		getIoBuffer().put(flags);
		int packSeq = (byte)getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);	
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);
		
		return false;
	}

	@Override
	public boolean assemblePayload() {
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			if(null != getPld().getError()) {
				getIoBuffer().putUnsignedShort(getPld().getError().getSigId());
			}
		}

		
		return false;
	}
}
