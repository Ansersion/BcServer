package bc_server;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.FixedHeader;
import bp_packet.Payload;
import bp_packet.VariableHeader;
import other.CrcChecksum;

public class BcDecoder extends CumulativeProtocolDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 

	public enum DecodeState {
		DEC_INVALID, DEC_FX_HEAD, DEC_REMAINING_DATA;
	}

	public static final String NEW_CONNECTION = "NEW CONNECTION";
	public static final String BAD_CONNECTION = "BAD CONNECTION";
	public static final String FIXED_HEADER = "FIXED HEADER";
	public static final String VARIABLE_HEADER = "VARIABLE HEADER";
	public static final String PAYLOAD = "PAYLOAD";
	public static final String CRC_CHECKSUM = "CRC CHECKSUM";
	public static final String DECODE_STATE = "DECODE STATE";
	public static final String BP_PACKET = "BP PACKET";

	@Override
	protected boolean doDecode(IoSession session, IoBuffer ioIn,
			ProtocolDecoderOutput decoderOut) throws Exception {
		
		boolean ret = false;
	
		
		if (!session.containsAttribute(NEW_CONNECTION)) {
			session.setAttribute(NEW_CONNECTION, true);
			session.setAttribute(BAD_CONNECTION, false);
			session.setAttribute(FIXED_HEADER, new FixedHeader());
			session.setAttribute(VARIABLE_HEADER, new VariableHeader());
			session.setAttribute(PAYLOAD, new Payload());
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
		}

		DecodeState currState = (DecodeState) session
				.getAttribute(DECODE_STATE);
		

		switch (currState) {
		case DEC_FX_HEAD:
			// The length of fixed-header is 3 at most 
			if (ioIn.remaining() >= 3) { 
				byte[] tst = ioIn.array();
				if(tst[0] == 'T' && tst[1] == 'S' && tst[2] == 'T') {
					ioIn.get(new byte[ioIn.remaining()]);
					decoderOut.write("TST");
					return true;
				}
				BPPacket bpPack = BPPackFactory.createBPPack(ioIn);
				if(null == bpPack) {
					throw new Exception("Error: cannot create BPPacket!");
				}
				bpPack.putFxHead2Buf();
				session.setAttribute(BP_PACKET, bpPack);
				
				session.setAttribute(DECODE_STATE, DecodeState.DEC_REMAINING_DATA);
			} else {
				ret = false;
				break;
			}
		case DEC_REMAINING_DATA:
			BPPacket bpPack = (BPPacket)session.getAttribute(BP_PACKET);
			FixedHeader fxHead = bpPack.getFxHead();
			IoBuffer packIoBuf = bpPack.getIoBuffer();
			if(ioIn.remaining() >= fxHead.getRemainingLen()) {
				byte[] remainingData = new byte[fxHead.getRemainingLen()];
				ioIn.get(remainingData);
				int vrbPos = packIoBuf.position();
				packIoBuf.put(remainingData);
				bpPack.getIoBuffer().flip();
				bpPack.getIoBuffer().limit();
				byte[] data = new byte[bpPack.getIoBuffer().limit() - 4];
				
				packIoBuf.get(data);
				long crcGet = packIoBuf.getUnsignedInt();
				if(!CrcChecksum.crcCheck(data, fxHead.getCrcChk(), crcGet)) {
					session.setAttribute(BAD_CONNECTION, true);
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
					ret = false;
					return ret;
				}
				try {
					packIoBuf.rewind();
					packIoBuf.position(vrbPos);
					bpPack.parseVariableHeader();
					bpPack.parsePayload();
					
				} catch(Exception e) {
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
		            StringWriter sw = new StringWriter();
		            e.printStackTrace(new PrintWriter(sw, true));
		            String str = sw.toString();
		            logger.error(str);
					throw e;
				}
				session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
				decoderOut.write(bpPack);
				ret = true;
			} else {
				ret = false;
			}
			break;
		default:
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
			throw new Exception("Error: Bad decode state!");
		}

		return ret;
	}
}
