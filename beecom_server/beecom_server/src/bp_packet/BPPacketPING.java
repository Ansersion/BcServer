/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class BPPacketPING extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPING.class);
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {
		return true;
	}
	

	
	protected BPPacketPING() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.PING);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) {

		return true;
	}
	
	@Override
	public int parseVariableHeader() {
		try {

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int packSeq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(packSeq);
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
	public boolean parsePayload(byte[] buf) {
		return true;
	}

	@Override
	public int parsePayload() {
		return 0;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		vrb.initPackSeq();
		byte flags = vrb.getFlags();
		getIoBuffer().put(flags);
		int clntId = vrb.getClientId();
		getIoBuffer().putUnsignedShort(clntId);
		int packSeq = vrb.getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);	
		
		return false;
	}

	@Override
	public boolean assemblePayload() {
		
		return false;
	}

}
