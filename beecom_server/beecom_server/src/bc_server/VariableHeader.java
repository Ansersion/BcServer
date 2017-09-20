/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class VariableHeader {

	
	
	
	int Level = 0;
	int ClientIDLen = 0;
	int ClientID = 0;
	int AliveTime = 0;
	int Timeout = 0;
	
	Boolean Bit0 = new Boolean(false);
	Boolean Bit1 = new Boolean(false);
	Boolean Bit2 = new Boolean(false);
	Boolean Bit3 = new Boolean(false);
	Boolean Bit4 = new Boolean(false);
	Boolean Bit5 = new Boolean(false);
	Boolean Bit6 = new Boolean(false);
	Boolean Bit7 = new Boolean(false);
	
	Boolean UserFlag = Bit7;
	Boolean PwdFlag = Bit6;
	Boolean UserLoginFlag = Bit2;
	Boolean DeviceLoginFlag = Bit1;
	
	public void parseLevel(byte encoded_byte) {
		Level = encoded_byte;
	}
	
	public void parseFlags(byte flags) {
		Bit0 = (0x01 & flags) == 1;
		Bit1 = (0x02 & flags) == 1;
		Bit2 = (0x04 & flags) == 1;
		Bit3 = (0x08 & flags) == 1;
		Bit4 = (0x10 & flags) == 1;
		Bit5 = (0x20 & flags) == 1;
		Bit6 = (0x40 & flags) == 1;
		Bit7 = (0x80 & flags) == 1;
	}
	
	public void parseClientId(byte id_msb, byte id_lsb) {
		
	}
	
	public int parseClientIdLen(byte len) {
		ClientIDLen = len;
		
		return ClientIDLen;
	}
	
	public int parseClientId(byte[] id, int len) {
		ClientIDLen = len;
		ClientID = 0;

		if(id.length != len) {
			// throw new Exception("Error: parseClientId():id.length != len");
			return ClientID;
		}
		// the same as "len < sizeof(int)" 
		if(len > 4) {
			// throw new Exception("Error: parseClientId():len > 4");
			return ClientID;
		}

		for(int i = 0; i < len; i++) {
			ClientID = (ClientID << 8) | id[i];
		}
		
		return ClientID;
	}
	
	public void parseClientId(byte[] id) {
		
	}
	
	public int parseAliveTime(byte alive_time_msb, byte alive_time_lsb) {
		AliveTime = alive_time_msb;
		AliveTime = (AliveTime << 8) + alive_time_lsb;
		return AliveTime;
	}
	
	public int parseTimeout(byte timeout) {
		Timeout = timeout;
		return Timeout;
	}

}
