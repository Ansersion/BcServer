/**
 * 
 */
package bp_packet;

import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ansersion
 * 
 */

class SigData {
	private static final Logger logger = LoggerFactory.getLogger(SigData.class);
	
	boolean hasCustomSig;
	Vector<Short> vct1ByteDataSig;
	Vector<Short> vct2ByteDataSig;
	Vector<Short> vct4ByteDataSig;

	Vector<Short> vctNoCustomSig;

	SigData() {
		vct1ByteDataSig = new Vector<>();
		vct2ByteDataSig = new Vector<>();
		vct4ByteDataSig = new Vector<>();
		vctNoCustomSig = new Vector<>();

	}

	SigData(boolean customSigFlag) {
		hasCustomSig = customSigFlag;
		if (hasCustomSig) {
			vct1ByteDataSig = new Vector<>();
			vct2ByteDataSig = new Vector<>();
			vct4ByteDataSig = new Vector<>();
		} else {
			vctNoCustomSig = new Vector<>();
		}
	}

	public void setCustomSigFlag(boolean customSigFlag) {
		hasCustomSig = customSigFlag;
	}

	public boolean getCustomSigFlag() {
		return hasCustomSig;
	}
	
	public void addNoCusSig(short sig) {
		vctNoCustomSig.add(sig);
	}

	public int parseSigData(byte[] data, int offset) {
		int offsetOld = offset;

		int typeAndNum = data[offset++];

		int type = (typeAndNum >> 6) & 0x03;
		int num = typeAndNum & 0x3F;

		if (0x00 == type) {
			for (int i = 0; i < num; i++) {
				byte sigMsb = data[offset++];
				byte sigLsb = data[offset++];
				vct1ByteDataSig.add(BPPacket.assemble2ByteBigend(sigMsb,
						sigLsb));
			}
		} else if (0x01 == type) {
			for (int i = 0; i < num; i++) {
				byte sigMsb = data[offset++];
				byte sigLsb = data[offset++];
				vct2ByteDataSig.add(BPPacket.assemble2ByteBigend(sigMsb,
						sigLsb));
			}
		} else if (0x10 == type) {
			for (int i = 0; i < num; i++) {
				byte sigMsb = data[offset++];
				byte sigLsb = data[offset++];
				vct2ByteDataSig.add(BPPacket.assemble2ByteBigend(sigMsb,
						sigLsb));
			}
		} else {
			logger.warn("ValueType {} Not supported yet", type);
		}

		return offset - offsetOld;
	}
}

public class DeviceSignals {
	Short[] devId;
	HashMap<Short, SigData> sigDataMap;

	public DeviceSignals(int devNum) {
		devId = new Short[devNum];
		sigDataMap = new HashMap<>();
	}

	public int parseSigMap(byte[] data, int offset) {
		int offsetOld = offset;
		byte encodedByte;
		
		for (int i = 0; i < devId.length; i++) {
			devId[i] = BPPacket.assemble2ByteBigend(data, offset);
			offset += 2;
			encodedByte = data[offset++];
			
			sigDataMap.put(devId[i], new SigData((encodedByte & 0x80) == 0x80));

			boolean customSigFlag = (encodedByte & 0x80) == 0x80;
			int cusValTypeNum = (encodedByte) & 0x07;
			if(customSigFlag) {
				for(int j = 0; j < cusValTypeNum; j++) {
					offset += sigDataMap.get(devId[i]).parseSigData(data, offset);
				}
			} else {
				encodedByte = data[offset++];
				int sigNum = encodedByte & 0x3F;
				sigDataMap.put(devId[i],  new SigData(customSigFlag));
				for(int j = 0; j < sigNum; j++) {
					short sig = BPPacket.assemble2ByteBigend(data, offset);
					sigDataMap.get(devId[i]).addNoCusSig(sig);
				}
			}

		}

		return offset - offsetOld;
	}
}
