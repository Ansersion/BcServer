package bc_server;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;
import other.Util;

import org.apache.mina.core.buffer.IoBuffer;

public class BcEncoder extends ProtocolEncoderAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 
	
	@Override
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
			throws Exception {
		
		BPPacket packToEncode = (BPPacket)arg1;
		
		if(!packToEncode.assembleStart()) {
			Util.logger(logger, Util.ERROR, "!packToEncode.assembleStart()");
			return;
		}
		if(!packToEncode.assembleFixedHeader()) {
			Util.logger(logger, Util.ERROR, "!packToEncode.assembleFixedHeader()");
			return;
		}
		if(!packToEncode.assembleVariableHeader()) {
			Util.logger(logger, Util.ERROR, "!packToEncode.assembleVariableHeader()");
			return;
		}
		if(!packToEncode.assemblePayload()) {
			Util.logger(logger, Util.ERROR, "!packToEncode.assemblePayload()");
			return;
		}
		if(!packToEncode.assembleEnd()) {
			Util.logger(logger, Util.ERROR, "!packToEncode.assembleEnd()");
			return;
		}

		IoBuffer buf = packToEncode.getIoBuffer();
		int limit = buf.limit();
		String s = "send: ";
		for(int i = 0; i < limit; i++) {
			s += String.format("%02x", buf.getUnsigned()) + " ";
		}
		logger.info(s);
		buf.rewind();
		
		arg2.write(buf);

	}

}
