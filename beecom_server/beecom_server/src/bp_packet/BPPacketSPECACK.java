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
public class BPPacketSPECACK extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketSPECACK.class);
	public static final int RET_CODE_CLNT_ID_INVALID = 0x02;
	
	
	protected BPPacketSPECACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.SPECACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		
        VariableHeader vrb = getVrbHead();
        
		int specType = vrb.getSpecsetType();
		getIoBuffer().putUnsignedShort(specType);
		int packSeq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);
		
		return true;
	}

	@Override
	public boolean assemblePayload() {	
		try {
	        VariableHeader vrb = getVrbHead();
	        
			int specType = vrb.getSpecsetType();
			switch(specType) {
			case BPPacketSPECSET.SPEC_TYPE_SYNC_CHECKSUM_TIMESTAMP:
				long timestamp = getPld().getTimestamp();
				getIoBuffer().putLong(timestamp);
				break;
			default:
				break;
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}
		return true;
	}
}
