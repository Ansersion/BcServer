package bp_packet;

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
	public int parseVariableHeader() {

		try {
			// level(1 byte) + flags(1 byte) + client ID length(1 byte)
			byte encodedByte = 0;
			encodedByte = getIoBuffer().get();
			super.parseVrbHeadLevel(encodedByte);

			encodedByte = getIoBuffer().get();
			super.parseVrbHeadFlags(encodedByte);

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
		int ret = 0;
		try {
			VariableHeader vrb = this.getVrbHead();
			IoBuffer ioBuffer = getIoBuffer();
			Payload pld = getPld();
			if (!vrb.getUserNameFlag() || !vrb.getPwdFlag()) {
				return ret;
			}

			int userNameLen = ioBuffer.getUnsignedShort();
			byte[] userName = new byte[userNameLen];
			ioBuffer.get(userName);
			
			int passwordLen = ioBuffer.getUnsignedShort();
			byte[] password = new byte[passwordLen];
			ioBuffer.get(password);
			
			setSysSigTableVersion(ioBuffer.getUnsignedShort());
			
			pld.setUserName(new String(userName, "UTF-8"));
			pld.setPassword(new String(password));
			
			if(vrb.getDevIdFlag()) {
				int adminNameLen = ioBuffer.getUnsignedShort();
				if(adminNameLen > 0) {
					byte[] adminName = new byte[adminNameLen];
					pld.setAdminName(new String(adminName, "UTF-8"));
				} else {
					pld.setAdminName(null);
				}
			}

		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = -1;
		}
		return ret;
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
