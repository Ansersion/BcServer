/**
 * 
 */
package other;

/**
 * @author hub
 *
 */
public class Util {
	
	private Util() {
		
	}
	
	public static Integer toUnsigned(short n) {
		return n & 0xFFFF;
	}
	
	public static Long toUnsigned(int n) {
		return (long)(n & 0xFFFFFFFF);
	}
	
	public static Integer toSigned(long n) {
		return (int)(n & 0xFFFFFFFF);
	}
	
	public static Short toSigned(int n) {
		return (short)(n & 0xFFFF);
	}
	
	public static boolean isNull(String val) {
		return null == val || val.length() == 0 || val.trim().isEmpty() || val.equals(BPValue.NULL_VAL);
	}

}
