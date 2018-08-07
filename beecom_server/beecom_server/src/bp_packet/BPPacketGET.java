/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 * 
 */

public class BPPacketGET extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketGET.class); 
	
	public static final int RET_CODE_OK = 0x00;
	public static final int RET_CODE_VRB_HEADER_FLAG_ERR = 0x01;
	public static final int RET_CODE_SIGNAL_NOT_SUPPORT_ERR = 0x03;
	public static final int RET_CODE_SIGNAL_REPEAT_ERR = 0x05;
	public static final int RET_CODE_GET_SN_PERMISSION_DENY_ERR = 0x06;
	public static final int RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR = 0x07;
	
	int packSeq;
	DeviceSignals devSigData = null;
	int deviceNum;

	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {

		try {
			byte encodedByte = 0;
			encodedByte = ioBuf.get();
			super.parseVrbHeadFlags(encodedByte);
			if(getVrbHead().getCusSigMapFlag()) {
				encodedByte = ioBuf.get();
				getVrbHead().setLangFlags(encodedByte);
			}

			packSeq = ioBuf.getUnsignedShort();
			getVrbHead().setPackSeq(packSeq);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
	}

	@Override
	public boolean parseVariableHeader(byte[] buf) {
		try {
			int counter = 0;
			int clientIdLen = 0;
			byte encodedByte = 0;
			clientIdLen = 2;

			byte[] id = new byte[clientIdLen];
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, clientIdLen);

			encodedByte = buf[counter++];
			super.parseVrbHeadFlags(encodedByte);

			byte packSeqMsb = buf[counter++];
			byte packSeqLsb = buf[counter];
			packSeq = BPPacket.assemble2ByteBigend(packSeqMsb, packSeqLsb);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
	}

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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return 0;
	}

	@Override
	public int parsePayload() {
		try {
			if(getVrbHead().getReqAllDeviceId()) {
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}
		return 0;
	}

	@Override
	public boolean parsePayload(byte[] buf) {

		try {
			int counter = 0;

			deviceNum = buf[counter++];
			devSigData = new DeviceSignals(deviceNum);

			counter += devSigData.parseSigMap(buf, counter);

		} catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            logger.error(str);
			throw e;
		}

		return true;
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
	public boolean assembleVariableHeader() {
		byte flags = getVrbHead().getFlags();
		getIoBuffer().put(flags);
		int clntId = getVrbHead().getClientId();
		getIoBuffer().putUnsignedShort(clntId);
		int packSeqTmp = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeqTmp);	
		
		return false;
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
		
		return false;
	}
}
