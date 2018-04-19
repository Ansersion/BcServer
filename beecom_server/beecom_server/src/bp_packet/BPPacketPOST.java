/**
 * 
 */
package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ansersion
 *
 */
public class BPPacketPOST extends BPPacket {
	
	private static final Logger logger = LoggerFactory.getLogger(BPPacketPOST.class); 
	
	
	int PackSeq;
	DevSigData[] SigDatas = null; 
	int DeviceNum;
	
	@Override
	public boolean parseVariableHeader(IoBuffer ioBuf) throws Exception {
		// TODO Auto-generated method stub
		int clientIdLen = 0;

		try {
			byte encodedByte = 0;
			clientIdLen = 2;

			byte[] id = new byte[clientIdLen];
			for (int i = 0; i < clientIdLen; i++) {
				id[i] = (byte) ioBuf.get();
			}
			super.parseVrbClientId(id, clientIdLen);
			
			encodedByte = ioBuf.get();
			super.parseVrbHeadFlags(encodedByte);
			
			PackSeq = ioBuf.getUnsignedShort();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	
	@Override
	public boolean parseVariableHeader(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
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
			
			byte pack_seq_msb = buf[counter++];
			byte pack_seq_lsb = buf[counter++];
			PackSeq = BPPacket.assemble2ByteBigend(pack_seq_msb, pack_seq_lsb);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return true;
	}
	
	@Override
	public int parseVariableHeader() throws Exception {
		try {

			byte flags = getIoBuffer().get();
			super.parseVrbHeadFlags(flags);
			
			int client_id = getIoBuffer().getUnsignedShort();
			getVrbHead().setClientId(client_id);

			int pack_seq = getIoBuffer().getUnsignedShort();
			getVrbHead().setPackSeq(pack_seq);
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
	public int parsePayload() throws Exception {
		return 0;
	}
	
	@Override
	public boolean parsePayload(byte[] buf) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			int counter = 0;
			
			DeviceNum = buf[counter++];
			SigDatas = new DevSigData[DeviceNum];
			
			for(int i = 0; i < DeviceNum; i++) {
				counter += SigDatas[i].parseSigData(buf, counter);
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
	public boolean assembleFixedHeader() throws Exception {
		// TODO Auto-generated method stub
		int pack_type = getPackTypeIntFxHead();
		byte pack_flags = getPackFlagsByteFxHead();
		byte encodedByte = (byte) (((pack_type & 0xf) << 4) | (pack_flags & 0xf));
		
		getIoBuffer().put(encodedByte);
		
		// Remaininglength 1 byte reserved
		getIoBuffer().put((byte)0);
		
		return false;
	}

	@Override
	public boolean assembleVariableHeader() throws Exception {
		// TODO Auto-generated method stub
		byte flags = getVrbHead().getFlags();
		getIoBuffer().put(flags);
		int clnt_id = getVrbHead().getClientId();
		getIoBuffer().putUnsignedShort(clnt_id);
		int pack_seq = getVrbHead().getPackSeq();
		getIoBuffer().putUnsignedShort(pack_seq);	
		
		return false;
	}

	@Override
	public boolean assemblePayload() throws Exception {
		// TODO Auto-generated method stub
		DevSigData sig_data = getPld().getSigData();

		sig_data.assembleSigData(getIoBuffer());
		
		return false;
	}
}
