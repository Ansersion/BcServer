/**
 * 
 */
package bc_server;

import java.util.HashMap;

/**
 * @author Ansersion
 *
 */


class SigDataAck {


	HashMap<Short, Byte> Map1ByteDataSig;
	HashMap<Short, Short> Map2ByteDataSig;
	HashMap<Short, Integer> Map4ByteDataSig;
	
	SigDataAck() {
		Map1ByteDataSig = new HashMap<Short, Byte>();
		Map2ByteDataSig = new HashMap<Short, Short>();
		Map4ByteDataSig = new HashMap<Short, Integer>();
	}
	
	public void addNoCusSig(short sig) {
		
	}

}

public class DevSigDataAck {
	
	short DeviceID;
	SigDataAck SigData;
	
	DevSigDataAck() {
		DeviceID = 0;
		// SigData = new SigData();
	}
	
	public HashMap<Short, Byte> get1ByteDataMap() {
		return SigData.Map1ByteDataSig;
	}
	
	public HashMap<Short, Short> get2ByteDataMap() {
		return SigData.Map2ByteDataSig;
	}
	
	public HashMap<Short, Integer> get4ByteDataMap() {
		return SigData.Map4ByteDataSig;
	}
}
