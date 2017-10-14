package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 * 
 */
public class BPPacket implements BPPacketInterface {

	IoBuffer BPPacketData;
	FixedHeader FxHeader; // = new FixedHeader();
	VariableHeader VrbHeader; // = new VariableHeader();
	Payload Pld; // = new Payload();
	CrcChecksum Crc;

	protected BPPacket() {
		// BPPacketData.setAutoExpand(true);
		BPPacketData = IoBuffer.allocate(256, false);
		BPPacketData.setAutoExpand(true);
		FxHeader = new FixedHeader();
		VrbHeader = new VariableHeader();
		Pld = new Payload();
		BPPacketData.clear();
	}

	protected BPPacket(FixedHeader fx_header) {
		// BPPacketData.setAutoExpand(true);
		BPPacketData = IoBuffer.allocate(256, false);
		BPPacketData.setAutoExpand(true);
		VrbHeader = new VariableHeader();
		Pld = new Payload();
		BPPacketData.clear();
		FxHeader = fx_header;
	}
	
	protected void setRemainingData(byte[] data) {
		BPPacketData.clear();
		BPPacketData.put(data);
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
	
	public void putFxHead2Buf() {
		BPPacketData.clear();
		BPPacketData.put(FxHeader.getFirstByte());
		int len = FxHeader.getRemainingLen();
		byte encoded_byte;
		do {
			encoded_byte = (byte)(len % 128);
			len = len / 128;
			if(len > 0) {
				encoded_byte |= 128;
			}
			BPPacketData.put(encoded_byte);
		} while (len > 0);
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
		byte[] data = new byte[BPPacketData.remaining()];
		BPPacketData.get(data);
			if(CrcChecksum.CRC16 == ccCrc) {
				if(0 != CrcChecksum.calcCrc16(data)) {
					// throw new Exception("Check CRC16 Error");
					return false;
				}
				return true;
			}
			if(CrcChecksum.CRC32 == ccCrc) {
				if(0 != CrcChecksum.calcCrc32(data)) {
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
	
	public IoBuffer getIoBuffer() {
		return BPPacketData;
	}
	
	public BPPacketType getPackTypeFxHead() {
		return FxHeader.getPacketType();
	}
	
	public int getPackTypeIntFxHead() {
		return FxHeader.getPacketType().getType();
	}
	
	public byte getPackFlagsByteFxHead() {
		return FxHeader.getFlags();
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
	
	public boolean getChineseFlag() {
		return VrbHeader.getChineseFlag();
	}
	
	public boolean getEnglishFlag() {
		return VrbHeader.getEnglishFlag();
	}
	
	public int getPackSeq() {
		return VrbHeader.getPackSeq();
	}

	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	@Override
	public boolean assembleCrc() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	*/
	
	@Override
	public byte[] getPackByByte() throws Exception {
		// TODO Auto-generated method stub
		byte[] data = new byte[BPPacketData.remaining()];
		return data;
	}

	@Override
	public boolean assembleStart() throws Exception {
		// TODO Auto-generated method stub
		// BPPacketData = IoBuffer.allocate(256, false);
		BPPacketData.clear();
		return false;
	}

	@Override
	public boolean assembleEnd() throws Exception {
		// TODO Auto-generated method stub
		int CrcLen = (Crc == CrcChecksum.CRC32 ? 4 : 2);
		int pack_data_len = BPPacketData.remaining() - 2;
		
		if(pack_data_len + CrcLen > 256) {
			int data_len_old = BPPacketData.minimumCapacity();
			byte byte_buf[] = new byte[data_len_old - 1 - 1];
			BPPacketData.flip();
			byte encoded_byte = BPPacketData.get();
			// skip 1 byte of temporary "RemainingLength"
			BPPacketData.get();
			BPPacketData.get(byte_buf);
			
			BPPacketData.clear();
			
			BPPacketData.put(encoded_byte);
			
			int data_len_new = data_len_old + 1;
			do {
				encoded_byte = (byte)(data_len_new % 128);
				
				data_len_new = data_len_new / 128;
				// if there are more data to encode, set the top bit of this byte
				if ( data_len_new > 0 ) {
					encoded_byte = (byte)(encoded_byte | 0x80);
				}
				BPPacketData.put(encoded_byte);
			} while ( data_len_new > 0 );

		} else {
			int limit_tmp = BPPacketData.limit();
			BPPacketData.rewind();
			BPPacketData.get();
			BPPacketData.put((byte)pack_data_len);
			BPPacketData.flip();
			BPPacketData.limit(limit_tmp);
			
		}
		return false;
	}
	
	public FixedHeader getFxHead() {
		return FxHeader;
	}
	
	public VariableHeader getVrbHeader() {
		return VrbHeader;
	}
	
	public Payload getPld() {
		return Pld;
	}
	
	public static short assemble2ByteBigend(byte msb, byte lsb) {
		int ret = 0;
		ret = msb;
		ret = (ret << 8) | lsb;
		return (short)ret;
	}
	
	/*
	public static short assemble2ByteBigend(byte[] data, Integer offset) {
		int ret = 0;
		
		ret = data[offset.intValue()];
		offset++;
		ret = (ret << 8) | data[offset.intValue()];
		offset++;
		
		return (short)ret;
	}
	*/                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
	
	public static short assemble2ByteBigend(byte[] data, int offset) {
		int ret = 0;
		ret = data[offset++];
		ret = (ret << 8) | data[offset++];
		
		return (short)ret;
	}
	

}
