/**
 * 
 */
package bp_packet;


import org.apache.mina.core.buffer.IoBuffer;




/**
 * @author Ansersion
 *
 */
public class BPPacketPUSH extends BPPacket {
	
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {
		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) {
		return true;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) {
		return true;
	}
}
