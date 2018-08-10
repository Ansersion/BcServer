/**
 * 
 */
package bp_packet;

import other.CrcChecksum;
import sys_sig_table.BPSysSigTable;

/**
 * @author Ansersion
 *
 */
public class BPPacketCONNACK extends BPPacket {
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_LEVEL_ERR = 0x01;
	public static final int RET_CODE_SERVER_ERR = 0x02;
	public static final int RET_CODE_USER_INVALID = 0x03;
	public static final int RET_CODE_PWD_INVALID = 0x04;
	public static final int RET_CODE_ENCRYPT_ERR = 0x05;
	public static final int RET_CODE_CRC_UNSUPPORT = 0x06;
	public static final int RET_CODE_CRC_CHECK_ERR = 0x07;
	public static final int RET_CODE_CLNT_ID_INVALID = 0x08;
	public static final int RET_CODE_CLNT_UNKNOWN = 0x09;

	protected BPPacketCONNACK(FixedHeader fxHeader) {
		super(fxHeader);
	}
	
	protected BPPacketCONNACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.CONNACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	protected BPPacketCONNACK(BPPacketCONNECT packConnnect) {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.CONNACK);
		fxHead.setCrcType(packConnnect.getFxHead().getCrcChk());
		VariableHeader vrbHead = getVrbHead();
		if(packConnnect.getVrbHead().getLevel() > BPPacket.BP_LEVEL) {
			vrbHead.setLevel(BPPacket.BP_LEVEL);
			vrbHead.setRetCode(0x01);
		} else {
			vrbHead.setLevel(packConnnect.getVrbHead().getLevel());
			if(0 == packConnnect.getVrbHead().getClientId()) {
				vrbHead.setNewCliIdFlg();
			}
			vrbHead.setRetCode(0x00);
		}
	}
	
	@Override
	public boolean assembleFixedHeader() {
        int pack_type = getPackTypeIntFxHead();
        byte pack_flags = getPackFlagsByteFxHead();
        byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));

        getIoBuffer().put(encoded_byte);

        getIoBuffer().putUnsignedShort(0);
        return false;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		byte encodedByte;
		
		encodedByte = (byte)getVrbHead().getLevel();
		getIoBuffer().put(encodedByte);
		encodedByte = getVrbHead().getFlags();
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(encodedByte);
		
		return false;
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			return false;
		}
		
		encodedByte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encodedByte);
		
		short clientId = (short)getPld().getClntId();

		getIoBuffer().putUnsignedShort(clientId);

		getIoBuffer().putUnsignedShort(BPSysSigTable.BP_SYS_SIG_SET_VERSION);
		
		return true;
	}

}
