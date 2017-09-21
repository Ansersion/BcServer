/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPPacketFlags {
	Boolean Bit0 = new Boolean(false);
	Boolean Bit1 = new Boolean(false);
	Boolean Bit2 = new Boolean(false);
	Boolean Bit3 = new Boolean(false);
	
	Boolean EncryptionMSB = Bit2;
	Boolean EncryptionLSB = Bit1;
	Boolean Crc16 = Bit0;
	Boolean SymTable = Bit1;
	
	
	public static final byte CRC_TYPE_MASK = 0x01;
	public static final int CRC_TYPE_BIT_OFFSET = 0;
	
	public static final byte SYM_TABLE_FLAG_MASK = 0x01;
	public static final int SYM_TABLE_FLAG_BIT_OFFSET = 1;
	
	public static final byte ENCRYPT_TYPE_MASK = 0x03;
	public static final int ENCRYPT_TYPE_BIT_OFFSET = 1;
	
	public BPPacketFlags() {
		Bit0 = false;
		Bit1 = false;
		Bit2 = false;
		Bit3 = false;	
	}
	public void reset() {
		Bit0 = false;
		Bit1 = false;
		Bit2 = false;
		Bit3 = false;	
	}
	public void reset(byte flags) {
		Bit0 = (0x01 & flags) == 1;
		Bit1 = (0x02 & flags) == 1;
		Bit2 = (0x04 & flags) == 1;
		Bit3 = (0x08 & flags) == 1;
	}
	public void setCrc32() {
		Crc16 = false;
	}
	public void setCrc16() {
		Crc16 = true;
	}
	public void setSymTable() {
		SymTable = true;
	}
	public void clearSymTable() {
		SymTable = false;
	}
	
	public CrcChecksum getCrcChk() {
		return Crc16 == true ? CrcChecksum.CRC16 : CrcChecksum.CRC32;
	}
	
	public EncryptType getEncryptType() {
		// only support NO_ENCRYPTION now
		return EncryptType.NO_ENCRYPTION;
	}
	
	public byte getFlags() {
		byte ret = 0;
		ret |= (Bit0 == true) ? (0x01 << 0) : 0;
		ret |= (Bit1 == true) ? (0x01 << 1) : 0;
		ret |= (Bit2 == true) ? (0x01 << 2) : 0;
		ret |= (Bit3 == true) ? (0x01 << 3) : 0;
		
		return ret;
	}
}
