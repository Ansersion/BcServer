/**
 * 
 */
package other;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author hub
 *
 */
public class Util {
	
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	private static Random random = new Random();
	
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
		return null == val || val.length() == 0 || val.trim().isEmpty() || val.equalsIgnoreCase(BPValue.NULL_VAL);
	}
	
	public static void bcLog(Exception e, Logger logger) {
		Util.logger(logger, Util.ERROR, e);
	}
	
	public static void bcLog(String s) {
		logger.info(s);
	}
	
	public static void bcDump3DepthList(List<List<List<String>>> l) {
		for(int i = 0; i < l.size(); i++) {
			for(int j = 0; j < l.get(i).size(); j++) {
				for(int k = 0; k < l.get(i).get(j).size(); k++) {
					logger.info("{},{},{}: {}", i, j, k, l.get(i).get(j).get(k));
				}
			}
		}
	}
	
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int level = VERBOSE;
    
    public static void logger(Logger logger, int l, Exception e) {
    	if(null == logger || null == e) {
    		return;
    	}
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        switch(l) {
            case VERBOSE:
                logger.trace(str);
                break;
            case DEBUG:
                logger.debug(str);
                break;
            case INFO:
                logger.info(str);
                break;
            case WARN:
                logger.warn(str);
                break;
            case ERROR:
                logger.error(str);
                break;
            default:
                logger.error(str);
            	break;
            	
        }
    }
    
    public static void logger(Logger logger, int l, String str) {
    	if(null == logger || null == str) {
    		return;
    	}
        switch(l) {
        case VERBOSE:
            logger.trace(str);
            break;
        case DEBUG:
            logger.debug(str);
            break;
        case INFO:
            logger.info(str);
            break;
        case WARN:
            logger.warn(str);
            break;
        case ERROR:
            logger.error(str);
            break;
        default:
            logger.error(str);
        	break;
    }
    }
    
    /* parse string to integer
     * @return 0 when error occurred or the integer parsed*/
    public static int parseInt(String s) {
    	int ret = 0;
    	try {
    		ret = Integer.parseInt(s);
    	} catch(Exception e) {
    		ret = 0;
    	}
    	
    	return ret;
    }
    
    public static String generatePassword (int length) {
        // 最终生成的密码
        String password = "";
        for (int i = 0; i < length; i ++) {
            // 随机生成0或1，用来确定是当前使用数字还是字母 (0则输出数字，1则输出字母)
            int charOrNum = random.nextInt(2);
            if (charOrNum == 1) {
                // 随机生成0或1，用来判断是大写字母还是小写字母 (0则输出小写字母，1则输出大写字母)
                int temp = random.nextInt(2) == 1 ? 65 : 97;
                password += (char) (random.nextInt(26) + temp);
            } else {
                // 生成随机数字
                password += random.nextInt(10);
            }
        }
        return password;
    }

}
