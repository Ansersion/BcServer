/**
 * 
 */
package bp_packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;
import other.Util;

/**
 * @author Ansersion
 *
 */
public class BPPacketPING extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPING.class);

	protected BPPacketPING() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.PING);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public int parseVariableHeader() {
		try {

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int packSeq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(packSeq);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
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
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		
		return true;
	}

}
