/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ansersion
 *
 */
public class BPPacketDISCONN extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketCONNECT.class); 

	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) throws Exception {
		// TODO Auto-generated method stub

		return true;
	}
	
	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		try {
			int counter = 0;

			byte clientIdMsb = buf[counter++];
			byte clientIdLsb = buf[counter];
			super.getVrbHead().parseClientId(clientIdMsb, clientIdLsb);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}
	
		return true;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {
		try {
			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);
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
	public boolean parsePayload(byte[] buf) throws Exception {
		return true;
	}
	
	@Override
	public int parsePayload() throws Exception {
		// No payload
		return 0;
	}


}
