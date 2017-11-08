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
	
	int ValType = -1;
	
	
	public BPValue(int val_type) {
		ValType = val_type;
		switch(ValType) {
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
	
	public void setValueType(int val_type) {
		u32Val = null;
		u16Val = null;
		i32Val = null;
		i16Val = null;
		enmVal = null;
		fltVal = null;
		strVal = null;
		ValType = val_type;
		
		switch(ValType) {
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
	
	/*
	public void setVal(int val) {
		switch(ValType) {
		case 0:
			u32Val = new Long(0);
			break;
		case 1:
			u16Val = val;
			break;
		case 2:
			i32Val = val;
			break;
		case 3:
			i16Val = (short)val;
			break;
		case 4:
			enmVal = new Long(0);
			break;
		default:
			System.out.println("Error: setVal(int):" + ValType + ":" + val);
			break;
		}
	}
	
	public void setFloat(float val) {
		switch(ValType) {
		case 5:
			fltVal = val;
			break;
		default:
			System.out.println("Error: setVal(float):" + ValType + ":" + val);
			break;
		}
	}
	
	public void setString(String val) {
		switch(ValType) {
		case 6:
			strVal = val;
			break;
		default:
			System.out.println("Error: setVal(String):" + ValType + ":" + val);
			break;
		}
	}
	*/
	
	public void setVal(String val) {
		switch(ValType) {
		case 0:
			u32Val = Long.parseLong(val);
			break;
		case 1:
			u16Val = Integer.parseInt(val);
			break;
		case 2:
			i32Val = Integer.parseInt(val);
			break;
		case 3:
			i16Val = Short.parseShort(val);
			break;
		case 4:
			enmVal = Long.parseLong(val);
			break;
		case 5:
			fltVal = Float.parseFloat(val);
			break;
		case 6:
			strVal = val;
			break;
		default:
			System.out.println("Error: setVal(String):" + ValType + ":" + val);
			break;
		}
	}
	
	public String getVal() {
		String ret = new String("");
		switch(ValType) {
		case 0:
			ret = u32Val.toString();
			break;
		case 1:
			ret = u16Val.toString();
			break;
		case 2:
			ret = i32Val.toString();
			break;
		case 3:
			ret = i16Val.toString();
			break;
		case 4:
			ret = enmVal.toString();
			break;
		case 5:
			ret = fltVal.toString();
			break;
		case 6:
			ret = strVal.toString();
			break;
		default:
			System.out.println("Error: getVal():" + ValType);
			break;
		}
		
		return ret;
	}
	
}
