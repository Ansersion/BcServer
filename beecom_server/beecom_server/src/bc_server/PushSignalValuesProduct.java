package bc_server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
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
import db.UserDevRelInfoInterface;

public class PushSignalValuesProduct extends Product {
	private static final Logger logger = LoggerFactory.getLogger(PushSignalValuesProduct.class);
	private BPPacket bpPacket;
	private BPDeviceSession bpDeviceSession;
	private byte[] reportData;

	public PushSignalValuesProduct(BPDeviceSession bpDeviceSession, byte[] reportData) {
		super();
		this.bpDeviceSession = bpDeviceSession;
		this.reportData = reportData;
	}

	@Override
	public boolean consume() {
		boolean ret = false; 
		try {
			logger.info("PushPacketDeviceIDProduct consumed");
			// IoSession session = bpDeviceSession.getSession();
			BeecomDB beecomDb = BeecomDB.getInstance();
			if(null == bpPacket) {
				/* not produced */
				return ret;
			}
			List<UserDevRelInfoInterface> userDevRelInfoInterfaceList = beecomDb.getSn2UserDevRelInfoList(bpDeviceSession.getSnId());
			if(null == userDevRelInfoInterfaceList) {
				return ret;
			}
			int size = userDevRelInfoInterfaceList.size();
			long devUniqId = bpDeviceSession.getUniqDevId();
			long userId;
			BPUserSession userSession;
			IoSession ioSession;
			Map<Long, BPSession> userId2SessionMap = beecomDb.getUserId2SessionMap();
			UserDevRelInfoInterface userDevRelInfoInterface;
			for(int i = 0; i < size; i++) {
				userDevRelInfoInterface = userDevRelInfoInterfaceList.get(i);
				if(0 == (userDevRelInfoInterface.getAuth() & BPPacket.USER_AUTH_READ)) {
					/* no read auth */
					continue;
				}
				userId = userDevRelInfoInterface.getUserId();
				userSession = (BPUserSession)userId2SessionMap.get(userId);
				ioSession = userSession.getSession();
				if(null == ioSession || !ioSession.isConnected()) {
					/* session is not active */
					continue;
				}
				ioSession.write(bpPacket);
			}
			ret = true;
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
		}
		return ret;
	}

	@Override
	public boolean produce() {
		boolean ret = false;
		if(null == bpDeviceSession || null == reportData) {
			return ret;
		}
		try {
			logger.info("PushSignalValuesProduct producing");
			bpPacket = BPPackFactory.createBPPack(BPPacketType.PUSH);
			bpPacket.getVrbHead().setSigValFlag(true);
			Payload pld = bpPacket.getPld();
			BeecomDB beecomDb = BeecomDB.getInstance();
			pld.setPushSigValData(reportData);
			ret = true;
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String str = sw.toString();
			logger.error(str);
			bpPacket = null;
		}
		return ret;
	}

}
