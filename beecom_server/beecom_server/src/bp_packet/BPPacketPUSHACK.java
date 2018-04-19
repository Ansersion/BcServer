/**
 * 
 */
package bp_packet;

/**
 * @author hubing
 *
 */
public class BPPacketPUSHACK extends BPPacket {
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
		// TODO Auto-generated method stub
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
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		byte encodedByte;
		
		encodedByte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encodedByte);
		
		int client_id = getPld().getClntId();
		encodedByte = (byte)((client_id & 0xff00) >> 8);
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)(client_id & 0xff);
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)getPld().getSymSetVer();
		getIoBuffer().put(encodedByte);
		
		return false;
	}
}
