package bp_packet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.RelayData;
import db.SignalInfoUnitInterface;

public class BPDeviceSession extends BPSession {
	private static final Logger logger = LoggerFactory.getLogger(BPDeviceSession.class); 
	private static final int MAX_RELAY_LIST_SIZE = 10;
	private Long uniqDeviceId;
	private String password;
	private Map<Integer, List<Object> > signalValueMap;
	private Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap;
	private boolean sigMapCheckOK;
	
	public BPDeviceSession(IoSession session) {
		super(session);
		uniqDeviceId = 0L;
		password = "";
		signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
		sigMapCheckOK = false;
	}
	
	public BPDeviceSession(IoSession session, Long uniqDeviceId, String password) {
		super(session);
		this.uniqDeviceId = uniqDeviceId;
		this.password = password;
		this.signalValueMap = new HashMap<>();
		super.setSystemSignalValueMap(new HashMap<Integer, Object>());
		sigMapCheckOK = false;
	}
	
	public boolean putRelayList(IoSession iosession, BPPacket bppacket, int timeout) {
		
		if(MAX_RELAY_LIST_SIZE <= getRelayListSize()) {
			return false;
		}
		
        TimerTask timeoutTask = new TimerTask() {  
            @Override  
            public void run() {  
            	try {
					RelayData relayData = getRelayData(this);
					BPPacket packAck = (BPPacket) relayData.getRelayData();
					packAck.getVrbHead().setRetCode(BPPacketPOST.RET_CODE_TIMEOUT_ERR);
					session.write(packAck);
					relayData.getIoSession().write(packAck);
					relayData.setTimeoutRelayed(true);
					removeRelayList(packAck.getVrbHead().getPackSeq());
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw, true));
					String str = sw.toString();
					logger.error(str);
				}
            }  
        };
        
		RelayData relayData = new RelayData(new Timer(), timeoutTask, iosession, System.currentTimeMillis(), bppacket);
		
		return super.putRelayList(bppacket.getVrbHead().getPackSeq(), timeout * 1000, relayData);
	}
	
	@Override
	public Map<Integer, List<Object> > getSignalValueMap() {
		return signalValueMap;
	}
	
	@Override
	public boolean ifUserSession() {
		return false;
	}

	@Override
	public void setUniqDeviceId(Long uniqDeviceId) {
		this.uniqDeviceId = uniqDeviceId;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return uniqDeviceId.toString();
	}
	
	public long getUniqDevId() {
		return uniqDeviceId;
	}

	public Map<Integer, SignalInfoUnitInterface> getSignalId2InfoUnitMap() {
		return signalId2InfoUnitMap;
	}

	public void setSignalId2InfoUnitMap(Map<Integer, SignalInfoUnitInterface> signalId2InfoUnitMap) {
		this.signalId2InfoUnitMap = signalId2InfoUnitMap;
	}

	public boolean isSigMapCheckOK() {
		return sigMapCheckOK;
	}

	public void setSigMapCheckOK(boolean sigMapCheckOK) {
		this.sigMapCheckOK = sigMapCheckOK;
	}
	
	
}
