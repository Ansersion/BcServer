package bc_server;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import bp_packet.BPPacket;

import org.apache.mina.core.buffer.IoBuffer;

public class BcEncoder extends ProtocolEncoderAdapter {

	
	@Override
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
			throws Exception {
		
		BPPacket packToEncode = (BPPacket)arg1;
		
		packToEncode.assembleStart();
		packToEncode.assembleFixedHeader();
		packToEncode.assembleVariableHeader();
		packToEncode.assemblePayload();
		packToEncode.assembleEnd();

		IoBuffer buf = packToEncode.getIoBuffer();
		int limit = buf.limit();
		int position = buf.position();
		String s = "send: ";
		for(int i = 0; i < limit; i++) {
			s += String.format("%02x", buf.getUnsigned()) + " ";
		}
		System.out.println(s);
		buf.rewind();
		
		arg2.write(buf);

	}

}
