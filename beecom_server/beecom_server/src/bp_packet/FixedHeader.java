/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class FixedHeader {
	BPPacketType PacketType = BPPacketType.INVALID;
	BPPacketFlags PacketFlags = new BPPacketFlags();
	int RemainingLength = 0;
	
	public FixedHeader(BPPacketType type) {
		PacketType = type;
	}
	
	public FixedHeader() {
	}
	
	public void reset() {
		PacketType = BPPacketType.INVALID;
		PacketFlags.reset();
		RemainingLength = 0;
	}
	
	public void setPacketType(BPPacketType type) {
		PacketType = type;
	}
	
	public void setPacketFlags(BPPacketFlags flags) {
		PacketFlags = flags;
	}
	
	public void setRemainLen(int len) {
		RemainingLength = len;
	}
	
	public void setRemainLen(IoBuffer io) throws Exception{
		if(io.remaining() >= 2) {
			int multiplier = 1;
			int len = 0;
			byte encodedByte;
			do {
				encodedByte = (byte)io.get();
				len += (encodedByte & 0x7F) * multiplier;
				multiplier *= 128;
				if (multiplier > 128) {
					throw new Exception("Remaining length too long");
				}
			} while ((encodedByte & 0x80) != 0);
			
			RemainingLength = len;
		}
	}
	
	public void setBPType(byte encodedByte) {
		PacketType = BPPacketType.getType(encodedByte);
	}
	
	public void setFlags(byte encodedByte) {
		PacketFlags.reset(encodedByte);
	}
	
	public void setCrcType(CrcChecksum crc) {
		if(crc == CrcChecksum.CRC32) {
			PacketFlags.setCrc32();
		} else if(crc == CrcChecksum.CRC16) {
			PacketFlags.setCrc16();
		} else {
			System.out.println("Error(FixedHeader): unknown CRC type");
		}
	}
	
	public byte getFlags() {
		return PacketFlags.getFlags();
	}
	
	public CrcChecksum getCrcChk() {
		return PacketFlags.getCrcChk();
	}
	
	public EncryptType getEncryptType() {
		return PacketFlags.getEncryptType();
	}

	public BPPacketType getPacketType() {
		return PacketType;
	}
	
	public byte getFirstByte() {
		byte type = PacketType.getTypeByte();
		byte flag = PacketFlags.getFlags();
		return (byte)(type | flag);
		
	}
	
	public int getRemainingLen() {
		return RemainingLength;
	}
}
