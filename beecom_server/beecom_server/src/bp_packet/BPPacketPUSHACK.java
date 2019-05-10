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
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		vrb.initPackSeq();
		byte encodedByte;
		
		encodedByte = (byte)vrb.getLevel();
		getIoBuffer().put(encodedByte);
		encodedByte = vrb.getFlags();
		getIoBuffer().put(encodedByte);
		encodedByte = (byte)vrb.getRetCode();
		getIoBuffer().put(encodedByte);
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;

		encodedByte = (byte)getPld().getSymSetVer();
		getIoBuffer().put(encodedByte);
		
		return true;
	}
}
