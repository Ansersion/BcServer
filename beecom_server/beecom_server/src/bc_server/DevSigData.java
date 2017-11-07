/**
 * 
 */
package bc_server;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ansersion
 *
 */


class SigDatas {

	Map<Integer, Byte> Map1ByteDataSig;
	Map<Integer, Short> Map2ByteDataSig;
	Map<Integer, Integer> Map4ByteDataSig;
	
	SigDatas() {
		Map1ByteDataSig = new HashMap<Integer, Byte>();
		Map2ByteDataSig = new HashMap<Integer, Short>();
		Map4ByteDataSig = new HashMap<Integer, Integer>();
	}
	
	public void addNoCusSig(short sig) {
		
	}

}

public class DevSigData {
	
	short DeviceID;
	SigDatas SigData;
	
	DevSigData() {
		DeviceID = 0;
		// SigData = new SigData();
	}
	
	public Map<Integer, Byte> get1ByteDataMap() {
		return SigData.Map1ByteDataSig;
	}
	
	public Map<Integer, Short> get2ByteDataMap() {
		return SigData.Map2ByteDataSig;
	}
	
	public Map<Integer, Integer> get4ByteDataMap() {
		return SigData.Map4ByteDataSig;
	}
	
	public int parseSigData(byte[] buf, int offset) {
		int offset_old = offset;
		byte encoded_byte;
		
		byte msb = buf[offset++];
		byte lsb = buf[offset++];
		DeviceID = BPPacket.assemble2ByteBigend(msb, lsb);
		
		encoded_byte = buf[offset++];
		int value_type = (encoded_byte & 0xC0)  >>> 6;
		int value_num = (encoded_byte & 0x3F);
		short sig_id;
		
		if(0x0 == value_type) {
			Map data_map = get1ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				byte data = buf[offset++];
				data_map.put(sig_id, data);
			}
		} else if(0x01 == value_type) {
			Map data_map = get1ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				int data = buf[offset++];
				data = (data << 8) + buf[offset++];
				data_map.put(sig_id, (short)data);
			}
		} else if(0x10 == value_type) {
			Map data_map = get1ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				long data = buf[offset++];
				data = (data << 8) + buf[offset++];
				data = (data << 8) + buf[offset++];
				data = (data << 8) + buf[offset++];
				data_map.put(sig_id, (int)data);
			}
		} else {
			System.out.println("Error: DevSigData: Unsupported value type");
		}
		
		return offset - offset_old;
	}
}
