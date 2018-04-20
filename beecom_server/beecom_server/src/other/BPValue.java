/**
 * 
 */
package other;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

	int valType = -1;
	boolean limitValid;

	public BPValue(int valType) {
		this.valType = valType;
		limitValid = true;
		switch(valType) {
		case 0:
			u32Val = 0;
			break;
		case 1:
			u16Val = (short) 0;
			break;
		case 2:
			i32Val = 0;
			break;
		case 3:
			i16Val = (short) 0;
			break;
		case 4:
			enmVal = (short) 0;
			break;
		case 5:
			fltVal = (float)0;
			break;
		case 6:
			strVal = "";
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
		return valType;
	}

	public void setValStr(String val) {
		try {
			switch (valType) {
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
				logger.error("Error: setVal(String):{}:{}", valType, val);
				break;
			}
		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
		}
	}

	public String getValStr() {
		String ret = "NULL";
		
		switch (valType) {
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
		if(0 == valType && null != u32Val) {
			u32Val = val;
		} else if(2 == valType && null != i32Val) {
			i32Val = val;
		}
	}
	
	public void setVal(Short val) {
		if(1 == valType && null != u16Val) {
			u16Val = val;
		} else if(3 == valType && null != i16Val) {
			i16Val = val;
		} else if(4 == valType && null != enmVal) {
			enmVal = val;
		}
	}
	
	public void setVal(Float val) {
		if(5 == valType && null != fltVal) {
			fltVal = val;
		}
	}
	
	public void setVal(String val) {
		if(6 == valType && null != strVal) {
			strVal = val;
		}
	}

}
