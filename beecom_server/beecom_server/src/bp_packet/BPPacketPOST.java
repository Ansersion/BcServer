/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 *
 */
public class BPPacketPOST extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPOST.class); 
	
	public static final int RET_CODE_INVALID_FLAGS_ERR = 0x01;
	public static final int RET_CODE_INVALID_DEVICE_ID_ERR = 0x02;
	public static final int RET_CODE_OFF_LINE_ERR = 0x06;
	public static final int RET_CODE_ACCESS_DEV_PERMISSION_DENY_ERR = 0x07;
	public static final int RET_CODE_BUFFER_FILLED_ERR = 0x0;
	
	
	int packSeq;
	DevSigData[] sigDatas = null; 
	int deviceNum;
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) {
		int clientIdLen = 0;

		try {
			byte encodedByte = 0;
			clientIdLen = 2;

			byte[] id = new byte[clientIdLen];
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = ioBuf.get();
			}
			super.parseVrbClientId(id, clientIdLen);
			
			encodedByte = ioBuf.get();
			super.parseVrbHeadFlags(encodedByte);
			
			packSeq = ioBuf.getUnsignedShort();

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
			for(int i = 0; i < clientIdLen; i++) {
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

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);
			
			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);

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
		return 0;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) {
		
		try {
			int counter = 0;
			
			deviceNum = buf[counter++];
			sigDatas = new DevSigData[deviceNum];
			
			for(int i = 0; i < deviceNum; i++) {
				counter += sigDatas[i].parseSigData(buf, counter);
			}

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
		DevSigData sigData = getPld().getSigData();

		sigData.assembleSigData(getIoBuffer());
		
		return false;
	}
}
