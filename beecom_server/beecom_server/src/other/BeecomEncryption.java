package other;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ansersion on 2018/2/14.
 */

public class BeecomEncryption {

	private static final Logger logger = LoggerFactory.getLogger(BeecomEncryption.class); 
    private static BeecomEncryption beecomEncryption = null;

    private BeecomEncryption() {

    }

    public static BeecomEncryption getInstance() {
        if(null == beecomEncryption) {
            beecomEncryption = new BeecomEncryption();
        }
        return beecomEncryption;
    }
    
    public String getSHA256StrJava(String str){

        String encodeStr = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
        	Util.logger(logger, Util.ERROR, e);
        }
        return encodeStr;
    }

    private String byte2Hex(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }
}
