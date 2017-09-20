package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */
public interface BPPacketInterface {
	public int Decrypt(EncryptType etEncryptType) throws Exception;
	
    /**
     * Parse the fixed header.
     * @return The number of bytes of the cryptograph 
     * @throws Exception If an error occurred while decrypting
     */
	public int parseFixedHeader() throws Exception;
	public int parseVariableHeader() throws Exception;
	public boolean parseVariableHeader(IoBuffer io_buf) throws Exception;
	public int parsePayload() throws Exception;
	public void checkCRC(CrcChecksum ctCrc) throws Exception;
	public void setFixedHeader(FixedHeader fx_header) throws Exception;
	public void setVariableHeader(VariableHeader vrb_header) throws Exception;
	public void setPayload(Payload pld) throws Exception;
	public void setCrcChecksum(CrcChecksum ctCrc) throws Exception;

}