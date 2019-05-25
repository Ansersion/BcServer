/**
 * 
 */
package bp_packet;


import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;


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
        } else if (((firstByte >> 4) & 0x0F) == BPPacketType.PUSH.getType()){
            ret = new BPPacketPUSH();
        } else if(((firstByte >> 4) & 0x0F) == BPPacketType.PUSHACK.getType()) {
            ret = new BPPacketPUSHACK();
		} else if(((firstByte >> 4)& 0x0F) == BPPacketType.DISCONN.getType()) {
			ret = new BPPacketDISCONN();
		} else if(((firstByte >> 4)& 0x0F) == BPPacketType.SPECSET.getType()) {
			ret = new BPPacketSPECSET();
		} else if(((firstByte >> 4)& 0x0F) == BPPacketType.SPECACK.getType()) {
			ret = new BPPacketSPECACK();
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
				ret.putFxHead2Buf();
			}
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
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
		} else if(BPPacketType.SPECSET == packReqType) {
			ret = new BPPacketSPECACK();
		} else {
			ret = null;
		}
		if(ret != null) {
			ret.getVrbHead().setPackSeq(packReq.getVrbHead().getPackSeq());
			ret.getVrbHead().parseFlags(packReq.getVrbHead().getFlags());
			ret.getVrbHead().setLangFlags(packReq.getVrbHead().getLangFlags());
		}
		
		return ret;
	}
	
	public static boolean packetNeedAck(BPPacket pack) {
		boolean ret = false;
		try {
			if(null == pack) {
				return ret;
			}
			BPPacketType packReqType = pack.getPackTypeFxHead();

			if (BPPacketType.CONNECT == packReqType) {
				ret = true;
			} else if (BPPacketType.GET == packReqType) {
				ret = true;
			} else if (BPPacketType.POST == packReqType) {
				ret = true;
			} else if (BPPacketType.REPORT == packReqType) {
				ret = true;
			} else if (BPPacketType.PING == packReqType) {
				ret = true;
			} else if (BPPacketType.PUSH == packReqType) {
				ret = true;
			} else if (BPPacketType.SPECSET == packReqType) {
				ret = true;
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
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
		} else if(BPPacketType.SPECSET == type) {
			ret = new BPPacketSPECSET();
		} else if(BPPacketType.SPECACK == type) {
			ret = new BPPacketSPECACK();
		} else {
			ret = null;
		}
		
		return ret;
	}
}
