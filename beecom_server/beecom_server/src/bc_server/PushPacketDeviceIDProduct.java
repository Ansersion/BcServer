package bc_server;

import java.io.PrintWriter;
import java.io.StringWriter;
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

class PushPacketDeviceIDProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PushPacketDeviceIDProduct.class);
	private BPUserSession bpUserSession;
	private BPPacket bpPacket;
	
	public PushPacketDeviceIDProduct(BPUserSession bpSession) {
		super();
		this.bpUserSession = bpSession;
		bpPacket = null;
	}
	
	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			logger.info("PushPacketDeviceIDProduct consumed");
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
			logger.info("PushPacketDeviceIDProduct producing");
			// IoSession session = bpUserSession.getSession();
			bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
			bpPacket.getVrbHead().setReqAllDeviceIdFlag(true);
			// String userName = bpUserSession.getUserName();
			Payload pld = bpPacket.getPld();
			BeecomDB beecomDb = BeecomDB.getInstance();
			Map<Long, Long> deviceIdMap = beecomDb.getDeviceIDMap(bpUserSession.getUserName());
			pld.setDeviceIdMap(deviceIdMap);
			ret = true;
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		return ret;
	}	
}