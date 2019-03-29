package bc_console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_server.BcDecoder.DecodeState;
import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPParseException;
import bp_packet.BPParseFxHeaderException;
import bp_packet.FixedHeader;
import bp_packet.Payload;
import bp_packet.VariableHeader;
import other.CrcChecksum;
import other.Util;

public class BcConsoleDecoder extends CumulativeProtocolDecoder {
	private static final Logger logger = LoggerFactory.getLogger(BcConsoleDecoder.class); 

	public enum DecodeState {
		DEC_INVALID, DEC_FX_HEAD, DEC_REMAINING_DATA;
	}

	public static final String OLD_CONNECTION = "OLD CONNECTION";
	public static final String DATA = "DATA";
	private static final Charset cs = Charset.forName("utf8");
	private static final CharsetDecoder decoder = cs.newDecoder();

	@Override
	protected boolean doDecode(IoSession session, IoBuffer ioIn,
			ProtocolDecoderOutput decoderOut) throws Exception {
		boolean ret = false;
		
		if (!session.containsAttribute(OLD_CONNECTION)) {
			session.setAttribute(OLD_CONNECTION, true);
			session.setAttribute(DATA, new String(""));
		}
	
		if (ioIn.remaining() > 0) { 
			String s = session.getAttribute("DATA") + ioIn.getString(decoder);
			if(s.indexOf(";")  < 0) {
				session.setAttribute(DATA, s);
			} else {
				ret = true;
				session.setAttribute(DATA, "");
				s.replace(';', ' ');
				String[] array = s.split(" +");
				BcConsoleCommand bcCosoleCommand = BcConsoleCommand.createConsoleCommand(array);
				if(null != bcCosoleCommand) {
					// BcConsoleCommand 
					// decoderOut.write(s);
				}

			}
		}
		
		return ret;
	}
}
