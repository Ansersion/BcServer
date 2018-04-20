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
	public boolean assembleFixedHeader() {
		int packType = getPackTypeIntFxHead();
		byte packFlags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((packType & 0xf) << 4) | (packFlags & 0xf));
		
		getIoBuffer().put(encodedByte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() {
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
		
		encodedByte = (byte)getPld().getClntIdLen();
		getIoBuffer().put(encodedByte);
		
		int clientId = getPld().getClntId();
		encodedByte = (byte)((clientId & 0xff00) >> 8);
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)(clientId & 0xff);
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)getPld().getSymSetVer();
		getIoBuffer().put(encodedByte);
		
		return false;
	}
}
