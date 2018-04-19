/**
 * 
 */
package bp_packet;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author Ansersion
 * 
 */

class SigData {
	boolean HasCustomSig;
	Vector<Short> Vct1ByteDataSig;
	Vector<Short> Vct2ByteDataSig;
	Vector<Short> Vct4ByteDataSig;

	Vector<Short> VctNoCustomSig;

	SigData() {
		Vct1ByteDataSig = new Vector<Short>();
		Vct2ByteDataSig = new Vector<Short>();
		Vct4ByteDataSig = new Vector<Short>();
		VctNoCustomSig = new Vector<Short>();

	}

	SigData(boolean custom_sig_flag) {
		HasCustomSig = custom_sig_flag;
		if (HasCustomSig) {
			Vct1ByteDataSig = new Vector<Short>();
			Vct2ByteDataSig = new Vector<Short>();
			Vct4ByteDataSig = new Vector<Short>();
		} else {
			VctNoCustomSig = new Vector<Short>();
		}
	}

	public void setCustomSigFlag(boolean custom_sig_flag) {
		HasCustomSig = custom_sig_flag;
	}

	public boolean getCustomSigFlag() {
		return HasCustomSig;
	}
	
	public void addNoCusSig(short sig) {
		VctNoCustomSig.add(sig);
	}

	public int parseSigData(byte[] data, int offset) {
		int offset_old = offset;

		int type_and_num = data[offset++];

		int type = 0;
		int num = type_and_num & 0x3F;

		if (0x00 == type) {
			for (int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct1ByteDataSig.add(BPPacket.assemble2ByteBigend(sig_msb,
						sig_lsb));
			}
		} else if (0x01 == type) {
			for (int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct2ByteDataSig.add(BPPacket.assemble2ByteBigend(sig_msb,
						sig_lsb));
			}
		} else if (0x10 == type) {
			for (int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct2ByteDataSig.add(BPPacket.assemble2ByteBigend(sig_msb,
						sig_lsb));
			}
		} else {
			System.out.println("ValueType " + type + " Not supported yet");
		}

		return offset - offset_old;
	}
}

public class DeviceSignals {
	Short DevId[];
	HashMap<Short, SigData> SigDataMap;

	public DeviceSignals(int DevNum) {
		DevId = new Short[DevNum];
		SigDataMap = new HashMap<Short, SigData>();
	}

	public int parseSigMap(byte[] data, int offset) {
		int offset_old = offset;
		byte encodedByte;
		
		for (int i = 0; i < DevId.length; i++) {
			DevId[i] = BPPacket.assemble2ByteBigend(data, offset);
			offset += 2;
			encodedByte = data[offset++];
			
			SigDataMap.put(DevId[i], new SigData((encodedByte & 0x80) == 0x80));

			boolean custom_sig_flag = (encodedByte & 0x80) == 0x80;
			int cus_val_type_num = (encodedByte) & 0x07;
			if(custom_sig_flag) {
				for(int j = 0; j < cus_val_type_num; j++) {
					offset += SigDataMap.get(DevId[i]).parseSigData(data, offset);
				}
			} else {
				encodedByte = data[offset++];
				int sig_num = encodedByte & 0x3F;
				SigDataMap.put(DevId[i],  new SigData(custom_sig_flag));
				for(int j = 0; j < sig_num; j++) {
					short sig = BPPacket.assemble2ByteBigend(data, offset);
					SigDataMap.get(DevId[i]).addNoCusSig(sig);
				}
			}

		}

		return offset - offset_old;
	}
}
