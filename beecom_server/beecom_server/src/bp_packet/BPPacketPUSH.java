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
	
	public static final int RET_CODE_OK = 0;
	public static final int RET_CODE_UNSUPPORTED_SIGNAL_ID = 0x01;
	
	
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
