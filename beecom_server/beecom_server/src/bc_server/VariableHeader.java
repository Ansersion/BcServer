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
	int RetCode = 0;
	int PackSeq = 0;
	
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
	Boolean LanChinese = Bit7;
	Boolean LanEnglish = Bit6;
	Boolean LanFrench = Bit5;
	Boolean LanRussian = Bit4;
	Boolean LanArabic = Bit3;
	Boolean LanSpanish = Bit2;
	Boolean OtherLanguageFlag = Bit1;

	
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
		ClientID = id_msb;
		ClientID = (ClientID << 8) | id_lsb;
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
	
	public boolean getUserNameFlag() {
		return UserFlag;
	}
	
	public boolean getPwdFlag() {
		return PwdFlag;
	}
	
	public int getClientId() {
		return ClientID;
	}
	
	public boolean getUserLoginFlag() {
		return UserLoginFlag;
	}
	
	public boolean getDeviceLoginFlag() {
		return DeviceLoginFlag;
	}
	
	public int getLevel() {
		return Level;
	}
	
	
	public byte getFlags() {
		byte ret = 0;
		ret |= (Bit0 == true) ? (0x01 << 0) : 0;
		ret |= (Bit1 == true) ? (0x01 << 1) : 0;
		ret |= (Bit2 == true) ? (0x01 << 2) : 0;
		ret |= (Bit3 == true) ? (0x01 << 3) : 0;
		ret |= (Bit4 == true) ? (0x01 << 4) : 0;
		ret |= (Bit5 == true) ? (0x01 << 5) : 0;
		ret |= (Bit6 == true) ? (0x01 << 6) : 0;
		ret |= (Bit7 == true) ? (0x01 << 7) : 0;
		
		return ret;
	}
	
	public int getRetCode() {
		return RetCode;
	}

}
