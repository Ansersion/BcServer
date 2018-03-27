/**
 * 
 */
package bp_packet;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class BPPacket_RPRTACK extends BPPacket {
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_FLAGS_INVALID = 0x01;
	public static final int RET_CODE_CLNT_ID_INVALID = 0x02;
	public static final int RET_CODE_SIG_ID_INVALID = 0x03;
	public static final int RET_CODE_SIG_VAL_INVALID = 0x04;
	
	
	protected BPPacket_RPRTACK() {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.RPRTACK);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));
		
		getIoBuffer().put(encoded_byte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		
		int pack_seq = (byte)getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(pack_seq);	
		byte ret_code = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(ret_code);
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			if(null != getPld().Error) {
				getIoBuffer().putUnsignedShort(getPld().Error.getLst().get(0));
			}
		}

		
		return false;
	}
}
