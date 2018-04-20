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
		
		if(arg1 instanceof String) {
			IoBuffer ioTstBuf = IoBuffer.allocate(16, false);
			byte b = 'T';
			ioTstBuf.put(b);
			b = 'S';
			ioTstBuf.put(b);
			b = 'T';
			ioTstBuf.put(b);
			ioTstBuf.flip();
			arg2.write(ioTstBuf);
			return;
		}
		
		BPPacket packToEncode = (BPPacket)arg1;
		
		packToEncode.assembleStart();
		packToEncode.assembleFixedHeader();
		packToEncode.assembleVariableHeader();
		packToEncode.assemblePayload();
		packToEncode.assembleEnd();

		IoBuffer buf = packToEncode.getIoBuffer();
		
		arg2.write(buf);

	}

}
