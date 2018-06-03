package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;

import other.CrcChecksum;

/**
 * @author Ansersion
 * 
 */
public class BPPacket implements BPPacketInterface {

	public static final int BP_SIGNAL_RESERVED = 0x0000;
	public static final int BP_LEVEL = 0;
	
	public static final int MAX_SYS_SIG_DIST_NUM = 16;
	public static final int SYS_SIG_START_ID = 0xE000;
	public static final int CUS_SIG_START_ID = 0x0001;
	public static final int CUS_SIG_END_ID = 0xDFFF;
	public static final int SYS_SIG_DIST_STEP = 0x200;
	public static final int MAX_SIG_ID = 0xFFFF;
	

	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string, 7-boolean */
	public static final int VAL_TYPE_UINT32 = 0;
	public static final int VAL_TYPE_UINT16 = 1;
	public static final int VAL_TYPE_IINT32 = 2;
	public static final int VAL_TYPE_IINT16 = 3;
	public static final int VAL_TYPE_ENUM = 4;
	public static final int VAL_TYPE_FLOAT = 5;
	public static final int VAL_TYPE_STRING = 6;
	public static final int VAL_TYPE_BOOLEAN = 7;
	public static final int VAL_TYPE_INVALID = 0x7F;
	
	IoBuffer bpPacketData;
	FixedHeader fxHeader;
	VariableHeader vrbHeader;
	Payload pld;
	CrcChecksum crc;
	boolean clntIdExpired;

	protected BPPacket() {
		bpPacketData = IoBuffer.allocate(256, false);
		bpPacketData.setAutoExpand(true);
		fxHeader = new FixedHeader();
		vrbHeader = new VariableHeader();
		pld = new Payload();
		bpPacketData.clear();
		clntIdExpired = false;
	}

	protected BPPacket(FixedHeader fxHeader) {
		/** bpPacketData.setAutoExpand(true); */
		bpPacketData = IoBuffer.allocate(256, false);
		bpPacketData.setAutoExpand(true);
		vrbHeader = new VariableHeader();
		pld = new Payload();
		bpPacketData.clear();
		this.fxHeader = fxHeader;
		clntIdExpired = false;
	}
	
