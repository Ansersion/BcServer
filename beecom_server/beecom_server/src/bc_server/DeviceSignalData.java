/**
 * 
 */
package bc_server;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author Ansersion
 *
 */

class SigData {
	Vector<Integer> Vct1ByteData;
	Vector<Integer> Vct2ByteData;
	Vector<Integer> Vct4ByteData;
	
	Vector<Integer> VctSigNoCustom;

	
	SigData() {
		Vct1ByteData = new Vector<Integer>();
		Vct2ByteData = new Vector<Integer>();
		Vct4ByteData = new Vector<Integer>();
		VctSigNoCustom = new Vector<Integer>();

	}
	
	public int parseSigData(byte[] data, int offset) {
		int offset_old = offset;
		
		int type_and_num = data[offset++];
		
		// boolean  = (type_and_num >> 6) & 0x3;
		int type = 0;
		int num = type_and_num & 0x3F;
		
		if(0x00 == type) {
			for(int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct1ByteData.add(BPPacket.assemble2ByteBigend(sig_msb, sig_lsb));
			}
		} else if(0x01 == type) {
			for(int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct2ByteData.add(BPPacket.assemble2ByteBigend(sig_msb, sig_lsb));
			}
		} else if(0x10 == type) {
			for(int i = 0; i < num; i++) {
				byte sig_msb = data[offset++];
				byte sig_lsb = data[offset++];
				Vct2ByteData.add(BPPacket.assemble2ByteBigend(sig_msb, sig_lsb));
			}
		} else {
			System.out.println("ValueType " + type + " Not supported yet");
		}
		
		return offset - offset_old;
	}
}
public class DeviceSignalData {
	Integer DevId[];
	HashMap<Integer, SigData> SigDataMap;
	boolean HasCustomSig;
	int CusValTypeNum;
	
	public DeviceSignalData(int DevNum) {
		DevId = new Integer[DevNum];
		SigDataMap = new HashMap<Integer, SigData>();
		HasCustomSig = false;
		CusValTypeNum = 0;
	}
	
	public int parseSigMap(byte[] data, int offset) {
		int offset_old = offset;
		byte encoded_byte = data[offset++];
		
		HasCustomSig = (encoded_byte & 0x80) == 0x80;
		
		/*
		for(int i = 0; i < ValueTypeNum; i++) {
			encoded_byte = data[offset++];
		}
		*/
		
		return offset - offset_old;
	}
	
}
