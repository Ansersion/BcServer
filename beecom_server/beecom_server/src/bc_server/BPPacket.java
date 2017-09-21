package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 * 
 */
public class BPPacket implements BPPacketInterface {

	byte[] BPPacketData;
	FixedHeader FxHeader; // = new FixedHeader();
	VariableHeader VrbHeader; // = new VariableHeader();
	Payload Pld; // = new Payload();
	CrcChecksum Crc;

	public BPPacket() {
	}

	public BPPacket(FixedHeader fx_header) {
		FxHeader = fx_header;
	}
	
	public void setRemainingData(byte[] data) {
		BPPacketData = data;
	}

	/**
	 * Decrypt the packet data.
	 * 
	 * @param etEncryptType
	 *            The encryption type.
	 * @return The number of bytes of the cryptograph
	 * @throws Exception
	 *             If an error occurred while decrypting
	 */

	@Override
	public int Decrypt(EncryptType etEncryptType) throws Exception {
		return 0;
	}

	/**
	 * Parse the fixed header.
	 * 
	 * @return The number of bytes of the cryptograph
	 * @throws Exception
	 *             If an error occurred while decrypting
	 */
	@Override
	public int parseFixedHeader() throws Exception {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		return false;
	}

	public void parseVrbHeadLevel(byte level) {
		VrbHeader.parseLevel(level);
	}

	public void parseVrbHeadFlags(byte flags) {
		VrbHeader.parseFlags(flags);
	}

	public int parseVrbClientIdLen(byte len) {
		return VrbHeader.parseClientIdLen(len);
	}

	public int parseVrbClientId(byte[] id, int len) {
		return VrbHeader.parseClientId(id, len);
	}

	public int parseVrbAliveTime(byte alive_time_msb, byte alive_time_lsb) {
		return VrbHeader.parseAliveTime(alive_time_msb, alive_time_lsb);
	}

	public int parseVrbTimeout(byte timeout) {
		return VrbHeader.parseTimeout(timeout);
	}

	@Override
	public int parsePayload() throws Exception {
		return 0;
	}

	@Override
	public boolean checkCRC(CrcChecksum ccCrc) throws Exception {
		// try {
			if(CrcChecksum.CRC16 == ccCrc) {
				if(0 != CrcChecksum.calcCrc16(BPPacketData)) {
					// throw new Exception("Check CRC16 Error");
					return false;
				}
				return true;
			}
			if(CrcChecksum.CRC32 == ccCrc) {
				if(0 != CrcChecksum.calcCrc32(BPPacketData)) {
					// throw new Exception("Check CRC32 Error");
					return false;
				}
				return true;
			}
			return false;
		// }catch(Exception e) {
		// 	e.printStackTrace();
		// 	throw e;
		// }
	}

	@Override
	public void setFixedHeader(FixedHeader fx_header) throws Exception {
		// TODO Auto-generated method stub
		FxHeader = fx_header;
	}

	@Override
	public void setVariableHeader(VariableHeader vrb_header) throws Exception {
		// TODO Auto-generated method stub
		VrbHeader = vrb_header;

	}

	@Override
	public void setPayload(Payload pld) throws Exception {
		// TODO Auto-generated method stub
		Pld = pld;

	}

	@Override
	public void setCrcChecksum(CrcChecksum ccCrc) throws Exception {
		// TODO Auto-generated method stub
		Crc = ccCrc;
	}

	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
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
		return false;
	}
	
	public BPPacketType getPackTypeFxHead() {
		return FxHeader.getPacketType();
	}
	
	public boolean getUsrNameFlagVrbHead() {
		return VrbHeader.getUserNameFlag();
	}
	
	public boolean getPwdFlagVrbHead() {
		return VrbHeader.getPwdFlag();
	}
	
	public void setPldUserName(byte[] user_name) {
		Pld.setUserName(user_name);
	}
	
	public void setPldPassword(byte[] password) {
		Pld.setPassword(password);
	}
	
	public int getClientId() {
		return VrbHeader.getClientId();
	}
	
	public void getUserNamePld(byte[] user_name) {
		Pld.getUserName(user_name);
	}
	
	public byte[] getUserNamePld() {
		return Pld.getUserName();
	}
	
	public void getPasswordPld(byte[] password) {
		Pld.getPassword(password);
	}
	
	public byte[] getPasswordPld() {
		return Pld.getPassword();
	}
	
	public boolean getUsrLoginFlagVrbHead() {
		return VrbHeader.getUserLoginFlag();
	}
	
	public boolean getDevLoginFlagVrbHead() {
		return VrbHeader.getDeviceLoginFlag();
	}

}
