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
	
	public static Object setVal(byte valType, String src, Object dst) {
		try {
			switch (valType) {
			case BPPacket.VAL_TYPE_UINT32:
				if (null == src) {
					dst = Long.valueOf(BPPacket.VAL_U32_UNLIMIT);
				} else {
					dst = Long.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_UINT16:
				if (null == src) {
					dst = Integer.valueOf(BPPacket.VAL_U16_UNLIMIT);
				} else {
					dst = Integer.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_IINT32:
				if (null == src) {
					dst = Integer.valueOf(BPPacket.VAL_I32_UNLIMIT);
				} else {
					dst = Integer.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_IINT16:
				if (null == src) {
					dst = Short.valueOf(BPPacket.VAL_I16_UNLIMIT);
				} else {
					dst = Short.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_ENUM:
				if (null == src) {
					dst = Integer.valueOf(BPPacket.VAL_ENUM_UNLIMIT);
				} else {
					dst = Integer.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_FLOAT:
				if (null == src) {
					dst = Float.valueOf(BPPacket.VAL_FLOAT_UNLIMIT);
				} else {
					dst = Float.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_STRING:
				if (null == src) {
					dst = BPPacket.VAL_STR_UNLIMIT;
				} else {
					dst = String.valueOf(src);
				}
				break;
			case BPPacket.VAL_TYPE_BOOLEAN:
				if (null == src) {
					dst = Boolean.valueOf(BPPacket.VAL_BOOLEAN_UNLIMIT);
				} else {
					dst = Boolean.valueOf(src);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			dst = null;
		}
		
		return dst;
	}
	
	public static Object setVal(byte valType, String src) {
		return setVal(valType, src, null);
	}

}
