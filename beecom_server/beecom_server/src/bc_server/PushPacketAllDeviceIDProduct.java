package bc_server;

import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPUserSession;
import bp_packet.Payload;
import db.BeecomDB;
import other.Util;

class PushPacketAllDeviceIDProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PushPacketAllDeviceIDProduct.class);
	private BPUserSession bpUserSession;
	private BPPacket bpPacket;
	
	public PushPacketAllDeviceIDProduct(BPUserSession bpSession) {
		super();
		this.bpUserSession = bpSession;
		bpPacket = null;
	}
	
	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			logger.info("PushPacketAllDeviceIDProduct consumed");
			IoSession session = bpUserSession.getSession();
			session.write(bpPacket);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}

	@Override
	public boolean produce() {
		boolean ret = false;
		if(null == bpUserSession) {
			return ret;
		}
		try {
			logger.info("PushPacketAllDeviceIDProduct producing");
			bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
			bpPacket.getFxHead().setCrcType(bpUserSession.getCrcType());
			bpPacket.getFxHead().setEncryptType(bpUserSession.getEncryptionType());
			bpPacket.getVrbHead().setReqAllDeviceIdFlag(true);
			Payload pld = bpPacket.getPld();
			BeecomDB beecomDb = BeecomDB.getInstance();
			Map<Long, Long> deviceIdMap = beecomDb.getDeviceIDMap(bpUserSession.getUserName());
			pld.setDeviceIdMap(deviceIdMap);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}	
}