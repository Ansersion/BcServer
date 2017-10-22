/**
 * 
 */
package bc_server;


import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */

class dataDevSigGET {
	int DevId;
	boolean CustomSig;
	
}

public class BPPacket_GET extends BPPacket {
	
	int PackSeq;
	DeviceSignals DevSigData = null; 
	int DeviceNum;
	
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
			for(int i = 0; i < client_id_len; i++) {
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

	/*
	 * @Override public boolean parseVariableHeader() throws Exception { // TODO
	 * Auto-generated method stub // return parseVariableHeader(); return false;
	 * }
	 */

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {
			

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			int counter = 0;
			
			DeviceNum = buf[counter++];
			DevSigData = new DeviceSignals(DeviceNum);
			
			counter += DevSigData.parseSigMap(buf, counter);

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}
}
