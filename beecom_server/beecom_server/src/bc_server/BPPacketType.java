/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPPacketType {
    /**
     * Represents the BP packet type
     */
	public static final BPPacketType INVALID = new BPPacketType("NONE", -1);
    public static final BPPacketType CONNECT = new BPPacketType("CONNECT", 1);
    public static final BPPacketType CONNACK = new BPPacketType("CONNACK", 2);
    public static final BPPacketType GET = new BPPacketType("GET", 3);
    public static final BPPacketType GETACK = new BPPacketType("GETACK", 4);
    public static final BPPacketType POST = new BPPacketType("POST", 5);
    public static final BPPacketType POSTACK = new BPPacketType("POSTACK", 6);
    public static final BPPacketType REPORT = new BPPacketType("REPORT", 7);
    public static final BPPacketType RPRTACK = new BPPacketType("RPRTACK", 8);
    public static final BPPacketType PINGREQ = new BPPacketType("PINGREQ", 9);
    public static final BPPacketType PINGACK = new BPPacketType("PINGACK", 10);
    public static final BPPacketType PUSH = new BPPacketType("PUSH", 11);
    public static final BPPacketType PUSHACK = new BPPacketType("PUSHACK", 12);
    public static final BPPacketType DISCONN = new BPPacketType("DISCONN", 13);
    
    private final String strName;
    private final int iType;

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
    
    public int GetType() {
    	return iType;
    }

}
