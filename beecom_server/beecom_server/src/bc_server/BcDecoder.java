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
import bp_packet.BPPacketCONNECT;
import bp_packet.FixedHeader;
import bp_packet.Payload;
import bp_packet.VariableHeader;
import other.CrcChecksum;

public class BcDecoder extends CumulativeProtocolDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 

	public enum DecodeState {
		// DEC_INVALID, DEC_FX_HEAD, DEC_VRB_HEAD, DEC_PLD, DEC_CRC;
		DEC_INVALID, DEC_FX_HEAD, DEC_REMAINING_DATA;
	}

	final String NEW_CONNECTION = new String("NEW CONNECTION");
	final String BAD_CONNECTION = new String("BAD CONNECTION");
	final String FIXED_HEADER = new String("FIXED HEADER");
	final String VARIABLE_HEADER = new String("VARIABLE HEADER");
	final String PAYLOAD = new String("PAYLOAD");
	final String CRC_CHECKSUM = new String("CRC CHECKSUM");
	final String DECODE_STATE = new String("DECODE STATE");
	final String BP_PACKET = new String("BP PACKET");

	@Override
	protected boolean doDecode(IoSession session, IoBuffer io_in,
			ProtocolDecoderOutput decoder_out) throws Exception {
		// TODO Auto-generated method stub
		
		boolean ret = false;
	
		
		if (!session.containsAttribute(NEW_CONNECTION)) {
			session.setAttribute(NEW_CONNECTION, true);
			session.setAttribute(BAD_CONNECTION, false);
			session.setAttribute(FIXED_HEADER, new FixedHeader());
			session.setAttribute(VARIABLE_HEADER, new VariableHeader());
			session.setAttribute(PAYLOAD, new Payload());
			// session.setAttribute(BP_PACKET, null);
			// session.setAttribute(BP_PACKET, new FixedHeader());
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
		}

		DecodeState curr_state = (DecodeState) session
				.getAttribute(DECODE_STATE);
		

		switch (curr_state) {
		case DEC_FX_HEAD:
			// The length of fixed-header is 3 at most 
			if (io_in.remaining() >= 3) { 
				byte[] tst = io_in.array();
				if(tst[0] == 'T' && tst[1] == 'S' && tst[2] == 'T') {
					io_in.get(new byte[io_in.remaining()]);
					decoder_out.write(new String("TST"));
					return true;
				}
				BPPacket bpPack = BPPackFactory.createBPPack(io_in);
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
			if(io_in.remaining() >= fxHead.getRemainingLen()) {
				byte[] remaining_data = new byte[fxHead.getRemainingLen()];
				io_in.get(remaining_data);
				int vrbPos = packIoBuf.position();
				packIoBuf.put(remaining_data);
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
				decoder_out.write(bpPack);
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
