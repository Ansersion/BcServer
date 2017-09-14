package bc_server;

public interface BPPacketMethods {
	int Decrypt(EncryptType etEncryptType) throws Exception;
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	int ParseFixedHeader() throws Exception;
	int ParseVariableHeader() throws Exception;
	int ParsePayload() throws Exception;
	void CheckCRC(CrcType ctCrc) throws Exception;

}
