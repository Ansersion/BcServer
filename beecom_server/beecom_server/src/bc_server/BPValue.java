/**
 * 
 */
package bc_server;

/**
 * @author Ansersion
 *
 */
public class BPValue {
	Long u32Val = null;
	Integer u16Val = null;
	Integer i32Val = null;
	Short i16Val = null;
	Long enmVal = null;
	Float fltVal = null;
	String strVal = null;
	
	
	public BPValue(int val_type) {
		switch(val_type) {
		case 0:
			u32Val = new Long(0);
			break;
		case 1:
			u16Val = new Integer(0);
			break;
		case 2:
			i32Val = new Integer(0);
			break;
		case 3:
			i16Val = new Short((short)0);
			break;
		case 4:
			enmVal = new Long(0);
			break;
		case 5:
			fltVal = new Float(0);
			break;
		case 6:
			strVal = new String("");
			break;
		default:
			// System.out.println("Error: unsupported BPValue");
			// NULL value
			break;
		}
		
	}
	
	public BPValue() {
		// NULL value
	}
	
	
}
