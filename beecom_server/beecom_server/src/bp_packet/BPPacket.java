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
    public static final int MAX_SYS_SIG_CLASS_NUM = 7;
	public static final int BYTE_NUM_OF_A_DIST = 64;
	public static final int SYS_SIG_START_ID = 0xE000;
	public static final int CUS_SIG_START_ID = 0x0000;
	public static final int CUS_SIG_END_ID = 0xDFFF;
	public static final int SYS_SIG_DIST_STEP = 0x200;
	public static final int MAX_SIG_ID = 0xFFFF;
    public static final int FIXED_HEADER_SIZE = 3;
    
    /* 0-ro, 1-rw */
    public static final int SIGNAL_PERMISSION_CODE_RO = 0; 
    public static final int SIGNAL_PERMISSION_CODE_RW = 1; 

	/* 0-u32, 1-u16, 2-i32, 3-i16, 4-enum, 5-float, 6-string, 7-boolean */
    public static final int MAX_VAL_TYPE_NUM = 8;
	public static final int VAL_TYPE_UINT32 = 0;
	public static final int VAL_TYPE_UINT16 = 1;
	public static final int VAL_TYPE_IINT32 = 2;
	public static final int VAL_TYPE_IINT16 = 3;
	public static final int VAL_TYPE_ENUM = 4;
	public static final int VAL_TYPE_FLOAT = 5;
	public static final int VAL_TYPE_STRING = 6;
	public static final int VAL_TYPE_BOOLEAN = 7;
	public static final int VAL_TYPE_MASK = 0x0F;
	public static final int VAL_ALARM_FLAG = 0x80;
	public static final int VAL_TYPE_INVALID = 0x7F;
	
	/* unlimit value */
	public static final long VAL_U32_UNLIMIT =0xFFFFFFFF;
	public static final int VAL_U16_UNLIMIT =0xFFFF;
	public static final int VAL_I32_UNLIMIT =0x7FFFFFFF;
	public static final short VAL_I16_UNLIMIT =0x7FFF;
	public static final int VAL_ENUM_UNLIMIT =0xFFFFFFFF;
	public static final float VAL_FLOAT_UNLIMIT =0x7FFFFFFF;
	public static final String VAL_STR_UNLIMIT ="";
	public static final boolean VAL_BOOLEAN_UNLIMIT =false;
	public static final int INVALID_LANGUAGE_ID = 0;
	public static final int MAX_CUSTOM_SIGNAL_NAME_LENGTH = 255;
	public static final int MAX_CUSTOM_SIGNAL_UNIT_LENGTH = 127;
	public static final int MAX_CUSTOM_SIGNAL_GROUP_LENGTH = 127;
	public static final int MAX_CUSTOM_SIGNAL_STRING_LENGTH = 255;
	public static final int SYSTEM_UNIT_LANGUAGE_FLAG = 0x80;
	
	public static final long INVALID_SIGNAL_MAP_CHECKSUM = 0x7FFFFFFFFFFFFFFFL;
	
	/* alarm info */
	public static final int ALARM_CLASS_EMERGANCY = 0x00;
	public static final int ALARM_CLASS_SERIOUS = 0x01;
	public static final int ALARM_CLASS_WARNING = 0x02;
	public static final int ALARM_CLASS_ATTENTION = 0x03;
	public static final short ALARM_CLASS_NOTE = 0x04;
	public static final short ALARM_CLASS_NONE = 0xFF;
	public static final short ALARM_DELAY_DEFAULT = 0x05;
	
	/* system signal custom flgs */
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_STATISTICS = 0x0001;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ENUM_LANG = 0x0002;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_GROUP_LANG = 0x0004;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ACCURACY = 0x0008;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MIN = 0x0010;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_MAX = 0x0020;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_VALUE_DEF = 0x0040;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM = 0x0080;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_CLASS = 0x0100;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_BEF = 0x0200;
	public static final int SYSTEM_SIGNAL_CUSTOM_FLAGS_ALARM_DELAY_AFT = 0x0400;
	
	/* user auth to device */
	public static final short USER_AUTH_NONE= 0x0000;
	public static final short USER_AUTH_ALL = 0x7FFF;
	public static final short USER_AUTH_READ = 0x0001;
	public static final short USER_AUTH_WRITE = 0x0002;
	public static final short USER_AUTH_MANAGE = 0x0004;
	
	public static boolean inDist(int distIndex, int signalId) {
		return (signalId >= SYS_SIG_START_ID + distIndex * SYS_SIG_DIST_STEP) && (signalId < SYS_SIG_START_ID + (distIndex + 1) * SYS_SIG_DIST_STEP);
	}
	
	public static int whichClass(int distIndex, int signalId) {
		int signalIdDistOffset = signalId - distIndex * SYS_SIG_DIST_STEP - SYS_SIG_START_ID;
		if(signalIdDistOffset < 0) {
			return 0;
		}
		for(int i = 0; i < MAX_SYS_SIG_CLASS_NUM; i++) {
			if(signalIdDistOffset <= (1 << i) * 8) {
				return i + 1;
			}
		}
		return 0;
	}
	
	/* NOTE:
	 * all parameter is thought formal!!! 
	 * bitBytes.length == 64 */
	public static void setSystemSignalBit(int distInfo, int signalId, byte[] bitBytes) {
		int signalIdDistOffset = signalId - SYS_SIG_START_ID - SYS_SIG_DIST_STEP * distInfo;
		bitBytes[signalIdDistOffset/8] |= (1 << (signalIdDistOffset%8));
	}
	
	
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
		// int len = fxHeader.getRemainingLen();
		bpPacketData.putUnsignedShort(fxHeader.getRemainingLen());
		/*
		byte encodedByte;
		do {
			encodedByte = (byte)(len % 128);
			len = len / 128;
			if(len > 0) {
				encodedByte |= 128;
			}
			bpPacketData.put(encodedByte);
		} while (len > 0);
		*/
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
        int pack_type = getPackTypeIntFxHead();
        byte pack_flags = getPackFlagsByteFxHead();
        byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));

        getIoBuffer().put(encoded_byte);
        getIoBuffer().putUnsignedShort(0);

        return true;

	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		// getVrbHead().initPackSeq();
		return false;
	}

	@Override
	public boolean assemblePayload() throws BPAssemblePldException {
		return true;
	}
	
	@Override
	public byte[] getPackByByte() throws BPException {
		return new byte[bpPacketData.remaining()];
	}

	@Override
	public boolean assembleStart() throws BPAssembleException {
		bpPacketData.clear();
		return true;
	}

	@Override
	public boolean assembleEnd() throws BPAssembleException {
		int crcLen = (getFxHead().getCrcChk() == CrcChecksum.CRC32 ? 4 : 2);
		int packRmnLen = bpPacketData.position() - BPPacket.FIXED_HEADER_SIZE + crcLen;

		int posCrc = bpPacketData.position();
		bpPacketData.rewind();
		bpPacketData.get();
		bpPacketData.putUnsignedShort(packRmnLen);
		bpPacketData.position(posCrc);
		byte[] buf = bpPacketData.array();
		long crcTmp = CrcChecksum.calcCrc32(buf, 0, posCrc);
		bpPacketData.putUnsignedInt(crcTmp);
		bpPacketData.flip();

		return true;
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
	
	public static int putValue2Buffer(int valType, Object value, IoBuffer buffer) {
		int ret = 0;
		try {
			switch (valType) {
			case VAL_TYPE_UINT32: {
				ret = 4;
				Long v = (Long) value;
				buffer.putUnsignedInt(v);
				break;
			}
			case VAL_TYPE_UINT16: {
				ret = 2;
				Integer v = (Integer) value;
				buffer.putUnsignedShort(v);
				break;
			}
			case VAL_TYPE_IINT32: {
				ret = 4;
				Integer v = (Integer) value;
				buffer.putInt(v);
				break;
			}
			case VAL_TYPE_IINT16: {
				ret = 2;
				Short v = (Short) value;
				buffer.putShort(v);
				break;
			}
			case VAL_TYPE_ENUM: {
				ret = 2;
				Integer v = (Integer) value;
				buffer.putUnsignedShort(v);
				break;
			}
			case VAL_TYPE_FLOAT: {
				ret = 4;
				Float v = (Float) value;
				buffer.putFloat(v);
				break;
			}
			case VAL_TYPE_STRING: {
				String v = (String) value;
				ret = v.length();
				buffer.put(v.getBytes());
				break;
			}
			case VAL_TYPE_BOOLEAN: {
				ret = 1;
				Boolean v = (Boolean) value;
				buffer.put(v == true ? (byte)1 : (byte)0);
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = 0;
		}
		return ret;

	}
	
	public static boolean ifSigTypeValid(int sigType) {
		return sigType >= VAL_TYPE_UINT32 && sigType <= VAL_TYPE_BOOLEAN;
	}
	
	public byte[] getSignalValueRelay() {
		return null;
	}

}
