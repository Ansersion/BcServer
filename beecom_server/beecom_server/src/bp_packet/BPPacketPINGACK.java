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
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.PINGACK);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));
		
		getIoBuffer().put(encodedByte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		byte encodedByte;
		
		encodedByte = getVrbHead().getFlags();
		getIoBuffer().put(encodedByte);
		int pack_seq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(pack_seq);
		byte ret_code = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(ret_code);
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			return false;
		}
		
		return true;
	}
}
