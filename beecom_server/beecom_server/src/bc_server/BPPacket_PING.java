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
		int counter = 0;
		int client_id_len = 0;

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encoded_byte = 0;
			// encoded_byte = getIoBuffer().get();
			// super.parseVrbHeadLevel(encoded_byte);

			encoded_byte = getIoBuffer().get();
			super.parseVrbHeadFlags(encoded_byte);

			// encoded_byte = getIoBuffer().get();
			// client_id_len = super.parseVrbClientIdLen(encoded_byte);

			// client ID(client_id_len byte) + alive time(2 byte) + timeout(1
			// byte)
			// byte[] id = new byte[client_id_len];
			// for (int i = 0; i < client_id_len; i++) {
			//	id[i] = getIoBuffer().get();
			// }
			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);
			// super.parseVrbClientId(id, client_id_len);

			int pack_seq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(pack_seq);

			int time_out = getIoBuffer().get();
			getVrbHead().setTimeout(time_out);
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
