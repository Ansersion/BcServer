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
public class BPPacketSPECSET extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketSPECSET.class);
    /* specset type*/
    public static final int SPEC_TYPE_RESET_SSID_AND_ADMIN_USER = 0;
    public static final int SPEC_TYPE_SYNC_CHECKSUM_TIMESTAMP = 1;
    /* specset type detail */
    public static final int USER_INFO_TIMESTAMP = 0;

	protected BPPacketSPECSET() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.SPECSET);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public int parseVariableHeader() {
		try {
			VariableHeader vrb = getVrbHead();
			int specsetType = getIoBuffer().getUnsignedShort();
			vrb.setSpecsetType(specsetType);
			
			int packSeq = getIoBuffer().getUnsignedShort();
			vrb.setPackSeq(packSeq);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {
			VariableHeader vrb = getVrbHead();
			Payload pld = getPld();
			switch(vrb.getSpecsetType()) {
			case SPEC_TYPE_SYNC_CHECKSUM_TIMESTAMP: {
				pld.setTimestampType(getIoBuffer().getUnsigned());
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}
		return 0;
	}
}
