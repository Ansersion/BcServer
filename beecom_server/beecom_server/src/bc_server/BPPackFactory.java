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
		
		if((first_byte & 0xF0) == BPPacketType.CONNECT.getTypeByte()) {
			ret = new BPPacket_CONNECT();
		} else if((first_byte & 0xF0) == BPPacketType.CONNACK.getTypeByte()) {
			ret = new BPPacket_CONNACK();
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
		
		if(pack_req_type == BPPacketType.CONNECT) {
			ret = new BPPacket_CONNACK();
		} else {
			ret = null;
		}
		
		return ret;
	}
}
