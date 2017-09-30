/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPPacket_POSTACK extends BPPacket {
	
	int PackSeq;
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));

		getIoBuffer().put(encoded_byte);

		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte) 0);

		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		getIoBuffer().putShort((short)PackSeq);

		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {

		return false;
	}

}