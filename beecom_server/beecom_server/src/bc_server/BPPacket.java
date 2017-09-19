package bc_server;


/**
 * @author Ansersion
 *
 */
public class BPPacket implements BPPacketMethods{
	
	byte[] BPPacketData;
	FixedHeader FxHeader = new FixedHeader();
	VariableHeader VrbHeader = new VariableHeader();
	Payload Pld = new Payload();
	
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
	public int ParseFixedHeader() throws Exception {
		return 0;
	}
	/**
     * {@inheritDoc}
     */
	@Override
	public int ParseVariableHeader() throws Exception {
		return 0;
	}
	@Override
	public int ParsePayload() throws Exception {
		return 0;
	}
	@Override
	public void CheckCRC(CrcChecksum ctCrc) throws Exception {
		try {
			if(CrcChecksum.CRC16 == ctCrc) {
				if(0 != CrcChecksum.calcCrc16(BPPacketData)) {
					throw new Exception("Check CRC16 Error");
				}
				return;
			}
			if(CrcChecksum.CRC32 == ctCrc) {
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
}
