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
	Boolean Crc = Bit0;
	Boolean SymTable = Bit1;
	
	public BPPacketFlags() {
		Bit0 = false;
		Bit1 = false;
		Bit2 = false;
		Bit3 = false;	
	}
	public void Reset() {
		Bit0 = false;
		Bit1 = false;
		Bit2 = false;
		Bit3 = false;	
	}
	public void SetCrc32() {
		Crc = false;
	}
	public void SetCrc16() {
		Crc = true;
	}
	public void SetSymTable() {
		SymTable = true;
	}
	public void ClearSymTable() {
		SymTable = false;
	}
}
