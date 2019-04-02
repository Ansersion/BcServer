package bp_packet;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public interface BPPacketInterface {
	public int decrypt(EncryptType etEncryptType) throws BPException;
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	public int parseFixedHeader() throws BPParseFxHeaderException;
	public int parseVariableHeader() throws BPParseVrbHeaderException;
	public int parsePayload() throws BPParsePldException;
	public boolean assembleStart() throws BPAssembleException;
	public boolean assembleFixedHeader() throws BPAssembleFxHeaderException;
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException;
	public boolean assemblePayload() throws BPAssemblePldException;
	public boolean assembleEnd() throws BPAssembleException;
	public boolean checkCRC(CrcChecksum ctCrc) throws BPCrcException;
	public void setFixedHeader(FixedHeader fxHeader);
	public void setVariableHeader(VariableHeader vrbHeader);
	public void setPayload(Payload pld);
	public void setCrcChecksum(CrcChecksum ctCrc) throws BPCrcException;
	
	public byte[] getPackByByte() throws BPException;

}
