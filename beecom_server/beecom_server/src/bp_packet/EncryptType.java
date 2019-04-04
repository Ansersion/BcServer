package bp_packet;

/**
 * @author Ansersion
 *
 */

public class EncryptType {
	
	public enum EnType {
		PLAIN,
		BASE64,
		EN_2,
		EN_3
	}
	
    /**
     * Represents the encryption type of no encryption
     */
    public static final EncryptType NO_ENCRYPTION = new EncryptType("no encryptioin", 0);

    private final String strValue;
    private final int iType;

    /**
     * Creates a new instance.
     */
    private EncryptType(String strValue, int type) {
        this.strValue = strValue;
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
        return strValue;
    }
    
    public int getType() {
    	return iType;
    }
}
