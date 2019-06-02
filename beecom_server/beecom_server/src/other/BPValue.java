/**
 * 
 */
package other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPacket;


/**
 * @author Ansersion
 * 
 */
public class BPValue {
	private static final Logger logger = LoggerFactory.getLogger(BPValue.class);
	public static final String NULL_VAL = "NULL";
	
	private BPValue() {}
	
	public static Object getVal(byte valType, String str) {
		Object ret = null;
		try {
			switch (valType) {
			case BPPacket.VAL_TYPE_UINT32:
				if (null == str) {
					ret = Long.valueOf(BPPacket.VAL_U32_UNLIMIT);
				} else {
					ret = Long.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_UINT16:
				if (null == str) {
					ret = Integer.valueOf(BPPacket.VAL_U16_UNLIMIT);
				} else {
					ret = Integer.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_IINT32:
				if (null == str) {
					ret = Integer.valueOf(BPPacket.VAL_I32_UNLIMIT);
				} else {
					ret = Integer.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_IINT16:
				if (null == str) {
					ret = Short.valueOf(BPPacket.VAL_I16_UNLIMIT);
				} else {
					ret = Short.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_ENUM:
				if (null == str) {
					ret = Integer.valueOf(BPPacket.VAL_ENUM_UNLIMIT);
				} else {
					ret = Integer.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_FLOAT:
				if (null == str) {
					ret = Float.valueOf(BPPacket.VAL_FLOAT_UNLIMIT);
				} else {
					ret = Float.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_STRING:
				if (null == str) {
					ret = BPPacket.VAL_STR_UNLIMIT;
				} else {
					ret = String.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_BOOLEAN:
				if (null == str) {
					ret = Boolean.valueOf(BPPacket.VAL_BOOLEAN_UNLIMIT);
				} else {
					ret = Boolean.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_DATE:
				if (null == str) {
					ret = BPPacket.VAL_DATE_UNLIMIT;
				} else {
					ret = Integer.valueOf(str);
				}
				break;
			case BPPacket.VAL_TYPE_TIME:
				if (null == str) {
					ret = BPPacket.VAL_TIME_UNLIMIT;
				} else {
					ret = Integer.valueOf(str);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			ret = null;
		}
		
		return ret;
	}

}
