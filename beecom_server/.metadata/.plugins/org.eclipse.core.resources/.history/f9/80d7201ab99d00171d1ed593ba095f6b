package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */
public class BPPacket implements BPPacketInterface{
	
	byte[] BPPacketData;
	FixedHeader FxHeader; // = new FixedHeader();
	VariableHeader VrbHeader; // = new VariableHeader();
	Payload Pld; // = new Payload();
	CrcChecksum Crc;
	
	public BPPacket() {}
	
	public BPPacket(FixedHeader fx_header) {
		FxHeader = fx_header;
	}
	
    /**
     * Decrypt the packet data.
     * @param etEncryptType The encryption type.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */

	@Override
	public int Decrypt(EncryptType etEncryptType) throws Exception {
		return 0;
	}
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	@Override
	public int parseFixedHeader() throws Exception {
		return 0;
	}
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception {
		return false;
	}
	
	public void parseVrbHeadLevel(byte level) {
		VrbHeader.parseLevel(level);
	}
	
	public void parseVrbHeadFlags(byte flags) {
		VrbHeader.parseFlags(flags);
	}
	
	public int parseVrbClientIdLen(byte len) {
		return VrbHeader.parseClientIdLen(len);
	}
	
	public int parseVrbClientId(byte[] id, int len) {
		return VrbHeader.parseClientId(id, len);
	}
	
	public int parseVrbAliveTime(byte alive_time_msb, byte alive_time_lsb) {
		return VrbHeader.parseAliveTime(alive_time_msb, alive_time_lsb);
	}
	
	public int parseVrbTimeout(byte timeout) {
		return VrbHeader.parseTimeout(timeout);
	}
	
	@Override
	public int parsePayload() throws Exception {
		return 0;
	}
	@Override
	public void checkCRC(CrcChecksum ccCrc) throws Exception {
		try {
			if(CrcChecksum.CRC16 == ccCrc) {
				if(0 != CrcChecksum.calcCrc16(BPPacketData)) {
					throw new Exception("Check CRC16 Error");
				}
				return;
			}
			if(CrcChecksum.CRC32 == ccCrc) {
				if(0 != CrcChecksum.calcCrc32(BPPacketData)) {
					throw new Exception("Check CRC32 Error");
				}
				return;
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void setFixedHeader(FixedHeader fx_header) throws Exception {
		// TODO Auto-generated method stub
		FxHeader = fx_header;
	}

	@Override
	public void setVariableHeader(VariableHeader vrb_header) throws Exception {
		// TODO Auto-generated method stub
		VrbHeader = vrb_header;
		
	}

	@Override
	public void setPayload(Payload pld) throws Exception {
		// TODO Auto-generated method stub
		Pld = pld;
		
	}

	@Override
	public void setCrcChecksum(CrcChecksum ccCrc) throws Exception {
		// TODO Auto-generated method stub
		Crc = ccCrc;
	}

	@Override
	public int parseVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
