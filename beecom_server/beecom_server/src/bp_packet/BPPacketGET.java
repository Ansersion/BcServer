/**
 * 
 */
package bp_packet;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.Util;

/**
 * @author Ansersion
 * 
 */

public class BPPacketGET extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketGET.class); 
	
	int packSeq;
	int deviceNum;
	private byte[] signalValueRelay;

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
		int ret = -1;
		IoBuffer ioBuffer = getIoBuffer();
		try {
			VariableHeader vrb = getVrbHead();
			if(vrb.getSNFlag()) {
				String sn = parseString(ioBuffer);
				if(null == sn) {
					return ret;
				}
				getPld().setSN(sn);
			} else if(vrb.getReqAllDeviceId()) {
				/* no payload */
			} else if(vrb.getSysSigMapFlag() || vrb.getCusSigMapFlag() || vrb.getSysSigMapCustomInfo()) {
				getPld().setDevUniqId(ioBuffer.getUnsignedInt());
			} else if(vrb.getSigValFlag()) {
				getPld().setDevUniqId(ioBuffer.getUnsignedInt());
                List<Integer> signalLst = new ArrayList<>();
                int signalNum = ioBuffer.getUnsigned();
                for(int i = 0; i < signalNum; i++) {
                	signalLst.add(ioBuffer.getUnsignedShort());
                }
                getPld().setSignalLst(signalLst);
            } else {
                logger.error("GET: variable head flags error");
                return ret;
            }
			
			ret = 0;
		} catch (Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}

	@Override
	public boolean assembleVariableHeader() throws BPAssembleVrbHeaderException {
		super.assembleVariableHeader();
		VariableHeader vrb = getVrbHead();
		if(0 == vrb.getPackSeq()) {
			vrb.initPackSeq();
		}
		byte flags = vrb.getFlags();
		getIoBuffer().put(flags);
		int packSeqTmp = vrb.getPackSeq();
		getIoBuffer().putUnsignedShort(packSeqTmp);	
		
		return true;
	}

	@Override
	public boolean assemblePayload() {
		if(0 != getVrbHead().getRetCode()) {
			return false;
		}
		
		byte[] relayData = getPld().getRelayData();
		if(null == relayData || 0 == relayData.length) {
			return false;
		}
		
		getIoBuffer().put(relayData);
		
		return true;
	}
	
	@Override
	public byte[] getSignalValueRelay() {
		return signalValueRelay;
	}
}
