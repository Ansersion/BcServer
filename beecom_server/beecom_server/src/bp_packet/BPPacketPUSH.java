/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DB_DevAuthRec;

/**
 * @author Ansersion
 *
 */
public class BPPacketPUSH extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPUSH.class);
	
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) throws Exception {
		// TODO Auto-generated method stub

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		try {

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
	}
}
