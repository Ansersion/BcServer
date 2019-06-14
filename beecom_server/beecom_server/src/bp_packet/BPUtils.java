/**
 * 
 */
package bp_packet;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;

/**
 * @author Ansersion
 *
 */
public class BPUtils {
	private static final Logger logger = LoggerFactory.getLogger(BPUtils.class); 
	
	private static Map<String, Integer> signalType2IntMap;
	private static Map<String, Boolean> yesOrNo2BooleanMap;
	private static Map<String, Integer> str2PermissionMap;
	
	static {
		signalType2IntMap = new HashMap<>();
		signalType2IntMap.put("", BPPacket.VAL_TYPE_UINT32);
		signalType2IntMap.put("NULL", BPPacket.VAL_TYPE_UINT32);
		signalType2IntMap.put("UINT32", BPPacket.VAL_TYPE_UINT32);
		signalType2IntMap.put("UINT16", BPPacket.VAL_TYPE_UINT16);
		signalType2IntMap.put("INT32", BPPacket.VAL_TYPE_IINT32);
		signalType2IntMap.put("INT16", BPPacket.VAL_TYPE_IINT16);
		signalType2IntMap.put("ENUM", BPPacket.VAL_TYPE_ENUM);
		signalType2IntMap.put("FLOAT", BPPacket.VAL_TYPE_FLOAT);
		signalType2IntMap.put("STRING", BPPacket.VAL_TYPE_STRING);
		signalType2IntMap.put("BOOLEAN", BPPacket.VAL_TYPE_BOOLEAN);
		signalType2IntMap.put("DATE", BPPacket.VAL_TYPE_DATE);
		signalType2IntMap.put("TIME", BPPacket.VAL_TYPE_TIME);
		
		yesOrNo2BooleanMap = new HashMap<>();
		yesOrNo2BooleanMap.put("", false);
		yesOrNo2BooleanMap.put("NULL", false);
		yesOrNo2BooleanMap.put("YES", true);
		yesOrNo2BooleanMap.put("NO", false);
		
		str2PermissionMap = new HashMap<>();
		str2PermissionMap.put("", BPPacket.SIGNAL_PERMISSION_CODE_RW);
		str2PermissionMap.put("NULL", BPPacket.SIGNAL_PERMISSION_CODE_RW);
		str2PermissionMap.put("RO", BPPacket.SIGNAL_PERMISSION_CODE_RO);
		str2PermissionMap.put("RW", BPPacket.SIGNAL_PERMISSION_CODE_RW);
	}
	
	private BPUtils() {
		throw new IllegalStateException("Utility class");
	}
	
    /* assemble the String into IoBuffer
     * @return 0 when success, other for failed*/
	public static int assembleStr(IoBuffer ioBuffer, String s) {
		int ret = 0;
		
		if(null == ioBuffer || null == s) {
			return -1;
		}
		
		byte[] bytes = s.getBytes();
		
		if(bytes.length > BPPacket.MAX_STR_LENGTH) {
			return -2;
		}
		
		ioBuffer.put((byte)bytes.length);
		ioBuffer.put(bytes);
		
		return ret;
	}
	
	public static int bpSignalTypeStr2Int(String s) {
		int ret = BPPacket.VAL_TYPE_INVALID;
		try {
			if(null != s) {
				if(signalType2IntMap.containsKey(s)) {
					ret = signalType2IntMap.get(s);
				}
			} 
		} catch(Exception e) {
			Util.bcLog(e, logger);
			ret = BPPacket.VAL_TYPE_INVALID;
		}
		return ret;
	}
	
	public static boolean bpStr2Boolean(String s) throws BPParseCsvFileException {
		if(null == s) {
			throw new BPParseCsvFileException("Error: boolean str==null");
		}
		if(!yesOrNo2BooleanMap.containsKey(s)) {
			throw new BPParseCsvFileException("Error: boolean str==" + s);
		}
		
		return yesOrNo2BooleanMap.get(s);
	}
	
	public static int bpStr2Permission(String s) {
		int ret = BPPacket.SIGNAL_PERMISSION_INVALID;
		try {
			if(null != s) {
				if(str2PermissionMap.containsKey(s)) {
					ret = str2PermissionMap.get(s);
				}
			} 
		} catch(Exception e) {
			Util.bcLog(e, logger);
			ret = BPPacket.SIGNAL_PERMISSION_INVALID;
		}
		return ret;
	}
	
}
