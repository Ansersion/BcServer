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
	
	BPPacketType packetType = BPPacketType.INVALID;
	BPPacketFlags packetFlags = new BPPacketFlags();
	int remainingLength = 0;
	
	public FixedHeader(BPPacketType type) {
		packetType = type;
	}
	
	public FixedHeader() {
	}
	
	public void reset() {
		packetType = BPPacketType.INVALID;
		packetFlags.reset();
		remainingLength = 0;
	}
	
	public void setPacketType(BPPacketType type) {
		packetType = type;
	}
	
	public void setPacketFlags(BPPacketFlags flags) {
		packetFlags = flags;
	}
	
	public void setRemainLen(int len) {
		remainingLength = len;
	}
	
	public void setRemainLen(IoBuffer io) throws BPParseFxHeaderException{
		remainingLength = io.get() << 8;
		remainingLength += (io.get() & 0xFF); 
	}
	
	public void setBPType(byte encodedByte) {
		packetType = BPPacketType.getType(encodedByte);
	}
	
	public void setFlags(byte encodedByte) {
		packetFlags.reset(encodedByte);
	}
	
	public void setCrcType(CrcChecksum crc) {
		String s;
		if(crc == CrcChecksum.CRC32) {
			packetFlags.setCrc32();
		} else if(crc == CrcChecksum.CRC16) {
			packetFlags.setCrc16();
		} else {
			s = "Error(FixedHeader): unknown CRC type";
			logger.warn(s);
		}
	}
	
	public byte getFlags() {
		return packetFlags.getFlags();
	}
	
	public CrcChecksum getCrcChk() {
		return packetFlags.getCrcChk();
	}
	
	public EncryptType getEncryptType() {
		return packetFlags.getEncryptType();
	}

	public BPPacketType getPacketType() {
		return packetType;
	}
	
	public byte getFirstByte() {
		byte type = packetType.getTypeByte();
		byte flag = packetFlags.getFlags();
		return (byte)(type | flag);
		
	}
	
	public int getRemainingLen() {
		return remainingLength;
	}
}
