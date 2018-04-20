/**
 * 
 */
package bp_packet;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bc_server.BcDecoder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Vector;

/**
 * @author Ansersion
 * 
 */
public class BPPacketREPORT extends BPPacket {

	int packSeq;
	int devNameLen;
	Vector<BPPartitation> partitation;
	byte[] devName;

	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 
	
	BPPacketREPORT() {
		super();
		packSeq = 0;
		devNameLen = 0;
		devName = new byte[256];
	}

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
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = buf[counter++];
			}
			super.parseVrbClientId(id, clientIdLen);

			encodedByte = buf[counter++];
			super.parseVrbHeadFlags(encodedByte);

			byte packSeqMsg = buf[counter++];
			byte packSeqLsb = buf[counter];
			packSeq = BPPacket.assemble2ByteBigend(packSeqMsg, packSeqLsb);

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
	public boolean parsePayload(byte[] buf) {
		
		try {
			int counter = 0;
			
			devNameLen = buf[counter++];
			for(int i = 0; i < devNameLen; i++) {
				devName[i] = buf[counter + devNameLen - 1 - i];
			}
			
			boolean endFlag;
			int part1;
			int part2;
			do {
				byte part = buf[counter++];
				part1 = BPPartitation.parsePart1(part);
				part2 = BPPartitation.parsePart2(part);
				endFlag = BPPartitation.parseEndFlag(part);
				BPPartitation newPart = BPPartitation.createPartitation(part1, part2);
				
				counter += newPart.parseSymTable(buf, counter);
				
				
				partitation.addElement(newPart);
				
				
			}while(!endFlag);		

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
			// flags(1 byte) + client ID(2 byte) + sequence id(2 byte)
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
		try {

			VariableHeader vb = getVrbHead();
			Payload pld = getPld();
			
			if (vb.getSigFlag()) {
				int sigTabNum = getIoBuffer().get();
				if(null == getPld().getSigData()) {
					getPld().setSigData(new DevSigData());
				}
				getPld().getSigData().clear();
				for(int i = 0; i < sigTabNum; i++) {
					getPld().getSigData().parseSigDataTab(getIoBuffer());
				}
				
			} else {

				if (vb.getDevNameFlag()) {
					int devNameLenTmp = getIoBuffer().get();
					byte[] devNameTmp = new byte[devNameLenTmp];
					getIoBuffer().get(devNameTmp);
					pld.setDevName(devNameTmp);
				}

				if (vb.getSysSigMapFlag()) {
					byte distAndClass;
					int dist;
					int sysSigClass;
					int mapNum;

					do {
						distAndClass = getIoBuffer().get();
						dist = (distAndClass >> 4) & 0x0F;
						sysSigClass = (distAndClass >> 1) & 0x07;
						if (sysSigClass >= 0x07) {
							throw new BPParsePldException("Error: System signal class 0x7");
						}
						mapNum = 0x200 / 8 / (1 << sysSigClass);
						Byte[] sysSigMap = new Byte[mapNum];
						Map<Integer, Byte[]> sysMap = pld
								.getMapDist2SysSigMap();
						for (int i = 0; i < mapNum; i++) {
							sysSigMap[i] = getIoBuffer().get();
						}
						sysMap.put(dist, sysSigMap);
					} while ((distAndClass & VariableHeader.DIST_END_FLAG_MSK) != VariableHeader.DIST_END_FLAG_MSK);
				}
			}

		} catch (Exception e) {
	        StringWriter sw = new StringWriter();
	        e.printStackTrace(new PrintWriter(sw, true));
	        String str = sw.toString();
	        logger.error(str);
		}
		return 0;
	}
	
	public Vector<BPPartitation> getPartitation() {
		return partitation;
	}
}
