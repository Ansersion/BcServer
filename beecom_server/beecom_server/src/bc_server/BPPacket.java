package bc_server;

public class BPPacket implements BPPacketMethods{
	
	byte[] BPPacketData;
	
    /**
     * Decrypt the packet data.
     * @param etEncryptType The encryption type.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	public int Decrypt(EncryptType etEncryptType) throws Exception {
		return 0;
	}
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	public int ParseFixedHeader() throws Exception {
		return 0;
	}
	/**
     * {@inheritDoc}
     */
	public int ParseVariableHeader() throws Exception {
		return 0;
	}
	public int ParsePayload() throws Exception {
		return 0;
	}
	public void CheckCRC(CrcType ctCrc) throws Exception {
		try {
			if(CrcType.CRC16 == ctCrc) {
				if(0 != CrcType.calcCrc16(BPPacketData)) {
					throw new Exception("Check CRC16 Error");
				}
				return;
			}
			if(CrcType.CRC32 == ctCrc) {
				if(0 != CrcType.calcCrc32(BPPacketData)) {
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
