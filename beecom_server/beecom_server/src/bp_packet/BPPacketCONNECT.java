package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;
import other.Util;

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
	private int sysSigTableVersion;

	protected BPPacketCONNECT(FixedHeader fxHeader) {
		super(fxHeader);
	}

	protected BPPacketCONNECT() {
	}

	@Override
	public int decrypt(EncryptType etEncryptType) throws BPException {
		return 0;
	}

	@Override
	public int parseFixedHeader() {
		return 0;
	}

	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {
		int clientIdLen = 0;

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encodedByte = 0;
			encodedByte = ioBuf.get();
			super.parseVrbHeadLevel(encodedByte);

			encodedByte = ioBuf.get();
			super.parseVrbHeadFlags(encodedByte);

			clientIdLen = 2;

			// client ID(clientIdLen byte) + alive time(2 byte) + timeout(1
			// byte)
			byte[] id = new byte[clientIdLen];
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = ioBuf.get();
			}
			
			super.parseVrbClientId(id, clientIdLen);

			byte aliveTimeMsb = ioBuf.get();
			byte aliveTimeLsb = ioBuf.get();
			super.parseVrbAliveTime(aliveTimeMsb, aliveTimeLsb);

			byte timeout = ioBuf.get();
			super.parseVrbTimeout(timeout);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) {
		int counter = 0;
		int clientIdLen = 0;

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encodedByte = 0;
			encodedByte = buf[counter++];
			super.parseVrbHeadLevel(encodedByte);

			encodedByte = buf[counter++];
			super.parseVrbHeadFlags(encodedByte);

			encodedByte = buf[counter++];
			clientIdLen = super.parseVrbClientIdLen(encodedByte);

			// client ID(clientIdLen byte) + alive time(2 byte) + timeout(1
			// byte)
			byte[] id = new byte[clientIdLen];
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, clientIdLen);

			byte aliveTimeMsb = buf[counter++];
			byte aliveTimeLsb = buf[counter++];
			super.parseVrbAliveTime(aliveTimeMsb, aliveTimeLsb);

			byte timeout = buf[counter];
			super.parseVrbTimeout(timeout);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return true;
	}
	
	@Override
	public int parseVariableHeader() {

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encodedByte = 0;
			encodedByte = getIoBuffer().get();
			super.parseVrbHeadLevel(encodedByte);

			encodedByte = getIoBuffer().get();
			super.parseVrbHeadFlags(encodedByte);

			// int clientId = getIoBuffer().getUnsignedShort();
			// getVrbHead().setClientId(clientId);

			int aliveTime = getIoBuffer().getUnsignedShort();
			getVrbHead().setAliveTime(aliveTime);

			short timeOut = getIoBuffer().get();
			getVrbHead().setTimeout(timeOut);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
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
			
			setSysSigTableVersion(getIoBuffer().getUnsignedShort());
			
			setPldUserName(new String(userName));
			setPldPassword(new String(password));

		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}
		return 0;
	}

	@Override
	public boolean checkCRC(CrcChecksum ctCrc) throws BPCrcException {
		return false;
	}

	@Override
	public boolean parsePayload(IoBuffer ioBuf) {
		return false;
	}

	@Override
	public boolean parsePayload(byte[] buf) {
		try {

			if (!getUsrNameFlagVrbHead() || !getPwdFlagVrbHead()) {
				return false;
			}
			
			int counter = 0;
			int userNameLen = buf[counter++];
			userNameLen = (userNameLen << 8) + (buf[counter++] & 0xFF);
			byte[] userName = new byte[userNameLen];
			for (int i = 0; i < userNameLen; i++) {
				userName[i] = buf[counter++];
			}

			int passwordLen = buf[counter++];
			passwordLen = (passwordLen << 8) + (buf[counter++] & 0xFF);
			byte[] password = new byte[passwordLen];
			for (int i = 0; i < passwordLen; i++) {
				password[i] = buf[counter++];
			}
			
			// setPldUserName(userName);
			// setPldPassword(password);

		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return true;
	}

	public int getSysSigTableVersion() {
		return sysSigTableVersion;
	}

	public void setSysSigTableVersion(int sysSigTableVersion) {
		this.sysSigTableVersion = sysSigTableVersion;
	}


}
