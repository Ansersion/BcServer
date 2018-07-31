/**
 * 
 */
package bp_packet;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import other.CrcChecksum;

import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * @author Ansersion
 * 
 */
public class BPPacketGETACK extends BPPacket {
	private static final Logger logger = LoggerFactory.getLogger(BPPacketGETACK.class); 
	
	int deviceNum;
	Vector<DevSigData> vctDevSigData;

	protected BPPacketGETACK(FixedHeader fxHeader) {
		super(fxHeader);
	}

	protected BPPacketGETACK(FixedHeader fxHeader, int devNum) {
		super(fxHeader);
		deviceNum = devNum;
	}

	
	protected BPPacketGETACK() {
		super();
		FixedHeader fxHead = getFxHead();
		fxHead.setPacketType(BPPacketType.GETACK);
		fxHead.setCrcType(CrcChecksum.CRC32);
	}
	
	public void setDevNum(int num) {
		deviceNum = num;
	}
	
	@Override
	public int parseVariableHeader() {

		try {
			// flags(1 byte) + client ID(2 byte) + sequence ID(2 byte) + return code(1 byte)
			byte flags = 0;

			flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);

			int clientId = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(clientId);

			int seqId = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(seqId);
			
			byte retCode = getIoBuffer().get();
			getVrbHead().setRetCode(retCode);
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
			String s;
			if(getVrbHead().getRetCode() != 0) {
				return -1;
			}
			int sigTabNum = getIoBuffer().get();
			DevSigData sigData = new DevSigData();
			for(int i = 0; i < sigTabNum; i++) {
				boolean ret = sigData.parseSigDataTab(getIoBuffer());
				if(!ret) {
					s = "Error(GETACK): parsePayload error";
					logger.error(s);
					return -1;
				}
				
			}
			getPld().setSigData(sigData);

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
	public boolean assembleFixedHeader() {
		int packType = getPackTypeIntFxHead();
		byte packFlags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((packType & 0xf) << 4) | (packFlags & 0xf));

		getIoBuffer().put(encodedByte);

		// Remaininglength 1 byte reserved
		getIoBuffer().putUnsignedShort(0);

		return false;
	}

	@Override
	public boolean assembleVariableHeader() {
		
		int packSeq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(packSeq);
		byte retCode = (byte)getVrbHead().getRetCode();
		getIoBuffer().put(retCode);

		return false;
	}

	@Override
	public boolean assemblePayload() {
		byte encodedByte;

		int devNum = vctDevSigData.size() & 0x0000FFFF;
		getIoBuffer().putUnsignedShort(devNum);
		String s;

		for (int i = 0; i < devNum; i++) {
			DevSigData sigDataAck = vctDevSigData.get(i);
			int sigNum;
			sigNum = sigDataAck.get1ByteDataMap().size();

			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 1 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					getIoBuffer().put(encodedByte);
					Map<Integer, Byte> map = sigDataAck.get1ByteDataMap();

					Iterator<Map.Entry<Integer, Byte>> it = map.entrySet().iterator();
					while (it.hasNext()) {

						Map.Entry<Integer, Byte> entry = it.next();
						getIoBuffer().putUnsignedShort((Integer) entry.getKey());
						getIoBuffer().put((Byte) entry.getValue());
					}

				}
			}

			sigNum = sigDataAck.get2ByteDataMap().size();
			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 2 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encodedByte |= 0x40;
					getIoBuffer().put(encodedByte);
					Map<Integer, Short> map = sigDataAck.get2ByteDataMap();
					Iterator<Map.Entry<Integer, Short>> it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Short> entry = it.next();
						getIoBuffer().putUnsignedShort(entry.getKey());
						getIoBuffer().putShort((Short) entry.getValue());
					}

				}
			}

			sigNum = sigDataAck.get4ByteDataMap().size();
			if (sigNum > 0) {
				if (sigNum > 0x3F) {
					s = "WARNING: too many signal of 4 byte";
					logger.error(s);
				} else {
					encodedByte = (byte) (sigNum & 0x3F);
					// clear bit6, bit7
					encodedByte &= ~0xC0;
					// set the value type of bit6, bit7 to '00b'
					encodedByte |= 0x80;
					getIoBuffer().put(encodedByte);
					Map<Integer, Integer> map = sigDataAck.get4ByteDataMap();
					Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Integer> entry = it.next();
						getIoBuffer().putUnsignedShort(entry.getKey());
						getIoBuffer().putInt((Integer) entry.getValue());
					}

				}
			}
		}

		return false;
	}

}
