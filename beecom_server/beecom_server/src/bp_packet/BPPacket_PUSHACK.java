/**
 * 
 */
package bp_packet;

/**
 * @author hubing
 *
 */
public class BPPacket_PUSHACK extends BPPacket {
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
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		byte encoded_byte;
		
		encoded_byte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encoded_byte);
		
		int client_id = getPld().getClntId();
		encoded_byte = (byte)((client_id & 0xff00) >> 8);
		getIoBuffer().put(encoded_byte);
		encoded_byte = (byte)((client_id & 0xff));
		getIoBuffer().put(encoded_byte);
		encoded_byte = (byte)getPld().getSymSetVer();
		getIoBuffer().put(encoded_byte);
		
		return false;
	}
}
