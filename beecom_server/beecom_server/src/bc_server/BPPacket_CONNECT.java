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

	protected BPPacket_CONNECT(FixedHeader fx_header) {
		super(fx_header);
	}

	protected BPPacket_CONNECT() {
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

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encoded_byte = 0;
			encoded_byte = io_buf.get();
			super.parseVrbHeadLevel(encoded_byte);

			encoded_byte = io_buf.get();
			super.parseVrbHeadFlags(encoded_byte);

			// encoded_byte = io_buf.get();
			// client_id_len = super.parseVrbClientIdLen(encoded_byte);
			client_id_len = 2;

			// client ID(client_id_len byte) + alive time(2 byte) + timeout(1
			// byte)
			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = (byte) io_buf.get();
			}
			
			super.parseVrbClientId(id, client_id_len);

			byte alive_time_msb = io_buf.get();
			byte alive_time_lsb = io_buf.get();
			super.parseVrbAliveTime(alive_time_msb, alive_time_lsb);

			byte timeout = io_buf.get();
			super.parseVrbTimeout(timeout);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		int counter = 0;
		int client_id_len = 0;

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encoded_byte = 0;
			encoded_byte = buf[counter++];
			super.parseVrbHeadLevel(encoded_byte);

			encoded_byte = buf[counter++];
			super.parseVrbHeadFlags(encoded_byte);

			encoded_byte = buf[counter++];
			client_id_len = super.parseVrbClientIdLen(encoded_byte);

			// client ID(client_id_len byte) + alive time(2 byte) + timeout(1
			// byte)
			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, client_id_len);

			byte alive_time_msb = buf[counter++];
			byte alive_time_lsb = buf[counter++];
			super.parseVrbAliveTime(alive_time_msb, alive_time_lsb);

			byte timeout = buf[counter++];
			super.parseVrbTimeout(timeout);
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
			encoded_byte = getIoBuffer().get();
			super.parseVrbHeadLevel(encoded_byte);

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

			int alive_time = getIoBuffer().getUnsignedShort();
			getVrbHead().setAliveTime(alive_time);

			int time_out = getIoBuffer().get();
			getVrbHead().setTimeout(time_out);
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

			if (!getUsrNameFlagVrbHead() || !getPwdFlagVrbHead()) {
				return 0;
			}
			
			// int user_name_len = getIoBuffer().get();
			// user_name_len = (user_name_len << 8) + getIoBuffer().get();
			int user_name_len = getIoBuffer().getUnsignedShort();
			byte[] user_name = new byte[user_name_len];
			getIoBuffer().get(user_name);
			/*for (int i = 0; i < user_name_len; i++) {
				user_name[i] = getIoBuffer().get();
			}*/

			// int password_len = getIoBuffer().get();
			// password_len = (password_len << 8) + getIoBuffer().get();
			
			int password_len = getIoBuffer().getUnsignedShort();
			byte[] password = new byte[password_len];
			getIoBuffer().get(password);
			// for (int i = 0; i < password_len; i++) {
			//	password[i] = getIoBuffer().get();
			// }
			
			setPldUserName(user_name);
			setPldPassword(password);

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	@Override
	public boolean checkCRC(CrcChecksum ctCrc) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean parsePayload(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {

			if (!getUsrNameFlagVrbHead() || !getPwdFlagVrbHead()) {
				return false;
			}
			
			int counter = 0;
			int user_name_len = buf[counter++];
			user_name_len = (user_name_len << 8) + buf[counter++];
			byte[] user_name = new byte[user_name_len];
			for (int i = 0; i < user_name_len; i++) {
				user_name[i] = buf[counter++];
			}

			int password_len = buf[counter++];
			password_len = (password_len << 8) + buf[counter++];
			byte[] password = new byte[password_len];
			for (int i = 0; i < password_len; i++) {
				password[i] = buf[counter++];
			}
			
			setPldUserName(user_name);
			setPldPassword(password);

		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}


}
