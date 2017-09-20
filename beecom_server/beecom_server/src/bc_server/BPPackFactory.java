/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 * 
 */
public class BPPackFactory {
	public static BPPacket createBPPack(FixedHeader fx_header) {
		
		BPPacket ret;
		
		if(fx_header.getPacketType() == BPPacketType.CONNECT) {
			ret = new BPPacket_CONNECT(fx_header);
		} else if(fx_header.getPacketType() == BPPacketType.CONNACK) {
			ret = new BPPacket_CONNACK(fx_header);
		} else {
			ret = null;
		}
		
		return ret;
	}
}