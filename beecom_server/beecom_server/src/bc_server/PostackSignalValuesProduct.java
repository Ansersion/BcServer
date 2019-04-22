/**
 * 
 */
package bc_server;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPUserSession;
import bp_packet.Payload;
import other.Util;

/**
 * @author Ansersion
 *
 */
public class PostackSignalValuesProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PostackSignalValuesProduct.class);
	
	private BPPacket bpPacket;
	private BPUserSession bpUserSession;
	private byte[] postackData;
	private int packSeq;
	
	public PostackSignalValuesProduct(BPUserSession bpUserSession, byte[] postackData, int packSeq) {
		super();
		this.bpUserSession = bpUserSession;
		this.postackData = postackData;
		this.packSeq = packSeq;
		this.bpPacket = null;
	}
	
	
	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			if(null == bpPacket) {
				return ret;
			}
			logger.info("PostackSignalValuesProduct consumed");
			IoSession session = bpUserSession.getSession();
			session.write(bpPacket);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see bc_server.Product#produce()
	 */
	@Override
	public boolean produce() {
		boolean ret = false;
		try {
			logger.info("PostackSignalValuesProduct producing");
			bpPacket = BPPackFactory.createBPPack(BPPacketType.POSTACK);
			bpPacket.getFxHead().setCrcType(bpUserSession.getCrcType());
			bpPacket.getFxHead().setEncryptType(bpUserSession.getEncryptionType());
			bpPacket.getVrbHead().setSigValFlag(true);
			bpPacket.getVrbHead().setPackSeq(packSeq);
			Payload pld = bpPacket.getPld();
			pld.setRelayData(postackData);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			bpPacket = null;
		}
		return ret;
	}

}
