package bc_server;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bp_packet.BPDeviceSession;
import bp_packet.BPPackFactory;
import bp_packet.BPPacket;
import bp_packet.BPPacketType;
import bp_packet.BPSession;
import bp_packet.BPUserSession;
import bp_packet.Payload;
import db.BeecomDB;
import other.Util;

class PushPacketNewDeviceIDProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PushPacketNewDeviceIDProduct.class);
	private BPDeviceSession bpDeviceSession;
	private BPPacket bpPacket;
	private long adminId;
	
	public PushPacketNewDeviceIDProduct(BPDeviceSession bpSession, long adminId) {
		super();
		this.bpDeviceSession = bpSession;
		bpPacket = null;
		this.adminId = adminId;
	}
	
	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			logger.info("PushPacketNewDeviceIDProduct consumed");
			BeecomDB beecomDb = BeecomDB.getInstance();
			if(null == bpPacket) {
				/* not produced */
				return ret;
			}

			BPUserSession userSession;
			IoSession ioSession;
			Map<Long, BPSession> userId2SessionMap = beecomDb.getUserId2SessionMap();
			if(userId2SessionMap.containsKey(adminId)) {
				userSession = (BPUserSession)userId2SessionMap.get(adminId);
				if(null == userSession) {
					/* not logined yet */
					return ret;
				}
				ioSession = userSession.getSession();
				if(null == ioSession || !ioSession.isConnected()) {
					/* session is not active */
					return ret;
				}
				ioSession.write(bpPacket);
			}
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}

	@Override
	public boolean produce() {
		boolean ret = false;
		if(null == bpDeviceSession) {
			return ret;
		}
		try {
			logger.info("PushPacketNewDeviceIDProduct producing");
			bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
			bpPacket.getFxHead().setCrcType(bpDeviceSession.getCrcType());
			bpPacket.getFxHead().setEncryptType(bpDeviceSession.getEncryptionType());
			bpPacket.getVrbHead().setReqNewDeviceIdFlag(true);
			Payload pld = bpPacket.getPld();
			Map<Long, Long> deviceIdMap = new HashMap<>();
			deviceIdMap.put(bpDeviceSession.getUniqDevId(), BPPacket.INVALID_SIGNAL_MAP_CHECKSUM);
			pld.setDeviceIdMap(deviceIdMap);
			ret = true;
		} catch(Exception e) {
			Util.logger(logger, Util.ERROR, e);
		}
		return ret;
	}	
}