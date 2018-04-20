/**
 * 
 */
package bp_packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ansersion
 * 
 */

class SigDatas {

	Map<Integer, Byte> map1ByteDataSig;
	Map<Integer, Short> map2ByteDataSig;
	Map<Integer, Integer> map4ByteDataSig;
	Map<Integer, Byte[]> mapxByteDataSig;


	SigDatas() {
		map1ByteDataSig = new HashMap<>();
		map2ByteDataSig = new HashMap<>();
		map4ByteDataSig = new HashMap<>();
		mapxByteDataSig = new HashMap<>();
		
	}
	
	public void clear() {
		map1ByteDataSig.clear();
		map2ByteDataSig.clear();
		map4ByteDataSig.clear();
		mapxByteDataSig.clear();
		
	}

}

public class DevSigData {
	
	private static final Logger logger = LoggerFactory.getLogger(DevSigData.class);

	int deviceID;
	SigDatas sigData;

	public DevSigData() {
		deviceID = 0;
		sigData = new SigDatas();
	}
	
	public void clear() {
		deviceID = 0;
		sigData.clear();
	}

	public void setDevID(int devId) {
		deviceID = devId;
	}

	public int getDevID() {
		return deviceID;
	}

	public Map<Integer, Byte> get1ByteDataMap() {
		return sigData.map1ByteDataSig;
	}

	public Map<Integer, Short> get2ByteDataMap() {
		return sigData.map2ByteDataSig;
	}

	public Map<Integer, Integer> get4ByteDataMap() {
		return sigData.map4ByteDataSig;
	}

	public Map<Integer, Byte[]> getxByteDataMap() {
		return sigData.mapxByteDataSig;
	}
	
	public void dump() {
		Map map;
		Iterator entries;

		map = get1ByteDataMap();
		entries = map.entrySet().iterator();
		String printFormat = "{}=>{}";
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte> entry = (Map.Entry<Integer, Byte>) entries
					.next();
			logger.debug(printFormat, entry.getKey(), Integer.toHexString(entry.getValue()));
		}

