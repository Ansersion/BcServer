/**
 * 
 */
package bp_packet;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import javafx.util.Pair;
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
		VariableHeader vrb = getVrbHead();
		Payload pld = getPld();
		IoBuffer buffer = getIoBuffer();
		if(vrb.getReqAllDeviceId()) {
			List<Long> deviceIdList = pld.getDeviceIdList();
			if(null == deviceIdList) {
				buffer.putUnsignedShort(0);
				return false;
			}
			buffer.putUnsignedShort(deviceIdList.size());
			for(int i = 0; i < deviceIdList.size(); i++) {
				buffer.putUnsignedInt(deviceIdList.get(i));
			}
		} else if(vrb.getSigValFlag()) {
			byte[] data = pld.getPushSigValData();
			long uniqDevId = pld.getUniqDevId();
			buffer.putUnsignedInt(uniqDevId);
			if(null == data) {
				buffer.putUnsigned(0);
				return false;
			}
			buffer.put(data);
		} else {
			buffer.putUnsignedShort(pld.getUniqDevId());
			if(vrb.getSysSigFlag()) {
				// TODO: pack system signal
			}
			if(vrb.getCusSigFlag()) {
				/* iterator all type of signal value and put them into buffer*/
				List<Object> valueList = new ArrayList<>(Payload.CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_MAX_NUM);
				int attr = 0;
				int signalNum = 0;
				int putNum = 0;
				Map<Integer, Pair<Byte, Object> > customSignalValueMap =  pld.getCusSigValMap();
				for (int i = 0; i < BPPacket.MAX_VAL_TYPE_NUM; i++) {
					Iterator<Map.Entry<Integer, Pair<Byte, Object>>> it = customSignalValueMap.entrySet().iterator();
					attr = ((i << 4) & 0xF0);
					signalNum = 0;
					valueList.clear();
					while (it.hasNext()) {
						Map.Entry<Integer, Pair<Byte, Object>> entry = it.next();
						Byte valType = entry.getValue().getKey();
						if ((valType & 0xFF) == i) {
							signalNum++;
							valueList.add(entry.getValue().getValue());
							it.remove();
						}
						if (Payload.CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_MAX_NUM == signalNum) {
							attr |= (Payload.CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_MAX_NUM & 0x0F);
							if (customSignalValueMap.isEmpty()) {
								attr |= Payload.CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_END_MASK;
							}
							buffer.putUnsigned(attr);
							for (int j = 0; j < valueList.size(); j++) {
								putNum += putValue2Buffer(i, valueList.get(j), buffer);
							}
							attr = ((i << 4) & 0xF0);
							signalNum = 0;
							valueList.clear();
						}
					}
					if (customSignalValueMap.isEmpty() || i == BPPacket.MAX_VAL_TYPE_NUM - 1) {
						attr |= Payload.CUSTOM_SIGNAL_PUSH_SIGNAL_VALUE_END_MASK;
					}

					if (signalNum > 0) {
						attr |= (signalNum & 0x07);
						buffer.putUnsigned(attr);
						buffer.putUnsigned(attr);
						for (int j = 0; j < valueList.size(); j++) {
							putNum += putValue2Buffer(i, valueList.get(j), buffer);
						}
					}

				}
				if(0 == putNum) {
					/* no signal value put(because of no valid value or error occuring)*/
					buffer.put((byte)0);
				} 

			}
		}
		return true;
	}
	
	
}
