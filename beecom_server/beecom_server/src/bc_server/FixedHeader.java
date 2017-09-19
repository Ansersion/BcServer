/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */
public class FixedHeader {
	BPPacketType PacketType = BPPacketType.INVALID;
	BPPacketFlags PacketFlags = new BPPacketFlags();
	int RemainingLength = 0;
	CrcChecksum CrcType = CrcChecksum.CRC32;
	
	public FixedHeader(BPPacketType type) {
		PacketType = type;
	}
	
	public FixedHeader() {
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
			byte encoded_byte;
			do {
				encoded_byte = (byte)io.getChar();
				len += (encoded_byte & 0x7F) * multiplier;
				multiplier *= 128;
				if (multiplier > 128) {
					throw new Exception("Remaining length too long");
				}
			} while ((encoded_byte & 0x80) != 0);
			
			RemainingLength = len;
		}
	}
	
	public void setBPType(byte encoded_byte) {
		PacketType = BPPacketType.getType(encoded_byte);
	}
	
	public void setFlags(byte encoded_byte) {
		PacketFlags.reset(encoded_byte);
	}
	
	public CrcChecksum getCrcChk() {
		return PacketFlags.getCrcChk();
	}
	
	public EncryptType getEncryptType() {
		return PacketFlags.getEncryptType();
	}

}