	protected void setRemainingData(byte[] data) {
		bpPacketData.clear();
		bpPacketData.put(data);
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
	public int decrypt(EncryptType etEncryptType) throws BPException {
		return 0;
	}
	
	public void putFxHead2Buf() {
		bpPacketData.clear();
		bpPacketData.put(fxHeader.getFirstByte());
		int len = fxHeader.getRemainingLen();
		byte encodedByte;
		do {
			encodedByte = (byte)(len % 128);
			len = len / 128;
			if(len > 0) {
				encodedByte |= 128;
			}
			bpPacketData.put(encodedByte);
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
	public int parseFixedHeader() throws BPParseFxHeaderException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) throws BPParseVrbHeaderException {
		return false;
	}

	public void parseVrbHeadLevel(byte level) {
		vrbHeader.parseLevel(level);
	}

	public void parseVrbHeadFlags(byte flags) {
		vrbHeader.parseFlags(flags);
	}

	public int parseVrbClientIdLen(byte len) {
		return vrbHeader.parseClientIdLen(len);
	}

	public int parseVrbClientId(byte[] id, int len) {
		return vrbHeader.parseClientId(id, len);
	}

	public int parseVrbAliveTime(byte aliveTimeMsb, byte aliveTimeLsb) {
		return vrbHeader.parseAliveTime(aliveTimeMsb, aliveTimeLsb);
	}

	public int parseVrbTimeout(byte timeout) {
		return vrbHeader.parseTimeout(timeout);
	}

	@Override
	public int parsePayload() throws BPParsePldException {
		return 0;
	}

	@Override
	public boolean checkCRC(CrcChecksum ccCrc) throws BPCrcException {
		byte[] data = new byte[bpPacketData.remaining()];
		bpPacketData.get(data);
			if(CrcChecksum.CRC16 == ccCrc) {
				return 0 == CrcChecksum.calcCrc16(data);
			}
			if(CrcChecksum.CRC32 == ccCrc) {
				return 0 == CrcChecksum.calcCrc32(data);
			}
			return false;
	}

	@Override
	public void setFixedHeader(FixedHeader fxHeader) {
		this.fxHeader = fxHeader;
	}

	@Override
	public void setVariableHeader(VariableHeader vrbHeader) {
		this.vrbHeader = vrbHeader;

	}

	@Override
	public void setPayload(Payload pld) {
		this.pld= pld;

	}

	@Override
	public void setCrcChecksum(CrcChecksum ccCrc) {
		crc = ccCrc;
	}

	@Override
	public int parseVariableHeader() throws BPParseVrbHeaderException {
		return 0;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws BPParseVrbHeaderException {
		return false;
	}

	@Override
	public boolean parsePayload(IoBuffer ioBuf) throws BPParsePldException {
		return false;
	}

	@Override
	public boolean parsePayload(byte[] buf) throws BPParsePldException {
		return false;
	}
	
	public IoBuffer getIoBuffer() {
		return bpPacketData;
	}
	
	public BPPacketType getPackTypeFxHead() {
		return fxHeader.getPacketType();
	}
	
	public int getPackTypeIntFxHead() {
		return fxHeader.getPacketType().getType();
	}
	
	public byte getPackFlagsByteFxHead() {
		return fxHeader.getFlags();
	}
	
	public boolean getUsrNameFlagVrbHead() {
		return vrbHeader.getUserNameFlag();
	}
	
	public boolean getPwdFlagVrbHead() {
		return vrbHeader.getPwdFlag();
	}
	
	public void setPldUserName(String userName) {
		pld.setUserName(userName);
	}
	
	public void setPldPassword(String password) {
		pld.setPassword(password);
	}
	
	public void setNewClntIdFlg() {
		vrbHeader.setNewCliIdFlg();
	}
	
	public void setClntIdExpired(boolean b) {
		clntIdExpired = b;
	}
	
	public boolean isClntIdExpired() {
		return clntIdExpired;
	}
	
	public int getClientId() {
		return vrbHeader.getClientId();
	}
	
	public String getUserNamePld() {
		return pld.getUserName();
	}
	
	public String getPasswordPld() {
		return pld.getPassword();
	}
	
	
	public boolean getUsrClntFlagVrbHead() {
		return vrbHeader.getUserClntFlag();
	}
	
	public boolean getDevClntFlagVrbHead() {
		return vrbHeader.getDevClntFlag();
	}
	
	public boolean getUsrClntFlag() {
		return vrbHeader.getUserClntFlag();
	}
	
	public boolean getDevClntFlag() {
		return vrbHeader.getDevClntFlag();
	}
	
	public boolean getChineseFlag() {
		return vrbHeader.getChineseFlag();
	}
	
	public boolean getEnglishFlag() {
		return vrbHeader.getEnglishFlag();
	}
	
	public int getPackSeq() {
		return vrbHeader.getPackSeq();
	}

	@Override
	public boolean assembleFixedHeader() throws BPAssembleFxHeaderException {
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		return false;
	}

	@Override
	public boolean assemblePayload() throws BPAssemblePldException {
		return false;
	}
	
	@Override
	public byte[] getPackByByte() throws BPException {
		return new byte[bpPacketData.remaining()];
	}

	@Override
	public boolean assembleStart() throws BPAssembleException {
		bpPacketData.clear();
		return false;
	}

	@Override
	public boolean assembleEnd() throws BPAssembleException {
		int crcLen = (getFxHead().getCrcChk() == CrcChecksum.CRC32 ? 4 : 2);
		int packRmnLen = bpPacketData.position() - 2 + crcLen;
		
		if(packRmnLen > 256) {
			int dataLenOld = bpPacketData.minimumCapacity();
			byte[] byteBuf = new byte[dataLenOld - 1 - 1];
			bpPacketData.flip();
			byte encodedByte = bpPacketData.get();
			// skip 1 byte of temporary "RemainingLength"
			bpPacketData.get();
			bpPacketData.get(byteBuf);
			
			bpPacketData.clear();
			
			bpPacketData.put(encodedByte);
			
			int dataLenNew = dataLenOld + 1;
			do {
				encodedByte = (byte)(dataLenNew % 128);
				
				dataLenNew = dataLenNew / 128;
				// if there are more data to encode, set the top bit of this byte
				if ( dataLenNew > 0 ) {
					encodedByte = (byte)(encodedByte | 0x80);
				}
				bpPacketData.put(encodedByte);
			} while ( dataLenNew > 0 );

		} else {
			int posCrc = bpPacketData.position();
			bpPacketData.rewind();
			bpPacketData.get();
			bpPacketData.put((byte)packRmnLen);
			bpPacketData.position(posCrc);
			byte[] buf = bpPacketData.array();
			long crcTmp = CrcChecksum.calcCrc32(buf, 0, posCrc);
			bpPacketData.putUnsignedInt(crcTmp);
			bpPacketData.flip();
			
		}
		return false;
	}
	
	public FixedHeader getFxHead() {
		return fxHeader;
	}
	
	public VariableHeader getVrbHead() {
		return vrbHeader;
	}
	
	public Payload getPld() {
		return pld;
	}
	
	public static short assemble2ByteBigend(byte msb, byte lsb) {
		int ret = 0;
		ret = msb & 0xFF;
		ret = (ret << 8) | (lsb & 0xFF);
		return (short)ret;
	}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	
	public static short assemble2ByteBigend(byte[] data, int offset) {
		int ret = 0;
		ret = data[offset++] & 0xFF;
		ret = (ret << 8) | (data[offset] & 0xFF);
		
		return (short)ret;
	}
	

}
