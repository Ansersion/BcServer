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
	
	int PackSeq;
	
	
	protected BPPacketPOSTACK() {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.POSTACK);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));

		getIoBuffer().put(encodedByte);

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
	
	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub

		try {
			// flags(1 byte) + client ID(2 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int seq_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seq_id);
			
			byte ret_code = getIoBuffer().get();
			getVrbHead().setRetCode(ret_code);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}
		return 0;
	}

}
