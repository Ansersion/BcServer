/**
 * 
 */
package bc_server;

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
	
	public void SetPacketType(BPPacketType type) {
		PacketType = type;
	}
	
	public void SetPacketFlags(BPPacketFlags flags) {
		PacketFlags = flags;
	}

}
