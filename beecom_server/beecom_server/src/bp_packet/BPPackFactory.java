/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ansersion
 * 
 */
public class BPPackFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPackFactory.class); 
	
	private BPPackFactory() {
		
	}
	
	public static BPPacket createBPPack(FixedHeader fxHeader) {
		
		BPPacket ret;
		
		if(fxHeader.getPacketType() == BPPacketType.CONNECT) {
			ret = new BPPacketCONNECT(fxHeader);
		} else if(fxHeader.getPacketType() == BPPacketType.CONNACK) {
			ret = new BPPacketCONNACK(fxHeader);
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public static BPPacket createBPPack(byte firstByte) {
		
		BPPacket ret;
		
		if(((firstByte >> 4) & 0x0F) == BPPacketType.CONNECT.getType()) {
			ret = new BPPacketCONNECT();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.CONNACK.getType()) {
			ret = new BPPacketCONNACK();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.GET.getType()) { 
			ret = new BPPacketGET();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.GETACK.getType()) { 
			ret = new BPPacketGETACK();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.POST.getType()) { 
			ret = new BPPacketPOST();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.POSTACK.getType()) {
			ret = new BPPacketPOSTACK();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.REPORT.getType()) { 
			ret = new BPPacketREPORT();
		} else if (((firstByte >> 4) & 0x0F) == BPPacketType.PING.getType()){
			ret = new BPPacketPING();
		} else if(((firstByte >> 4) & 0x0F) == BPPacketType.PINGACK.getType()) {
			ret = new BPPacketPINGACK();
		} else if(((firstByte >> 4)& 0x0F) == BPPacketType.DISCONN.getType()) {
			ret = new BPPacketDISCONN();
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public static BPPacket createBPPack(IoBuffer io) {
		
		BPPacket ret;

		try {
			byte b = io.get();
			ret = createBPPack(b);
			if(null != ret) {
				FixedHeader fxHead = ret.getFxHead();
				fxHead.setBPType(b);
				fxHead.setFlags(b);
				fxHead.setRemainLen(io);
			}
		} catch(Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			ret = null;
		}
		return ret;
	}
	
	
	public static BPPacket createBPPackAck(BPPacket packReq) {
		
		BPPacket ret;
		
		BPPacketType packReqType = packReq.getPackTypeFxHead();
		
		if(BPPacketType.CONNECT == packReqType) {
			ret = new BPPacketCONNACK();
		} else if(BPPacketType.GET == packReqType) {
			ret = new BPPacketGETACK();
		} else if(BPPacketType.POST == packReqType) {
			ret = new BPPacketPOSTACK();
		} else if(BPPacketType.REPORT == packReqType) {
			ret = new BPPacketRPRTACK();
		} else if(BPPacketType.PING == packReqType) {
			ret = new BPPacketPINGACK();
		} else if(BPPacketType.PUSH == packReqType) {
			ret = new BPPacketPUSHACK();
		} else {
			ret = null;
		}
		if(ret != null) {
			ret.getVrbHead().setPackSeq(packReq.getVrbHead().getPackSeq());
		}
		
		return ret;
	}
	
	public static BPPacket createBPPack(BPPacketType type) {
		
		BPPacket ret;
		
		if(BPPacketType.CONNECT == type) {
			ret = new BPPacketCONNECT();
		} else if(BPPacketType.CONNACK == type) {
			ret = new BPPacketCONNACK();
		} else if(BPPacketType.GET == type) {
			ret = new BPPacketGET();
		} else if(BPPacketType.GETACK == type) {
			ret = new BPPacketGETACK();
		} else if(BPPacketType.POST == type) {
			ret = new BPPacketPOST();
		} else if(BPPacketType.POSTACK == type) {
			ret = new BPPacketPOSTACK();
		} else if(BPPacketType.REPORT == type) {
			ret = new BPPacketREPORT();
		} else if(BPPacketType.RPRTACK == type) {
			ret = new BPPacketRPRTACK();
		} else if(BPPacketType.PING == type) {
			ret = new BPPacketPING();
		} else if(BPPacketType.PINGACK == type) {
			ret = new BPPacketPINGACK();
		} else if(BPPacketType.PUSH == type) {
			ret = new BPPacketPUSH();
		} else if(BPPacketType.PUSHACK == type) {
			ret = new BPPacketPUSHACK();
		} else if(BPPacketType.DISCONN == type) {
			ret = new BPPacketDISCONN();
		} else {
			ret = null;
		}
		
		return ret;
	}
}