		map = get2ByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Short> entry = (Map.Entry<Integer, Short>) entries
					.next();
			logger.debug(printFormat, entry.getKey(), Integer.toHexString(entry.getValue()));
		}

		map = get4ByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) entries
					.next();
			logger.debug(printFormat, entry.getKey(), Integer.toHexString(entry.getValue()));
		}

		map = getxByteDataMap();
		entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Integer, Byte[]> entry = (Map.Entry<Integer, Byte[]>) entries
					.next();
			logger.debug(printFormat, entry.getKey(), entry.getValue());
		}
	}

	public int parseSigData(byte[] buf, int offset) {
		int offsetOld = offset;
		byte encodedByte;

		byte msb = buf[offset++];
		byte lsb = buf[offset++];
		deviceID = BPPacket.assemble2ByteBigend(msb, lsb);

		encodedByte = buf[offset++];
		int valueType = (encodedByte & 0xC0) >>> 6;
		int valueNum = (encodedByte & 0x3F);
		short sigId;

		if (0x0 == valueType) {
			Map dataMap = get1ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sigId = BPPacket.assemble2ByteBigend(msb, lsb);
				byte data = buf[offset++];
				dataMap.put(sigId, data);
			}
		} else if (0x01 == valueType) {
			Map dataMap = get1ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sigId = BPPacket.assemble2ByteBigend(msb, lsb);
				int data = buf[offset++];
				data = (data << 8) + (buf[offset++] & 0xFF);
				dataMap.put(sigId, (short) data);
			}
		} else if (0x10 == valueType) {
			Map dataMap = get1ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				msb = buf[offset++];
				lsb = buf[offset++];
				sigId = BPPacket.assemble2ByteBigend(msb, lsb);
				long data = buf[offset++];
				data = (data << 8) + (buf[offset++] & 0xFF);
				data = (data << 8) + (buf[offset++] & 0xFF);
				data = (data << 8) + (buf[offset++] & 0xFF);
				dataMap.put(sigId, (int) data);
			}
		} else {
			logger.warn("Error: DevSigData: Unsupported value type");
		}

		return offset - offsetOld;
	}

	public boolean parseSigDataTab(IoBuffer ioBuf) {
		byte encodedByte;

		encodedByte = ioBuf.get();
		int valueType = (encodedByte & 0xC0) >>> 6;
		int valueNum = (encodedByte & 0x3F);
		Map dataMap;
		switch (valueType) {
		case 0:
			dataMap = get1ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				int sigId = ioBuf.getUnsignedShort();
				byte val = ioBuf.get();
				dataMap.put(sigId, val);
			}
			break;
		case 1:
			dataMap = get2ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				int sigId = ioBuf.getUnsignedShort();
				short val = ioBuf.getShort();
				dataMap.put(sigId, val);
			}
			break;
		case 2:
			dataMap = get4ByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				int sigId = ioBuf.getUnsignedShort();
				int val = ioBuf.getInt();
				dataMap.put(sigId, val);
			}
			break;
		case 3:
			dataMap = getxByteDataMap();
			for (int i = 0; i < valueNum; i++) {
				int sigId = ioBuf.getUnsignedShort();
				byte len = ioBuf.get();
				Byte[] val = new Byte[len];
				for (int j = 0; j < len; j++) {
					val[j] = ioBuf.get();
				}
				dataMap.put(sigId, val);
			}
			break;
		default:
			logger.warn("Error: DevSigData: Unsupported value type(parseSigData): {}", valueType);
			return false;
		}
		return true;
	}

	public boolean assembleSigData(IoBuffer ioBuf) {
		int posTabNum = ioBuf.position();
		byte tabNum = 0;
		ioBuf.put(tabNum);// skip the tab_num
		for (int data_type = 0; data_type < 4; data_type++) {
			switch (data_type) {
			case 0: {
				Map<Integer, Byte> sigMap = sigData.map1ByteDataSig;
				if (sigMap.size() > 0) {
					byte typeAndNum = (byte) (sigMap.size() & 0x3F);
					ioBuf.put(typeAndNum);
					Iterator<Map.Entry<Integer, Byte>> entries = sigMap
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Byte> entry = entries.next();
						ioBuf.putUnsignedShort(entry.getKey());
						ioBuf.put(entry.getValue());
					}
					tabNum++;
				}
				break;
			}
			case 1: {
				Map<Integer, Short> sigMap = sigData.map2ByteDataSig;
				if (sigMap.size() > 0) {
					byte typeAndNum = (byte) ((sigMap.size() & 0x3F) | 0x40);
					ioBuf.put(typeAndNum);
					Iterator<Map.Entry<Integer, Short>> entries = sigMap
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Short> entry = entries.next();
						ioBuf.putUnsignedShort(entry.getKey());
						ioBuf.putShort(entry.getValue());
					}
					tabNum++;
				}
				break;
			}
			case 2: {
				Map<Integer, Integer> sigMap = sigData.map4ByteDataSig;
				if (sigMap.size() > 0) {
					byte typeAndNum = (byte) ((sigMap.size() & 0x3F) | 0x80);
					ioBuf.put(typeAndNum);
					Iterator<Map.Entry<Integer, Integer>> entries = sigMap
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Integer> entry = entries.next();
						ioBuf.putUnsignedShort(entry.getKey());
						ioBuf.putInt(entry.getValue());
					}
					tabNum++;
				}
				break;
			}
			case 3: {
				Map<Integer, Byte[]> sigMap = sigData.mapxByteDataSig;
				if (sigMap.size() > 0) {
					byte typeAndNum = (byte) ((sigMap.size() & 0x3F) | 0xC0);
					ioBuf.put(typeAndNum);
					Iterator<Map.Entry<Integer, Byte[]>> entries = sigMap
							.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<Integer, Byte[]> entry = entries.next();
						ioBuf.putUnsignedShort(entry.getKey());
						ioBuf.put((byte) entry.getValue().length);
						for (int i = 0; i < entry.getValue().length; i++) {
							ioBuf.put(entry.getValue()[i]);
						}
					}
					tabNum++;
				}
				break;
			}
			default:
				logger.warn("Error: DevSigData: Unsupported value type(assembleSigData): {}", data_type);
				break;
			}
		}
		int posLast = ioBuf.position();
		ioBuf.position(posTabNum);
		ioBuf.put(tabNum);
		ioBuf.position(posLast);
		return true;
	}
}
