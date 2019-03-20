/**
 * 
 */
package bp_packet;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ansersion
 *
 */
public class VariableHeader {
    private static final int MAX_GLOBAL_PACK_SEQ_PLUS_ONE = 0xFFFF+1;
    private static final int MIN_GLOBAL_PACK_SEQ = 1;
    private static int globalPackSeq = MIN_GLOBAL_PACK_SEQ + new Random().nextInt(MAX_GLOBAL_PACK_SEQ_PLUS_ONE - 1);
    
    private static Lock globalPackSeqLock = new ReentrantLock();
    
	public static final byte DIST_END_FLAG_MSK = 0x01; 
	
	private byte flags;
	int level = 0;
	int clientIDLen = 0;
	int clientID = 0;
	int aliveTime = 0;
	short timeout = 0;
	int retCode = 0;
	int packSeq = 0;
	private byte langFlags;
	
	/*
	Boolean bit0 = false;
	Boolean bit1 = false;
	Boolean bit2 = false;
	Boolean bit3 = false;
	Boolean bit4 = false;
	Boolean bit5 = false;
	Boolean bit6 = false;
	Boolean bit7 = false;
	*/
	
	/*
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
	*/
	
    private static int generatePackSeq() {
    	int ret = 0;
    	try {
    		globalPackSeqLock.lock();
    		ret = globalPackSeq++;
    		if(globalPackSeq >= MAX_GLOBAL_PACK_SEQ_PLUS_ONE) {
    			globalPackSeq = MIN_GLOBAL_PACK_SEQ;
    		}
    	} finally {
    		globalPackSeqLock.unlock();
    	}
        return ret;
    }
	
	public VariableHeader() {
		/*
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
		*/
	}

    public void initPackSeq() {
    	packSeq = generatePackSeq();
    }
    
	public void parseLevel(byte encodedByte) {
		level = encodedByte;
	}
	
	public void setSysSigMapFlag(boolean flag) {
		flags |= 0x80;
	}
	
	public void parseFlags(byte flags) {
		/*
		bit0 = (0x01 & flags) == 0x01;
		bit1 = (0x02 & flags) == 0x02;
		bit2 = (0x04 & flags) == 0x04;
		bit3 = (0x08 & flags) == 0x08;
		bit4 = (0x10 & flags) == 0x10;
		bit5 = (0x20 & flags) == 0x20;
		bit6 = (0x40 & flags) == 0x40;
		bit7 = (0x80 & flags) == 0x80;
		*/
		this.flags = flags;
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
		return (flags & 0x80) != 0;
	}
	
	public boolean getPwdFlag() {
		return (flags & 0x40) != 0;
	}
	
	public int getClientId() {
		return clientID;
	}
	
	public int getAliveTime() {
		return aliveTime;
	}
	
	public boolean getUserClntFlag() {
		return (flags & 0x04) != 0;
	}
	
	public boolean getDevClntFlag() {
		return (flags & 0x02) != 0;
	}
	
	public int getLevel() {
		return level;
	}
	
	public short getTimeout() {
		return timeout;
	}
	
	public byte getFlags() {
		return flags;
	}
	
	public int getRetCode() {
		return retCode;
	}
	
	public boolean getChineseFlag() {
		return false;
	}

	public boolean getEnglishFlag() {
		return false;
	}
	
	public int getPackSeq() {
		return packSeq;
	}
	
	public boolean getReportToken() {
		return (flags & 0x08) != 0;
	}
	
	public boolean getPushToken() {
		return (flags & 0x08) != 0;
	}
	
	public void setClientId(int id) {
		clientID = id;
	}
	
	public boolean getDevNameFlag() {
		return (flags & 0x80) != 0;
	}
	
	   /* for GET */
    public boolean getSysSigMapCustomInfo() {
    	return (flags & 0x04) != 0;
    }

    public VariableHeader setSysSigMapCustomInfo() {
        flags |= 0x04;
        return this;
    }

    public VariableHeader clrSysSigMapCustomInfo() {
    	flags &= (~0x04);
        return this;
    }

    public boolean getCusSigMapFlag() {
    	return (flags & 0x40) != 0;
    }

    public VariableHeader setCusSigMapFlag() {
    	flags |= 0x40;
        return this;
    }

    public VariableHeader clrCusSigMapFlag() {
    	flags &= (~0x40);
        return this;
    }

    public boolean getSysSigMapFlag() {
    	return (flags & 0x80) != 0;
    }

    public VariableHeader setSysSigMapFlag() {
    	flags |= 0x80;
        return this;
    }

    public VariableHeader clrSysSigMapFlag() {
    	flags &= (~0x80);
        return this;
    }
    /* for GET end */
	
	public boolean getSysSigFlag() {
		return (flags & 0x10) != 0;
	}
	
	public boolean getCusSigFlag() {
		return (flags & 0x08) != 0;
	}
	
	public boolean getDevIdFlag() {
		return (flags & 0x20) != 0;
	}
	
	public boolean getSigValFlag() {
		return (flags & 0x10) != 0;
	}
	
	public void setSigValFlag(boolean sigValFlag) {
		flags |= 0x10;
	}
	
	public boolean getSysCusFlag() {
		return (flags & 0x04) != 0;
	}
	
	public boolean getInfoLeftFlag() {
		return (flags & 0x02) != 0;
	}
	
	public void setAliveTime(int time) {
		aliveTime = time;
	}
	
	public void setTimeout(short timeout) {
		this.timeout = timeout;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setRetCode(int code) {
		retCode = code;
	}
	
	public void setNewCliIdFlg() {
		flags |= 0x80;
	}
	
	public void setPackSeq(int packSeq) {
		this.packSeq = packSeq;
	}
	
	public boolean getDebugMode() {
		return (flags & 0x01) != 0;
	}
	
	public byte getPerformanceClass() {
		return 0;
	}
	
	public boolean getUserOnLine() {
		return (flags & 0x08) != 0;
	}
	
	public boolean getSigMapChecksumFlag() {
		return (flags & 0x02) != 0;
	}
	
	public void setDevIdFlag(boolean b) {
		if(b) {
			flags |= 0x80;
		} else {
			flags &= (~0x80);
		}
	}
	
	public byte getLangFlags() {
		return langFlags;
	}
	
	public boolean getSysSigAttrFlag() {
		return (flags & 0x02) != 0;
	}
	
	public boolean getCusSigAttrFlag() {
		return (flags & 0x01) != 0;
	}
	
	public boolean getReqAllDeviceId() {
		return (flags & 0x02) != 0;
	}
	
	public void setReqAllDeviceIdFlag(boolean reqAllDeviceIdFlag) {
		if(reqAllDeviceIdFlag) {
			flags |= 0x02;
		} else {
			flags &= (~0x02);
		}
	}


	public void setLangFlags(byte langFlags) {
		this.langFlags = langFlags;
	}
	
	public boolean getSigMapChecksumOnly() {
		return (flags & 0x02) != 0;
	}
	
}
