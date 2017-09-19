package bc_server;

/**
 * @author Ansersion
 *
 */
public interface BPPacketMethods {
	public int Decrypt(EncryptType etEncryptType) throws Exception;
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	public int ParseFixedHeader() throws Exception;
	public int ParseVariableHeader() throws Exception;
	public int ParsePayload() throws Exception;
	public void CheckCRC(CrcChecksum ctCrc) throws Exception;

}
