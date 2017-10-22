/**
 * 
 */
package bc_server;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 * 
 */
public class BPPackFactory {
	public static BPPacket createBPPack(FixedHeader fx_header) {
		
		BPPacket ret;
		
		if(fx_header.getPacketType() == BPPacketType.CONNECT) {
			ret = new BPPacket_CONNECT(fx_header);
		} else if(fx_header.getPacketType() == BPPacketType.CONNACK) {
			ret = new BPPacket_CONNACK(fx_header);
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public static BPPacket createBPPack(byte first_byte) {
		
		BPPacket ret;
		byte type = BPPacketType.DISCONN.getTypeByte();
		
		if(((first_byte >> 4) & 0x0F) == BPPacketType.CONNECT.getType()) {
			ret = new BPPacket_CONNECT();
		} else if(((first_byte >> 4) & 0x0F) == BPPacketType.CONNACK.getType()) {
			ret = new BPPacket_CONNACK();
		} else if(((first_byte >> 4) & 0x0F) == BPPacketType.GET.getType()) { 
			ret = new BPPacket_GET();
		} else if(((first_byte >> 4) & 0x0F) == BPPacketType.GETACK.getType()) { 
			ret = new BPPacket_GETACK();
		} else if (((first_byte >> 4) & 0x0F) == BPPacketType.PING.getType()){
			ret = new BPPacket_PING();
		} else if(((first_byte >> 4) & 0x0F) == BPPacketType.PINGACK.getType()) {
			ret = new BPPacket_PINGACK();
		} else if(((first_byte >> 4)& 0x0F) == BPPacketType.DISCONN.getType()) {
			ret = new BPPacket_DISCONN();
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public static BPPacket createBPPack(IoBuffer io) {
		
		BPPacket ret;
		byte b = io.get();
		ret = createBPPack(b);
		FixedHeader fxHead = ret.getFxHead();
		fxHead.setBPType(b);
		fxHead.setFlags(b);
		// fxHead.setCrcChk(b);
		// fxHead.getEncryptType();
		try {
			fxHead.setRemainLen(io);
		} catch(Exception e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}
	
	
	public static BPPacket createBPPackAck(BPPacket pack_req) {
		
		BPPacket ret;
		
		BPPacketType pack_req_type = pack_req.getPackTypeFxHead();
		
		if(BPPacketType.CONNECT == pack_req_type) {
			ret = new BPPacket_CONNACK();
		} else if(BPPacketType.PING == pack_req_type) {
			ret = new BPPacket_PINGACK();
		} else {
			ret = null;
		}
		
		return ret;
	}
}
