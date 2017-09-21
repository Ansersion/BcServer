package bc_server;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.core.buffer.IoBuffer;

public class BcEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
			throws Exception {
		// TODO Auto-generated method stub
		
		BPPacket pack_to_encode = (BPPacket)arg1;
		
		pack_to_encode.assembleStart();
		pack_to_encode.assembleFixedHeader();
		pack_to_encode.assembleVariableHeader();
		pack_to_encode.assemblePayload();
		pack_to_encode.assembleEnd();
		// pack_to_encode.assembleCrc();
		
		// byte[] pack_buf = pack_to_encode.getPackByByte();
		
		// IoBuffer buf = IoBuffer.allocate(pack_buf.length, false);
		IoBuffer buf = pack_to_encode.getIoBuffer();
		
		arg2.write(buf);

	}

}
