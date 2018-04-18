package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;

import other.CrcChecksum;

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
	public boolean parseVariableHeader(byte[] buf) throws Exception;
	public int parsePayload() throws Exception;
	public boolean parsePayload(IoBuffer io_buf) throws Exception;
	public boolean parsePayload(byte[] buf) throws Exception;
	public boolean assembleStart() throws Exception;
	public boolean assembleFixedHeader() throws Exception;
	public boolean assembleVariableHeader() throws Exception;
	public boolean assemblePayload() throws Exception;
	public boolean assembleEnd() throws Exception;
	// public boolean assembleCrc() throws Exception;
	public boolean checkCRC(CrcChecksum ctCrc) throws Exception;
	public void setFixedHeader(FixedHeader fx_header) throws Exception;
	public void setVariableHeader(VariableHeader vrb_header) throws Exception;
	public void setPayload(Payload pld) throws Exception;
	public void setCrcChecksum(CrcChecksum ctCrc) throws Exception;
	
	public byte[] getPackByByte() throws Exception;

}