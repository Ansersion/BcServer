package bc_server;

/**
 * @author Ansersion
 *
 */

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class BcDecoder extends CumulativeProtocolDecoder {

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
			session.setAttribute(BP_PACKET, null);
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
		}

		DecodeState curr_state = (DecodeState) session
				.getAttribute(DECODE_STATE);

		switch (curr_state) {
		case DEC_FX_HEAD:
			// The length of fixed-header is 3 at most 
			if (io_in.remaining() >= 3) { 
				FixedHeader fxHead = (FixedHeader)session.getAttribute(FIXED_HEADER);
				byte encoded_byte;
				encoded_byte = (byte)io_in.getChar();
				
				fxHead.setBPType(encoded_byte);
				fxHead.setFlags(encoded_byte);
				fxHead.getCrcChk();
				fxHead.getEncryptType();
				fxHead.setRemainLen(io_in);
				session.setAttribute(BP_PACKET, BPPackFactory.createBPPack(fxHead));
				// session.setAttribute(DECODE_STATE, DecodeState.DEC_VRB_HEAD);
				session.setAttribute(DECODE_STATE, DecodeState.DEC_REMAINING_DATA);
			} else {
				ret = false;
				break;
			}
		case DEC_REMAINING_DATA:
			FixedHeader fxHead = (FixedHeader)session.getAttribute(FIXED_HEADER);
			if(io_in.remaining() >= fxHead.getRemainingLen()) {
				byte[] remaining_data = new byte[fxHead.getRemainingLen()];
				io_in.get(remaining_data);
				if(!CrcChecksum.crcCheck(remaining_data, fxHead.getCrcChk())) {
					session.setAttribute(BAD_CONNECTION, true);
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
					ret = false;
					return ret;
				}
				BPPacket bp_pack = (BPPacket)session.getAttribute(BP_PACKET);
				try {
					// bp_pack.setRemainingData(remaining_data);
					bp_pack.parseVariableHeader(remaining_data);
					bp_pack.parsePayload(remaining_data);
				} catch(Exception e) {
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
					e.printStackTrace();
					throw e;
				}
				session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
				decoder_out.write(bp_pack);
				ret = true;
			} else {
				ret = false;
			}
			break;
			
			/*
			BPPacket bp_pack = (BPPacket)session.getAttribute(BP_PACKET);
			try {
				ret = bp_pack.parseVariableHeader(io_in);
				if(ret) {
					session.setAttribute(DECODE_STATE, DecodeState.DEC_PLD);
				}
			} catch(Exception e) {
				System.out.println("Error: decode variable header err!");
				e.printStackTrace();
				throw e;
			}
			break;
			
		case DEC_PLD:
			break;
		case DEC_CRC:
			break;
			*/
		default:
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
			throw new Exception("Error: Bad decode state!");
		}

		return ret;
	}
}
