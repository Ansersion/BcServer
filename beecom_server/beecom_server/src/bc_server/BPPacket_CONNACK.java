/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPPacket_CONNACK extends BPPacket {

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
		
		encoded_byte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encoded_byte);
		
		short client_id = (short)getPld().getClntId();
		/*
		encoded_byte = (byte)((client_id & 0xff00) >> 8);
		getIoBuffer().put(encoded_byte);
		encoded_byte = (byte)((client_id & 0xff));
		getIoBuffer().put(encoded_byte);
		*/
		getIoBuffer().putUnsignedShort(client_id);
		// encoded_byte = (byte)getPld().getSymSetVer();
		// getIoBuffer().put(encoded_byte);
		
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
