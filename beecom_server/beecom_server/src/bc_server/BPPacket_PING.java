/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */
public class BPPacket_PING extends BPPacket {
	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
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
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		try {

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);
			
			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int pack_seq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(pack_seq);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	
	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {
			// No payload for PING
		} catch (Exception e) {
			System.out.println("Error(PING): parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

}
