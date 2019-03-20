/**
 * 
 */
package bc_server;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPDeviceSession;
import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.Payload;
import other.Util;

/**
 * @author Ansersion
 *
 */
public class PostSignalValuesProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PostSignalValuesProduct.class);
	private BPPacket bpPacket;
	private BPDeviceSession bpDeviceSession;
	private byte[] postData;
	private int packSeq;
	
	public PostSignalValuesProduct(BPDeviceSession bpDeviceSession, byte[] postData, int packSeq) {
		super();
		this.bpDeviceSession = bpDeviceSession;
		this.postData = postData;
		this.packSeq = packSeq;
		this.bpPacket = null;
	}

	/* (non-Javadoc)
	 * @see bc_server.Product#consume()
	 */
	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			logger.info("PostPacketDeviceIDProduct consumed");
			IoSession session = bpDeviceSession.getSession();
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
		if(null == postData) {
			return ret;
		}
		try {
			logger.info("PostSignalValuesProduct producing");
			bpPacket = BPPackFactory.createBPPack(BPPacketType.POST);
			bpPacket.getVrbHead().setSigValFlag(true);
			bpPacket.getVrbHead().setPackSeq(packSeq);
			Payload pld = bpPacket.getPld();
			pld.setRelayData(postData);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
			bpPacket = null;
		}
		return ret;
	}

}
