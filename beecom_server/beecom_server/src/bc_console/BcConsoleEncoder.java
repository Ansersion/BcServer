package bc_console;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;


import org.apache.mina.core.buffer.IoBuffer;

public class BcConsoleEncoder extends ProtocolEncoderAdapter {
	@Override
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
			throws Exception {
		IoBuffer io = IoBuffer.allocate(256, false);
		
		io.put(arg1.toString().getBytes());
		io.limit();
		io.rewind();
		arg2.write(io);
	}

}
