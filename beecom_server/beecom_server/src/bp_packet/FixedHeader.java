/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class FixedHeader {
	private static final Logger logger = LoggerFactory.getLogger(FixedHeader.class);
	
	public static final byte CRC16_FLAG_MASK = 0x01;
	public static final byte ENCRYPTION_FLAG_MASK = 0x03;
	public static final int ENCRYPTION_FLAG_OFFSET = 1;
	
	BPPacketType packetType = BPPacketType.INVALID;
	int remainingLength = 0;
	private byte flags;

	public FixedHeader() {
		this.remainingLength = 0;
		this.flags = 0;
	}
	
	public void reset() {
		packetType = BPPacketType.INVALID;
		// packetFlags.reset();
		remainingLength = 0;
		flags = 0;
	}
	
	public void setPacketType(BPPacketType type) {
		packetType = type;
	}
	
	public void setRemainLen(int len) {
		remainingLength = len;
	}
	
	public void setRemainLen(IoBuffer io) {
		remainingLength = io.get() << 8;
		remainingLength += (io.get() & 0xFF); 
	}
	
	public void setBPType(byte encodedByte) {
		packetType = BPPacketType.getType(encodedByte);
	}
	
	public void setFlags(byte encodedByte) {
		// packetFlags.reset(encodedByte);
		flags = encodedByte;
	}
	
	public void setCrcType(CrcChecksum crc) {
		String s;
		if(crc == CrcChecksum.CRC32) {
			// packetFlags.setCrc32();
			flags &= ~CRC16_FLAG_MASK;
		} else if(crc == CrcChecksum.CRC16) {
			// packetFlags.setCrc16();
			flags |= CRC16_FLAG_MASK;
		} else {
			s = "Error(FixedHeader): unknown CRC type";
			logger.warn(s);
		}
	}	
	
	public void setEncryptType(EncryptType.EnType type) {
		switch(type) {
			case PLAIN:
				flags &= (byte)(~(ENCRYPTION_FLAG_MASK << ENCRYPTION_FLAG_OFFSET));
				flags |= (byte)(0 << ENCRYPTION_FLAG_OFFSET);
				break;
			case BASE64:
				flags &= (byte)(~(ENCRYPTION_FLAG_MASK << ENCRYPTION_FLAG_OFFSET));
				flags |= (byte)(0 << ENCRYPTION_FLAG_OFFSET);
				break;
			case EN_2:
				flags &= (byte)(~(ENCRYPTION_FLAG_MASK << ENCRYPTION_FLAG_OFFSET));
				flags |= (byte)(0 << ENCRYPTION_FLAG_OFFSET);
				break;
			case EN_3:
				flags &= (byte)(~(ENCRYPTION_FLAG_MASK << ENCRYPTION_FLAG_OFFSET));
				flags |= (byte)(0 << ENCRYPTION_FLAG_OFFSET);
				break;
		}
		
	}
	
	public byte getFlags() {
		return flags;
	}
	
	public CrcChecksum getCrcChk() {
		if((flags & CRC16_FLAG_MASK) != 0) {
			return CrcChecksum.CRC16;
		} else {
			return CrcChecksum.CRC32;
		}
	}
	
	public EncryptType.EnType getEncryptType() {
		byte encryptionType = (byte)((flags >> ENCRYPTION_FLAG_OFFSET) & ENCRYPTION_FLAG_MASK);
		EncryptType.EnType ret;
		switch(encryptionType) {
			case 0:
				ret = EncryptType.EnType.PLAIN;
				break;
			case 1:
				ret = EncryptType.EnType.BASE64;
				break;
			case 2:
				ret = EncryptType.EnType.EN_2;
				break;
			case 3:
				ret = EncryptType.EnType.EN_3;
				break;
			default:
				/* never come here */
				ret = EncryptType.EnType.PLAIN;
				break;
		}
		
		return ret;
	}

	public BPPacketType getPacketType() {
		return packetType;
	}
	
	public byte getFirstByte() {
		return flags;
	}
	
	public int getRemainingLen() {
		return remainingLength;
	}
}
