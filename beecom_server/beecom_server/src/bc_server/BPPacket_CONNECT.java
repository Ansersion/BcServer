package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 * 
 */
public class BPPacket_CONNECT extends BPPacket {

	
	enum ParseVrbState {
		PARSE_STATE_1, PARSE_STATE_2;
	}
	
	ParseVrbState PrsVrbSt = ParseVrbState.PARSE_STATE_1;
	
	public BPPacket_CONNECT(FixedHeader fx_header) {
		super(fx_header);
	}

	public BPPacket_CONNECT() {
	}

	@Override
	public int Decrypt(EncryptType etEncryptType) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int parseFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub
		int client_id_len = 0;

		boolean ret = false;
		// level(1 byte) + flags(1 byte) + client ID length(1 byte)
		if ((ParseVrbState.PARSE_STATE_1 == PrsVrbSt) && io_buf.remaining() >= 1 + 1 + 1) {
			byte encoded_byte = 0;
			encoded_byte = (byte)io_buf.getChar();
			super.parseVrbHeadLevel(encoded_byte);

			encoded_byte = (byte)io_buf.getChar();
			super.parseVrbHeadFlags(encoded_byte);
			
			encoded_byte = (byte)io_buf.getChar();
			client_id_len = super.parseVrbClientIdLen(encoded_byte);
			
			PrsVrbSt = ParseVrbState.PARSE_STATE_2;
			
		}
		
		// client ID(client_id_len byte) + alive time(2 byte) + timeout(1 byte)
		if((ParseVrbState.PARSE_STATE_2 == PrsVrbSt) && (io_buf.remaining() >= client_id_len + 2 + 1)) {
			byte[] id = new byte[client_id_len];
			for(int i = 0; i < client_id_len; i++) {
				id[i] = (byte)io_buf.getChar();
			}
			super.parseVrbClientId(id, client_id_len);

			byte alive_time_msb = (byte)io_buf.getChar();
			byte alive_time_lsb = (byte)io_buf.getChar();
			super.parseVrbAliveTime(alive_time_msb, alive_time_lsb);
			
			byte timeout = (byte)io_buf.getChar();
			super.parseVrbTimeout(timeout);
			
			PrsVrbSt = ParseVrbState.PARSE_STATE_1;
			
			ret = true;
		}

		return ret;
	}

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void checkCRC(CrcChecksum ctCrc) throws Exception {
		// TODO Auto-generated method stub
	}

}
