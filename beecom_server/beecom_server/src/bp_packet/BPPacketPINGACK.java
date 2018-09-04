/**
 * 
 */
package bp_packet;

import other.CrcChecksum;

/**
 * @author hubing
 *
 */
public class BPPacketPINGACK extends BPPacket {
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_CLNT_ID_INVALID = 0x02;
	
	
	protected BPPacketPINGACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.PINGACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte encodedByte;
		
		encodedByte = getVrbHead().getFlags();
		getIoBuffer().put(encodedByte);
		int packSeq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);
		
		return false;
	}

	@Override
	public boolean assemblePayload() {		
		return RET_CODE_OK == getVrbHead().getRetCode();
	}
}
