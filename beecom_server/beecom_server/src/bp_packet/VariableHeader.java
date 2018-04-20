/**
 * 
 */
package bp_packet;

/**
 * @author Ansersion
 *
 */
public class VariableHeader {
	
	public static final byte DIST_END_FLAG_MSK = 0x01; 
	
	int level = 0;
	int clientIDLen = 0;
	int clientID = 0;
	int aliveTime = 0;
	int timeout = 0;
	int retCode = 0;
	int packSeq = 0;
	
	Boolean bit0 = false;
	Boolean bit1 = false;
	Boolean bit2 = false;
	Boolean bit3 = false;
	Boolean bit4 = false;
	Boolean bit5 = false;
	Boolean bit6 = false;
	Boolean bit7 = false;
	
	Boolean userFlag;
	Boolean pwdFlag;
	Boolean userLoginFlag;
	Boolean deviceLoginFlag;
	Boolean lanChinese;
	Boolean lanEnglish;
	Boolean lanFrench;
	Boolean lanRussian;
	Boolean lanArabic;
	Boolean lanSpanish;
	Boolean otherLanguageFlag;
	
	public VariableHeader() {
		userFlag = bit7;
		pwdFlag = bit6;
		userLoginFlag = bit2;
		deviceLoginFlag = bit1;
		lanChinese = bit7;
		lanEnglish = bit6;
		lanFrench = bit5;
		lanRussian = bit4;
		lanArabic = bit3;
		lanSpanish = bit2;
		otherLanguageFlag = bit1;
	}

	
	public void parseLevel(byte encodedByte) {
		level = encodedByte;
	}
	
	public void parseFlags(byte flags) {
		bit0 = (0x01 & flags) == 0x01;
		bit1 = (0x02 & flags) == 0x02;
		bit2 = (0x04 & flags) == 0x04;
		bit3 = (0x08 & flags) == 0x08;
		bit4 = (0x10 & flags) == 0x10;
		bit5 = (0x20 & flags) == 0x20;
		bit6 = (0x40 & flags) == 0x40;
		bit7 = (0x80 & flags) == 0x80;
	}
	
	public void parseClientId(byte idMsb, byte idLsb) {
		clientID = idMsb;
		clientID = (clientID << 8) | (idLsb & 0xFF);
	}
	
	public int parseClientIdLen(byte len) {
		clientIDLen = len;
		
		return clientIDLen;
	}
	
	public int parseClientId(byte[] id, int len) {
		clientIDLen = len;
		clientID = 0;

		if(id.length != len) {
			return clientID;
		}
		// the same as "len < sizeof(int)" 
		if(len > 4) {
			return clientID;
		}

		for(int i = 0; i < len; i++) {
			clientID = (clientID << 8) | (id[i] & 0xFF);
		}
		
		return clientID;
	}
	
	
	public int parseAliveTime(byte aliveTimeMsb, byte aliveTimeLsb) {
		aliveTime = aliveTimeMsb;
		aliveTime = (aliveTime << 8) + (aliveTimeLsb & 0xFF);
		return aliveTime;
	}
	
	public int parseTimeout(byte timeout) {
		this.timeout = timeout;
		return timeout;
	}
	
	public boolean getUserNameFlag() {
		return bit7;
	}
	
	public boolean getPwdFlag() {
		return bit6;
	}
	
	public int getClientId() {
		return clientID;
	}
	
	public int getAliveTime() {
		return aliveTime;
	}
	
	public boolean getUserClntFlag() {
		return bit2;
	}
	
	public boolean getDevClntFlag() {
		return bit1;
	}
	
	public int getLevel() {
		return level;
	}
	
	
	public byte getFlags() {
		byte ret = 0;
		ret |= bit0 ? (0x01 << 0) : 0;
		ret |= bit1 ? (0x01 << 1) : 0;
		ret |= bit2 ? (0x01 << 2) : 0;
		ret |= bit3 ? (0x01 << 3) : 0;
		ret |= bit4 ? (0x01 << 4) : 0;
		ret |= bit5 ? (0x01 << 5) : 0;
		ret |= bit6 ? (0x01 << 6) : 0;
		ret |= bit7 ? (0x01 << 7) : 0;
		
		return ret;
	}
	
	public int getRetCode() {
		return retCode;
	}
	
	public boolean getChineseFlag() {
		return lanChinese;
	}

	public boolean getEnglishFlag() {
		return lanEnglish;
	}
	
	public int getPackSeq() {
		return packSeq;
	}
	
	public boolean getReportToken() {
		return bit3;
	}
	
	public boolean getPushToken() {
		return bit3;
	}
	
	public void setClientId(int id) {
		clientID = id;
	}
	
	public boolean getDevNameFlag() {
		return bit7;
	}
	
	public boolean getSysSigMapFlag() {
		return bit6;
	}
	
	public boolean getCusSigFlag() {
		return bit5;
	}
	
	public boolean getSigFlag() {
		return bit4;
	}
	
	public void setAliveTime(int time) {
		aliveTime = time;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setRetCode(int code) {
		retCode = code;
	}
	
	public void setNewCliIdFlg() {
		bit7 = true;
	}
	
	public void setPackSeq(int packSeq) {
		this.packSeq = packSeq;
	}
}
