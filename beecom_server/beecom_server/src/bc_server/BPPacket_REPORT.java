/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;
import java.util.Vector;

/**
 * @author Ansersion
 * 
 */
public class BPPacket_REPORT extends BPPacket {

	int PackSeq;
	int DevNameLen;
	Vector<BPPartitation> Partitation;
	byte[] DevName;

	BPPacket_REPORT() {
		super();
		PackSeq = 0;
		DevNameLen = 0;
		DevName = new byte[256];
	}

	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub
		int client_id_len = 0;

		try {
			byte encoded_byte = 0;
			client_id_len = 2;

			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = (byte) io_buf.get();
			}
			super.parseVrbClientId(id, client_id_len);

			encoded_byte = io_buf.get();
			super.parseVrbHeadFlags(encoded_byte);

			PackSeq = io_buf.getUnsignedShort();

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
			int counter = 0;
			int client_id_len = 0;
			byte encoded_byte = 0;
			client_id_len = 2;

			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, client_id_len);

			encoded_byte = buf[counter++];
			super.parseVrbHeadFlags(encoded_byte);

			byte pack_seq_msb = buf[counter++];
			byte pack_seq_lsb = buf[counter++];
			PackSeq = BPPacket.assemble2ByteBigend(pack_seq_msb, pack_seq_lsb);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			int counter = 0;
			
			DevNameLen = buf[counter++];
			for(int i = 0; i > DevNameLen; i++) {
				DevName[i] = buf[counter + DevNameLen - 1 - i];
			}
			
			do {
				byte part = buf[counter++];
				// BPPart
				
			}while(false);

			

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}
}
