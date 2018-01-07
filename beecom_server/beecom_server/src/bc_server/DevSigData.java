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
	
	/*
	 Map<Integer, Long> MapData2U32;
	 Map<Integer, Integer> MapData2I32;
	 Map<Integer, > MapData2U16;
	 HashMap<Integer, Short> MapData2I16;
	 Map<Integer, Short> MapData2Enm;
	 Map<Integer, Float> MapData2Flt;
	 Map<Integer, String> MapData2Str;
	*/


	SigDatas() {
		Map1ByteDataSig = new HashMap<Integer, Byte>();
		Map2ByteDataSig = new HashMap<Integer, Short>();
		Map4ByteDataSig = new HashMap<Integer, Integer>();
		MapxByteDataSig = new HashMap<Integer, Byte[]>();
		
		/*
		 MapData2U32 = new HashMap<Integer, Integer>();
		 MapData2I32 = new HashMap<Integer, Integer>();
		 MapData2U16 = new HashMap<Integer, Short>();
		 MapData2I16 = new HashMap<Integer, Short>();
		 MapData2Enm = new HashMap<Integer, Short>();
		 MapData2Flt = new HashMap<Integer, Float>();
		 MapData2Str = new HashMap<Integer, String>();
		 */
	}

	public void addNoCusSig(short sig) {

	}
	
	public void clear() {
		Map1ByteDataSig.clear();
		Map2ByteDataSig.clear();
		Map4ByteDataSig.clear();
		MapxByteDataSig.clear();
		
		/*
		MapData2U32.clear();
		MapData2I32.clear();
		MapData2U16.clear();
		MapData2I16.clear();
		MapData2Enm.clear();
		MapData2Flt.clear();
		MapData2Str.clear();
		*/
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
	
	public void clear() {
		DeviceID = 0;
		SigData.clear();
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
	
	/*
	public Map<Integer, Integer> getU32DataMap() {
		return SigData.MapData2U32;
	}

	public Map<Integer, Short> getI32DataMap() {
		return SigData.Map2ByteDataSig;
	}

	public Map<Integer, Integer> getU16DataMap() {
		return SigData.Map4ByteDataSig;
	}

	public Map<Integer, Byte[]> getI16DataMap() {
		return SigData.MapxByteDataSig;
	}

	public Map<Integer, Byte[]> getEnmDataMap() {
		return SigData.MapxByteDataSig;
	}

	public Map<Integer, Byte[]> getFltDataMap() {
		return SigData.MapxByteDataSig;
	}

	public Map<Integer, Byte[]> getStrDataMap() {
		return SigData.MapxByteDataSig;
	}
*/
	public void dump() {
		Map map;
		Iterator entries;

		map = get1ByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte> entry = (Map.Entry<Integer, Byte>) entries
					.next();
			System.out.println(entry.getKey() + "=>"
					+ Integer.toHexString(entry.getValue()));
		}

		map = get2ByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Short> entry = (Map.Entry<Integer, Short>) entries
					.next();
			System.out.println(entry.getKey() + "=>"
					+ Integer.toHexString(entry.getValue()));
		}

		map = get4ByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) entries
					.next();
			System.out.println(entry.getKey() + "=>"
					+ Integer.toHexString(entry.getValue()));
		}

		map = getxByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte[]> entry = (Map.Entry<Integer, Byte[]>) entries
					.next();
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
		int value_type = (encoded_byte & 0xC0) >>> 6;
		int value_num = (encoded_byte & 0x3F);
		short sig_id;

		if (0x0 == value_type) {
			Map data_map = get1ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				byte data = buf[offset++];
				data_map.put(sig_id, data);
			}
		} else if (0x01 == value_type) {
			Map data_map = get1ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				int data = buf[offset++];
				data = (data << 8) + buf[offset++];
				data_map.put(sig_id, (short) data);
			}
		} else if (0x10 == value_type) {
			Map data_map = get1ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sig_id = BPPacket.assemble2ByteBigend(msb, lsb);
				long data = buf[offset++];
				data = (data << 8) + buf[offset++];
				data = (data << 8) + buf[offset++];
				data = (data << 8) + buf[offset++];
				data_map.put(sig_id, (int) data);
			}
		} else {
			System.out.println("Error: DevSigData: Unsupported value type");
		}

		return offset - offset_old;
	}

	public boolean parseSigDataTab(IoBuffer io_buf) {
		byte encoded_byte;

		encoded_byte = io_buf.get();
		int value_type = (encoded_byte & 0xC0) >>> 6;
		int value_num = (encoded_byte & 0x3F);
		Map data_map;
		switch (value_type) {
		case 0:
			data_map = get1ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				byte val = io_buf.get();
				data_map.put(sig_id, val);
			}
			break;
		case 1:
			data_map = get2ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				short val = io_buf.getShort();
				data_map.put(sig_id, val);
			}
			break;
		case 2:
			data_map = get4ByteDataMap();
			for (int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				int val = io_buf.getInt();
				data_map.put(sig_id, val);
			}
			break;
		case 3:
			data_map = getxByteDataMap();
			for (int i = 0; i < value_num; i++) {
				int sig_id = io_buf.getUnsignedShort();
				byte len = io_buf.get();
				Byte[] val = new Byte[len];
				for (int j = 0; j < len; j++) {
					val[j] = io_buf.get();
				}
				data_map.put(sig_id, val);
			}
			break;
		default:
			System.out
					.println("Error: DevSigData: Unsupported value type(parseSigData):"
							+ value_type);
			return false;
		}
		return true;
	}

	public boolean assembleSigData(IoBuffer io_buf) {
		int pos_tab_num = io_buf.position();
		byte tab_num = 0;
		io_buf.put(tab_num);// skip the tab_num
		for (int data_type = 0; data_type < 4; data_type++) {
			switch (data_type) {
			case 0: {
				Map<Integer, Byte> sig_map = SigData.Map1ByteDataSig;
				if (sig_map.size() > 0) {
					byte type_and_num = (byte) ((sig_map.size() & 0x3F) | 0x00);
					io_buf.put(type_and_num);
					Iterator<Map.Entry<Integer, Byte>> entries = sig_map
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Byte> entry = entries.next();
						io_buf.putUnsignedShort(entry.getKey());
						io_buf.put(entry.getValue());
					}
					tab_num++;
				}
				break;
			}
			case 1: {
				Map<Integer, Short> sig_map = SigData.Map2ByteDataSig;
				if (sig_map.size() > 0) {
					byte type_and_num = (byte) ((sig_map.size() & 0x3F) | 0x40);
					io_buf.put(type_and_num);
					Iterator<Map.Entry<Integer, Short>> entries = sig_map
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Short> entry = entries.next();
						io_buf.putUnsignedShort(entry.getKey());
						io_buf.putShort(entry.getValue());
					}
					tab_num++;
				}
				break;
			}
			case 2: {
				Map<Integer, Integer> sig_map = SigData.Map4ByteDataSig;
				if (sig_map.size() > 0) {
					byte type_and_num = (byte) ((sig_map.size() & 0x3F) | 0x80);
					io_buf.put(type_and_num);
					Iterator<Map.Entry<Integer, Integer>> entries = sig_map
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Integer> entry = entries.next();
						io_buf.putUnsignedShort(entry.getKey());
						io_buf.putInt(entry.getValue());
					}
					tab_num++;
				}
				break;
			}
			case 3: {
				Map<Integer, Byte[]> sig_map = SigData.MapxByteDataSig;
				if (sig_map.size() > 0) {
					byte type_and_num = (byte) ((sig_map.size() & 0x3F) | 0xC0);
					io_buf.put(type_and_num);
					Iterator<Map.Entry<Integer, Byte[]>> entries = sig_map
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Byte[]> entry = entries.next();
						io_buf.putUnsignedShort(entry.getKey());
						io_buf.put((byte) entry.getValue().length);
						for (int i = 0; i < entry.getValue().length; i++) {
							io_buf.put(entry.getValue()[i]);
						}
					}
					tab_num++;
				}
				break;
			}
			default:
				System.out
						.println("Error: DevSigData: Unsupported value type(assembleSigData): "
								+ data_type);
				break;
			}
		}
		int pos_last = io_buf.position();
		io_buf.position(pos_tab_num);
		io_buf.put(tab_num);
		io_buf.position(pos_last);
		return true;
	}
}
