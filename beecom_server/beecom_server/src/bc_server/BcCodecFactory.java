package bc_server;

/**
 * @author Ansersion
 *
 */


import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class BcCodecFactory implements ProtocolCodecFactory {

	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
