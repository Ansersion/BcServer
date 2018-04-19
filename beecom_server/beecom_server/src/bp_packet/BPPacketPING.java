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
public class BPPacketPING extends BPPacket {
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) throws Exception {
		// TODO Auto-generated method stub

		try {
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	

	
	protected BPPacketPING() {
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
		return true;
	}

	@Override
	public int parsePayload() throws Exception {
		return 0;
	}
	
	@Override
	public boolean assembleFixedHeader() throws Exception {
		int packType = getPackTypeIntFxHead();
		byte packFlags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((packType & 0xf) << 4) | (packFlags & 0xf));
		
		getIoBuffer().put(encodedByte);
		
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
