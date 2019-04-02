/**
 * 
 */
package bp_packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;


/**
 * @author Ansersion
 *
 */
public class BPPacketDISCONN extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketCONNECT.class); 

	@Override
	public int parseVariableHeader() {
		try {
			byte encodedByte = 0;
			encodedByte = getIoBuffer().get();
			parseVrbHeadFlags(encodedByte);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}
	
	@Override
	public int parsePayload() {
		// No payload
		return 0;
	}


}
