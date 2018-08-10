/**
 * 
 */
package bp_packet;


import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import other.CrcChecksum;




/**
 * @author Ansersion
 *
 */
public class BPPacketPUSH extends BPPacket {
	
	public static final int RET_CODE_OK = 0;
	public static final int RET_CODE_UNSUPPORTED_SIGNAL_ID = 0x01;
	
	protected BPPacketPUSH(FixedHeader fxHeader) {
		super(fxHeader);
	}
	
	protected BPPacketPUSH() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.PUSH);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {
		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) {
		return true;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) {
		return true;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		vrb.initPackSeq();
		IoBuffer buffer = getIoBuffer();
		byte flags = vrb.getFlags();
		buffer.put(flags);
		int packSeq = vrb.getPackSeq();
		buffer.putUnsignedShort(packSeq);
		return true;
	}

	@Override
	public boolean assemblePayload() throws BPAssemblePldException {
		Payload pld = getPld();
		IoBuffer buffer = getIoBuffer();
		if(getVrbHead().getReqAllDeviceId()) {
			List<Long> deviceIdList = pld.getDeviceIdList();
			if(null == deviceIdList) {
				buffer.putUnsignedShort(0);
				return false;
			}
			buffer.putUnsignedShort(deviceIdList.size());
			for(int i = 0; i < deviceIdList.size(); i++) {
				buffer.putUnsignedInt(deviceIdList.get(i));
			}
		} else {
			// TODO: signal values
		}
		return true;
	}
	
	
}
