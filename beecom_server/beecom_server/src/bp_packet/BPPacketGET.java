/**
 * 
 */
package bp_packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;

/**
 * @author Ansersion
 * 
 */

public class BPPacketGET extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketGET.class); 
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_VRB_HEADER_FLAG_ERR = 0x01;
	public static final int RET_CODE_SIG_MAP_UNCHECKED_ERR = 0x02;
	public static final int RET_CODE_SIGNAL_NOT_SUPPORT_ERR = 0x03;
	public static final int RET_CODE_SIGNAL_REPEAT_ERR = 0x05;
	public static final int RET_CODE_GET_SN_PERMISSION_DENY_ERR = 0x06;
	public static final int RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR = 0x07;
	public static final int RET_CODE_INNER_ERR = 0xFF;
	
	int packSeq;
	DeviceSignals devSigData = null;
	int deviceNum;

	@Override
	public int parseVariableHeader() {
		try {
			byte encodedByte = 0;
			encodedByte = getIoBuffer().get();
			getVrbHead().parseFlags(encodedByte);
			if(getVrbHead().getCusSigMapFlag()) {
				encodedByte = getIoBuffer().get();
				getVrbHead().setLangFlags(encodedByte);
			}
			int packSeqTmp = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(packSeqTmp);
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {
			VariableHeader vrb = getVrbHead();
			if(vrb.getReqAllDeviceId()) {
				return 0;
			}
			if(vrb.getSysSigMapFlag() || vrb.getCusSigMapFlag() || vrb.getSysSigMapCustomInfo()) {
				getPld().setDevUniqId(getIoBuffer().getUnsignedInt());
				return 0;
			}
			String s;
			short devNum = getIoBuffer().get();
			Map<Integer, List<Integer> > mapDev2siglst = getPld().getMapDev2SigLst();
			
			for (short i = 0; i < devNum; i++) {
				int devId = getIoBuffer().getUnsignedShort();
				byte cusFlags = getIoBuffer().get();
				List<Integer> lstSig = new ArrayList<>();
				if ((cusFlags & 0x80) == 0x80) {
					s = "Error: Not supported GET payload custom signals";
					logger.error(s);
				} else {
					short sigNum = getIoBuffer().get();
					for(short j = 0; j < sigNum; j++) {
						int sigId = getIoBuffer().getUnsignedShort();
						lstSig.add(sigId);
					}
				}
				mapDev2siglst.put(devId, lstSig);
			}
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
			throw e;
		}
		return 0;
	}

	@Override
	public boolean assembleFixedHeader() {
		int packType = getPackTypeIntFxHead();
		byte packFlags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((packType & 0xf) << 4) | (packFlags & 0xf));
		
		getIoBuffer().put(encodedByte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		vrb.initPackSeq();
		byte flags = vrb.getFlags();
		getIoBuffer().put(flags);
		int packSeqTmp = vrb.getPackSeq();
		getIoBuffer().putUnsignedShort(packSeqTmp);	
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		Map<Integer, List<Integer>> sigMap = getPld().getMapDev2SigLst();
		if(sigMap.size() == 1) {
			Iterator<Map.Entry<Integer, List<Integer>>> entries = sigMap.entrySet().iterator();
			Map.Entry<Integer, List<Integer>> entry = entries.next();  
			List<Integer> sigLst = entry.getValue();
			getIoBuffer().put((byte)sigLst.size());
			for(int i = 0; i < sigLst.size(); i++) {
				getIoBuffer().putUnsignedShort(sigLst.get(i));
			}
		}
		
		return true;
	}
}
