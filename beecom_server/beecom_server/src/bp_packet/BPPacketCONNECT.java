package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;

/**
 * @author Ansersion
 * 
 */
public class BPPacketCONNECT extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketCONNECT.class); 

	enum ParseVrbState {
		PARSE_STATE_1, PARSE_STATE_2;
	}

	ParseVrbState prsVrbSt = ParseVrbState.PARSE_STATE_1;

	protected BPPacketCONNECT(FixedHeader fx_header) {
		super(fx_header);
	}

	protected BPPacketCONNECT() {
	}

	@Override
	public int decrypt(EncryptType etEncryptType) throws Exception {
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

			client_id_len = 2;

			// client ID(client_id_len byte) + alive time(2 byte) + timeout(1
			// byte)
			byte[] id = new byte[client_id_len];
			for (int i = 0; i < client_id_len; i++) {
				id[i] = io_buf.get();
			}
			
			super.parseVrbClientId(id, client_id_len);

			byte aliveTimeMsb = io_buf.get();
			byte aliveTimeLsb = io_buf.get();
			super.parseVrbAliveTime(aliveTimeMsb, aliveTimeLsb);

			byte timeout = io_buf.get();
			super.parseVrbTimeout(timeout);
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encodedByte = 0;
			encodedByte = getIoBuffer().get();
			super.parseVrbHeadLevel(encodedByte);

			encodedByte = getIoBuffer().get();
			super.parseVrbHeadFlags(encodedByte);

			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);

			int aliveTime = getIoBuffer().getUnsignedShort();
			getVrbHead().setAliveTime(aliveTime);

			int timeOut = getIoBuffer().get();
			getVrbHead().setTimeout(timeOut);
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() throws Exception {
		try {
			if (!getUsrNameFlagVrbHead() || !getPwdFlagVrbHead()) {
				return 0;
			}
			

			int userNameLen = getIoBuffer().getUnsignedShort();
			byte[] userName = new byte[userNameLen];
			getIoBuffer().get(userName);
			
			int passwordLen = getIoBuffer().getUnsignedShort();
			byte[] password = new byte[passwordLen];
			getIoBuffer().get(password);
			
			setPldUserName(userName);
			setPldPassword(password);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
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
			user_name_len = (user_name_len << 8) + (buf[counter++] & 0xFF);
			byte[] user_name = new byte[user_name_len];
			for (int i = 0; i < user_name_len; i++) {
				user_name[i] = buf[counter++];
			}

			int passwordLen = buf[counter++];
			passwordLen = (passwordLen << 8) + (buf[counter++] & 0xFF);
			byte[] password = new byte[passwordLen];
			for (int i = 0; i < passwordLen; i++) {
				password[i] = buf[counter++];
			}
			
			setPldUserName(user_name);
			setPldPassword(password);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
	}


}
