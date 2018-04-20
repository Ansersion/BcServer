/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class BPPacketPOSTACK extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPOSTACK.class);
	
	int packSeq;
	
	
	protected BPPacketPOSTACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.POSTACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	@Override
	public boolean assembleFixedHeader() {
		int packType = getPackTypeIntFxHead();
		byte packFlags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((packType & 0xf) << 4) | (packFlags & 0xf));

		getIoBuffer().put(encodedByte);

		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte) 0);

		return false;
	}

	@Override
	public boolean assembleVariableHeader() {
		getIoBuffer().putShort((short)packSeq);

		return false;
	}

	@Override
	public boolean assemblePayload() {

		return false;
	}
	
	@Override
	public int parseVariableHeader() {

		try {
			// flags(1 byte) + client ID(2 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);

			int seqId = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seqId);
			
			byte retCode = getIoBuffer().get();
			getVrbHead().setRetCode(retCode);
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		return 0;
	}

}
