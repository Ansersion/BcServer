/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class VariableHeader {
	
	public static final byte DIST_END_FLAG_MSK = 0x01; 
	
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
	
	Boolean UserFlag;
	Boolean PwdFlag;
	Boolean UserLoginFlag;
	Boolean DeviceLoginFlag;
	Boolean LanChinese;
	Boolean LanEnglish;
	Boolean LanFrench;
	Boolean LanRussian;
	Boolean LanArabic;
	Boolean LanSpanish;
	Boolean OtherLanguageFlag;
	
	VariableHeader() {
		UserFlag = Bit7;
		PwdFlag = Bit6;
		UserLoginFlag = Bit2;
		DeviceLoginFlag = Bit1;
		LanChinese = Bit7;
		LanEnglish = Bit6;
		LanFrench = Bit5;
		LanRussian = Bit4;
		LanArabic = Bit3;
		LanSpanish = Bit2;
		OtherLanguageFlag = Bit1;
	}

	
	public void parseLevel(byte encoded_byte) {
		Level = encoded_byte;
	}
	
	public void parseFlags(byte flags) {
		Bit0 = (0x01 & flags) == 0x01;
		Bit1 = (0x02 & flags) == 0x02;
		Bit2 = (0x04 & flags) == 0x04;
		Bit3 = (0x08 & flags) == 0x08;
		Bit4 = (0x10 & flags) == 0x10;
		Bit5 = (0x20 & flags) == 0x20;
		Bit6 = (0x40 & flags) == 0x40;
		Bit7 = (0x80 & flags) == 0x80;
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
		// return UserFlag;
		return Bit7;
	}
	
	public boolean getPwdFlag() {
		// return PwdFlag;
		return Bit6;
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
	
	public boolean getChineseFlag() {
		return LanChinese;
	}

	public boolean getEnglishFlag() {
		return LanEnglish;
	}
	
	public int getPackSeq() {
		return PackSeq;
	}
	
	public boolean getReportToken() {
		return Bit3;
	}
	
	public boolean getPushToken() {
		return Bit3;
	}
	
	public void setClientId(int id) {
		ClientID = id;
	}
	
	public boolean getDevNameFlag() {
		return Bit7;
	}
	
	public boolean getSysSigFlag() {
		return Bit6;
	}
	
	public boolean getCusSigFlag() {
		return Bit5;
	}
	
	public boolean getSigFlag() {
		return Bit4;
	}
	
	public void setAliveTime(int time) {
		AliveTime = time;
	}
	
	public void setTimeout(int timeout) {
		Timeout = timeout;
	}
	
	public void setLevel(int level) {
		Level = level;
	}
	
	public void setRetCode(int code) {
		RetCode = code;
	}
	
	public void setNewCliIdFlg() {
		Bit7 = true;
	}
	
	public void setPackSeq(int pack_seq) {
		PackSeq = pack_seq;
	}
}
