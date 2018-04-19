/**
 * 
 */
package other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPSession;

/**
 * @author Ansersion
 * 
 */
public class BPValue {
	private static final Logger logger = LoggerFactory.getLogger(BPValue.class);
	
	
	public static final String NULL_VAL = "NULL";
	Integer u32Val = null;
	Short u16Val = null;
	Integer i32Val = null;
	Short i16Val = null;
	Short enmVal = null;
	Float fltVal = null;
	String strVal = null;

	int ValType = -1;
	boolean limitValid;

	public BPValue(int val_type) {
		ValType = val_type;
		limitValid = true;
		switch (ValType) {
		case 0:
			u32Val = new Integer(0);
			break;
		case 1:
			u16Val = new Short((short) 0);
			break;
		case 2:
			i32Val = new Integer(0);
			break;
		case 3:
			i16Val = new Short((short) 0);
			break;
		case 4:
			enmVal = new Short((short) 0);
			break;
		case 5:
			fltVal = new Float(0);
			break;
		case 6:
			strVal = new String("");
			break;
		default:
			// NULL value
			break;
		}

	}
	
	public void setLimitValid(boolean valid) {
		limitValid = valid;
	}
	
	public int getType() {
		return ValType;
	}

	public void setValStr(String val) {
		try {
			switch (ValType) {
			case 0:
				u32Val = Util.toSigned(Long.parseLong(val));
				break;
			case 1:
				u16Val = Util.toSigned(Integer.parseInt(val));
				break;
			case 2:
				i32Val = Integer.parseInt(val);
				break;
			case 3:
				i16Val = Short.parseShort(val);
				break;
			case 4:
				enmVal = Util.toSigned(Integer.parseInt(val));
				break;
			case 5:
				fltVal = Float.parseFloat(val);
				break;
			case 6:
				strVal = val;
				break;
			default:
				logger.error("Error: setVal(String):" + ValType + ":"
						+ val);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getValStr() {
		String ret = new String("NULL");
		
		switch (ValType) {
		case 0:
			ret = Util.toUnsigned(u32Val).toString();
			break;
		case 1:
			ret = Util.toUnsigned(u16Val).toString();
			break;
		case 2:
			ret = i32Val.toString();
			break;
		case 3:
			ret = i16Val.toString();
			break;
		case 4:
			ret = Util.toUnsigned(enmVal).toString();
			break;
		case 5:
			ret = fltVal.toString();
			break;
		case 6:
			ret = strVal;
			break;
		default:
			break;
		}

		return ret;
	}
	
	public void setVal(Integer val) {
		if(0 == ValType && null != u32Val) {
			u32Val = val;
		} else if(2 == ValType && null != i32Val) {
			i32Val = val;
		}
	}
	
	public void setVal(Short val) {
		if(1 == ValType && null != u16Val) {
			u16Val = val;
		} else if(3 == ValType && null != i16Val) {
			i16Val = val;
		} else if(4 == ValType && null != enmVal) {
			enmVal = val;
		}
	}
	
	public void setVal(Float val) {
		if(5 == ValType && null != fltVal) {
			fltVal = val;
		}
	}
	
	public void setVal(String val) {
		if(6 == ValType && null != strVal) {
			strVal = val;
		}
	}
	
	public void getVal(Integer val) {
		if(0 == ValType && null != u32Val) {
			val = u32Val;
		} else if(2 == ValType && null != i32Val) {
			val = i32Val;
		}
	}
	
	public void getVal(Short val) {
		if(1 == ValType && null != u16Val) {
			val = u16Val;
		} else if(3 == ValType && null != i16Val) {
			val = i16Val;
		} else if(4 == ValType && null != enmVal) {
			val = enmVal;
		}
	}
	
	public void getVal(Float val) {
		if(5 == ValType && null != fltVal) {
			val = fltVal;
		}
	}
	
	public void getVal(String val) {
		if(6 == ValType && null != strVal) {
			val = strVal;
		}
	}

}
