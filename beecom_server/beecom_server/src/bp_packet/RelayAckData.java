/**
 * 
 */
package bp_packet;

/**
 * @author Ansersion
 *
 */
public class RelayAckData {
	private BPUserSession userSession;
	private long timestamp;
	private int packSeq;
	
	public RelayAckData(BPUserSession userSession, int packSeq) {
		super();
		this.userSession = userSession;
		this.timestamp = System.currentTimeMillis();
		this.packSeq = packSeq;
	}
	public BPUserSession getUserSession() {
		return userSession;
	}
	public void setUserSession(BPUserSession userSession) {
		this.userSession = userSession;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public int getPackSeq() {
		return packSeq;
	}
	public void setPackSeq(int packSeq) {
		this.packSeq = packSeq;
	}
	
	
}
