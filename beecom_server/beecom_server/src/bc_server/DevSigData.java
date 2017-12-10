/**
 * 
 */
package bc_server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author Ansersion
 *
 */


class SigDatas {

	Map<Integer, Byte> Map1ByteDataSig;
	Map<Integer, Short> Map2ByteDataSig;
	Map<Integer, Integer> Map4ByteDataSig;
	Map<Integer, Byte[]> MapxByteDataSig;
	
	SigDatas() {
		Map1ByteDataSig = new HashMap<Integer, Byte>();
		Map2ByteDataSig = new HashMap<Integer, Short>();
		Map4ByteDataSig = new HashMap<Integer, Integer>();
		MapxByteDataSig = new HashMap<Integer, Byte[]>();
	}
	
	public void addNoCusSig(short sig) {
		
	}

}

public class DevSigData {
	
	int DeviceID;
	SigDatas SigData;
	
	DevSigData() {
		DeviceID = 0;
		SigData = new SigDatas();
		// SigData = new SigData();
	}
	
	public void setDevID(int dev_id) {
		DeviceID = dev_id;
	}
	
	public int getDevID() {
		return DeviceID;
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
	
	public Map<Integer, Byte[]> getxByteDataMap() {
		return SigData.MapxByteDataSig;
	}
	
	public void dump() {
		Map map;
		Iterator entries;
		
		map = get1ByteDataMap();
		entries = map.entrySet().iterator();  
		while (entries.hasNext()) {  
		    Map.Entry<Integer, Byte> entry = (Map.Entry<Integer, Byte>)entries.next();  
		    System.out.println(entry.getKey() + "=>" + Integer.toHexString(entry.getValue()));  
		}
		
		map = get2ByteDataMap();
		entries = map.entrySet().iterator();  
		while (entries.hasNext()) {  
		    Map.Entry<Integer, Short> entry = (Map.Entry<Integer, Short>)entries.next();  
		    System.out.println(entry.getKey() + "=>" + Integer.toHexString(entry.getValue()));  
		}
		
		map = get4ByteDataMap();
		entries = map.entrySet().iterator();  
		while (entries.hasNext()) {  
		    Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>)entries.next();  
		    System.out.println(entry.getKey() + "=>" + Integer.toHexString(entry.getValue()));  
		}
		
		map = getxByteDataMap();
		entries = map.entrySet().iterator();  
		while (entries.hasNext()) {  
		    Map.Entry<Integer, Byte[]> entry = (Map.Entry<Integer, Byte[]>)entries.next();  
		    System.out.println(entry.getKey() + "=>" + entry.getValue());  
		}
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
	
	public boolean parseSigDataTab(IoBuffer io_buf) {
		byte encoded_byte;
		
		encoded_byte = io_buf.get();
		int value_type = (encoded_byte & 0xC0)  >>> 6;
		int value_num = (encoded_byte & 0x3F);
		Map data_map;
		switch(value_type) {
		case 0:
			data_map = get1ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				byte val = io_buf.get();
				data_map.put(sig_id, val);
			}
			break;
		case 1:
			data_map = get2ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				short val = io_buf.getShort();
				data_map.put(sig_id, val);
			}
			break;
		case 2:
			data_map = get4ByteDataMap();
			for(int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				int val = io_buf.getInt();
				data_map.put(sig_id, val);
			}
			break;
		case 3:
			data_map = getxByteDataMap();
			for(int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				byte len = io_buf.get();
				Byte[] val = new Byte[len];
				for(int j = 0; j < len; j++) {
					val[j] = io_buf.get();
				}
				data_map.put(sig_id, val);
			}
			break;
		default:
			System.out.println("Error: DevSigData: Unsupported value type");
			return false;
		}
		return true;
	}
}
