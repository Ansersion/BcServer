/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */
public class BPUtils {
	
    /* assemble the String into IoBuffer
     * @return 0 when success, other for failed*/
	public static int assembleStr(IoBuffer ioBuffer, String s) {
		int ret = 0;
		
		if(null == ioBuffer || null == s) {
			return -1;
		}
		
		byte[] bytes = s.getBytes();
		
		if(bytes.length > BPPacket.MAX_STR_LENGTH) {
			return -2;
		}
		
		ioBuffer.put((byte)bytes.length);
		ioBuffer.put(bytes);
		
		return ret;
	}
}
