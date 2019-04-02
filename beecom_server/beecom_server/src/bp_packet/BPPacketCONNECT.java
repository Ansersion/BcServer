package bp_packet;

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

	public int getSysSigTableVersion() {
		return sysSigTableVersion;
	}

	public void setSysSigTableVersion(int sysSigTableVersion) {
		this.sysSigTableVersion = sysSigTableVersion;
	}


}
