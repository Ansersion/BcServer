/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPPacket_CONNACK extends BPPacket {
	
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

	protected BPPacket_CONNACK(FixedHeader fx_header) {
		super(fx_header);
	}
	
	protected BPPacket_CONNACK() {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.CONNACK);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}
	
	protected BPPacket_CONNACK(BPPacket_CONNECT pack_con) {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.CONNACK);
		fx_head.setCrcType(pack_con.getFxHead().getCrcChk());
		VariableHeader vrb_head = getVrbHead();
		if(pack_con.getVrbHead().getLevel() > BPPacket.BP_LEVEL) {
			vrb_head.setLevel(BPPacket.BP_LEVEL);
			// TODO: use macro instead of constant number
			vrb_head.setRetCode(0x01);
		} else {
			vrb_head.setLevel(pack_con.getVrbHead().getLevel());
			if(0 == pack_con.getVrbHead().getClientId()) {
				vrb_head.setNewCliIdFlg();
			}
			// TODO: use macro instead of constant number
			vrb_head.setRetCode(0x00);
		}
	}
	
	/*
	@Override
	public boolean assembleStart() throws Exception {
		return true;
	}
	*/
	
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
		byte encoded_byte;
		
		encoded_byte = (byte)getVrbHead().getLevel();
		getIoBuffer().put(encoded_byte);
		encoded_byte = getVrbHead().getFlags();
		getIoBuffer().put(encoded_byte);
		encoded_byte = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(encoded_byte);
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		byte encoded_byte;
		if(RET_CODE_OK != getVrbHead().getRetCode()) {
			return false;
		}
		
		encoded_byte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encoded_byte);
		
		short client_id = (short)getPld().getClntId();

		getIoBuffer().putUnsignedShort(client_id);

		getIoBuffer().putUnsignedShort(BPSysSigTable.BP_SYS_SIG_SET_VERSION);
		
		return false;
	}

	/*
	@Override
	public boolean assembleCrc() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	*/
}
