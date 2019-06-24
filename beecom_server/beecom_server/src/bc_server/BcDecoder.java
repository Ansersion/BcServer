package bc_server;

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
import bp_packet.BPParseException;
import bp_packet.FixedHeader;
import other.CrcChecksum;
import other.Util;

public class BcDecoder extends CumulativeProtocolDecoder {
	private static final Logger logger = LoggerFactory.getLogger(BcDecoder.class); 

	public enum DecodeState {
		DEC_INVALID, DEC_FX_HEAD, DEC_REMAINING_DATA;
	}

	public static final String OLD_CONNECTION = "OLD CONNECTION";
	public static final String CRC_CHECKSUM = "CRC CHECKSUM";
	public static final String DECODE_STATE = "DECODE STATE";
	public static final String BP_PACKET = "BP PACKET";

	@Override
	protected boolean doDecode(IoSession session, IoBuffer ioIn,
			ProtocolDecoderOutput decoderOut) throws Exception {
		boolean ret = false;
		
		if (!session.containsAttribute(OLD_CONNECTION)) {
			session.setAttribute(OLD_CONNECTION, true);
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
			session.setAttribute(CRC_CHECKSUM, true);
		}

		DecodeState currState = (DecodeState) session
				.getAttribute(DECODE_STATE);
		
		switch (currState) {
		case DEC_FX_HEAD:
			if (ioIn.remaining() >= BPPacket.FIXED_HEADER_SIZE) { 
				BPPacket bpPack = BPPackFactory.createBPPack(ioIn);
				if(null == bpPack) {
					break;
				}
				session.setAttribute(BP_PACKET, bpPack);
				session.setAttribute(DECODE_STATE, DecodeState.DEC_REMAINING_DATA);
				/* no 'break' here for performance */
			} else {
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
				packIoBuf.flip();
				packIoBuf.limit();
				int crcSize = (fxHead.getCrcChk() == CrcChecksum.CRC32 ? 4 : 2);
				
				if(packIoBuf.limit() < crcSize) {
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
					return false;
				}
				byte[] data = new byte[packIoBuf.limit() - crcSize];
				
				packIoBuf.get(data);
				
				long crcGet;
				if(4 == crcSize) {
					crcGet = packIoBuf.getUnsignedInt();
				} else {
					crcGet = packIoBuf.getUnsignedShort();
				}
				if(!CrcChecksum.crcCheck(data, fxHead.getCrcChk(), crcGet)) {
					session.setAttribute(CRC_CHECKSUM, false);
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
					decoderOut.write(bpPack);
					return true;
				}
				try {
					packIoBuf.rewind();
					packIoBuf.position(vrbPos);
					if(0 != bpPack.parseVariableHeader()) {
						return ret;
					}
					if(0 != bpPack.parsePayload()) {
						return ret;
					}
					
				} catch(Exception e) {
					session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
		            Util.logger(logger, Util.ERROR, e);
					throw e;
				}
				session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
				decoderOut.write(bpPack);
				ret = true;
			}
			break;
		default:
			session.setAttribute(DECODE_STATE, DecodeState.DEC_FX_HEAD);
			throw new BPParseException("Error: Bad decode state!");
		}

		return ret;
	}
}
