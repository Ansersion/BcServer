/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;


/**
 * @author Ansersion
 *
 */
public class BPPacket_DISCONN extends BPPacket {

	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub

		return true;
	}
	
	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {
			int counter = 0;

			byte client_id_msb = buf[counter++];
			byte client_id_lsb = buf[counter++];
			super.getVrbHeader().parseClientId(client_id_msb, client_id_lsb);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return true;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		
		try {
		} catch (Exception e) {
			System.out.println("Error: parsePayload(DISCONN) error");
			e.printStackTrace();
			throw e;
		}
	
		return true;
	}

}