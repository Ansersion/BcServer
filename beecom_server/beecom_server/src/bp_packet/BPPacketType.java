/**
 * 
 */
package bp_packet;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Ansersion
 *
 */
public class BPPacketType {
    /**
     * Represents the BP packet type
     */
	public static final BPPacketType INVALID = new BPPacketType("INVALID", -1);
	
    public static final BPPacketType CONNECT = new BPPacketType("CONNECT", 1);
    public static final BPPacketType CONNACK = new BPPacketType("CONNACK", 2);
    public static final BPPacketType GET = new BPPacketType("GET", 3);
    public static final BPPacketType GETACK = new BPPacketType("GETACK", 4);
    public static final BPPacketType POST = new BPPacketType("POST", 5);
    public static final BPPacketType POSTACK = new BPPacketType("POSTACK", 6);
    public static final BPPacketType REPORT = new BPPacketType("REPORT", 7);
    public static final BPPacketType RPRTACK = new BPPacketType("RPRTACK", 8);
    public static final BPPacketType PING = new BPPacketType("PING", 9);
    public static final BPPacketType PINGACK = new BPPacketType("PINGACK", 10);
    public static final BPPacketType PUSH = new BPPacketType("PUSH", 11);
    public static final BPPacketType PUSHACK = new BPPacketType("PUSHACK", 12);
    public static final BPPacketType DISCONN = new BPPacketType("DISCONN", 13);
    
	public static final BPPacketType INVALID_14 = INVALID;
	public static final BPPacketType INVALID_15 = INVALID;
	
	public static final byte CONNECT_BYTE = 0x10;
    
    private static Map<Integer, BPPacketType> bpMap;
    static {
    	bpMap = new HashMap<>();
    	bpMap.put(0, INVALID);
    	
    	bpMap.put(1, CONNECT);
    	bpMap.put(2, CONNACK);
    	bpMap.put(3, GET);
    	bpMap.put(4, GETACK);
    	bpMap.put(5, POST);
    	bpMap.put(6, POSTACK);
    	bpMap.put(7, REPORT);
    	bpMap.put(8, RPRTACK);
    	bpMap.put(9, PING);
    	bpMap.put(10, PINGACK);
    	bpMap.put(11, PUSH);
    	bpMap.put(12, PUSHACK);
    	bpMap.put(13, DISCONN);
    	
    	bpMap.put(14, INVALID);
    	bpMap.put(15, INVALID);
    }
    
    private final String strName;
    private final int iType;
    
	public static final byte BPPACK_TYPE_MASK = 0x0F;
	public static final int BPPACK_TYPE_BIT_OFFSET = 4;

    /**
     * Creates a new instance.
     */
    private BPPacketType(String name, int type) {
        this.strName = name;
        this.iType = type;
    }

    /**
     * @return the string representation of this encrpytion type
     * <ul>
     *   <li>{@link #NO_ENCRYPTION} - <tt>"no encryptioin"</tt></li>
     * </ul>
     */
    @Override
    public String toString() {
        return strName;
    }
    
    public int getType() {
    	return iType;
    } 
    
    public byte getTypeByte() {
    	return (byte)((iType & BPPACK_TYPE_MASK) << BPPACK_TYPE_BIT_OFFSET);
    }
    
    public static BPPacketType getType(byte encodedByte) {
    	int index = (encodedByte >>> BPPACK_TYPE_BIT_OFFSET) & BPPACK_TYPE_MASK;
    	if(index >= bpMap.size()) {
    		return INVALID;
    	}
    	return bpMap.get(index);
    	
    }

}
