/**
 * 
 */
package bp_packet;

import other.CrcChecksum;

/**
 * @author Ansersion
 *
 */
public class BPPacketFlags {
	Boolean bit0 = false;
	Boolean bit1 = false;
	Boolean bit2 = false;
	Boolean bit3 = false;
	
	Boolean encryptionMSB = bit2;
	Boolean encryptionLSB = bit1;
	Boolean crc16 = bit0;
	Boolean symTable = bit1;
	
	
	public static final byte CRC_TYPE_MASK = 0x01;
	public static final int CRC_TYPE_BIT_OFFSET = 0;
	
	public static final byte SYM_TABLE_FLAG_MASK = 0x01;
	public static final int SYM_TABLE_FLAG_BIT_OFFSET = 1;
	
	public static final byte ENCRYPT_TYPE_MASK = 0x03;
	public static final int ENCRYPT_TYPE_BIT_OFFSET = 1;
	
	public BPPacketFlags() {
		bit0 = false;
		bit1 = false;
		bit2 = false;
		bit3 = false;	
	}
	public void reset() {
		bit0 = false;
		bit1 = false;
		bit2 = false;
		bit3 = false;	
	}
	public void reset(byte flags) {
		bit0 = (0x01 & flags) == 1;
		bit1 = (0x02 & flags) == 1;
		bit2 = (0x04 & flags) == 1;
		bit3 = (0x08 & flags) == 1;
	}
	public void setCrc32() {
		bit0 = false;
	}
	public void setCrc16() {
		bit0 = true;
	}
	public void setSymTable() {
		symTable = true;
	}
	public void clearSymTable() {
		symTable = false;
	}
	
	public CrcChecksum getCrcChk() {
		return bit0 ? CrcChecksum.CRC16 : CrcChecksum.CRC32;
	}
	
	public EncryptType getEncryptType() {
		// only support NO_ENCRYPTION now
		return EncryptType.NO_ENCRYPTION;
	}
	
	public byte getFlags() {
		byte ret = 0;
		ret |= bit0 ? (0x01 << 0) : 0;
		ret |= bit1 ? (0x01 << 1) : 0;
		ret |= bit2 ? (0x01 << 2) : 0;
		ret |= bit3 ? (0x01 << 3) : 0;
		
		return ret;
	}
}
