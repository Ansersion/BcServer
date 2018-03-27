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
public class BPPacket_PING extends BPPacket {
	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		// TODO Auto-generated method stub

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	

	
	protected BPPacket_PING() {
		super();
		FixedHeader fx_head = getFxHead();
		fx_head.setPacketType(BPPacketType.PING);
		fx_head.setCrcType(CrcChecksum.CRC32);
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		try {

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);
			
			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int pack_seq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(pack_seq);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

	
	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		try {
			System.out.println("PING: NO PAYLOAD");
		} catch (Exception e) {
			System.out.println("Error: parsePayload error");
			e.printStackTrace();
			throw e;
		}

		return true;
	}

	@Override
	public int parsePayload() throws Exception {
		// TODO Auto-generated method stub
		try {
			// No payload for PING
		} catch (Exception e) {
			System.out.println("Error(PING): parsePayload error");
			e.printStackTrace();
			throw e;
		}
		return 0;
	}
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encoded_byte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));
		
		getIoBuffer().put(encoded_byte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		byte flags = getVrbHead().getFlags();
		getIoBuffer().put(flags);
		int clnt_id = getVrbHead().getClientId();
		getIoBuffer().putUnsignedShort(clnt_id);
		int pack_seq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(pack_seq);	
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		
		return false;
	}

}
